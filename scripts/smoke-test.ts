import assert from "node:assert/strict";
import { spawn } from "node:child_process";
import { resolve } from "node:path";
import { setTimeout as delay } from "node:timers/promises";

async function waitUntil(ready: () => boolean, description: string, timeout = 5_000) {
  const deadline = Date.now() + timeout;
  while (!ready()) {
    if (Date.now() >= deadline) throw new Error(`Timed out: ${description}`);
    await delay(10);
  }
}

function client(port: number) {
  const socket = new WebSocket(`ws://127.0.0.1:${port}/ws`);
  const messages: string[] = [];
  let error: Error | undefined;
  socket.addEventListener("message", (event) => messages.push(String(event.data)));
  socket.addEventListener("error", () => { error = new Error("WebSocket error"); });

  return {
    socket,
    async expect(expected: string) {
      await waitUntil(() => {
        if (error) throw error;
        if (messages.length > 0) return true;
        if (socket.readyState === WebSocket.CLOSED) throw new Error("WebSocket closed unexpectedly");
        return false;
      }, `WebSocket message ${JSON.stringify(expected)}`);
      assert.equal(messages.shift(), expected);
    },
  };
}

async function main() {
  const executable = resolve(process.argv[2] ?? "build/native/nativeCompile/heli-ws");
  // Let the server select a free port, then read its startup message.
  const server = spawn(executable, ["0"], { stdio: ["ignore", "pipe", "pipe"] });
  let output = "";
  let port: number | undefined;
  let startupError: Error | undefined;
  let stopped = false;
  const sockets: WebSocket[] = [];
  const exited = new Promise<void>((done) => {
    server.once("exit", () => { stopped = true; done(); });
    server.once("error", (error) => { startupError = error; stopped = true; done(); });
  });
  server.stdout.on("data", (chunk) => {
    output += chunk.toString();
    const match = output.match(/heli-ws started: http:\/\/localhost:(\d+)/);
    if (match) port = Number(match[1]);
  });
  server.stderr.on("data", (chunk) => { output += chunk.toString(); });

  try {
    await waitUntil(() => {
      if (startupError) throw startupError;
      if (stopped) throw new Error(`Server exited with status ${server.exitCode}`);
      return port !== undefined;
    }, "server startup", 30_000);
    assert.ok(port);
    const response = await fetch(`http://127.0.0.1:${port}/`, { signal: AbortSignal.timeout(5_000) });
    assert.equal(response.status, 200);
    assert.match(await response.text(), /<html/i);

    const first = client(port);
    sockets.push(first.socket);
    await first.expect("[system] online: 1");
    const second = client(port);
    sockets.push(second.socket);
    await first.expect("[system] online: 2");
    await second.expect("[system] online: 2");

    first.socket.send("/ping");
    await first.expect("pong");
    first.socket.send("/online");
    await first.expect("[system] online: 2");
    first.socket.send("native smoke test");
    await first.expect("native smoke test");
    await second.expect("native smoke test");
    console.log("PASS: homepage, WebSocket handshake, online count, ping, two-client broadcast");
  } catch (error) {
    console.error(output);
    throw error;
  } finally {
    for (const socket of sockets) socket.close();
    if (!stopped) {
      server.kill("SIGTERM");
      try {
        await waitUntil(() => stopped, "server shutdown", 10_000);
      } catch {
        server.kill("SIGKILL");
      }
    }
    await exited;
  }
}

try {
  await main();
} catch (error) {
  console.error(error);
  process.exitCode = 1;
}

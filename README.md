# heli-ws

A small WebSocket chat server built with Helidon.

## Build a native binary

Requirements:
- GraalVM for JDK 25 with `native-image` available.
- Gradle 9.x installed (this repository does not include a Gradle wrapper).
- A native C/C++ toolchain for your platform (on Ubuntu: `sudo apt install build-essential zlib1g-dev`).

Set `JAVA_HOME` to your GraalVM installation and add `$JAVA_HOME/bin` to `PATH`, then run:

```sh
gradle nativeCompile
```

The binary is generated at `build/native/nativeCompile/heli-ws` (`heli-ws.exe` on Windows). Build on the target OS and architecture.

Native builds use Helidon's `helidon-graal-native-image-extension`, matching the
Helidon dependency version, to register framework reflection metadata at build
time. The extension is only on the native compilation classpath. Gradle also
detects resources for inclusion in the binary; `resource-config.json` explicitly
includes the static web content and its directory. Service discovery keeps its
default behavior, without a generated `reachability-metadata.json` in the source
tree. This follows the [Helidon SE native build profile](https://github.com/helidon-io/helidon/blob/4.5.6/applications/se/pom.xml).

## Run

```sh
./build/native/nativeCompile/heli-ws
# Optional port (default: 8080)
./build/native/nativeCompile/heli-ws 9090
```

Open `http://localhost:8080` in your browser. The WebSocket endpoint is `/ws`.

After building, test the actual native executable using a current Node.js or Bun
version that can run TypeScript directly (no dependencies to install):

```sh
node scripts/smoke-test.ts
# Or use Bun:
bun scripts/smoke-test.ts
# Or test a binary in another location:
node scripts/smoke-test.ts /path/to/heli-ws
```

This checks startup, the homepage, WebSocket handshakes, online counts, ping, and
broadcasting between two clients. A successful native compilation alone does not
verify that runtime service discovery works.

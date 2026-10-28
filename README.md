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

## Run

```sh
./build/native/nativeCompile/heli-ws
# Optional port (default: 8080)
./build/native/nativeCompile/heli-ws 9090
```

Open `http://localhost:8080` in your browser. The WebSocket endpoint is `/ws`.

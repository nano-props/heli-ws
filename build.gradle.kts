plugins {
    java
    application
    id("org.graalvm.buildtools.native") version "0.11.3"
}

group = "helo.ws"

val helidonVersion = "4.5.6"

repositories { mavenCentral() }

java { toolchain { languageVersion = JavaLanguageVersion.of(25) } }

dependencies {
    implementation(platform("io.helidon:helidon-dependencies:$helidonVersion"))
    implementation("io.helidon.webserver:helidon-webserver")
    implementation("io.helidon.webserver:helidon-webserver-websocket")
    implementation("io.helidon.webserver:helidon-webserver-static-content")
    implementation("io.helidon.config:helidon-config")
    implementation("io.helidon.logging:helidon-logging-jul")
    nativeImageCompileOnly("io.helidon.integrations.graal:helidon-graal-native-image-extension:$helidonVersion")
}

application { mainClass = "helo.ws.Main" }

graalvmNative {
    binaries {
        named("main") {
            imageName = "heli-ws"
            mainClass = "helo.ws.Main"
            buildArgs.add("-O2")
            buildArgs.add("--add-exports=org.graalvm.nativeimage.builder/com.oracle.svm.core.configure=ALL-UNNAMED")
            resources.autodetect()
        }
    }
}

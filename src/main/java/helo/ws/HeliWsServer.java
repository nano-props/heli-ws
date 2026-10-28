package helo.ws;

import io.helidon.webserver.WebServer;
import io.helidon.webserver.http.HttpRouting;
import io.helidon.webserver.staticcontent.ClasspathHandlerConfig;
import io.helidon.webserver.staticcontent.StaticContentFeature;
import io.helidon.webserver.websocket.WsRouting;

final class HeliWsServer {

    private HeliWsServer() {}

    static WebServer start(int port) {
        var listener = new ChatListener(new ChatSessions());
        return WebServer.builder()
                .port(port)
                .addRouting(HttpRouting.builder().register("/", staticWeb()))
                .addRouting(WsRouting.builder().endpoint("/ws", listener))
                .build()
                .start();
    }

    private static io.helidon.webserver.http.HttpService staticWeb() {
        return StaticContentFeature.createService(
                ClasspathHandlerConfig.builder().location("web").welcome("index.html").build());
    }
}

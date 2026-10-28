package helo.ws;

import io.helidon.service.registry.GlobalServiceRegistry;
import io.helidon.service.registry.ServiceRegistryConfig;
import io.helidon.service.registry.ServiceRegistryManager;

public class Main {

    public static void main(String[] args) {
        disableServiceDiscovery();
        var port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        var server = HeliWsServer.start(port);
        IO.println("heli-ws started: http://localhost:" + server.port());
    }

    private static void disableServiceDiscovery() {
        var config = ServiceRegistryConfig.builder()
                .discoverServices(false)
                .discoverServicesFromServiceLoader(false)
                .build();
        GlobalServiceRegistry.registry(ServiceRegistryManager.create(config).registry());
    }
}

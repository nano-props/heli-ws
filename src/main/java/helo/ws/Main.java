package helo.ws;

public class Main {

    public static void main(String[] args) {
        var port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        var server = HeliWsServer.start(port);
        IO.println("heli-ws started: http://localhost:" + server.port());
    }
}

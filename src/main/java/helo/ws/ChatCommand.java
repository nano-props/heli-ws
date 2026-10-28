package helo.ws;

record ChatCommand(String name, String arg) {

    static ChatCommand parse(String text) {
        var t = text.strip();
        if (!t.startsWith("/")) return new ChatCommand("say", t);
        var i = t.indexOf(' ');
        return i < 0
                ? new ChatCommand(t.substring(1), "")
                : new ChatCommand(t.substring(1, i), t.substring(i + 1).strip());
    }
}

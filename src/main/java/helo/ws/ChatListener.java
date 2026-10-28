package helo.ws;

import io.helidon.websocket.WsListener;
import io.helidon.websocket.WsSession;

final class ChatListener implements WsListener {

    private final ChatSessions sessions;

    ChatListener(ChatSessions sessions) {
        this.sessions = sessions;
    }

    @Override
    public void onOpen(WsSession session) {
        sessions.add(session);
        sessions.broadcast("[system] online: " + sessions.size());
    }

    @Override
    public void onMessage(WsSession session, String text, boolean last) {
        var cmd = ChatCommand.parse(text);
        switch (cmd.name()) {
            case "ping" -> sessions.send(session, "pong");
            case "online" -> sessions.send(session, "[system] online: " + sessions.size());
            case "say" -> sessions.broadcast(cmd.arg());
            default -> sessions.send(session, "[system] unknown command: /" + cmd.name());
        }
    }

    @Override
    public void onClose(WsSession session, int status, String reason) {
        sessions.remove(session);
        sessions.broadcast("[system] online: " + sessions.size());
    }

    @Override
    public void onError(WsSession session, Throwable t) {
        sessions.remove(session);
    }
}

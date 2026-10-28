package helo.ws;

import io.helidon.websocket.WsSession;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

final class ChatSessions {

    private final Set<WsSession> sessions = ConcurrentHashMap.newKeySet();

    void add(WsSession session) {
        sessions.add(session);
    }

    void remove(WsSession session) {
        sessions.remove(session);
    }

    int size() {
        return sessions.size();
    }

    void send(WsSession session, String msg) {
        synchronized (session) {
            try {
                session.send(msg, true);
            } catch (Exception _) {
                sessions.remove(session);
            }
        }
    }

    void broadcast(String msg) {
        sessions.forEach(s -> send(s, msg));
    }
}

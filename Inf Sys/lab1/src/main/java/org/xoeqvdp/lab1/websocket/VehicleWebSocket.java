package org.xoeqvdp.lab1.websocket;

import jakarta.websocket.OnClose;
import jakarta.websocket.OnMessage;
import jakarta.websocket.OnOpen;
import jakarta.websocket.Session;
import jakarta.websocket.server.ServerEndpoint;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@ServerEndpoint("/ws/vehicles")
public class VehicleWebSocket {

    private static final Set<Session> sessions = Collections.synchronizedSet(new HashSet<>());

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Session closed: " + session.getId());
    }
//    @OnMessage
//    public void onMessage(String message, Session session) throws IOException {
//        // Полученное сообщение можно обработать, если нужно
//        broadcast("update");
//    }



    public static void broadcast(String message) {
        synchronized (sessions) {
            for (Session session : sessions) {
                try {
                    if (session.isOpen()) {
                        session.getBasicRemote().sendText(message);
                    } else {
                        System.out.println("Сессия закрыта: " + session.getId());
                        sessions.remove(session);
                    }
                } catch (IOException e) {
                    sessions.remove(session);
                    System.out.println("Closed session removed: " + session.getId());
                }
            }
        }
    }
}

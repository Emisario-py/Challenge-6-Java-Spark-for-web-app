package org.digitalnao.controller;

import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.*;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@WebSocket
public class OfferWebSocket {

    private static final Gson gson = new Gson();

    private static final Map<Integer, Set<Session>> itemSubscriptions = new ConcurrentHashMap<>();

    private static final Set<Session> allSessions = ConcurrentHashMap.newKeySet();

    @OnWebSocketConnect
    public void onConnect(Session session) {
        allSessions.add(session);
        System.out.println("✅ Cliente WebSocket conectado: " + session.getRemoteAddress());

        // Enviar mensaje de bienvenida
        Map<String, Object> message = new HashMap<>();
        message.put("type", "connected");
        message.put("message", "Conectado al sistema de subastas");
        sendToSession(session, gson.toJson(message));
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) {
        System.out.println("📨 Mensaje recibido: " + message);

        try {
            Map<String, Object> data = gson.fromJson(message, Map.class);
            String action = (String) data.get("action");

            if ("subscribe".equals(action)) {
                Number itemIdNum = (Number) data.get("itemId");
                if (itemIdNum != null) {
                    int itemId = itemIdNum.intValue();
                    subscribeToItem(session, itemId);

                    Map<String, Object> response = new HashMap<>();
                    response.put("type", "subscribed");
                    response.put("message", "Suscrito a actualizaciones del item " + itemId);
                    response.put("itemId", itemId);
                    sendToSession(session, gson.toJson(response));
                }
            } else if ("unsubscribe".equals(action)) {
                Number itemIdNum = (Number) data.get("itemId");
                if (itemIdNum != null) {
                    int itemId = itemIdNum.intValue();
                    unsubscribeFromItem(session, itemId);
                }
            }
        } catch (Exception e) {
            System.err.println("Error procesando mensaje: " + e.getMessage());
        }
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        allSessions.remove(session);
        itemSubscriptions.values().forEach(sessions -> sessions.remove(session));
        System.out.println("Cliente desconectado: " + reason);
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        System.err.println("Error en WebSocket: " + error.getMessage());
    }

    public static void notifyNewOffer(int itemId, int userId, double amount, String userName) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "new_offer");
        message.put("message", "Nueva oferta en el item " + itemId);
        message.put("itemId", itemId);
        message.put("userId", userId);
        message.put("userName", userName);
        message.put("amount", amount);
        message.put("timestamp", System.currentTimeMillis());

        broadcastToItem(itemId, gson.toJson(message));
    }

    public static void notifyNewItem(int itemId, String itemName, double initialPrice, String userName) {
        Map<String, Object> message = new HashMap<>();
        message.put("type", "new_item");
        message.put("message", "Nuevo item disponible: " + itemName);
        message.put("itemId", itemId);
        message.put("itemName", itemName);
        message.put("userName", userName);
        message.put("initialPrice", initialPrice);
        broadcast(gson.toJson(message));
    }

    private void subscribeToItem(Session session, int itemId) {
        itemSubscriptions.computeIfAbsent(itemId, k -> ConcurrentHashMap.newKeySet())
                .add(session);
        System.out.println("Session suscrita al item " + itemId);
    }

    private void unsubscribeFromItem(Session session, int itemId) {
        Set<Session> sessions = itemSubscriptions.get(itemId);
        if (sessions != null) {
            sessions.remove(session);
        }
    }

    private static void broadcastToItem(int itemId, String json) {
        Set<Session> sessions = itemSubscriptions.get(itemId);
        if (sessions != null) {
            sessions.forEach(session -> sendToSession(session, json));
            System.out.println("Broadcast a " + sessions.size() + " clientes del item " + itemId);
        }
    }

    private static void broadcast(String json) {
        allSessions.forEach(session -> sendToSession(session, json));
        System.out.println("Broadcast global a " + allSessions.size() + " clientes");
    }

    private static void sendToSession(Session session, String json) {
        if (session != null && session.isOpen()) {
            try {
                session.getRemote().sendString(json);
            } catch (IOException e) {
                System.err.println("Error enviando mensaje: " + e.getMessage());
            }
        }
    }
}

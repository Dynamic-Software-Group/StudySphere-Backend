package dev.dynamic.studysphere.realtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import name.fraser.neil.plaintext.diff_match_patch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class WebsocketServer extends WebSocketServer {

    private Map<String, List<WebsocketClient>> clients = new HashMap<>();
    private Map<String, LinkedList<diff_match_patch.Diff>> diffs = new HashMap<>();

    private Logger logger = LogManager.getLogger();

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        logger.info("New connection: {}", webSocket.getRemoteSocketAddress());
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {

    }

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        try {
            JsonNode jsonNode = mapper.readTree(message);

            switch (jsonNode.get("type").asText()) {
                case "register" -> {
                    String email = jsonNode.get("email").asText();
                    String notecardId = jsonNode.get("notecardId").asText();

                    WebsocketClient client = new WebsocketClient();
                    client.setEmail(email);
                    client.setNotecardId(notecardId);
                    client.setWebSocket(webSocket);

                    if (clients.containsKey(notecardId)) {
                        clients.get(notecardId).add(client);
                    } else {
                        clients.put(notecardId, List.of(client));
                    }
                    webSocket.send("Registered successfully");
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
    }

    @Override
    public void onStart() {

    }

    public void broadcastToNotecard(String nodecardId, String content) {
        for (WebsocketClient client : clients.get(nodecardId)) {
            client.getWebSocket().send(content);
        }
    }
}
package dev.dynamic.studysphere.realtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import crdt.sets.TwoPSet;
import dev.dynamic.studysphere.auth.JwtUtil;
import dev.dynamic.studysphere.model.NotecardRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.util.*;

@Service
public class WebsocketServer extends WebSocketServer {

    private final NotecardRepository notecardRepository;
    private final JwtUtil jwtUtil;

    final Map<String, List<WebsocketClient>> clients = new HashMap<>();
    final Map<String, TwoPSet<String>> notecardContent = new HashMap<>(); // Master document
    final Map<String, List<TextChange>> pendingUpdates = new HashMap<>();

    public WebsocketServer(NotecardRepository notecardRepository, JwtUtil jwtUtil) {
        super(new InetSocketAddress(59101));
        this.notecardRepository = notecardRepository;
        this.jwtUtil = jwtUtil;
        start();
        new Thread(new WebsocketRunnable(this)).start();
    }

    private final Logger logger = LogManager.getLogger();

    public static class WebsocketRunnable implements Runnable {

        private final WebsocketServer server;

        public WebsocketRunnable(WebsocketServer server) {
            this.server = server;
        }

        @Override
        public void run() {
            while (true) {
                for (String notecardId : server.pendingUpdates.keySet()) {
                    List<TextChange> pending = server.pendingUpdates.get(notecardId);
                    if (pending == null || pending.isEmpty()) {
                        continue;
                    }

                    TwoPSet<String> master = server.notecardContent.get(notecardId);

                    List<TwoPSet<String>> awaiting = new ArrayList<>();

                    for (TextChange change : pending) {
                        TwoPSet<String> newSet = new TwoPSet<>();

                        for (String s : master.get()) {
                            newSet.add(s);
                        }

                        if (change.getAction().equals("add")) {
                            newSet.add(change.getText());
                        } else {
                            newSet.remove(change.getText());
                        }
                        awaiting.add(newSet);
                    }

                    for (TwoPSet<String> set : awaiting) {
                        master.merge(set);
                    }

                    server.notecardContent.put(notecardId, master);
                }

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        logger.info("New connection: {}", webSocket.getRemoteSocketAddress());
        webSocket.send("Connection established");
        // check if they have auth header
        String auth = clientHandshake.getFieldValue("Authorization");

        logger.info("Auth: {}", auth);

        if (auth == null) {
            webSocket.send("Unauthorized");
            webSocket.close();
            return;
        }

        if (!auth.contains("Bearer")) {
            webSocket.send("Invalid auth format");
            webSocket.close();
            return;
        }

        String token = auth.split(" ")[1];

        // check if token is valid

        logger.info("Token: {}", token);

        if (!jwtUtil.validToken(token)) {
            webSocket.send("Invalid token");
            webSocket.close();
            return;
        }

        webSocket.send("Authorized");
    }

    @Override
    public void onClose(WebSocket webSocket, int code, String reason, boolean remote) {
        logger.info("Closed connection: {}", webSocket.getRemoteSocketAddress());
        for (String notecardId : clients.keySet()) {
            clients.get(notecardId).removeIf(client -> client.getWebSocket().equals(webSocket));
        }
    }

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onMessage(WebSocket webSocket, String message) {
        try {
            JsonNode jsonNode = mapper.readTree(message);

            logger.info("Received message: {}", jsonNode);

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

                    TwoPSet<String> set = new TwoPSet<>();
                    set.add(notecardRepository.findById(UUID.fromString(notecardId)).get().getContent());
                    notecardContent.put(notecardId, set);
                    webSocket.send("Registered successfully");
                }
                case "update" -> {
                    String notecardId = jsonNode.get("notecardId").asText();
                    String content = jsonNode.get("content").asText();
                    String action = jsonNode.get("action").asText();
                    int version = jsonNode.get("version").asInt();

                    if (pendingUpdates.containsKey(notecardId)) {
                        List<TextChange> pendingUpdatesList = pendingUpdates.get(notecardId);
                        for (int i = 0; i < pendingUpdatesList.size(); i++) {
                            TextChange pendingUpdate = pendingUpdatesList.get(i);
                            if (pendingUpdate.getVersion() > version) {
                                break;
                            }
                            if (i == pendingUpdatesList.size() - 1) {
                                pendingUpdate.setText(content);
                                pendingUpdate.setAction(action);
                                pendingUpdate.setVersion(version + 1);
                                pendingUpdatesList.set(i, pendingUpdate);
                            }
                        }
                    } else {
                        pendingUpdates.put(notecardId, List.of(new TextChange(content, action, version + 1)));
                    }

                    webSocket.send(mapper.writeValueAsString(new TextChange(content, action, version + 1)));
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
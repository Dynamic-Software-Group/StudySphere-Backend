package dev.dynamic.studysphere.realtime;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import crdt.sets.GSet;
import crdt.sets.ORSet;
import crdt.sets.TwoPSet;
import dev.dynamic.studysphere.model.NotecardRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.util.*;

@Service
public class WebsocketServer extends WebSocketServer {

    @Autowired
    public NotecardRepository notecardRepository;

    final Map<String, List<WebsocketClient>> clients = new HashMap<>();
    final Map<String, TwoPSet<String>> notecardContent = new HashMap<>(); // Master document
    final Map<String, List<TextChange>> pendingUpdates = new HashMap<>();

    public WebsocketServer() {
        int startPort = 49152;
        int endPort = 65535;
        for (int port = startPort; port <= endPort; port++) {
            try {
                ServerSocket serverSocket = new ServerSocket(port);
                System.out.println("Websocket server started on port " + port);
                break;
            } catch (Exception e) {
                if (port == endPort) {
                    throw new RuntimeException("No available ports");
                }
            }
        }
        super(new InetSocketAddress(59101));
        start();
        WebsocketRunnable runnable = new WebsocketRunnable(this);
        new Thread(runnable).start();
    }

    private Logger logger = LogManager.getLogger();

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

                    TwoPSet<String> set = new TwoPSet<>();
                    set.add(notecardRepository.findById(UUID.fromString(notecardId)).get().getContent());
                    notecardContent.put(notecardId, set);
                    webSocket.send("Registered successfully");
                }
                case "update" -> {
                    String notecardId = jsonNode.get("notecardId").asText();
                    String content = jsonNode.get("content").asText();
                    String action = jsonNode.get("action").asText();

                    if (pendingUpdates.containsKey(notecardId)) {
                        pendingUpdates.get(notecardId).add(new TextChange(content, action));
                    } else {
                        pendingUpdates.put(notecardId, List.of(new TextChange(content, action)));
                    }

                    webSocket.send("Update received");
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
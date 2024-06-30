package dev.dynamic.studysphere.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dynamic.studysphere.auth.JwtUtil;
import dev.dynamic.studysphere.model.Notecard;
import dev.dynamic.studysphere.model.NotecardRepository;
import dev.dynamic.studysphere.model.NotecardRole;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.UUID;

@Service
public class WebsocketService extends WebSocketServer {

    @Autowired
    private NotecardRepository notecardRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private BatchInsertService batchInsertService;

    private final Logger logger = LogManager.getLogger(WebsocketService.class);

    public WebsocketService() {
        super(new InetSocketAddress(8887));
        start();
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {
        URI requestUri = URI.create(clientHandshake.getResourceDescriptor());
        String query = requestUri.getQuery();
        String token = null;

        if (query != null) {
            String[] params = query.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length != 2) {
                    continue;
                }

                String key = keyValue[0];
                if ("token".equals(key)) {
                    token = keyValue[1];
                    break;
                }
            }
        }

        if (token == null) {
            logger.info("Unauthorized connection attempt.");
            webSocket.close();
            return;
        }

        if (!jwtUtil.validToken(token)) {
            logger.info("Unauthorized connection attempt.");
            webSocket.close();
            return;
        }

        webSocket.send("Connection established.");
    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {
        logger.info(STR."Connection closed: \{i}");
    }

    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(s);
        } catch (Exception e) {
            logger.error(STR."Failed to parse JSON: \{e.getMessage()}");
            return;
        }

        String token = jsonNode.get("token").asText();
        String email = jwtUtil.getEmail(token);
        String notecardId = jsonNode.get("notecardId").asText();

        Notecard notecard = notecardRepository.findById(UUID.fromString(notecardId)).orElse(null);

        if (notecard == null) {
            logger.error(STR."Notecard not found.");
            return;
        }

        if (!notecard.getOwner().getEmail().equals(email) ||
                notecard.getUserRoles().stream().noneMatch(userRole -> userRole.getUser().getEmail().equals(email) && userRole.getRole().equals(NotecardRole.EDITOR))) {
            logger.error(STR."Unauthorized access attempt.");
            return;
        }

        notecard.setContent(jsonNode.get("content").asText());
        batchInsertService.addToBatch(notecard);
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        logger.error("An error occurred.", e);
    }

    @Override
    public void onStart() {
        logger.info("Websocket server started.");
    }
}

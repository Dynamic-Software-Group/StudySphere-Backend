package dev.dynamic.studysphere.services;

import dev.dynamic.studysphere.auth.JwtUtil;
import dev.dynamic.studysphere.model.NotecardRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Map;

@Service
public class WebsocketService extends WebSocketServer {

    @Autowired
    private NotecardRepository notecardRepository;

    @Autowired
    private JwtUtil jwtUtil;

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

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        logger.info(STR."Message received: \{s}");

    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {

    }

    @Override
    public void onStart() {

    }
}

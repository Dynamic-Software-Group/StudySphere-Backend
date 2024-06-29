package dev.dynamic.studysphere.realtime;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.java_websocket.WebSocket;

@Data
@Getter
@Setter
public class WebsocketClient {
    private String email;
    private String notecardId;
    private WebSocket webSocket;
}

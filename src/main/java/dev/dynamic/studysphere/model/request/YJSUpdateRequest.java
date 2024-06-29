package dev.dynamic.studysphere.model.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class YJSUpdateRequest {
    private final String token;
    private final String content;
    private final String notecardId;
}

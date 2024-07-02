package dev.dynamic.studysphere.model.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ShareNotecardRequest {
    private final String notecardId;
    private final String email;
    private final String token;
}

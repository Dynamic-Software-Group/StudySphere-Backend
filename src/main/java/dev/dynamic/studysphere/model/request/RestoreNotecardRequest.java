package dev.dynamic.studysphere.model.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class RestoreNotecardRequest {
    private final String token;
    private final String notecardId;
}

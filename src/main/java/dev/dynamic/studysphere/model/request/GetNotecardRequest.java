package dev.dynamic.studysphere.model.request;

import lombok.Data;
import lombok.Getter;

import java.util.UUID;

@Data
@Getter
public class GetNotecardRequest {
    private UUID id;
    private String token;
}

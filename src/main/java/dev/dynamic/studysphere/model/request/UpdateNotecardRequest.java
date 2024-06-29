package dev.dynamic.studysphere.model.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class UpdateNotecardRequest {
    private String notecardId;
    private String name;
    private String content;
    private String category;
    private String email;
}

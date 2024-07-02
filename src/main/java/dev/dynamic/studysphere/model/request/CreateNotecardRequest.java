package dev.dynamic.studysphere.model.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CreateNotecardRequest {
    private String name;
    private String category;
    private String token;
}

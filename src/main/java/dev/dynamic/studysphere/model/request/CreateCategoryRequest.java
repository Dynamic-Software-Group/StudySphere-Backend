package dev.dynamic.studysphere.model.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class CreateCategoryRequest {
    private String name;
    private String token;
}

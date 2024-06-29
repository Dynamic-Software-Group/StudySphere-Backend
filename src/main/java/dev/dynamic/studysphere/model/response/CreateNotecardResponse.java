package dev.dynamic.studysphere.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class CreateNotecardResponse {
    private long id;
    private String name;
}

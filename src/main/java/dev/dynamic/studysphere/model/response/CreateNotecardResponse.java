package dev.dynamic.studysphere.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Data
@Getter
@Setter
@AllArgsConstructor
public class CreateNotecardResponse {
    private UUID id;
    private String name;
}

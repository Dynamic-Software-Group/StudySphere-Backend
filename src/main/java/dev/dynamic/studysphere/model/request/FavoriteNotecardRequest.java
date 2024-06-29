package dev.dynamic.studysphere.model.request;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class FavoriteNotecardRequest {
    private String notecardId;
    private String email;
}

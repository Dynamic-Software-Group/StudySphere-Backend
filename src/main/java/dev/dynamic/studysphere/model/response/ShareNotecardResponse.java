package dev.dynamic.studysphere.model.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
public class ShareNotecardResponse {
    private final String name;
    private final String email;

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

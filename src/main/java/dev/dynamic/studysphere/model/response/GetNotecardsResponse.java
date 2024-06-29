package dev.dynamic.studysphere.model.response;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.dynamic.studysphere.model.Notecard;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Data
public class GetNotecardsResponse {
    private Set<Notecard> notecards;

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(this);
        } catch (Exception e) {
            return super.toString();
        }
    }
}

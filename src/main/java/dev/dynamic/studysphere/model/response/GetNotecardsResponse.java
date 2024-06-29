package dev.dynamic.studysphere.model.response;

import dev.dynamic.studysphere.model.Notecard;
import lombok.AllArgsConstructor;

import java.util.Set;

@AllArgsConstructor
public class GetNotecardsResponse {
    private Set<Notecard> notecards;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("notecards=");
        sb.append("[");
        for (Notecard notecard : notecards) {
            sb.append(notecard.toString());
            sb.append(", ");
        }
        sb.append("]");
        sb.append("}");
        return sb.toString();
    }
}

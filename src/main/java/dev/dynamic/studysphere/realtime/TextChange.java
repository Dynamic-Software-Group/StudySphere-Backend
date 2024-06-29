package dev.dynamic.studysphere.realtime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@AllArgsConstructor
public class TextChange {
    private String text;
    private String action;
    private int version;
}

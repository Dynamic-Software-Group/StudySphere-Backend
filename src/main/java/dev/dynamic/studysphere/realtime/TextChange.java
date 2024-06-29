package dev.dynamic.studysphere.realtime;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class TextChange {
    private final String text;
    private final String action;
}

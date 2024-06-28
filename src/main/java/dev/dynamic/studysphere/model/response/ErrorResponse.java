package dev.dynamic.studysphere.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Data
@Getter
@Setter
@AllArgsConstructor
public class ErrorResponse {
    private HttpStatus httpStatus;
    private String message;
}

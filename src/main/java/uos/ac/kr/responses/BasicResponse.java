package uos.ac.kr.responses;

import lombok.*;
import org.springframework.http.HttpStatus;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class BasicResponse<T> {
    private Integer code;
    private HttpStatus httpStatus;
    private String error;
    private String message;
    private Integer count;
    private T data;
}

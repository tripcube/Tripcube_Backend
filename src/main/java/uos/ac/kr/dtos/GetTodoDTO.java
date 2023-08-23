package uos.ac.kr.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import uos.ac.kr.domains.Todo;

import java.util.Date;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class GetTodoDTO {
    private int todoId;
    private String placeName;
    private int placeId;
    private String content;
    private int likes;
    private Date createdAt;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String date;

    private String tag;
    private boolean isLike;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int userId;

}

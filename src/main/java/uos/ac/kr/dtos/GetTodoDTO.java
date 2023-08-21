package uos.ac.kr.dtos;

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
    private String date;
    private String tag;
    private boolean isLike;
    private int userId;

}

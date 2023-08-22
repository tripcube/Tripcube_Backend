package uos.ac.kr.dtos;

import lombok.*;

import java.util.Date;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class GetCommentDTO {
    private int commentId;
    private int todoId;
    private String todo_content;
    private String comment_content;
    private String placeName;
    private int placeId;
    private String tag;
    private int todo_likes;
    private int comment_likes;
    private boolean todo_islike;
    private boolean comment_islike;
    private String date;
    private Date createdAt;
}

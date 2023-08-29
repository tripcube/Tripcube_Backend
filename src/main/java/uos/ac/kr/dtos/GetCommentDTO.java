package uos.ac.kr.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Date;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class GetCommentDTO {
    private int commentId;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int todoId;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String todo_content;

    private String comment_content;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String placeName;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int placeId;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String tag;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int todo_likes;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int userId;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String userName;

    private int comment_likes;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private boolean todo_islike;

    private boolean comment_islike;
    private String date;
    private Date createdAt;
}

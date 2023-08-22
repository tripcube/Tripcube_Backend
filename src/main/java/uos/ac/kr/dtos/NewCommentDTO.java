package uos.ac.kr.dtos;

import lombok.*;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class NewCommentDTO {
    private int todoId;
    private String content;
}

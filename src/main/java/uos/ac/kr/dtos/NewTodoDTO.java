package uos.ac.kr.dtos;

import lombok.*;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class NewTodoDTO {
    private int placeId;
    private String content;
    private String tag;

}

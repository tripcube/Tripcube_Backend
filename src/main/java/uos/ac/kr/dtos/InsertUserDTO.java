package uos.ac.kr.dtos;

import lombok.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class InsertUserDTO {
    @NotEmpty(message = "id는 null일 수 없습니다.")
    @Size(min = 1, max = 20, message = "id의 크기는 1에서 16 사이입니다.")
    private String loginId;

    private String password;

    private String name;

    private String createdAt;

}

package uos.ac.kr.dtos;

import lombok.*;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class UpdateUserDTO {
    //@NotEmpty(message = "password는 null일 수 없습니다.")
    //@Size(min = 1, max = 32, message = "password의 크기는 1에서 32 사이입니다.")
    private String password;

    //@NotEmpty(message = "name은 null일 수 없습니다.")
    //@Size(min = 1, max = 48, message = "name의 크기는 1에서 48 사이입니다.")
    private String nickName;


}

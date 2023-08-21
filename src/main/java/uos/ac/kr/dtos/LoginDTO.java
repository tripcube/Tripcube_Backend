package uos.ac.kr.dtos;

import lombok.*;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class LoginDTO {

    private String loginId;
    private String password;

}

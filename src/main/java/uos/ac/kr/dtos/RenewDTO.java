package uos.ac.kr.dtos;

import lombok.*;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class RenewDTO {

    private String refreshToken;

    private Integer userId;

}

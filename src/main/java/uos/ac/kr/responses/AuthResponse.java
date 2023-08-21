package uos.ac.kr.responses;

import lombok.*;

import java.util.Date;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class AuthResponse {

    private String accessToken, refreshToken;

    private Date createdAt;

    private boolean isLogout = false;
}

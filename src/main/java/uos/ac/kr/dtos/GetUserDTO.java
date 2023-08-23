package uos.ac.kr.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor()
@Setter
@Getter
public class GetUserDTO {
    private int userId;
    private String name;
    private String oneliner;
    private String profileImage;
    private String backgroundImage;
}

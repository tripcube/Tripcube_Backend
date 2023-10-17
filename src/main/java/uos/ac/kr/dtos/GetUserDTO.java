package uos.ac.kr.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
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

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String loginId;
}

package uos.ac.kr.dtos;

import lombok.*;
import uos.ac.kr.enums.UserSortKey;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class SelectUserDTO {

    private int skip = 0, take = 20;

    private String id;

    private String nickName;

    private UserSortKey sortKey = UserSortKey.ID_ASC;
}

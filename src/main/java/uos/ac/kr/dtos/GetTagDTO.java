package uos.ac.kr.dtos;

import lombok.*;

import javax.validation.constraints.NotEmpty;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class GetTagDTO {
    @NotEmpty(message = "여행지 활동을 입력해주세요.")
    private String todo;
}

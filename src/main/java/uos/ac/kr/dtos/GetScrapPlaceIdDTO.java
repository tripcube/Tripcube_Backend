package uos.ac.kr.dtos;

import lombok.*;

import java.util.List;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class GetScrapPlaceIdDTO {
    private List<Integer> scrapPlaceIds;
}

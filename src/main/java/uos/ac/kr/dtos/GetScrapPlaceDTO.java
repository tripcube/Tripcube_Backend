package uos.ac.kr.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class GetScrapPlaceDTO {
    private int scrapPlaceId;
    private int placeId;
    private String placeAddress;
    private String placeName;
    private String placeImage;
    private List<Integer> tags;
}

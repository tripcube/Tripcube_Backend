package uos.ac.kr.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class GetMainPlaceDTO {
    private int placeId;
    private String placeName;
    private String placeImage;
    private List<Integer> tags;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String content;
}

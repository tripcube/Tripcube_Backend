package uos.ac.kr.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class GetPlaceDTO {
    private String address;
    private String placeName;
    private int placeId;
    private String image;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private double mapX;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private double mapY;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private int contenttype;
    private boolean scrap;

    private ArrayList<String> tags;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String tel;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String website;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String useTime;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private String parking;

    @JsonInclude(JsonInclude.Include.NON_DEFAULT)
    private ArrayList<String> moreImages;
}

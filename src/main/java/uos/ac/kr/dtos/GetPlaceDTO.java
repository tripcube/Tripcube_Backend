package uos.ac.kr.dtos;

import lombok.*;

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
    private double mapX;
    private double mapY;
    private int contenttype;
    private boolean scrap;
}

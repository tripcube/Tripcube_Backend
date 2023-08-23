package uos.ac.kr.dtos;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class GetLocationPlaceDTO {
    private int page;
    private ArrayList<GetPlaceDTO> places;
}

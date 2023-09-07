package uos.ac.kr.dtos;

import lombok.*;

@AllArgsConstructor()
@NoArgsConstructor()
@Setter
@Getter
@Builder
public class GetFolderDTO {
    private int folderId;
    private String coverImage;
    private String name;
    private int placeCount;
}

package uos.ac.kr.domains;

import lombok.*;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Embeddable
public class ScrapFolderId implements Serializable {
    private int scrapPlaceId;
    private int folderId;
}

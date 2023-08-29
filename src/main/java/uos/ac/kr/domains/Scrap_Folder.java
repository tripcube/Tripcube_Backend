package uos.ac.kr.domains;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Data
@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "Scrap_Folder")
public class Scrap_Folder {
    @EmbeddedId
    private ScrapFolderId scrapFolderId;

    @MapsId("scrapPlaceId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "scrapPlaceId")
    private Scrap_Place scrapPlace;

    @MapsId("folderId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folderId")
    private Folder folder;

}

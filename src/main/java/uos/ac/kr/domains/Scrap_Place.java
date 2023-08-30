package uos.ac.kr.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity(name="Scrap_Place")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Scrap_Place implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "scrapPlaceId")
    private int scrapPlaceId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (nullable = false, name = "userId")
    private User user;

    @Column(nullable = false)
    private int placeId;

    @Column(nullable = false)
    private String placeName;

    @Column(nullable = false)
    private String placeAddress;

    @Column(nullable = false)
    private String placeImage;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createdAt", nullable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "scrapPlace", cascade = CascadeType.REMOVE)
    private List<Scrap_Folder> scrapFolders = new ArrayList<>();
}

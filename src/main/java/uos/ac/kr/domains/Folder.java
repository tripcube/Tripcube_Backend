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

@Entity(name="Folder")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Folder implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "folderId")
    private int folderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (nullable = false, name = "userId")
    private User user;

    @Column(nullable = false)
    private String coverImage;

    @Column(nullable = false)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createdAt", nullable = false)
    private Date createdAt;

    // 연관관계
    @OneToMany(mappedBy = "folder", cascade = CascadeType.REMOVE)
    private List<Scrap_Folder> scrapFolders = new ArrayList<>();
}

package uos.ac.kr.domains;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name="Activity")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
@Getter
public class Activity implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "activityId")
    private int activityId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column
    private int placeId;

    @Column
    private String placeName;

    @Column(nullable = false, length = 1000)
    private String placeImage;

    @Column
    private String type;

    @Column
    private int areaCode1;

    @Column
    private int areaCode2;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createdAt", nullable = false)
    private Date createdAt;
}

package uos.ac.kr.domains;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name="Todo")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Todo implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todoId")
    private int todoId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn (nullable = false, name = "userId")
    private User user;

    @Column(nullable = false)
    private int placeId;

    @Column(nullable = false)
    private String placeName;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(nullable = false, length = 30)
    private String tag;

    @Column(nullable = true)
    private int likes;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createdAt", nullable = false)
    private Date createdAt;
}

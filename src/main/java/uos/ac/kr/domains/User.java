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

@Entity(name="User")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private int userId;

    @Column(nullable = false, unique = true, length = 20)
    private String loginId;
    @Column(nullable = false, length = 1000)
    private String password;
    @Column(nullable = false, length = 20)
    private String name;
    @Column(nullable = true, length = 1000)
    private String profileImage;
    @Column(nullable = true, length = 200)
    private String oneliner;
    @Column(nullable = true, length = 1000)
    private String backgroundImage;
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="createdAt", nullable = false)
    private Date createdAt;

    // 연관관계
    @OneToMany(mappedBy = "user")
    private List<Todo> todos = new ArrayList<Todo>();

}

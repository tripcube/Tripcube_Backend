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

@Entity(name="Comment")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Comment implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commentId")
    private int commentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todoId")
    private Todo todo;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column
    private int likes;

    @Column
    private Date createdAt;

    @Column
    private Date updatedAt;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.REMOVE)
    private List<Like_Comment> likeComments = new ArrayList<>();
}

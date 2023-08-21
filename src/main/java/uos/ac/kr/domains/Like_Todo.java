package uos.ac.kr.domains;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@Entity
@Setter
@Getter
@Table(name = "Like_Todo")
public class Like_Todo {
    @EmbeddedId
    private LikeTodoId likeTodoId;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @MapsId("todoId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "todoId")
    private Todo todo;
}

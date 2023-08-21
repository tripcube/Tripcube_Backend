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
public class LikeTodoId implements Serializable {
    private int userId;
    private int todoId;
}
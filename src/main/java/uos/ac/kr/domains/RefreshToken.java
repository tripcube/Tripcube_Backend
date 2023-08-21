package uos.ac.kr.domains;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity(name="RefreshTokens")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class RefreshToken implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "refresh_token_num")
    private int num;

    /* Foreign Keys */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId", nullable = false)
    private User user;
    /* */

    @Column(name = "refresh_token", nullable = false, length = 3000)
    private String refreshToken;


    @Column(name = "is_expired", nullable = false, length = 5)
    private String isExpired = "N";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="created_at", nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name="expired_at", nullable = true)
    private Date expiredAt;
}

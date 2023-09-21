package uos.ac.kr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uos.ac.kr.domains.RefreshToken;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer>, RefreshTokenRepositoryCustom {
    // @Query(nativeQuery = true, value="SELECT * FROM Members WHERE id = :id or name = :name LIMIT 1")
    // Optional<Member> findById(@Param("id") String id, @Param("name") String name);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}

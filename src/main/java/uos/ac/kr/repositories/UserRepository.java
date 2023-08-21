package uos.ac.kr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uos.ac.kr.domains.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, UserRepositoryCustom {
    // @Query(nativeQuery = true, value="SELECT * FROM Members WHERE id = :id or name = :name LIMIT 1")
    // Optional<Member> findById(@Param("id") String id, @Param("name") String name);
}

package uos.ac.kr.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uos.ac.kr.domains.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
}

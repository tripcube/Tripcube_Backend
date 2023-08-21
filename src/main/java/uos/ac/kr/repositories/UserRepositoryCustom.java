package uos.ac.kr.repositories;

import uos.ac.kr.domains.User;
import uos.ac.kr.dtos.SelectUserDTO;

import java.util.List;
import java.util.Optional;

public interface UserRepositoryCustom {
    Optional<User> getDuplicateOne(String id, String name);

    Optional<User> getUserById(String id);

    Optional<User> getUserByName(String name);

    List<User> getUsers(SelectUserDTO selectDTO);
}

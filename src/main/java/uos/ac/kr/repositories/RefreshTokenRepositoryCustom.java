package uos.ac.kr.repositories;

import uos.ac.kr.domains.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepositoryCustom {
    Optional<RefreshToken> getLatestOne(Integer userId);

    void logout(Integer userId);
}

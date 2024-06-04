package project.tikklemoa_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tikklemoa_back.entity.UserEntity;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    UserEntity findByUserid(String userid);
    UserEntity findByNickname(String nickname);
}

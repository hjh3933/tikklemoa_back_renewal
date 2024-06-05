package project.tikklemoa_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import project.tikklemoa_back.entity.SettingEntity;

public interface SettingRepository extends JpaRepository<SettingEntity, Long> {
    SettingEntity findByUserId(long userid);
}

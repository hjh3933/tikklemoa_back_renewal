package project.tikklemoa_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.tikklemoa_back.entity.CommentEntity;
import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    @Query(value = "SELECT * FROM comment c WHERE c.id = :id AND c.userid = :userid", nativeQuery = true)
    CommentEntity findByIdAndUserid(@Param("id") Long id, @Param("userid") Long userId);

    @Query(value = "SELECT c.id, c.date, c.content, u.id, u.nickname, u.img, u.badge " +
            "FROM comment c " +
            "JOIN user u ON c.userid = u.id " +
            "WHERE c.boardid = :boardid", nativeQuery = true)
    Optional<List<Object[]>> findByBoardid(@Param("boardid") Long boardid);
}

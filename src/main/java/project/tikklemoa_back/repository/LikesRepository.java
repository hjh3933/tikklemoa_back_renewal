package project.tikklemoa_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.tikklemoa_back.entity.LikesEntity;

public interface LikesRepository extends JpaRepository<LikesEntity, Long> {
    @Query(value = "SELECT * FROM likes l WHERE l.boardid = :boardid AND l.userid = :userid", nativeQuery = true)
    LikesEntity findByBoardidAndUserid(@Param("boardid") Long boardId, @Param("userid") Long userId);

    @Query("SELECT COUNT(l) FROM LikesEntity l WHERE l.board.id = :boardId")
    int countByBoardId(@Param("boardId") Long boardId);
}

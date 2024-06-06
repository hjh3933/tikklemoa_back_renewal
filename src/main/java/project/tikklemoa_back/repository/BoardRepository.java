package project.tikklemoa_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.tikklemoa_back.entity.BoardEntity;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    @Query(value = "SELECT * FROM board b WHERE b.id = :id AND b.userid = :userid", nativeQuery = true)
    BoardEntity findByIdAndUserid(@Param("id") Long id, @Param("userid") Long userId);

    // @Query("SELECT b.id, b.title, b.date, b.content, b.img, u.id, u.nickname, u.img, u.badge " +
    //         "FROM BoardEntity b JOIN b.user u " +
    //         "WHERE b.id = :boardId")

    @Query(value = "SELECT b.id, b.title, b.date, b.content, b.img, u.id, u.nickname, u.img, u.badge " +
            "FROM board b JOIN user u ON b.userid = u.id " +
            "WHERE b.id = :boardId", nativeQuery = true)
    Optional<List<Object[]>>  findBoardWithUserDetails(@Param("boardId") Long boardId);
}

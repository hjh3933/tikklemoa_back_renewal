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

    @Query(value = "SELECT b.id, b.title, b.date, u.id, u.nickname, u.badge " +
            "FROM board b JOIN user u ON b.userid = u.id " +
            "ORDER BY b.date DESC", nativeQuery = true)
    Optional<List<Object[]>> findBoardAllAuthor();

    @Query(value = "SELECT b.id, b.title, b.date, u.id AS user_id, u.nickname, u.badge " +
            "FROM board b JOIN user u ON b.userid = u.id " +
            "WHERE b.userid = :userid" +
            "ORDER BY b.date DESC", nativeQuery = true)
    Optional<List<Object[]>> findByUserid(@Param("userid") Long userid);

    // likes 테이블의 userid 값을 가지는 boardid에 대하여 조인 데이터 출력
    @Query(value = "SELECT b.id, b.title, b.date, u.id AS user_id, u.nickname, u.badge " +
            "FROM board b JOIN user u ON b.userid = u.id " +
            "WHERE b.id IN (SELECT l.boardid FROM likes l WHERE l.userid = :userid)" +
            "ORDER BY b.date DESC", nativeQuery = true)
    Optional<List<Object[]>> findByLikes(@Param("userid") Long userid);

    @Query(value = "SELECT b.id, b.title, b.date, u.id AS user_id, u.nickname, u.badge " +
            "FROM board b JOIN user u ON b.userid = u.id " +
            "WHERE b.id IN (SELECT c.boardid FROM comment c WHERE c.userid = :userid)" +
            "ORDER BY b.date DESC", nativeQuery = true)
    Optional<List<Object[]>> findByComment(@Param("userid") Long userid);

    @Query(value = "SELECT b.id, b.title, b.date, u.id AS user_id, u.nickname, u.badge " +
            "FROM board b JOIN user u ON b.userid = u.id " +
            "WHERE b.title LIKE %:searchText% OR b.content LIKE %:searchText% " +
            "ORDER BY b.date DESC", nativeQuery = true)
    Optional<List<Object[]>> findByContent(@Param("searchText") String searchText);

    @Query(value = "SELECT b.id, b.title, b.date, u.id AS user_id, u.nickname, u.badge " +
            "FROM board b JOIN user u ON b.userid = u.id " +
            "WHERE b.userid IN (SELECT u_inner.id FROM user u_inner WHERE u_inner.nickname LIKE %:searchText%)" +
            "ORDER BY b.date DESC", nativeQuery = true)
    Optional<List<Object[]>> findByAuthor(@Param("searchText") String searchText);
}

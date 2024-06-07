package project.tikklemoa_back.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.tikklemoa_back.entity.PostEntity;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<PostEntity, Long> {

    @Query(value = "SELECT p.id, p.title, p.date, p.isRead, u.id, u.nickname, u.badge " +
            "FROM post p JOIN user u ON p.senderid = u.id " +
            "WHERE p.senderid = :userid AND p.senderDel = false " +
            "ORDER BY p.date DESC", nativeQuery = true)
    Optional<List<Object[]>> findBySenderid(@Param("userid") Long userId);

    @Query(value = "SELECT p.id, p.title, p.date, p.isRead, u.id, u.nickname, u.badge " +
            "FROM post p JOIN user u ON p.senderid = u.id " +
            "WHERE p.recipientid = :userid AND p.recipientDel = false " +
            "ORDER BY p.date DESC", nativeQuery = true)
    Optional<List<Object[]>> findByRecipientid(@Param("userid") Long userId);

    @Query(value = "SELECT p.id, p.title, p.date, p.isRead, p.content, p.img, u.id, u.nickname, u.img, u.badge " +
            "FROM post p JOIN user u ON p.senderid = u.id " +
            "WHERE p.id = :postid", nativeQuery = true)
    Optional<List<Object[]>>  findPostWithUserDetails(@Param("postid") Long postid);
}

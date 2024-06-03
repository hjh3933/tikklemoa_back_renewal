package project.tikklemoa_back.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="comment")
public class CommentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private long id;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "content", nullable = false, length = 100)
    private String content;

    // 외래키 설정
    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    @JsonBackReference
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "boardid", nullable = false)
    @JsonBackReference
    private BoardEntity board;
}

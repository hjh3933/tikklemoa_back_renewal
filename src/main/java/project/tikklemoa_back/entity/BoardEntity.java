package project.tikklemoa_back.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="board")
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private long id;

    @Column(name = "title", nullable = false, length = 30)
    private String title;

    @Column(name = "date", nullable = false)
    private Date date;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "img", nullable = true, columnDefinition = "TEXT")
    private String img;

    // 외래키 설정
    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    @JsonBackReference
    private UserEntity user;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<LikesEntity> likes;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CommentEntity> comments;
}

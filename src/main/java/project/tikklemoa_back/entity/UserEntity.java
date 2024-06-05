package project.tikklemoa_back.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="user")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private long id;

    @Column(name = "nickname", nullable = false, length = 20)
    private String nickname;

    @Column(name = "userid", nullable = false, length = 20)
    private String userid;

    @Column(name = "userpw", nullable = false, columnDefinition = "TEXT")
    private String userpw;

    @Column(name = "img", nullable = false, columnDefinition = "TEXT")
    private String img;

    @Column(name = "badge", nullable = false, length = 20)
    private String badge;

    // calendar
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CalendarEntity> calenders;

    // board
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<BoardEntity> boards;

    // setting
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL )
    @JsonManagedReference
    private SettingEntity setting;

    // likes
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<LikesEntity> likes;

    // comment
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<CommentEntity> comments;

    // Post
    @OneToMany(mappedBy = "sender")
    @JsonManagedReference
    private List<PostEntity> sentPosts;

    @OneToMany(mappedBy = "recipient")
    @JsonManagedReference
    private List<PostEntity> receivedPosts;
}

package project.tikklemoa_back.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="post")
public class PostEntity {
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

    @Column(name = "isRead", nullable = false)
    private boolean isRead = false;

    @Column(name = "senderDel", nullable = false)
    private boolean senderDel = false;

    @Column(name = "recipientDel", nullable = false)
    private boolean recipientDel = false;

    //외래 키 설정
    @ManyToOne
    @JoinColumn(name = "senderid", nullable = false)
    @JsonBackReference
    private UserEntity sender;

    @ManyToOne
    @JoinColumn(name = "recipientid", nullable = false)
    @JsonBackReference
    private UserEntity recipient;
}

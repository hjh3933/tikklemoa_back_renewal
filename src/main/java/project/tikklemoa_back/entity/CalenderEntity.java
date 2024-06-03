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
@Table(name="calendar")
public class CalenderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private long id;

    @Column(name = "date", nullable = false)
    private Date date;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Column(name = "subcategory", nullable = false, length = 20)
    private String subcategory;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "details", nullable = false, length = 100)
    private String details;

    // 외래키 설정
    @ManyToOne
    @JoinColumn(name = "userid", nullable = false)
    @JsonBackReference
    private UserEntity user;

    // Enum 타입 정의
    public enum Category {
        PLUS, MINUS
    }
}

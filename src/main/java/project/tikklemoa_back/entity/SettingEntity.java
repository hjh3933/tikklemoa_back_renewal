package project.tikklemoa_back.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="setting")
public class SettingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private long id;

    @Column(name = "theme", nullable = false, length = 20)
    private String theme;

    @Column(name = "Lone", nullable = false)
    private int Lone;

    @Column(name = "Ltwo", nullable = false)
    private int Ltwo;

    @Column(name = "Lthree", nullable = false)
    private int Lthree;

    @Column(name = "priceView", nullable = false)
    private boolean priceView = false;

    // 외래키 설정
    @OneToOne
    @JoinColumn(name = "userid", nullable = false, unique = true) // unique 속성 추가
    @JsonBackReference
    private UserEntity user;
}

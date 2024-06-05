package project.tikklemoa_back.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NextBadgeDTO {
    private String nextBadge; // 다음달 예상 badge
    private int totalPlus; // 이번달 총수익
    private int totalMinus; // 이번달 총지출
    private double stats; // 수입 대비 지출
}

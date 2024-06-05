package project.tikklemoa_back.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CalenderStatsDTO {
    // 월별 지출 통계 값을 제공할 DTO, List 로 전달
    private String subcategory;
    private double percentage;
    private int totalPrice;
}

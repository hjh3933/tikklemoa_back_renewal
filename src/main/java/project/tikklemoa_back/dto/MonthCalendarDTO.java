package project.tikklemoa_back.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MonthCalendarDTO {
    // 일별 데이터 전송, month 는 arrayList
    private String date;
    private int totalMinus;
    private int totalPlus;
    private int dateTotal;

}

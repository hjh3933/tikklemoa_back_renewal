package project.tikklemoa_back.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CalendarDTO {
    // calender
    private long id;
    private String date;
    private String category;
    private String subcategory;
    private int price;
    private String details;

    // response
    private boolean result;
    private String msg;

}

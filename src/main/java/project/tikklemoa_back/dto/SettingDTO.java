package project.tikklemoa_back.dto;

import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SettingDTO {
    // setting
    private long id;
    private String theme;
    private int Lone;
    private int Ltwo;
    private int Lthree;
    private boolean priceView;

    // response
    private boolean result;
    private String msg;
}

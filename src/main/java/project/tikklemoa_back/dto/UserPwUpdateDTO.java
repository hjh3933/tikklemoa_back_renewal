package project.tikklemoa_back.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserPwUpdateDTO {
    private String userpw;
    private String newUserpw;
}

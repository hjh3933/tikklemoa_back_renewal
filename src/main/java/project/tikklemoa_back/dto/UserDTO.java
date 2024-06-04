package project.tikklemoa_back.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserDTO {
    // user
    private String nickname;
    private String userid;
    private String userpw;
    private String img;
    private String badge;

    // response
    private boolean result;
    private String msg;

    // token
    private String token;
}

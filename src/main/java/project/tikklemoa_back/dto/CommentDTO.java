package project.tikklemoa_back.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommentDTO {
    // board
    private long boardid;

    // comment
    private long id;
    private String date;
    private String content;

    // user
    private long userid;
    private String nickname;
    private String img;
    private String badge;

    // response
    private boolean result;
    private String msg;
}

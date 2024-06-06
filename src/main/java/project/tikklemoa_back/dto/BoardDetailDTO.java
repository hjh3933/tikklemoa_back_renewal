package project.tikklemoa_back.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class BoardDetailDTO {
    // board
    private long id;
    private String title;
    private String date;
    private String content;
    private String img;
    private List<String> imgUrls; // JSON 배열 파싱 결과 저장

    // user
    private long userid; // index
    private String nickname;
    private String userImg;
    private String badge;

    // likes
    private int likesCount;
    private boolean likeOrNot;

    // response
    private boolean result;
    private String msg;
}

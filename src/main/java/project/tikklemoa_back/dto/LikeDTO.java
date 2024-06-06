package project.tikklemoa_back.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class LikeDTO {
    // 인덱스
    private long id;
    // 외래키
    private long boardid;
    private long userid;
    private boolean likeOrNot; // 좋아요 눌렀는지 여부

    // response
    private boolean result;
    private String msg;
}

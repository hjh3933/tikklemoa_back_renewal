package project.tikklemoa_back.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostDTO {
    private long id;
    private String title;
    private String date;
    private String content;
    private String img;
    private List<String> imgUrls; // JSON 배열 파싱 결과 저장
    private boolean isRead;
    
    // entity에서 검색 후 선택적으로 dto 반환 예정
    // private boolean senderDel;
    // private boolean recipientDel;

    // user - 전송하는 사람
    private long userid; // index
    private String nickname;
    private String userImg;
    private String badge;

    // user - 수신하는 사람 nickname
    private String recipient;

    // response
    private boolean result;
    private String msg;

}

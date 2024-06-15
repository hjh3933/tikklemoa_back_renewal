package project.tikklemoa_back.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.tikklemoa_back.dto.BoardDTO;
import project.tikklemoa_back.dto.BoardDetailDTO;
import project.tikklemoa_back.dto.PostDTO;
import project.tikklemoa_back.entity.BoardEntity;
import project.tikklemoa_back.entity.LikesEntity;
import project.tikklemoa_back.entity.PostEntity;
import project.tikklemoa_back.entity.UserEntity;
import project.tikklemoa_back.repository.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class PostService {
    final private PostRepository postRepository;
    final private UserRepository userRepository;
    final private ObjectMapper objectMapper;

    @Autowired
    public PostService(
            final PostRepository postRepository,
            final UserRepository userRepository,
            final ObjectMapper objectMapper
    ) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    public PostEntity createPost(PostDTO postDTO, String imgUrls, long id) {
        // 로그인 회원 객체 찾아서 sender 등록
        UserEntity sender = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("sender doesn't exist"));
        // 수신자 nickname 으로 받을 유저 찾기
        UserEntity recipient = userRepository.findByNickname(postDTO.getRecipient());

        if(recipient == null) {
            // 수신자 닉네임 잘못 입력함
            new RuntimeException("recipient doesn't exist");
        }

        // yyyy-mm-dd hh:mm:ss front에서 전송 -> 문자열을 java.util.Date로 변환
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date utilDate;
        try {
            utilDate = formatter.parse(postDTO.getDate());
        } catch (ParseException e) {
            throw new RuntimeException("Invalid date format", e);
        }

        PostEntity post = PostEntity.builder()
                .title(postDTO.getTitle())
                .date(utilDate)
                .content(postDTO.getContent())
                .img(imgUrls)
                .isRead(false)
                .sender(sender)
                .recipient(recipient)
                .build();

        return postRepository.save(post);
    }

    public ArrayList<PostDTO> getPosts(String type, long id) {
        Optional<List<Object[]>> results = null;

        if (type.equals("sender")) {
            // 내가 전송한 쪽지
            results = postRepository.findBySenderid(id);
        } else if (type.equals("recipient")) {
            // 내가 받은 쪽지
            results = postRepository.findByRecipientid(id);
        } else {
            throw new RuntimeException("type error: PathVariable wrong error");
        }

        // results 처리
        if (results.isPresent()) {
            List<Object[]> data = results.get();
            ArrayList<PostDTO> Posts = new ArrayList<>();

            // p.id, p.title, p.date, p.isRead, u.id, u.nickname, u.badge
            for (Object[] row : data) {
                PostDTO postDTO = PostDTO.builder()
                        .id(Long.parseLong(String.valueOf(row[0])))
                        .title(String.valueOf(row[1]))
                        .date(String.valueOf(row[2]))
                        .isRead((Boolean) row[3])
                        .userid(Long.parseLong(String.valueOf(row[4])))
                        .nickname(String.valueOf(row[5]))
                        .badge(String.valueOf(row[6]))
                        .build();

                Posts.add(postDTO);
            }

            return Posts;
        } else {
            return new ArrayList<>();
        }
    }

    //
    public PostDTO getPostDetail(long postid, long id) {
        PostEntity clickPost = postRepository.findById(postid)
                .orElseThrow(()->new RuntimeException("post doesn't exist"));

        // 상세보기 누른 post의 수신자id가 로그인 id(나) 이고 isRead가 false일 때
        if(clickPost.getRecipient().getId() == id && !clickPost.isRead()) {
            if(clickPost.isRecipientDel()) {
                // 수신자 삭제 상태임
                return null;
            }
            // isRead 만 false -> true로 변경
            PostEntity postEntity = PostEntity.builder()
                    .id(clickPost.getId())
                    .title(clickPost.getTitle())
                    .date(clickPost.getDate())
                    .content(clickPost.getContent())
                    .img(clickPost.getImg())
                    .senderDel(clickPost.isSenderDel())
                    .recipientDel(clickPost.isRecipientDel())
                    .isRead(true)
                    .sender(clickPost.getSender())
                    .recipient(clickPost.getRecipient())
                    .build();
            postRepository.save(postEntity);
        } else if (clickPost.getSender().getId() == id) {
            if(clickPost.isSenderDel()) {
                // 발신자 삭제 상태임
                return null;
            }
        } else {
            // 수신자도 발신자도 아님
            new RuntimeException("해당 post 조회가 불가능한 user 입니다");
        }

        // 이후 post 테이블의 내용과 작성자 가져오기
        Optional<List<Object[]>>  post = postRepository.findPostWithUserDetails(postid);
        if (post.isPresent() && !post.get().isEmpty()) {
            Object[] data = post.get().get(0);

            List<String> imgUrls = null;
            if(data[5] != null) {
                try {
                    imgUrls = objectMapper.readValue((String) data[5], new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // p.id, p.title, p.date, p.isRead, p.content, p.img, u.id, u.nickname, u.img, u.badge
            // DTO 만들기
            PostDTO postDTO = PostDTO.builder()
                    .id(Long.parseLong(String.valueOf(data[0])))
                    .title(String.valueOf(data[1]))
                    .date(String.valueOf(data[2]))
                    .isRead((Boolean) data[3])
                    .content(String.valueOf(data[4]))
                    .imgUrls(imgUrls)
                    .userid(Long.parseLong(String.valueOf(data[6])))
                    .nickname(String.valueOf(data[7]))
                    .userImg(String.valueOf(data[8]))
                    .badge(String.valueOf(data[9]))
                    .recipient(clickPost.getRecipient().getNickname())
                    .build();

            return postDTO;
        } else {
            return null;
        }

    }

    public PostEntity removePost(PostDTO postDTO, long id) {
        PostEntity post = postRepository.findById(postDTO.getId())
                .orElseThrow(()->new RuntimeException("post doesn't exist"));
        PostEntity removePost = null;
        if(post.getSender().getId()==id) {
            // 내가 발신자임
            removePost = PostEntity.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .date(post.getDate())
                    .content(post.getContent())
                    .img(post.getImg())
                    .isRead(post.isRead())
                    .senderDel(true)
                    .recipientDel(post.isRecipientDel())
                    .sender(post.getSender())
                    .recipient(post.getRecipient())
                    .build();
        } else if (post.getRecipient().getId()==id) {
            // 내가 수신자임
            removePost = PostEntity.builder()
                    .id(post.getId())
                    .title(post.getTitle())
                    .date(post.getDate())
                    .content(post.getContent())
                    .img(post.getImg())
                    .isRead(post.isRead())
                    .senderDel(post.isSenderDel())
                    .recipientDel(true)
                    .sender(post.getSender())
                    .recipient(post.getRecipient())
                    .build();
        } else {
            new RuntimeException("post 삭제 권한이 없는 user입니다");
        }
        return postRepository.save(removePost);
    }

    public PostEntity deletePost(PostDTO postDTO, long id) {
        PostEntity post = postRepository.findById(postDTO.getId())
                .orElseThrow(()->new RuntimeException("post doesn't exist"));
        if(post.getSender().getId()==id && !post.isRead()) {
            // 로그인 계정이 작성자 계정과 일치하고, 읽지 않음 상태일 경우
            postRepository.delete(post);
            return post; // 삭제한 요소 전달
        } else {
            return null;
        }
    }
}

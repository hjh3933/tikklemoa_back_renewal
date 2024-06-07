package project.tikklemoa_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.tikklemoa_back.dto.BoardDTO;
import project.tikklemoa_back.dto.BoardDetailDTO;
import project.tikklemoa_back.dto.PostDTO;
import project.tikklemoa_back.entity.BoardEntity;
import project.tikklemoa_back.entity.PostEntity;
import project.tikklemoa_back.service.PostService;
import project.tikklemoa_back.service.S3Service;

import java.util.ArrayList;

@Slf4j
@RestController
@RequestMapping("/api-server")
public class PostController {
    final private PostService postService;
    final private S3Service s3Service;;

    @Autowired
    public PostController(final PostService postService
            ,final S3Service s3Service) {
        this.postService = postService;
        this.s3Service = s3Service;
    }

    // 쪽지 전송
    @PostMapping("/insertPost")
    public ResponseEntity<?> insertPost(
            @RequestPart(value = "files") MultipartFile[] files
            , @RequestPart(value = "dto") PostDTO postDTO
            , @AuthenticationPrincipal String userid) {
        try {
            long id = Long.parseLong(userid);

            String imgUrlsJson;
            ObjectMapper objectMapper = new ObjectMapper();

            if (files != null && files.length > 0 && !files[0].isEmpty()) {
                ArrayList<String> imgUrls = new ArrayList<>();
                for (MultipartFile file : files) {
                    String imgUrl = s3Service.uploadFile(file);
                    imgUrls.add(imgUrl);
                }
                imgUrlsJson = objectMapper.writeValueAsString(imgUrls); // 새로운 이미지 URL로 갱신
            } else {
                imgUrlsJson = null;
            }

            PostEntity createdPost = postService.createPost(postDTO, imgUrlsJson, id);

            // 응답용
            PostDTO responsePostDTO = PostDTO.builder()
                    .result(true)
                    .msg("쪽지 전송이 완료되었습니다")
                    .build();

            return ResponseEntity.ok().body(responsePostDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 쪽지 목록 조회 /getPosts?type=type
    @GetMapping("/getPosts")
    public ResponseEntity<?> getPosts(@RequestParam String type, @AuthenticationPrincipal String userid) {
        try {
            // type = sender or recipient
            long id = Long.parseLong(userid);
            ArrayList<PostDTO> posts = postService.getPosts(type, id);

            return ResponseEntity.ok().body(posts);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 쪽지 상세 조회
    @GetMapping("/getPostDetail/{postId}")
    public ResponseEntity<?> getPostDetail(@PathVariable String postId, @AuthenticationPrincipal String userid) {
        try {
            long id = Long.parseLong(userid);
            long postid = Long.parseLong(postId);
            PostDTO postDetail = postService.getPostDetail(postid, id);

            return ResponseEntity.ok().body(postDetail);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


}

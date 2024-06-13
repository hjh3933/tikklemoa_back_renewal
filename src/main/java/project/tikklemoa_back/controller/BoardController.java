package project.tikklemoa_back.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.tikklemoa_back.dto.*;
import project.tikklemoa_back.entity.BoardEntity;
import project.tikklemoa_back.entity.CommentEntity;
import project.tikklemoa_back.service.BoardService;
import project.tikklemoa_back.service.S3Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api-server")
public class BoardController {
    final private BoardService boardService;
    final private S3Service s3Service;;

    @Autowired
    public BoardController(final BoardService boardService
            ,final S3Service s3Service) {
        this.boardService = boardService;
        this.s3Service = s3Service;
    }

    // 게시글 등록
    @PostMapping("/insertBoard")
    public ResponseEntity<?> insertBoard(
            @RequestPart(value = "files") MultipartFile[] files
            , @RequestPart(value = "dto")  BoardDTO boardDTO
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

            BoardEntity createdBoard = boardService.createBoard(boardDTO, imgUrlsJson, id);

            // 응답용
            BoardDTO responseBoardDTO = BoardDTO.builder()
                    .result(true)
                    .msg("게시글 작성이 완료되었습니다")
                    .build();

            return ResponseEntity.ok().body(responseBoardDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    @PatchMapping("/updateBoard")
    public ResponseEntity<?> updateBoard(
            @RequestPart(value = "files", required = false) MultipartFile[] files,
            @RequestPart(value = "dto") BoardDTO boardDTO,
            @AuthenticationPrincipal String userid) {
        try {
            long id = Long.parseLong(userid);
            ObjectMapper objectMapper = new ObjectMapper();

            // 기존 이미지 URL에서 삭제할 URL 제거
            List<String> finalImgUrls = new ArrayList<>(boardDTO.getImgUrls());

            // 새로 업로드한 파일 처리
            if (files != null && files.length > 0 && !files[0].isEmpty()) {
                for (MultipartFile file : files) {
                    String imgUrl = s3Service.uploadFile(file);
                    finalImgUrls.add(imgUrl);
                }
            }

            String imgUrlsJson = objectMapper.writeValueAsString(finalImgUrls);

            BoardEntity updateBoard = boardService.updateBoard(boardDTO, imgUrlsJson, id);

            // 응답용
            BoardDTO responseBoardDTO = BoardDTO.builder()
                    .title(updateBoard.getTitle())
                    .date(String.valueOf(updateBoard.getDate()))
                    .content(updateBoard.getContent())
                    .img(updateBoard.getImg())
                    .result(true)
                    .msg("게시글 수정이 완료되었습니다")
                    .build();

            return ResponseEntity.ok().body(responseBoardDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }
    
    // 게시글 수정
    // @PatchMapping("/updateBoard")
    // public ResponseEntity<?> updateBoard(
    //         @RequestPart(value = "files") MultipartFile[] files
    //         , @RequestPart(value = "dto")  BoardDTO boardDTO
    //         , @AuthenticationPrincipal String userid) {
    //     try {
    //         long id = Long.parseLong(userid);
    //         String imgUrlsJson;
    //         ObjectMapper objectMapper = new ObjectMapper();
    //
    //         if (files != null && files.length > 0 && !files[0].isEmpty()) {
    //             ArrayList<String> imgUrls = new ArrayList<>();
    //             for (MultipartFile file : files) {
    //                 String imgUrl = s3Service.uploadFile(file);
    //                 imgUrls.add(imgUrl);
    //             }
    //             imgUrlsJson = objectMapper.writeValueAsString(imgUrls); // 새로운 이미지 URL로 갱신
    //         } else {
    //             imgUrlsJson = null;
    //         }
    //
    //         BoardEntity updateBoard = boardService.updateBoard(boardDTO, imgUrlsJson, id);
    //
    //         // 응답용
    //         BoardDTO responseBoardDTO = BoardDTO.builder()
    //                 .title(updateBoard.getTitle())
    //                 .date(String.valueOf(updateBoard.getDate()))
    //                 .content(updateBoard.getContent())
    //                 .img(updateBoard.getImg())
    //                 .result(true)
    //                 .msg("게시글 수정이 완료되었습니다")
    //                 .build();
    //
    //         return ResponseEntity.ok().body(responseBoardDTO);
    //     } catch (Exception e) {
    //         return ResponseEntity
    //                 .badRequest()
    //                 .body(e.getMessage());
    //     }
    // }

    // 게시글 삭제
    @DeleteMapping("/deleteBoard")
    public ResponseEntity<?> updateBoard(@RequestBody BoardDTO boardDTO, @AuthenticationPrincipal String userid) {
        try {
            long id = Long.parseLong(userid);
            BoardEntity boardEntity = boardService.deleteBoard(boardDTO, id);

            BoardDTO responseBoardDTO;

            if(boardEntity != null) {
                responseBoardDTO = BoardDTO.builder()
                        .result(true)
                        .msg("게시글 삭제가 완료되었습니다")
                        .build();
            } else {
                responseBoardDTO = BoardDTO.builder()
                        .result(false)
                        .msg("존재하지 않는 게시글입니다.")
                        .build();
            }

            return ResponseEntity.ok().body(responseBoardDTO);
        } catch (Exception e) {
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
        }
    }

    // 좋아요 버튼 클릭 삭제 or 추가
    @PostMapping("/clickLikes")
    public ResponseEntity<?> clickLikes(@RequestBody BoardDTO boardDTO, @AuthenticationPrincipal String userid) {
        try {
            long id = Long.parseLong(userid);
            String resMsg = boardService.addLikes(boardDTO, id);
            LikeDTO responseLikeDTO = LikeDTO.builder()
                    .result(true)
                    .msg(resMsg)
                    .build();

            return ResponseEntity.ok().body(responseLikeDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 댓글 작성
    @PostMapping("/insertComment")
    public ResponseEntity<?> insertComment(@RequestBody CommentDTO commentDTO, @AuthenticationPrincipal String userid) {
        try {
            long id = Long.parseLong(userid);
            CommentEntity comment = boardService.insertComment(commentDTO, id);
            CommentDTO responseCommentDTO = CommentDTO.builder()
                    // .id(comment.getId())
                    // .date(String.valueOf(comment.getDate()))
                    // .content(comment.getContent())
                    .result(true)
                    .msg("댓글 작성이 완료되었습니다")
                    .build();

            return ResponseEntity.ok().body(responseCommentDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 댓글 삭제 - 작성자 검증 필요
    @DeleteMapping("/deleteComment")
    public ResponseEntity<?> deleteComment(@RequestBody CommentDTO commentDTO, @AuthenticationPrincipal String userid) {
        try {
            long id = Long.parseLong(userid);
            boardService.deleteComment(commentDTO, id);

            CommentDTO responseCommentDTO = CommentDTO.builder()
                    .result(true)
                    .msg("댓글 삭제가 완료되었습니다")
                    .build();

            return ResponseEntity.ok().body(responseCommentDTO);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 단일 게시글 조회
    @GetMapping("/getBoardDetail/{boardId}")
    public ResponseEntity<?> getBoardDetail(@PathVariable String boardId, @AuthenticationPrincipal String userid) {
        try {
            long id = Long.parseLong(userid);
            long boardid = Long.parseLong(boardId);
            BoardDetailDTO boardDetailDTO = boardService.getBoardDetail(boardid, id);

            return ResponseEntity.ok().body(boardDetailDTO);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 게시글 댓글 조회
    @GetMapping("/getBoardComments/{boardId}")
    public ResponseEntity<?> getBoardComments(@PathVariable String boardId) {
        try {
            long boardid = Long.parseLong(boardId);
            ArrayList<CommentDTO> commentDTOS = boardService.getBoardComments(boardid);

            return ResponseEntity.ok().body(commentDTOS);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 게시글 목록 조회 /auth/getAllBoards
    @GetMapping("/auth/getAllBoards")
    public ResponseEntity<?> getAllBoards() {
        try {
            ArrayList<BoardDTO> boards = boardService.getAllBoards();

            return ResponseEntity.ok().body(boards);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 게시글 좋아요 조회
    @GetMapping("/auth/getLikes/{boardId}")
    public ResponseEntity<?> getLikes(@PathVariable String boardId) {
        try {
            long boardid = Long.parseLong(boardId);
            int likes = boardService.getLikes(boardid);

            return ResponseEntity.ok().body(likes);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 로그인 회원 관련 게시글 조회
    @GetMapping("/getMyBoards/{type}")
    public ResponseEntity<?> getMyBoards(@PathVariable String type, @AuthenticationPrincipal String userid) {
        try {
            long id = Long.parseLong(userid);
            ArrayList<BoardDTO> boards = boardService.getMyBoards(type, id);

            return ResponseEntity.ok().body(boards);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 검색 - /auth/getSearchBoards?type=type&searchText=searchText
    @GetMapping("/auth/getSearchBoards")
    public ResponseEntity<?> getSearchBoards(@RequestParam  String type, @RequestParam  String searchText) {
        try {
            ArrayList<BoardDTO> boards = boardService.getSearchBoards(type, searchText);

            return ResponseEntity.ok().body(boards);

        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }

    }


}

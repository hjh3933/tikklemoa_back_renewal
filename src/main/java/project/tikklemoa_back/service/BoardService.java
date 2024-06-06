package project.tikklemoa_back.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import project.tikklemoa_back.dto.BoardDTO;
import project.tikklemoa_back.dto.BoardDetailDTO;
import project.tikklemoa_back.dto.CommentDTO;
import project.tikklemoa_back.entity.*;
import project.tikklemoa_back.repository.BoardRepository;
import project.tikklemoa_back.repository.CommentRepository;
import project.tikklemoa_back.repository.LikesRepository;
import project.tikklemoa_back.repository.UserRepository;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class BoardService {
    final private BoardRepository boardRepository;
    final private UserRepository userRepository;
    final private LikesRepository likesRepository;
    final private CommentRepository commentRepository;
    final private ObjectMapper objectMapper;

    @Autowired
    public BoardService(
            final BoardRepository boardRepository,
            final UserRepository userRepository,
            final LikesRepository likesRepository,
            final CommentRepository commentRepository,
            final ObjectMapper objectMapper
    ) {
        this.boardRepository = boardRepository;
        this.userRepository = userRepository;
        this.likesRepository = likesRepository;
        this.commentRepository = commentRepository;
        this.objectMapper = objectMapper;
    }

    public BoardEntity createBoard(BoardDTO boardDTO, String imgUrls, long id) {
        // 로그인 회원 객체 찾아서 외래키 등록
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));

        BoardEntity board = BoardEntity.builder()
                .title(boardDTO.getTitle())
                .date(Date.valueOf(boardDTO.getDate())) // yyyy-mm-dd hh:mm:ss front에서 전송
                .content(boardDTO.getContent())
                .img(imgUrls)
                .user(user)
                .build();

        return boardRepository.save(board);
    }

    public BoardEntity updateBoard(BoardDTO boardDTO, String imgUrls, long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));
        // 기존 저장된 board 불러옴
        BoardEntity board = boardRepository.findById(boardDTO.getId())
                .orElseThrow(()->new RuntimeException("board doesn't exist"));

        BoardEntity updateBoard;
        if (imgUrls != null) {
            // 이미지 수정
            updateBoard = BoardEntity.builder()
                    .id(board.getId())
                    .title(boardDTO.getTitle())
                    .date(board.getDate()) // 작성일은 수정되지 않아야함
                    .content(boardDTO.getContent())
                    .img(imgUrls)
                    .user(board.getUser())
                    .build();
        } else {
            // 이미지 수정 X
            updateBoard = BoardEntity.builder()
                    .id(board.getId())
                    .title(boardDTO.getTitle())
                    .date(board.getDate()) // 작성일은 수정되지 않아야함
                    .content(boardDTO.getContent())
                    .img(board.getImg())
                    .user(board.getUser())
                    .build();
        }
        return boardRepository.save(updateBoard);
    }

    public BoardEntity deleteBoard(BoardDTO boardDTO, long id) {
        // 로그인 회원 + id 와 일치하는 튜플 있는지 검사
        BoardEntity deleteThing = boardRepository.findByIdAndUserid(boardDTO.getId(), id);
        if(deleteThing != null) {
            // 삭제 작업
            boardRepository.delete(deleteThing);

            return deleteThing;
        } else {
            return null;
        }
    }

    public String addLikes(BoardDTO boardDTO, long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));
        BoardEntity board = boardRepository.findById(boardDTO.getId())
                .orElseThrow(()->new RuntimeException("board doesn't exist"));
        // 검색
        LikesEntity likes = likesRepository.findByBoardidAndUserid(boardDTO.getId(), id);
        String msg;
        if(likes != null) {
            // 해당 boardid + userid 튜플 있을 경우 삭제
            likesRepository.delete(likes);
            msg = "좋아요 한 게시물에서 삭제되었습니다";
        } else {
            // 없을 경우 추가
            LikesEntity likesEntity = LikesEntity.builder()
                    .board(board)
                    .user(userEntity)
                    .build();
            likesRepository.save(likesEntity);
            msg = "좋아요 한 게시물로 등록되었습니다";
        }

        return msg;

    }

    public CommentEntity insertComment(CommentDTO commentDTO, long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));
        BoardEntity boardEntity = boardRepository.findById(commentDTO.getBoardid())
                .orElseThrow(()->new RuntimeException("board doesn't exist"));

        CommentEntity comment = CommentEntity.builder()
                .date(Date.valueOf(commentDTO.getDate()))
                .content(commentDTO.getContent())
                .user(userEntity)
                .board(boardEntity)
                .build();
        CommentEntity newComment = commentRepository.save(comment);

        return newComment;

    }

    public void deleteComment(CommentDTO commentDTO, long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));
        // 검색
        CommentEntity comment = commentRepository.findByIdAndUserid(commentDTO.getId(), id);
        if(comment != null) {
            // 삭제
            commentRepository.delete(comment);

        } else {
            throw new RuntimeException("comment doesn't exist");
        }
    }

    public BoardDetailDTO getBoardDetail(long boardid, long id) {
        // board 테이블의 내용과 작성자 가져오기
        Optional<List<Object[]>>  board = boardRepository.findBoardWithUserDetails(boardid);
        if (board.isPresent() && !board.get().isEmpty()) {
            Object[] data = board.get().get(0);

            // 디버깅용 출력
            System.out.println("Query result: " + Arrays.toString(data));

            // 배열 길이 검사
            if (data.length < 9) {
                throw new IllegalStateException("Unexpected query result length: " + data.length);
            }

            // likes 테이블에서 boardId 해당하는 컬럼 수 가져오기
            int likesCount = likesRepository.countByBoardId(boardid);

            // 로그인 id와 boardId로 like 여부 가져오기: null이 아니면 좋아요 눌러놓음
            LikesEntity likes = likesRepository.findByBoardidAndUserid(boardid, id);

            List<String> imgUrls = null;
            try {
                imgUrls = objectMapper.readValue((String) data[4], new TypeReference<List<String>>() {});
            } catch (Exception e) {
                e.printStackTrace();
            }

            // DTO 만들기
            BoardDetailDTO boardDetail = BoardDetailDTO.builder()
                    .id(Long.parseLong(String.valueOf(data[0])))
                    .title(String.valueOf(data[1]))
                    .date(String.valueOf(data[2])) // String으로 변환
                    .content(String.valueOf(data[3]))
                    .imgUrls(imgUrls)
                    .userid(Long.parseLong(String.valueOf(data[5])))
                    .nickname(String.valueOf(data[6]))
                    .userImg(String.valueOf(data[7]))
                    .badge(String.valueOf(data[8]))
                    .likesCount(likesCount)
                    .likeOrNot(likes != null)
                    .build();

            return boardDetail;
        } else {
            return null;
        }

    }

    public ArrayList<CommentDTO> getBoardComments(long boardid) {
        Optional<List<Object[]>> results = commentRepository.findByBoardid(boardid);

        if (results.isPresent()) {
            List<Object[]> data = results.get();
            ArrayList<CommentDTO> commentDTOList = new ArrayList<>();

            for (Object[] row : data) {
                CommentDTO commentDTO = CommentDTO.builder()
                        .boardid(boardid)
                        .id(Long.parseLong(String.valueOf(row[0])))
                        .date(String.valueOf(row[1]))
                        .content(String.valueOf(row[2]))
                        .userid(Long.parseLong(String.valueOf(row[3])))
                        .nickname(String.valueOf(row[4]))
                        .img(String.valueOf(row[5]))
                        .badge(String.valueOf(row[6]))
                        .build();

                commentDTOList.add(commentDTO);
            }

            return commentDTOList;
        } else {
            return new ArrayList<>();
        }

    }

}

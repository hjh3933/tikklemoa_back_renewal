package project.tikklemoa_back.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import project.tikklemoa_back.dto.UserDTO;
import project.tikklemoa_back.dto.UserPwUpdateDTO;
import project.tikklemoa_back.entity.UserEntity;
import project.tikklemoa_back.security.TokenProvider;
import project.tikklemoa_back.service.S3Service;
import project.tikklemoa_back.service.UserService;

@Slf4j
@RestController
@RequestMapping("/api-server")
public class UserController {
    final private UserService userService;
    final private BCryptPasswordEncoder bCryptPasswordEncoder;
    final private TokenProvider tokenProvider;
    final private S3Service s3Service;;

    @Autowired
    public UserController(final UserService userService
            , final TokenProvider tokenProvider
            ,final S3Service s3Service
            , final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.tokenProvider = tokenProvider;
        this.s3Service = s3Service;
    }

    // 닉네임 중복검사
    @PostMapping("/auth/checkName")
    public ResponseEntity<?> checkName(@RequestBody UserDTO userDTO) {
        try {
            UserEntity user = userService.findByNickname(userDTO.getNickname());
            UserDTO responseUserDTO;
            if (user != null) {
                // 중복 회원 존재
                responseUserDTO = UserDTO.builder()
                        .result(false)
                        .msg("사용할 수 없는 닉네임입니다.")
                        .build();
            } else {
                responseUserDTO = UserDTO.builder()
                        .result(true)
                        .msg("사용가능한 닉네임입니다.")
                        .build();
            }
            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // id 중복검사
    @PostMapping("/auth/checkId")
    public ResponseEntity<?> checkId(@RequestBody UserDTO userDTO) {
        try {
            UserEntity user = userService.findById(userDTO.getUserid());
            UserDTO responseUserDTO;
            if (user != null) {
                // 중복 회원 존재
                responseUserDTO = UserDTO.builder()
                        .result(false)
                        .msg("사용할 수 없는 아이디입니다.")
                        .build();
            } else {
                responseUserDTO = UserDTO.builder()
                        .result(true)
                        .msg("사용가능한 아이디입니다.")
                        .build();
            }
            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 회원가입
    @PostMapping("/auth/signup")
    public ResponseEntity<?> insertUser(@RequestBody UserDTO userDTO){
        try{
            UserEntity user = UserEntity.builder()
                    .nickname(userDTO.getNickname())
                    .userid(userDTO.getUserid())
                    .userpw(bCryptPasswordEncoder.encode(userDTO.getUserpw()))
                    // front 의 public 에 저장된 이미지로 변경
                    .img("default.img")
                    // front 의 public 에 저장된 이미지로 변경, 일단 이름만
                    .badge("one")
                    .build();

            UserEntity registeredUser = userService.insertUser(user);
            UserDTO responseUserDTO = userDTO.builder()
                    .result(true)
                    .msg("회원가입이 완료되었습니다")
                    .build();

            return ResponseEntity.ok().body(responseUserDTO);
        }
        catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }

    // 로그인
    @PostMapping("/auth/signin")
    public ResponseEntity<?> loginUser(@RequestBody UserDTO userDTO) {
        UserEntity user = userService.getByCredentials(userDTO.getUserid(), userDTO.getUserpw());

        UserDTO responseUserDTO;
        if(user != null) {
            String token = tokenProvider.create(user);
             responseUserDTO = UserDTO.builder()
                    .nickname(user.getNickname())
                    .userid(user.getUserid())
                    .img(user.getImg())
                    .badge(user.getBadge())
                    .result(true)
                    .msg("로그인이 완료되었습니다")
                    .token(token)
                    .build();

            return ResponseEntity.ok().body(responseUserDTO);
        } else {
            responseUserDTO = UserDTO.builder()
                    .result(false)
                    .msg("아이디 또는 비밀번호가 일치하지 않습니다")
                    .build();
            return ResponseEntity
                    .ok().body(responseUserDTO);
        }
    }

    // 회원정보 수정
    @PatchMapping("/updateUser")
    public ResponseEntity<?> updateUser(@RequestPart(value = "file") MultipartFile file, @RequestPart(value = "dto") UserDTO userDTO, @AuthenticationPrincipal String userid) {
        try{
            log.warn("patch jwt check {}", userid);
            long id = Long.parseLong(userid);
            String imgUrl;
            if(file != null && !file.isEmpty()) {
                imgUrl = s3Service.uploadFile(file);
            } else {
                imgUrl = null;
            }

            UserEntity updateUser = userService.updateUser(userDTO, id, imgUrl);

            // 응답용
            UserDTO responseUserDTO = UserDTO.builder()
                    .nickname(updateUser.getNickname())
                    .userid(updateUser.getUserid())
                    .img(updateUser.getImg())
                    .badge(updateUser.getBadge())
                    .result(true)
                    .msg("회원정보 수정이 완료되었습니다")
                    .build();

            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }

    }

    // 비밀번호 변경
    @PatchMapping("/updatePw")
    public ResponseEntity<?> updatePw(@RequestBody UserPwUpdateDTO userPwUpdateDTO, @AuthenticationPrincipal String userid) {
        try {
            long id = Long.parseLong(userid);
            UserEntity userEntity = userService.getByCredentialsPw(userPwUpdateDTO.getUserpw(), id);
            UserDTO responseUserDTO;
            if(userEntity != null) {
                // 비밀번호 수정
                UserDTO user = UserDTO.builder()
                        .userpw(bCryptPasswordEncoder.encode(userPwUpdateDTO.getNewUserpw()))
                        .build();
                UserEntity updateUser = userService.updateUserPw(user, id);
                responseUserDTO = UserDTO.builder()
                        .result(true)
                        .msg("비밀번호 수정이 완료되었습니다")
                        .build();
            } else {
                responseUserDTO = UserDTO.builder()
                        .result(false)
                        .msg("현재 비밀번호가 일치하지 않습니다")
                        .build();
            }

            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }

    }

    // 회원 탈퇴
    @DeleteMapping("/deleteUser")
    public ResponseEntity<?> deleteUser(@RequestBody UserDTO userDTO) {
        try {
            // login 정보로 해당 user 있는지 검증
            UserEntity user = userService.getByCredentials(userDTO.getUserid(), userDTO.getUserpw());
            UserDTO responseUserDTO;
            if(user !=null) {
                // user 가 있으면 삭제 작업
                userService.deleteUser(user.getId());
                responseUserDTO = UserDTO.builder()
                        .result(true)
                        .msg("회원탈퇴가 완료되었습니다")
                        .build();
            } else {
                responseUserDTO = UserDTO.builder()
                        .result(false)
                        .msg("아이디 또는 비밀번호가 일치하지 않습니다")
                        .build();
            }
            
            return ResponseEntity.ok().body(responseUserDTO);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }


    }

    // 회원 정보 조회
    @GetMapping("/getProfile")
    public ResponseEntity<?> deleteUser(@AuthenticationPrincipal String userid) {
        try {
            long id = Long.parseLong(userid);
            // 회원정보 조회
            UserEntity userEntity = userService.getUser(id);
            // pass 외의 정보 보냄
            UserDTO user = UserDTO.builder()
                    .userid(userEntity.getUserid())
                    .nickname(userEntity.getNickname())
                    .img(userEntity.getImg())
                    .badge(userEntity.getBadge())
                    .result(true)
                    .build();

            return ResponseEntity.ok().body(user);
        } catch (Exception e) {
            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }


    }

}

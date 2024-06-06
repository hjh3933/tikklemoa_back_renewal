package project.tikklemoa_back.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import project.tikklemoa_back.dto.UserDTO;
import project.tikklemoa_back.entity.SettingEntity;
import project.tikklemoa_back.entity.UserEntity;
import project.tikklemoa_back.repository.SettingRepository;
import project.tikklemoa_back.repository.UserRepository;

@Slf4j
@Service
public class UserService {

    final private UserRepository userRepository;
    final private BCryptPasswordEncoder bCryptPasswordEncoder;
    final private SettingRepository settingRepository;

    @Autowired
    public UserService(final UserRepository userRepository
            , final SettingRepository settingRepository
            , final BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.settingRepository = settingRepository;
    }

    public UserEntity findByNickname(String nickname) {
        return userRepository.findByNickname(nickname);
    }
    public UserEntity findById(String userid) {
        return userRepository.findByUserid(userid);
    }

    public UserEntity insertUser(UserEntity user) {
        UserEntity newUser = userRepository.save(user);
        // setting 요소 default 로 추가
        SettingEntity settingDefault = SettingEntity.builder()
                .theme("purple")
                .Lone(50000)
                .Ltwo(100000)
                .Lthree(300000)
                .priceView(false)
                .user(newUser)
                .build();

        settingRepository.save(settingDefault);

        return newUser;
    }

    public UserEntity getByCredentials(final String userid, final String userpw) {
        UserEntity user = userRepository.findByUserid(userid);

        if(user != null && bCryptPasswordEncoder.matches(userpw, user.getUserpw())){
            return user;
        } else return null;
    }

    public UserEntity getByCredentialsPw(final String userpw, long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));

        if(user != null && bCryptPasswordEncoder.matches(userpw, user.getUserpw())){
            return user;
        } else return null;
    }

    public UserEntity updateUser(UserDTO user, long id, String imgUrl) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));

        UserEntity updateUser;
        if (imgUrl != null) {
            // img 전달됨
            updateUser = UserEntity.builder()
                    .id(userEntity.getId())
                    .userid(user.getUserid())
                    .nickname(user.getNickname())
                    .img(imgUrl)
                    .badge(userEntity.getBadge())
                    .userpw(userEntity.getUserpw())
                    .build();
        } else {
            // img 수정 X
            updateUser = UserEntity.builder()
                    .id(userEntity.getId())
                    .userid(user.getUserid())
                    .nickname(user.getNickname())
                    .img(userEntity.getImg())
                    .badge(userEntity.getBadge())
                    .userpw(userEntity.getUserpw())
                    .build();
        }

        return userRepository.save(updateUser);
    }

    public UserEntity updateUserPw(UserDTO user, long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));

        UserEntity updateUser = UserEntity.builder()
                .id(userEntity.getId())
                .userid(userEntity.getUserid())
                .nickname(userEntity.getNickname())
                .img(userEntity.getImg())
                .badge(userEntity.getBadge())
                .userpw(user.getUserpw())
                .build();

        return userRepository.save(updateUser);
    }

    public void deleteUser(long id) {
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));

        userRepository.delete(userEntity); // entity 정보로 삭제
    }

    public UserEntity getUser(long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(()->new RuntimeException("user doesn't exist"));

        if (user != null){
            return user;
        } else return null;
    }
}

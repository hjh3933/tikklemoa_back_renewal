package project.tikklemoa_back.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
public class LocalFileService {

    // 이미지가 저장될 절대 경로
    // private final String uploadDir = "C:/Users/본인PC경로/tikklemoa_front_renewal/public/images/";
    private final String uploadDir = "C:/Users/user/OneDrive/바탕 화면/github/TikkleMoa_renewal/tikklemoa_front_renewal/public/images/";

    public String saveFile(MultipartFile file) {
        try {
            String originalFileName = file.getOriginalFilename();
            String newFileName = UUID.randomUUID() + "_" + originalFileName;

            Path filePath = Paths.get(uploadDir + newFileName);

            // 폴더가 없으면 자동 생성
            Files.createDirectories(filePath.getParent());
            Files.write(filePath, file.getBytes());

            // DB에는 프론트에서 접근 가능한 경로만 저장 → React에서는 /images/~로 접근 가능
            return "/images/" + newFileName;

        } catch (IOException e) {
            throw new RuntimeException("Failed to save file locally", e);
        }
    }
}

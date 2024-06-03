package project.tikklemoa_back.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public S3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    // 파일 이름과 현재 시간을 기반으로 고유한 키 생성
    private String generateUniqueKey(String originalFilename) {
        String timestamp = LocalDateTime.now().toString().replace(":", "-");
        return "profile-img/" + timestamp + "-" + originalFilename;
    }

    // 파일 url을 return
    private String getFileUrl(String key) {
        return "https://"+ bucketName +".s3.ap-northeast-2.amazonaws.com/"+key;
    }

    public String uploadFile(MultipartFile file) {
        String key = generateUniqueKey(file.getOriginalFilename());
        try {
            s3Client.putObject(PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(key)
                            .build(),
                    RequestBody.fromBytes(file.getBytes()));
            return getFileUrl(key);
        } catch (S3Exception | IOException e) {
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }
}

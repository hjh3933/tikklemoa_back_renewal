package project.tikklemoa_back.controller;

import project.tikklemoa_back.service.S3Service;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;

@Controller
@RequestMapping("/api/files")
public class FileController {

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    private final S3Service s3Service;

    public FileController(S3Service s3Service) {
        this.s3Service = s3Service;
    }

    @GetMapping("")
    public String getPage() {
        return "test";
    }

    @PostMapping("/upload")
    @ResponseBody
    public String uploadFile(@RequestParam("file") MultipartFile file) {
        String url = s3Service.uploadFile(file);
        // 이름 설정 변경하는 법 알아내기

        return url;
    }
}

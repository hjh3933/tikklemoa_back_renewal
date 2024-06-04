package project.tikklemoa_back.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api-server")
public class TodoController {
    // 테스트용 postman에서 bearer로 전송 성공
    @GetMapping("/todo")
    public String getTodo(@AuthenticationPrincipal String userId) {
        return "get todo success "+userId;
    }
}

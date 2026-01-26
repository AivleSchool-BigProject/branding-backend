package com.branding.branding_backend.branding;

import com.branding.branding_backend.branding.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brands")
public class BrandController {

    private final InterviewService interviewService;

    //브랜드 인터뷰 제출 + AI 진단 + 결과 반환
    @PostMapping("/interview")
    public ResponseEntity<Map<String, Object>> submitInterview(
            Authentication authentication,
            @RequestBody Map<String, Object> interviewInput
    ) {
        Long user = (Long) authentication.getPrincipal();

        Map<String, Object> result =
                interviewService.processInterview(user, interviewInput);

        return ResponseEntity.ok(result);
    }
}

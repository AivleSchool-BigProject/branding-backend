package com.branding.branding_backend.branding;

import com.branding.branding_backend.branding.service.InterviewService;
import com.branding.branding_backend.branding.service.NamingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/brands")
public class BrandController {

    private final InterviewService interviewService;
    private final NamingService namingService;

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

    //브랜드 네이밍 폼 전송 + AI 결과 3개 생성
    @PostMapping("/{brandId}/naming")
    public ResponseEntity<Map<String, Object>> generateNaming(
            @PathVariable Long brandId,
            Authentication authentication,
            @RequestBody Map<String, Object> namingInput
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return ResponseEntity.ok(
                namingService.processNaming(userId, brandId, namingInput)
        );
    }

    //브랜드 네이밍 선택(저장)
    @PostMapping("/{brandId}/naming/select")
    public ResponseEntity<Void> selectNaming(
            @PathVariable Long brandId,
            Authentication authentication,
            @RequestBody Map<String, String> request
    ) {
        Long userId = (Long) authentication.getPrincipal();
        namingService.selectNaming(userId, brandId, request.get("selectedName"));
        return ResponseEntity.ok().build();
    }
}

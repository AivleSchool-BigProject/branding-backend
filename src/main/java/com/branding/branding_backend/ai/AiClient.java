package com.branding.branding_backend.ai;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AiClient {

    public Map<String, Object> requestInterviewReport(
            Map<String, Object> payload
    ) {
        //테스트
        return Map.of(
                "stage", "초기 브랜드",
                "summary", "AI 인터뷰 진단 결과",
                "todo", List.of("네이밍 진행", "컨셉 정의")
        );
    }
}

package com.branding.branding_backend.ai;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class AiClient {

    /**
     * 인터뷰 진단 MOCK
     * FastAPI 연동 전까지 임시 사용
     */
    public Map<String, Object> requestInterviewReport(
            Map<String, Object> payload
    ) {
        return Map.of(
                "user_result", Map.of(
                        "summary", "AI 인터뷰 진단 요약 (MOCK)",
                        "analysis", "현재 브랜드는 초기 단계이며, 타겟 고객과 문제 정의가 비교적 명확합니다.",
                        "key_insights", "네이밍과 브랜드 컨셉 정립이 다음 단계로 적절합니다."
                ),
                "rag_context", Map.of(
                        "step_1_analysis", Map.of(
                                "summary", "초기 브랜드",
                                "keywords", List.of("신뢰", "IT", "확장성"),
                                "analysis", "기술 기반 서비스로 성장 가능성이 높음",
                                "key_insights", "차별화된 메시지 필요"
                        ),
                        "step_1_raw_answers", payload
                )
        );
    }

    public Map<String, Object> requestNaming(Map<String, Object> payload) {
        return Map.of(
                "name1", "Brandify",
                "name2", "Cloudia",
                "name3", "Truston"
        );
    }

    public Map<String, Object> requestConcept(Map<String, Object> payload) {
        return Map.of(
                "concept1", "spring",
                "concept2", "summer",
                "concept3", "fall"
        );
    }
}
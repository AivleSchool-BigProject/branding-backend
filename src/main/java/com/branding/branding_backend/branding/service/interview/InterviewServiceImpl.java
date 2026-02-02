package com.branding.branding_backend.branding.service.interview;

import com.branding.branding_backend.ai.AiClient;
import com.branding.branding_backend.branding.entity.Brand;
import com.branding.branding_backend.branding.entity.CurrentStep;
import com.branding.branding_backend.branding.entity.InterviewReport;
import com.branding.branding_backend.branding.repository.BrandRepository;
import com.branding.branding_backend.branding.repository.InterviewReportRepository;
import com.branding.branding_backend.user.User;
import com.branding.branding_backend.user.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class InterviewServiceImpl implements InterviewService {

    private final BrandRepository brandRepository;
    private final InterviewReportRepository interviewReportRepository;
    private final UserRepository userRepository;
    private final AiClient aiClient;
    private final ObjectMapper objectMapper;

    @Override
    public Map<String, Object> processInterview(
            Long userId,
            Map<String, Object> interviewInput
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        //brand 생성
        Brand brand = new Brand();
        brand.setUser(user);
        brand.setCurrentStep(CurrentStep.INTERVIEW);
        brandRepository.save(brand);

        //AI 서버 호출
        Map<String, Object> aiReport =
                aiClient.requestInterviewReport(interviewInput);

        //Interview Report 저장
        String aiReportJson;
        try {
            aiReportJson = objectMapper.writeValueAsString(aiReport);
        } catch (Exception e) {
            throw new IllegalStateException("AI 인터뷰 결과 JSOn 변환 실패", e);
        }
        InterviewReport report = new InterviewReport(
                brand,
                aiReportJson
        );
        interviewReportRepository.save(report);

        //Brand 상태 업데이트
        brand.moveToNaming();

        //프런트로 반환할 데이터 구성
        return Map.of(
                "brandId", brand.getBrandId(),
                "interviewReport", aiReport
        );
    }
}

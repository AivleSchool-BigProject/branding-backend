package com.branding.branding_backend.branding.service.concept;

import com.branding.branding_backend.ai.AiClient;
import com.branding.branding_backend.branding.entity.Brand;
import com.branding.branding_backend.branding.entity.BrandOutput;
import com.branding.branding_backend.branding.entity.CurrentStep;
import com.branding.branding_backend.branding.entity.OutputType;
import com.branding.branding_backend.branding.repository.BrandOutputRepository;
import com.branding.branding_backend.branding.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ConceptServiceImpl implements ConceptService {

    private final BrandRepository brandRepository;
    private final AiClient aiClient;
    private final BrandOutputRepository brandOutputRepository;

    @Override
    public Map<String, Object> processConcept(
            Long userId,
            Long brandId,
            Map<String, Object> conceptInput
    ) {
        //브랜드 조회
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다."));
        //권한 검증
        if (!brand.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("해당 브랜드에 대한 권한이 없습니다.");
        }
        //AI 컨셉 생성 (Mock)
        Map<String, Object> aiResult =
                aiClient.requestConcept(conceptInput);
        //결과 반환
        return aiResult;
    }

    @Override
    @Transactional
    public void selectConcept(
            Long userId,
            Long brandId,
            String selectedConcept
    ) {
        //브랜드 조회
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다."));
        //권한 검증
        if (!brand.getUser().getUserId().equals(userId)){
            throw new IllegalStateException("해당 브랜드에 대한 권한이 없습니다.");
        }
        //단계 검증
        if (brand.getCurrentStep() != CurrentStep.CONCEPT) {
            throw new IllegalStateException("컨셉 단계가 아닙니다.");
        }
        //기존 컨셉 있으면 덮어쓰기
        BrandOutput output = brandOutputRepository
                .findByBrandAndOutputType(brand, OutputType.CONCEPT)
                .orElseGet(BrandOutput::new);
        //값 세팅 (덮어쓰기)
        output.setBrand(brand);
        output.setOutputType(OutputType.CONCEPT);
        output.setBrandContent(selectedConcept);

        //저장
        brandOutputRepository.save(output);

        brand.moveToStory();
    }
}

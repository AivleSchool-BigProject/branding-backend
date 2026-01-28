package com.branding.branding_backend.branding.service.naming;

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
public class NamingServiceImpl implements NamingService {

    private final BrandRepository brandRepository;
    private final AiClient aiClient;
    private final BrandOutputRepository brandOutputRepository;

    @Override
    public Map<String, Object> processNaming(
            Long userId,
            Long brandId,
            Map<String, Object> namingInput
    ) {
        //브랜드 조회
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다."));
        //권한 검증
        if (!brand.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("해당 브랜드에 대한 권한이 없습니다.");
        }
        //단계 검증
        if (brand.getCurrentStep() != CurrentStep.NAMING) {
            throw new IllegalStateException("네이밍 단계가 아닙니다.");
        }
        //AI 네이밍 생성 (Mock)
        Map<String, Object> aiResult =
                aiClient.requestNaming(namingInput);
        //결과 반환
        return aiResult;
    }

    @Override
    @Transactional
    public void selectNaming(
            Long userId,
            Long brandId,
            String selectedName
    ) {
        //브랜드 조회
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다."));
        //권한 검증
        if (!brand.getUser().getUserId().equals(userId)){
            throw new IllegalStateException("해당 브랜드에 대한 권한이 없습니다.");
        }
        //단계 검증
        if (brand.getCurrentStep() != CurrentStep.NAMING) {
            throw new IllegalStateException("네이밍 단계가 아닙니다.");
        }
        //기존 네이밍 있으면 덮어쓰기
        BrandOutput output = brandOutputRepository
                .findByBrandAndOutputType(brand, OutputType.NAME)
                .orElseGet(BrandOutput::new);

        //값 세팅 (덮어쓰기)
        output.setBrand(brand);
        output.setOutputType(OutputType.NAME);
        output.setBrandContent(selectedName);

        //저장
        brandOutputRepository.save(output);

        brand.moveToConcept();
    }
}

package com.branding.branding_backend.branding.service.logo;

import com.branding.branding_backend.ai.AiClient;
import com.branding.branding_backend.branding.entity.Brand;
import com.branding.branding_backend.branding.entity.BrandOutput;
import com.branding.branding_backend.branding.entity.CurrentStep;
import com.branding.branding_backend.branding.entity.OutputType;
import com.branding.branding_backend.branding.repository.BrandOutputRepository;
import com.branding.branding_backend.branding.repository.BrandRepository;
import com.branding.branding_backend.s3.ImageDownloader;
import com.branding.branding_backend.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LogoServiceImpl implements LogoService {

    private final BrandRepository brandRepository;
    private final AiClient aiClient;
    private final BrandOutputRepository brandOutputRepository;
    private final ImageDownloader imageDownloader;
    private final S3Uploader s3Uploader;

    @Override
    public Map<String, Object> processLogo(
            Long userId,
            Long brandId,
            Map<String, Object> logoInput
    ) {
        //브랜드 조회
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다."));
        //권한 검증
        if (!brand.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("해당 브랜드에 대한 권한이 없습니다.");
        }
        //단계 검증
        if (brand.getCurrentStep() != CurrentStep.LOGO) {
            throw new IllegalStateException("로고 단계가 아닙니다.");
        }
        //AI 로고 생성 (Mock)
        Map<String, Object> aiResult =
                aiClient.requestLogo(logoInput);
        //결과 반환
        return aiResult;
    }

    @Override
    @Transactional
    public void selectLogo(
            Long userId,
            Long brandId,
            String selectedLogo
    ) {
        //브랜드 조회
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다."));
        //권함 검증
        if (!brand.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("해당 브랜드에 대한 권한이 없습니다.");
        }
        //단계 검증
        if (brand.getCurrentStep() != CurrentStep.LOGO) {
            throw new IllegalStateException("로고 단계가 아닙니다.");
        }
        //이미지 다운로드
        byte[] imageBytes = imageDownloader.download(selectedLogo);
        //S3 업로드
        String s3Url = s3Uploader.upload(imageBytes);
        //기존 로고 있으면 덮어쓰기
        BrandOutput output = brandOutputRepository
                .findByBrandAndOutputType(brand, OutputType.LOGO)
                .orElseGet(BrandOutput::new);
        //값 세팅(덮어쓰기)
        output.setBrand(brand);
        output.setOutputType(OutputType.LOGO);
        output.setBrandContent(s3Url);

        //저장
        brandOutputRepository.save(output);

        brand.moveToFinal();
    }
}

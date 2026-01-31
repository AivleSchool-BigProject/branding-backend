package com.branding.branding_backend.branding.service.logo;

import com.branding.branding_backend.ai.AiClient;
import com.branding.branding_backend.branding.entity.Brand;
import com.branding.branding_backend.branding.entity.BrandOutput;
import com.branding.branding_backend.branding.entity.CurrentStep;
import com.branding.branding_backend.branding.entity.OutputType;
import com.branding.branding_backend.branding.repository.BrandOutputRepository;
import com.branding.branding_backend.branding.repository.BrandRepository;
import com.branding.branding_backend.s3.DownloadedImage;
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
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다."));

        if (!brand.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("해당 브랜드에 대한 권한이 없습니다.");
        }

        if (brand.getCurrentStep() != CurrentStep.LOGO) {
            throw new IllegalStateException("로고 단계가 아닙니다.");
        }

        // AI 로고 생성 (Mock)
        return aiClient.requestLogo(logoInput);
    }

    @Override
    @Transactional
    public void selectLogo(
            Long userId,
            Long brandId,
            String selectedLogo
    ) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new IllegalArgumentException("브랜드를 찾을 수 없습니다."));

        if (!brand.getUser().getUserId().equals(userId)) {
            throw new IllegalStateException("해당 브랜드에 대한 권한이 없습니다.");
        }

        if (brand.getCurrentStep() != CurrentStep.LOGO) {
            throw new IllegalStateException("로고 단계가 아닙니다.");
        }

        // 1. 이미지 다운로드 (bytes + contentType 확보)
        DownloadedImage downloadedImage =
                imageDownloader.download(selectedLogo);

        //2. S3 업로드 (포맷/확장자 결정은 S3Uploader 책임)
        String s3Url = s3Uploader.upload(downloadedImage);

        // 3. BrandOutput(LOGO) 저장
        BrandOutput output = brandOutputRepository
                .findByBrandAndOutputType(brand, OutputType.LOGO)
                .orElseGet(BrandOutput::new);

        output.setBrand(brand);
        output.setOutputType(OutputType.LOGO);
        output.setBrandContent(s3Url);

        brandOutputRepository.save(output);

        // 4. 단계 변경
        brand.moveToFinal();
    }
}

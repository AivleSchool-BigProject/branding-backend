package com.branding.branding_backend.mypage;

import com.branding.branding_backend.branding.entity.Brand;
import com.branding.branding_backend.branding.entity.BrandOutput;
import com.branding.branding_backend.branding.entity.OutputType;
import com.branding.branding_backend.branding.repository.BrandOutputRepository;
import com.branding.branding_backend.branding.repository.BrandRepository;
import com.branding.branding_backend.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final BrandRepository brandRepository;
    private final BrandOutputRepository brandOutputRepository;


    public List<BrandListResponseDto> getMyBrands(User user) {

        // 1. user로 brand 목록 조회 (최신순)
        List<Brand> brands = brandRepository.findByUserOrderByCreatedAtDesc(user);

        if (brands.isEmpty()) {
            return List.of();
        }

        // 2. brand 목록으로 brandOutput 전체 조회
        List<BrandOutput> outputs = brandOutputRepository.findByBrandIn(brands);

        // 3. BrandOutput을 brandId + outputType 기준으로 정리
        Map<Long, Map<OutputType, String>> outputMap = new HashMap<>();

        for (BrandOutput output : outputs) {
            Long brandId = output.getBrand().getBrandId();

            outputMap
                    .computeIfAbsent(brandId, k -> new EnumMap<>(OutputType.class))
                    .put(output.getOutputType(), output.getBrandContent());
        }

        // 4. Brand 하나씩 돌면서 BrandListResponseDto 생성
        List<BrandListResponseDto> result = new ArrayList<>();

        for (Brand brand : brands) {
            Map<OutputType, String> brandOutputs =
                    outputMap.getOrDefault(brand.getBrandId(), Map.of());

            result.add(new BrandListResponseDto(
                    brand.getBrandId(),
                    brandOutputs.get(OutputType.NAME),
                    brandOutputs.get(OutputType.CONCEPT),
                    brandOutputs.get(OutputType.STORY),
                    brand.getCurrentStep().name(),
                    brandOutputs.get(OutputType.LOGO),
                    brand.getCreatedAt()
            ));
        }

        // 5. DTO 리스트 반환
        return result;
    }

    public void deleteBrand(User user, Long brandId) {

        //필요 Repository/Service
        // BrandRepository
        // BrandOutputRepository
        // InterviewRepository
        // S3Service


        // 1.삭제 하는 유저와 해당 brandId가 일치 하는지 확인
        // 2.brandId에 해당하는 BrandOutput 조회
        // 3.S3로고 삭제
        // - URL이 실제로 있을 경우만 (실패시 로그만 남기고 진행)
        // 4.brandId에 해당하는 BrandOutput 삭제
        // 5.brandId에 해당하는 interviewreport 정리
        // 6. brandId에 해당하는 brand테이블 정리
    }
}
package com.grinder.service.implement;

import com.grinder.domain.dto.AlanDTO;
import com.grinder.domain.dto.CafeSummaryDTO;
import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.CafeSummary;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.CafeSummaryRepository;
import com.grinder.service.CafeSummaryService;
import com.grinder.utils.AlanAPI;
import jakarta.persistence.EntityNotFoundException;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CafeSummaryServiceImpl implements CafeSummaryService {
    private final CafeSummaryRepository cafeSummaryRepository;
    private final CafeRepository cafeRepository;
    private final AlanAPI alanAPI;

    /**
     * 앨런을 사용하여 cafe 정보를 분석
     * (! 반드시 key가 있어야 동작, properties에 정의)
     * @param cafeId : 카페 정보를 확인하여 Alan으로 분석하고, cafeSummary 테이블에 저장한다.
     * @return : 카페 이름과 주소를 분위기, 맛, 가격을 분석하여 반환한다.
     */
    public AlanDTO.AlanResponse analysisCafe(String cafeId) {
        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카페입니다."));
        AlanDTO.AlanResponse result = alanAPI.requestSummary(cafe.getName(), cafe.getAddress());
        cafeSummaryRepository.save(CafeSummary.builder().cafeId(cafeId).summary(result.getContent()).build());
        return result;
    }

    /**
     * 카페 정보 최신화
     * @return : 최신정보 업데이트
     */
    @Transactional
    public boolean updateCafeSummary(String cafeId) {
        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카페입니다."));
        CafeSummary summary = cafeSummaryRepository.findById(cafeId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 데이터입니다."));
        AlanDTO.AlanResponse result = alanAPI.requestSummary(cafe.getName(), cafe.getAddress());
        summary.setSummary(result.getContent());
        return true;
    }

    public boolean deleteCafeSummary(String cafeId) {
        CafeSummary summary = cafeSummaryRepository.findById(cafeId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 데이터입니다."));
        cafeSummaryRepository.delete(summary);
        return true;
    }

    /**
     * CafeSummary 테이블에서 정리된 데이터를 가져온다.
     * @param cafeId : cafeId를 호출하여 데이터를 가져온다.
     * @return
     */
    public CafeSummaryDTO.CafeSummaryResponse findCafeSummary(String cafeId) {
        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카페입니다."));
        CafeSummary summary = cafeSummaryRepository.findById(cafeId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 데이터입니다."));
        return new CafeSummaryDTO.CafeSummaryResponse(cafe, summary);
    }

    @Transactional
    public void saveCafeSummary(String cafeId) {
        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 카페입니다."));
        String content = alanAPI.requestSummary(cafe.getName(), cafe.getAddress()).getContent();

        CafeSummary cafeSummary = CafeSummary
                .builder()
                .cafeId(cafeId)
                .summary(content)
                .build();
        cafeSummaryRepository.save(cafeSummary);
    }
}

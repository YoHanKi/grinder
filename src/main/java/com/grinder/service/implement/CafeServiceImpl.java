package com.grinder.service.implement;

import com.grinder.domain.dto.CafeDTO;
import com.grinder.domain.dto.CafeDTO.CafeResponseDTO;
import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.CafeRegister;
import com.grinder.exception.AlreadyExistException;
import com.grinder.repository.CafeRegisterRepository;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.queries.CafeQueryRepository;
import com.grinder.service.CafeService;
import com.grinder.service.CafeSummaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CafeServiceImpl implements CafeService {

    private final CafeRepository cafeRepository;
    private final CafeRegisterRepository cafeRegisterRepository;
    private final CafeSummaryService cafeSummaryService;
    private final CafeQueryRepository cafeQueryRepository;

    @Override
    @Transactional
    public void saveCafe(String registerId) {
        CafeRegister register = cafeRegisterRepository.findById(registerId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 신청글입니다."));

        Optional<Cafe> registeredCafeHasName = cafeRepository.findByName(register.getName());
        if (registeredCafeHasName.isPresent()) {
            throw new AlreadyExistException("이미 등록된 카페 이름입니다.");
        }

        Cafe cafe = Cafe.builder().name(register.getName()).address(register.getAddress()).phoneNum(register.getPhoneNum()).build();

        Cafe result = cafeRepository.save(cafe);

        cafeSummaryService.saveCafeSummary(result.getCafeId());
    }

    @Override
    public Cafe findCafeById(String cafeId) {
        return cafeRepository.findById(cafeId).orElseThrow(() -> new NoSuchElementException("카페 아이디: " + cafeId + " 인 카페가 존재하지 않습니다."));
    }

    @Override
    public Slice<CafeDTO.CafeSearchByAdminDTO> searchCafeByAdmin(String keyword, Pageable pageable) {
        Slice<Cafe> cafeSlice =  cafeQueryRepository.searchCafeByNameAndAddressAndPhoneNum(keyword, pageable);

        return new SliceImpl<>(cafeSlice.getContent().stream().map(cafe ->
            new CafeDTO.CafeSearchByAdminDTO(cafe)).toList(), pageable, cafeSlice.hasNext());
    }

    @Override
    public List<Cafe> findCafeList(String cafeName) {
        return cafeRepository.findByNameContainingIgnoreCase(cafeName);
    }
  
    public CafeResponseDTO getCafeInfo(String cafeId) {
        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(() -> new NoSuchElementException("카페 아이디: " + cafeId + " 인 카페가 존재하지 않습니다."));
        return CafeResponseDTO.builder()
            .cafeId(cafe.getCafeId())
            .name(cafe.getName())
            .address(cafe.getAddress())
            .phoneNum(cafe.getPhoneNum())
            .averageGrade(cafe.getAverageGrade())
            .build();
    }

    @Override
    public Slice<CafeDTO.findAllWithImageAndTagResponse> searchCafes(String query, Pageable pageable) {
        return cafeQueryRepository.searchCafes(query, pageable);
    }

    @Override
    public List<CafeDTO.findAllWithImageAndTagResponse> weekTop3Cafe() {
        return cafeQueryRepository.findTop3CafesReferencedThisWeek();
    }
}

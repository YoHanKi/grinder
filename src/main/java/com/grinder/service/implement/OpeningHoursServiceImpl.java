package com.grinder.service.implement;

import com.grinder.domain.dto.OpeningHoursDTO;
import com.grinder.domain.entity.OpeningHours;
import com.grinder.domain.enums.Weekday;
import com.grinder.repository.OpeningHoursRepository;
import com.grinder.service.OpeningHoursService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpeningHoursServiceImpl implements OpeningHoursService {
    private final OpeningHoursRepository openingHoursRepository;

    @Transactional
    public boolean saveOpeningHours(String cafeId, List<OpeningHoursDTO.saveOpeningRequest> list) {
        for (OpeningHoursDTO.saveOpeningRequest request : list) {
            OpeningHours opening = openingHoursRepository.findByWeekdayAndCafeId(
                            cafeId, Weekday.valueOf(request.getDay().toUpperCase()))
                    .orElse(null);

            // 생성
            if (opening == null) {
                OpeningHours.OpeningHoursBuilder builder = OpeningHours.builder()
                        .cafeId(cafeId)
                        .weekday(Weekday.valueOf(request.getDay().toUpperCase()))
                        .isHoliday(request.getIsHoliday());

                if (!request.getIsHoliday()) {
                    builder.openTime(request.parseOpenTime())
                            .closeTime(request.parseCloseTime());
                }
                openingHoursRepository.save(builder.build());
            } else {
                //수정
                if (!request.getIsHoliday()) {
                    opening.setOpenTime(request.parseOpenTime());
                    opening.setCloseTime(request.parseCloseTime());
                }
            }
        }
        return true;
    }

    @Transactional
    public boolean updateOpeningHours(String cafeId, List<OpeningHoursDTO.saveOpeningRequest> list) {
        for (OpeningHoursDTO.saveOpeningRequest request : list) {
            if (!request.getIsHoliday()) {
                OpeningHours opening = openingHoursRepository.findByWeekdayAndCafeId(
                        cafeId, Weekday.valueOf(request.getDay().toUpperCase()))
                        .orElseThrow(() -> new EntityNotFoundException("해당 날짜와 카페 정보가 올바르지 않습니다."));
                opening.setOpenTime(request.parseOpenTime());
                opening.setCloseTime(request.parseCloseTime());
            } else {
                OpeningHours opening = openingHoursRepository.findByWeekdayAndCafeId(
                                cafeId, Weekday.valueOf(request.getDay().toUpperCase()))
                        .orElseThrow(() -> new EntityNotFoundException("해당 날짜와 카페 정보가 올바르지 않습니다."));
                opening.setIsHoliday(request.getIsHoliday());
            }
        }
        return true;
    }
}

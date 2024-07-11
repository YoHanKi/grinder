package com.grinder.service.implement;

import com.grinder.domain.dto.OpeningHoursDTO;
import com.grinder.domain.entity.OpeningHours;
import com.grinder.domain.enums.Weekday;
import com.grinder.repository.OpeningHoursRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class OpeningHoursServiceImplTest {
    @Spy
    @InjectMocks
    OpeningHoursServiceImpl openingHoursService;
    @Mock
    OpeningHoursRepository openingHoursRepository;

    OpeningHoursDTO.saveOpeningRequest request;
    OpeningHoursDTO.saveOpeningRequest request1;
    OpeningHours openingHoursData1;
    OpeningHours openingHoursData2;

    @BeforeEach
    void setUp() {
        openingHoursData1 = OpeningHours.builder().openingId(1L).cafeId("test").weekday(Weekday.MONDAY).openTime(LocalTime.MIDNIGHT).closeTime(LocalTime.NOON).isHoliday(false).build();
        openingHoursData2 = OpeningHours.builder().openingId(2L).cafeId("test").weekday(Weekday.WEDNESDAY).isHoliday(true).build();
        request = new OpeningHoursDTO.saveOpeningRequest();
        request.setDay("MONDAY");
        request.setOpenTime("11:00");
        request.setIsHoliday(openingHoursData1.getIsHoliday());
        request.setCloseTime("16:00");

        request1 = new OpeningHoursDTO.saveOpeningRequest();
        request1.setDay("MONDAY");
        request1.setOpenTime("11:00");
        request1.setIsHoliday(openingHoursData2.getIsHoliday());
        request1.setCloseTime("16:00");
    }


    @Test
    void saveOpeningHours_null() {
        doReturn(Optional.empty()).when(openingHoursRepository).findByWeekdayAndCafeId(anyString(),any(Weekday.class));
        doReturn(openingHoursData1).when(openingHoursRepository).save(any(OpeningHours.class));

        boolean result = openingHoursService.saveOpeningHours("test", List.of(request));

        assertThat(result).isTrue();
    }

    @Test
    void saveOpeningHours_Notnull() {
        doReturn(Optional.of(openingHoursData1)).when(openingHoursRepository).findByWeekdayAndCafeId(anyString(),any(Weekday.class));

        boolean result = openingHoursService.saveOpeningHours("test", List.of(request));

        assertThat(result).isTrue();
    }

    @Test
    void updateOpeningHours_true() {
        doReturn(Optional.of(openingHoursData2)).when(openingHoursRepository).findByWeekdayAndCafeId(anyString(), any(Weekday.class));

        boolean result = openingHoursService.updateOpeningHours("test", List.of(request1));

        assertThat(result).isTrue();
    }

    @Test
    void updateOpeningHours_false() {
        doReturn(Optional.of(openingHoursData1)).when(openingHoursRepository).findByWeekdayAndCafeId(anyString(), any(Weekday.class));

        boolean result = openingHoursService.updateOpeningHours("test", List.of(request));

        assertThat(result).isTrue();
    }
}
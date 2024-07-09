package com.grinder.service.implement;

import com.grinder.domain.dto.AlanDTO;
import com.grinder.domain.dto.CafeSummaryDTO;
import com.grinder.domain.entity.AnalysisTag;
import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.CafeSummary;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.CafeSummaryRepository;
import com.grinder.utils.AlanAPI;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CafeSummaryServiceImplTest {
    @InjectMocks
    private CafeSummaryServiceImpl cafeSummaryService;
    @Mock
    private CafeSummaryRepository cafeSummaryRepository;
    @Mock
    private CafeRepository cafeRepository;
    @Mock
    private AlanAPI alanAPI;

    @Test
    void analysisCafe() {
        Optional<Cafe> cafe = Optional.ofNullable(Cafe.builder().cafeId("cafeId").name("cafeName").address("cafeAddress").build());
        doReturn(cafe).when(cafeRepository).findById(any(String.class));

        AlanDTO.AlanResponse alanResponse = new AlanDTO.AlanResponse();
        alanResponse.setContent("content");
        alanResponse.setActionName("name");
        alanResponse.setActionSpeak("speak");
        doReturn(alanResponse).when(alanAPI).requestSummary(any(String.class),any(String.class));

        doReturn(CafeSummary.builder().cafeId("test").summary(alanResponse.getContent()).build()).when(cafeSummaryRepository).save(any(CafeSummary.class));

        AlanDTO.AlanResponse result = cafeSummaryService.analysisCafe("test");

        assertThat(result).extracting("content").isEqualTo(alanResponse.getContent());
    }
    @Test
    void updateCafeSummary() {
        Optional<Cafe> cafe = Optional.ofNullable(Cafe.builder().cafeId("cafeId").name("cafeName").address("cafeAddress").build());
        doReturn(cafe).when(cafeRepository).findById(any(String.class));

        Optional<CafeSummary> summary = Optional.ofNullable(CafeSummary.builder().cafeId(cafe.get().getCafeId()).summary("summary").build());
        doReturn(summary).when(cafeSummaryRepository).findById(any(String.class));

        AlanDTO.AlanResponse alanResponse = new AlanDTO.AlanResponse();
        alanResponse.setContent("content");
        alanResponse.setActionName("name");
        alanResponse.setActionSpeak("speak");
        doReturn(alanResponse).when(alanAPI).requestSummary(any(String.class),any(String.class));

        boolean result = cafeSummaryService.updateCafeSummary("test");

        assertThat(result).isTrue();
    }
    @Test
    void deleteCafeSummary() {
        Optional<CafeSummary> summary = Optional.ofNullable(CafeSummary.builder().cafeId("cafeId").summary("summary").build());
        doReturn(summary).when(cafeSummaryRepository).findById(any(String.class));

        doNothing().when(cafeSummaryRepository).delete(any(CafeSummary.class));

        boolean result = cafeSummaryService.deleteCafeSummary("test");

        assertThat(result).isTrue();
    }
    @Test
    void findCafeSummary() {
        Optional<Cafe> cafe = Optional.ofNullable(Cafe.builder().cafeId("cafeId").name("cafeName").address("cafeAddress").build());
        doReturn(cafe).when(cafeRepository).findById(any(String.class));

        Optional<CafeSummary> summary = Optional.ofNullable(CafeSummary.builder().cafeId(cafe.get().getCafeId()).summary("summary").build());
        setUpdatedAt(summary.get(), LocalDateTime.now());
        doReturn(summary).when(cafeSummaryRepository).findById(any(String.class));

        CafeSummaryDTO.CafeSummaryResponse result = cafeSummaryService.findCafeSummary("cafeId");

        assertThat(result).isNotNull();
        assertThat(result).extracting("cafeName").isEqualTo(cafe.get().getName());
    }
    @Test
    void saveCafeSummary() {
        Optional<Cafe> cafe = Optional.ofNullable(Cafe.builder().cafeId("cafeId").name("cafeName").address("cafeAddress").build());
        doReturn(cafe).when(cafeRepository).findById(any(String.class));

        AlanDTO.AlanResponse alanResponse = new AlanDTO.AlanResponse();
        alanResponse.setContent("content");
        alanResponse.setActionName("name");
        alanResponse.setActionSpeak("speak");
        doReturn(alanResponse).when(alanAPI).requestSummary(any(String.class),any(String.class));

        CafeSummary summary = CafeSummary.builder().cafeId("test").summary(alanResponse.getContent()).build();
        doReturn(summary).when(cafeSummaryRepository).save(any(CafeSummary.class));

        cafeSummaryService.saveCafeSummary("cafeId");

        verify(cafeSummaryRepository, times(1)).save(any(CafeSummary.class));
    }

    private void setUpdatedAt(CafeSummary cafeSummary, LocalDateTime updatedAt) {
        try {
            java.lang.reflect.Field field = cafeSummary.getClass().getSuperclass().getDeclaredField("updatedAt");
            field.setAccessible(true);
            field.set(cafeSummary, updatedAt);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
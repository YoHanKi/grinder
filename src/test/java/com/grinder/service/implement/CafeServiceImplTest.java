package com.grinder.service.implement;

import com.grinder.domain.dto.CafeDTO;
import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.CafeRegister;
import com.grinder.repository.CafeRegisterRepository;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.queries.CafeQueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CafeServiceImplTest {
    @InjectMocks
    private CafeServiceImpl cafeService;
    @Mock
    private CafeRepository cafeRepository;
    @Mock
    private CafeRegisterRepository cafeRegisterRepository;
    @Mock
    private CafeSummaryServiceImpl cafeSummaryService;
    @Mock
    private CafeQueryRepository cafeQueryRepository;
    @Mock
    private Pageable pageable;

    @Test
    void saveCafe() {
        Optional<CafeRegister> register = Optional.ofNullable(CafeRegister.builder().registerId("testId").name("testName").build());
        doReturn(register).when(cafeRegisterRepository).findById(any(String.class));

        Optional<Cafe> registeredCafeHasName = Optional.empty();
        doReturn(registeredCafeHasName).when(cafeRepository).findByName(any(String.class));

        Cafe cafe = Cafe.builder().cafeId("testCafeId").build();
        doReturn(cafe).when(cafeRepository).save(any(Cafe.class));

        doNothing().when(cafeSummaryService).saveCafeSummary(any(String.class));

        cafeService.saveCafe("testId");

        assertThat(cafe).extracting("cafeId").isEqualTo("testCafeId");
    }

    @Test
    void findCafeById() {
        Optional<Cafe> cafe = Optional.ofNullable(Cafe.builder().name("cafeName").build());
        doReturn(cafe).when(cafeRepository).findById(any(String.class));

        Cafe result = cafeService.findCafeById("test");
        assertThat(result).extracting("name").isEqualTo("cafeName");
    }

    @Test
    void findCafeById_예외() {
        doReturn(Optional.empty()).when(cafeRepository).findById(any(String.class));
        assertThatThrownBy(() -> cafeService.findCafeById("test")).isInstanceOf(NoSuchElementException.class).hasMessage("카페 아이디: " + "test" + " 인 카페가 존재하지 않습니다.");
    }

    @Test
    void searchCafeByAdmin() {
        List<Cafe> cafeList = List.of(Cafe.builder().cafeId("test").name("testName").build());
        doReturn(new SliceImpl<>(cafeList, pageable, true)).when(cafeQueryRepository).searchCafeByNameAndAddressAndPhoneNum(any(String.class), any(Pageable.class));

        Slice<CafeDTO.CafeSearchByAdminDTO> result = cafeService.searchCafeByAdmin("test", pageable);

        assertThat(result).extracting("name").contains("testName");
    }

    @Test
    void findCafeList() {
        doReturn(List.of(Cafe.builder().name("test").build())).when(cafeRepository).findByNameContainingIgnoreCase(any(String.class));
        List<Cafe> result = cafeService.findCafeList("test");
        assertThat(result).extracting("name").contains("test");
    }

    @Test
    void getCafeInfo() {
        Optional<Cafe> cafe = Optional.ofNullable(Cafe.builder().name("testName").build());
        doReturn(cafe).when(cafeRepository).findById(any(String.class));

        CafeDTO.CafeResponseDTO result = cafeService.getCafeInfo("test");

        assertThat(result).extracting("name").isEqualTo("testName");
    }

    @Test
    void searchCafes() {
        List<CafeDTO.findAllWithImageAndTagResponse> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CafeDTO.findAllWithImageAndTagResponse temp = new CafeDTO.findAllWithImageAndTagResponse();
            temp.setCafeName("cafeName"+i);
            list.add(temp);
        }

        doReturn(new SliceImpl<>(list, pageable, true)).when(cafeQueryRepository).searchCafes(any(String.class), any(Pageable.class));

        Slice<CafeDTO.findAllWithImageAndTagResponse> result = cafeService.searchCafes("test", pageable);

        assertThat(result).extracting("cafeName").contains("cafeName0");
    }

    @Test
    void weekTop3Cafe() {
        List<CafeDTO.findAllWithImageAndTagResponse> list = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            CafeDTO.findAllWithImageAndTagResponse temp = new CafeDTO.findAllWithImageAndTagResponse();
            temp.setCafeName("cafeName"+i);
            list.add(temp);
        }

        doReturn(list).when(cafeQueryRepository).findTop3CafesReferencedThisWeek();

        List<CafeDTO.findAllWithImageAndTagResponse> result = cafeService.weekTop3Cafe();
        assertThat(result).extracting("cafeName").contains("cafeName0","cafeName1","cafeName2");
    }
}
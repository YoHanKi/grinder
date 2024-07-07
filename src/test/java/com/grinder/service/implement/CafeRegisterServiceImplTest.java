package com.grinder.service.implement;

import static com.grinder.domain.dto.CafeRegisterDTO.*;
import com.grinder.domain.entity.CafeRegister;
import com.grinder.domain.entity.Member;
import com.grinder.repository.CafeRegisterRepository;
import com.grinder.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.grinder.domain.dto.CafeRegisterDTO.*;
import static org.assertj.core.api.Assertions.assertThat;


import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CafeRegisterServiceImplTest {
    @InjectMocks
    CafeRegisterServiceImpl cafeRegisterService;
    @Mock
    CafeRegisterRepository cafeRegisterRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    Pageable pageable;

    @DisplayName("신규 장소 등록 내역 조회")
    @Test
    void testFindAllCafeRegister() {
        //given
        List<CafeRegister> cafeRegisterList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            cafeRegisterList.add(CafeRegister.builder().member(new Member()).phoneNum("01045689852").name("newcafe556").address("서울시 마포구 서교동 632-8").build());
        }
        doReturn(new PageImpl<>(cafeRegisterList, pageable, 1)).when(cafeRegisterRepository).findAll(any(Pageable.class));

        //when
        Slice<FindCafeRegisterDTO> cafeRegisterDTOSlice = cafeRegisterService.FindAllCafeRegisters(pageable);

        //then
        assertThat(cafeRegisterDTOSlice.getContent().size()).isEqualTo(3);
    }

    @Test
    void deleteCafeRegister() {
        Optional<CafeRegister> cafeRegister = Optional.ofNullable(CafeRegister.builder().registerId("test").name("test").phoneNum("1234").build());
        doReturn(cafeRegister).when(cafeRegisterRepository).findById(any(String.class));
        doNothing().when(cafeRegisterRepository).delete(any(CafeRegister.class));

        cafeRegisterService.deleteCafeRegister("test");
    }

    @Test
    void saveCafeRegister() {
        Optional<Member> member = Optional.ofNullable(Member.builder().email("test@test.com").nickname("test").build());
        CafeRegister cafeRegister = CafeRegister.builder().registerId("test").build();
        CafeRegisterRequestDTO requestDTO = new CafeRegisterRequestDTO(cafeRegister);

        doReturn(member).when(memberRepository).findByEmail(any(String.class));
        doReturn(cafeRegister).when(cafeRegisterRepository).save(any(CafeRegister.class));

        String result = cafeRegisterService.saveCafeRegister(member.get().getEmail(), requestDTO);

        assertThat(result).isEqualTo("test");
    }
}
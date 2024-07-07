package com.grinder.service.implement;

import com.grinder.domain.entity.CafeRegister;
import com.grinder.domain.entity.Member;
import com.grinder.repository.CafeRegisterRepository;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.MemberRepository;
import com.grinder.service.CafeRegisterService;
import com.grinder.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static com.grinder.domain.dto.CafeRegisterDTO.*;

@Service
@RequiredArgsConstructor
public class CafeRegisterServiceImpl implements CafeRegisterService {
    private final MemberRepository memberRepository;
    private final CafeRegisterRepository cafeRegisterRepository;

    @Override
    public Slice<FindCafeRegisterDTO> FindAllCafeRegisters(Pageable pageable) {
        Page<CafeRegister> registerPage = cafeRegisterRepository.findAll(pageable);

        Slice<FindCafeRegisterDTO> registerDTOSlice = new SliceImpl<>(registerPage.getContent().stream().map(register -> new FindCafeRegisterDTO(register)).toList(), pageable, registerPage.hasNext());

        return registerDTOSlice;
    }

    @Override
    @Transactional
    public void deleteCafeRegister(String registerId) {
        CafeRegister cafeRegister = cafeRegisterRepository.findById(registerId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 신청글입니다."));
        cafeRegisterRepository.delete(cafeRegister);
    }

    @Override
    @Transactional
    public String saveCafeRegister(String memberEmail, CafeRegisterRequestDTO request) {
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        CafeRegister result = CafeRegister.builder()
            .member(member)
            .name(request.getName())
            .address(request.getAddress())
            .phoneNum(request.getPhoneNum())
            .build();


        return cafeRegisterRepository.save(result).getRegisterId();
    }
}

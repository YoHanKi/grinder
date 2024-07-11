package com.grinder.service.implement;

import com.grinder.domain.dto.SellerInfoDTO;
import com.grinder.domain.entity.*;
import com.grinder.exception.AlreadyExistException;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.MemberRepository;
import com.grinder.repository.SellerInfoRepository;
import com.grinder.repository.queries.ImageQueryRepository;
import com.grinder.repository.queries.SellerInfoQueryRepository;
import com.grinder.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class SellerInfoServiceImpl implements SellerInfoService {

    private final SellerInfoRepository sellerInfoRepository;
    private final CafeService cafeService;
    private final SellerApplyService sellerApplyService;
    private final SellerInfoQueryRepository sellerInfoQueryRepository;
    private final CafeRepository cafeRepository;
    private final MemberRepository memberRepository;
    private final ImageService imageService;
    private final MemberService memberService;

    @Override
    @Transactional
    public void saveSellerInfo(String applyId) {
        SellerApply sellerApply = sellerApplyService.findSellerApply(applyId);
        List<SellerInfo> sellerInfoList = findSellerInfoByCafeId(sellerApply.getCafe().getCafeId());
        if (!sellerInfoList.isEmpty()) {
            throw new AlreadyExistException("이미 판매자가 등록된 카페입니다.");
        }
        sellerInfoRepository.save(SellerInfo
                .builder()
                .cafe(sellerApply.getCafe())
                .member(sellerApply.getMember())
                .build());
        Image image = imageService.findImageByImageUrl(sellerApply.getRegImageUrl());
        image.updateContentId(sellerApply.getCafe().getCafeId());
        Cafe cafe = cafeService.findCafeById(sellerApply.getCafe().getCafeId());
        cafe.uploadRegImage(sellerApply.getRegImageUrl());
        Member member = memberService.findMemberById(sellerApply.getMember().getMemberId());
        member.toSeller();
        sellerApplyService.deleteSellerApply(applyId);
    }

    @Override
    public SellerInfo findSellerInfoById(Long sellerInfoId) {
        return sellerInfoRepository.findById(sellerInfoId).orElseThrow(() -> new NoSuchElementException("판매자 정보가 존재하지 않습니다."));
    }

    @Override
    @Transactional
    public void deleteSellerInfo(Long sellerInfoId) {
        sellerInfoRepository.delete(findSellerInfoById(sellerInfoId));
    }

    @Override
    public List<SellerInfoDTO.findAllResponse> findAllSellerInfoByEmail(String sellerEmail) {
        return sellerInfoQueryRepository.findAllSellerInfo(sellerEmail);
    }

    @Override
    public boolean existByMemberAndCafe(String cafeId, String memberEmail) {
        Cafe cafe = cafeRepository.findById(cafeId).orElseThrow(() -> new NoSuchElementException("카페 정보가 존재하지 않습니다."));
        Member member = memberRepository.findByEmail(memberEmail).orElseThrow(() -> new NoSuchElementException("카페 정보가 존재하지 않습니다."));
        return sellerInfoRepository.existsByMemberAndCafe(member, cafe);
    }

    @Override
    public List<SellerInfo> findSellerInfoByCafeId(String cafeId) {
        return sellerInfoRepository.findAllByCafe_CafeId(cafeId);
    }
}

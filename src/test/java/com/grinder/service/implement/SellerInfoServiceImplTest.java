package com.grinder.service.implement;

import com.grinder.domain.dto.SellerInfoDTO;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.Role;
import com.grinder.exception.AlreadyExistException;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.MemberRepository;
import com.grinder.repository.SellerInfoRepository;
import com.grinder.repository.queries.ImageQueryRepository;
import com.grinder.repository.queries.SellerInfoQueryRepository;
import com.grinder.service.CafeService;
import com.grinder.service.ImageService;
import com.grinder.service.MemberService;
import com.grinder.service.SellerApplyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class SellerInfoServiceImplTest {
    @Spy
    @InjectMocks
    SellerInfoServiceImpl sellerInfoService;
    @Mock
    SellerInfoRepository sellerInfoRepository;
    @Mock
    CafeServiceImpl cafeService;
    @Mock
    SellerApplyServiceImpl sellerApplyService;
    @Mock
    SellerInfoQueryRepository sellerInfoQueryRepository;
    @Mock
    CafeRepository cafeRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    ImageServiceImpl imageService;
    @Mock
    MemberServiceImpl memberService;

    SellerInfo sellerInfoData;
    SellerApply sellerApplyData;
    Image imageData;
    Cafe cafeData;
    Member memberData;


    @BeforeEach
    void setUp() {
        memberData = Member.builder().memberId("memberId").email("member1@example.com").nickname("user1").password("password1").role(Role.MEMBER).phoneNum("1234567890").build();
        cafeData = Cafe.builder().cafeId("cafeId").name("Cafe A").address("123 Main St, City A").phoneNum("1112223333").averageGrade(4).build();
        imageData = Image.builder().imageId("imageId").imageUrl("url").contentId("imageContent").contentType(ContentType.CAFE).build();
        sellerApplyData = SellerApply.builder().applyId("applyId").member(memberData).cafe(cafeData).regImageUrl(imageData.getImageUrl()).build();
        sellerInfoData = SellerInfo.builder().sellerInfoId(1L).member(memberData).cafe(cafeData).build();
    }

    @Test
    void saveSellerInfo() {
        doReturn(sellerApplyData).when(sellerApplyService).findSellerApply(anyString());
        doReturn(List.of()).when(sellerInfoService).findSellerInfoByCafeId(anyString());

        doReturn(sellerInfoData).when(sellerInfoRepository).save(any(SellerInfo.class));
        doReturn(imageData).when(imageService).findImageByImageUrl(anyString());
        doReturn(cafeData).when(cafeService).findCafeById(anyString());
        doReturn(memberData).when(memberService).findMemberById(anyString());
        doNothing().when(sellerApplyService).deleteSellerApply(anyString());

        sellerInfoService.saveSellerInfo("test");

        assertThat(memberData).extracting("role").isEqualTo(Role.SELLER);
        assertThat(imageData).extracting("contentId").isEqualTo(sellerApplyData.getCafe().getCafeId());
        assertThat(cafeData).extracting("regImageUrl").isEqualTo(sellerApplyData.getRegImageUrl());
    }

    @Test
    void saveSellerInfo_Already() {
        doReturn(sellerApplyData).when(sellerApplyService).findSellerApply(anyString());
        doReturn(List.of(sellerInfoData)).when(sellerInfoService).findSellerInfoByCafeId(anyString());

        assertThatThrownBy(() -> sellerInfoService.saveSellerInfo("test")).isInstanceOf(AlreadyExistException.class).hasMessage("이미 판매자가 등록된 카페입니다.");
    }

    @Test
    void findSellerInfoById() {
        doReturn(Optional.of(sellerInfoData)).when(sellerInfoRepository).findById(anyLong());

        SellerInfo result = sellerInfoService.findSellerInfoById(1L);

        assertThat(result).extracting("sellerInfoId").isEqualTo(1L);
        assertThat(result).extracting("member").isEqualTo(memberData);
    }

    @Test
    void deleteSellerInfo() {
        doReturn(sellerInfoData).when(sellerInfoService).findSellerInfoById(anyLong());
        doNothing().when(sellerInfoRepository).delete(any(SellerInfo.class));

        sellerInfoService.deleteSellerInfo(1L);

        verify(sellerInfoService).findSellerInfoById(1L);
        verify(sellerInfoRepository).delete(sellerInfoData);
    }

    @Test
    void findAllSellerInfoByEmail() {
        SellerInfoDTO.findAllResponse response = new SellerInfoDTO.findAllResponse(sellerInfoData, "testUrl");
        doReturn(List.of(response)).when(sellerInfoQueryRepository).findAllSellerInfo(anyString());

        List<SellerInfoDTO.findAllResponse> result = sellerInfoService.findAllSellerInfoByEmail("test");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result).extracting("cafeId").contains(sellerInfoData.getCafe().getCafeId());
        assertThat(result).extracting("cafeName").contains(sellerInfoData.getCafe().getName());
        assertThat(result).extracting("cafeAddress").contains(sellerInfoData.getCafe().getAddress());
        assertThat(result).extracting("CafePhoneNum").contains(sellerInfoData.getCafe().getPhoneNum());
        assertThat(result).extracting("averageGrade").contains(sellerInfoData.getCafe().getAverageGrade());
        assertThat(result).extracting("cafeImageUrl").contains("testUrl");
    }

    @Test
    void existByMemberAndCafe() {
        doReturn(Optional.of(cafeData)).when(cafeRepository).findById(anyString());
        doReturn(Optional.of(memberData)).when(memberRepository).findByEmail(anyString());
        doReturn(true).when(sellerInfoRepository).existsByMemberAndCafe(any(Member.class), any(Cafe.class));
        boolean result = sellerInfoService.existByMemberAndCafe("test", "test");

        assertThat(result).isTrue();
    }

    @Test
    void findSellerInfoByCafeId() {
        doReturn(List.of(sellerInfoData)).when(sellerInfoRepository).findAllByCafe_CafeId(anyString());

        List<SellerInfo> result = sellerInfoService.findSellerInfoByCafeId("test");

        assertThat(result).extracting("sellerInfoId").contains(sellerInfoData.getSellerInfoId());
        assertThat(result).extracting("member").contains(memberData);
    }
}
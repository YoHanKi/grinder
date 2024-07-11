package com.grinder.service.implement;

import com.grinder.domain.dto.SellerApplyDTO;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.grinder.exception.AlreadyExistException;
import com.grinder.repository.SellerApplyRepository;
import com.grinder.repository.SellerInfoRepository;
import com.grinder.service.AwsS3Service;
import com.grinder.service.MemberService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerApplyServiceImplTest {
    @InjectMocks
    SellerApplyServiceImpl sellerApplyService;
    @Mock
    SellerApplyRepository sellerApplyRepository;
    @Mock
    SellerInfoRepository sellerInfoRepository;
    @Mock
    AwsS3ServiceImpl awsS3Service;
    @Mock
    MemberServiceImpl memberService;
    @Mock
    CafeServiceImpl cafeService;
    @Mock
    Pageable pageable;

    @DisplayName("판매자 신청 내역 조회")
    @Test
    void testFindAllSellerApplies() {
        //given
        List<SellerApply> sellerApplyList = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            sellerApplyList.add(SellerApply.builder().applyId("apply" +i).member(Member.builder().memberId("testid"+i).nickname("membername"+i).build()).cafe(Cafe.builder().cafeId("cafeId"+i).name("name"+i).address("new address"+i).phoneNum("0109534246"+i).build()).regImageUrl("url"+i).build());
        }

        doReturn(new PageImpl<>(sellerApplyList, pageable, 3)).when(sellerApplyRepository).findAll(any(Pageable.class));

        //when
        Slice<SellerApplyDTO.FindSellerApplyDTO> sellerApplyDTOSlice = sellerApplyService.findAllSellerApplies(pageable);

        //then
        assertThat(sellerApplyDTOSlice.getContent().size()).isEqualTo(3);
        assertThat(sellerApplyDTOSlice.getContent()).extracting("applyId").contains("apply0");
        assertThat(sellerApplyDTOSlice.getContent()).extracting("memberId").contains("testid0");
        assertThat(sellerApplyDTOSlice.getContent()).extracting("nickname").contains("membername0");
        assertThat(sellerApplyDTOSlice.getContent()).extracting("cafeId").contains("cafeId0");
        assertThat(sellerApplyDTOSlice.getContent()).extracting("cafeName").contains("name0");
        assertThat(sellerApplyDTOSlice.getContent()).extracting("phoneNum").contains("01095342460");
        assertThat(sellerApplyDTOSlice.getContent()).extracting("regImageUrl").contains("url0");
    }

    @DisplayName("판매자 신청 내역 삭제")
    @Test
    void testDeleteSellerApply() {
        SellerApply apply = SellerApply.builder().applyId(UUID.randomUUID().toString()).member(new Member()).cafe(new Cafe()).build();

        doNothing().when(sellerApplyRepository).deleteById(apply.getApplyId());

        sellerApplyService.deleteSellerApply(apply.getApplyId());

        verify(sellerApplyRepository,times(1)).deleteById(apply.getApplyId());
    }

    @Test
    void saveSellerApply_isNotEmpty() {
        doReturn(List.of(SellerInfo.builder().sellerInfoId(1L).build())).when(sellerInfoRepository).findAllByCafe_CafeId(anyString());

        assertThatThrownBy(() -> sellerApplyService.saveSellerApply("test","test", any(MultipartFile.class))).isInstanceOf(AlreadyExistException.class)
                .hasMessage("이미 판매자가 등록된 카페입니다.");
    }
    @Test
    void saveSellerApply_isPresent() {
        doReturn(new ArrayList<SellerInfo>()).when(sellerInfoRepository).findAllByCafe_CafeId(anyString());
        doReturn(Optional.of(SellerApply.builder().applyId("test").build())).when(sellerApplyRepository).findByMember_MemberIdAndCafe_CafeId(anyString(), anyString());

        assertThatThrownBy(() -> sellerApplyService.saveSellerApply("test","test", any(MultipartFile.class))).isInstanceOf(AlreadyExistException.class)
                .hasMessage("이미 신청한 내역이 존재합니다.");
    }

    @Test
    void saveSellerApply() {
        doReturn(new ArrayList<SellerInfo>()).when(sellerInfoRepository).findAllByCafe_CafeId(anyString());
        doReturn(Optional.empty()).when(sellerApplyRepository).findByMember_MemberIdAndCafe_CafeId(anyString(), anyString());

        Image image = Image.builder().imageId("imageId").imageUrl("testUrl").contentId("testId").build();
        MultipartFile mockFile = mock(MultipartFile.class);
        doReturn(image).when(awsS3Service).uploadSingleImageBucket(eq(null), anyString(), eq(ContentType.SELLER_APPLY));

        doReturn(SellerApply.builder().applyId("testSellerId").build()).when(sellerApplyRepository).save(any(SellerApply.class));
        doReturn(Member.builder().memberId("memId").build()).when(memberService).findMemberById(anyString());
        doReturn(Cafe.builder().cafeId("cafId").build()).when(cafeService).findCafeById(anyString());

        sellerApplyService.saveSellerApply("test", "test", eq(mockFile));

        verify(sellerApplyRepository, times(1)).save(any(SellerApply.class));
    }

    @Test
    void findSellerApply() {
        Optional<SellerApply> sellerApply = Optional.of(SellerApply.builder().applyId("test").build());
        doReturn(sellerApply).when(sellerApplyRepository).findById(anyString());

        SellerApply result = sellerApplyService.findSellerApply(anyString());

        assertThat(result).extracting("applyId").isEqualTo(sellerApply.get().getApplyId());
    }
}
package com.grinder.service.implement;

import com.grinder.domain.dto.MemberDTO;
import com.grinder.domain.entity.Image;
import com.grinder.domain.entity.Member;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.Role;
import com.grinder.repository.ImageRepository;
import com.grinder.repository.MemberRepository;
import com.grinder.repository.queries.MemberQueryRepository;
import com.grinder.repository.queries.SearchQueryRepository;
import com.grinder.utils.RedisUtil;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceImplTest {
    @Spy
    @InjectMocks
    MemberServiceImpl memberService;
    @Mock
    MemberRepository memberRepository;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    MemberQueryRepository memberQueryRepository;
    @Mock
    MailServiceImpl mailService;
    @Mock
    RedisUtil redisUtil;
    @Mock
    SearchQueryRepository searchQueryRepository;
    @Mock
    ImageRepository imageRepository;
    @Mock
    Pageable pageable;

    private Member member1;
    private Member member2;
    private Member member3;
    private Image image1;

    @BeforeEach
    void setUp() {
        member1 = Member.builder().memberId("test").email("test@test.com").role(Role.MEMBER).nickname("testNick").password("test").phoneNum("1234").isDeleted(false).build();
        member2 = Member.builder().memberId("test1").email("test1@test.com").role(Role.ADMIN).nickname("testNick1").password("test").phoneNum("1234").isDeleted(false).build();
        member3 = Member.builder().memberId("test2").email("test2@test.com").role(Role.MEMBER).nickname("testNick2").password("test").phoneNum("1234").isDeleted(true).build();

        image1 = Image.builder().imageId("test").imageUrl("test1234").contentType(ContentType.MEMBER).build();
    }

    @DisplayName("회원 아이디로 조회")
    @Test
    void testFindMemberById() {
        Member member = Member.builder().memberId(UUID.randomUUID().toString()).build();

        doReturn(Optional.of(member)).when(memberRepository).findById(member.getMemberId());

        Member memberFound =  memberService.findMemberById(member.getMemberId());

        assertThat(memberFound).isEqualTo(member);
    }

    @Test
    void findMemberByEmail() {
        doReturn(Optional.of(member1)).when(memberRepository).findByEmail(anyString());

        Member memberFound = memberService.findMemberByEmail("test");

        assertThat(memberFound).isEqualTo(member1);
    }

    @Test
    void findMemberAndImageById() {
        doReturn(Optional.of(member1)).when(memberRepository).findById(anyString());

        doReturn(Optional.of(image1)).when(imageRepository).findByContentTypeAndContentId(any(ContentType.class), anyString());

        MemberDTO.FindMemberAndImageDTO result = memberService.findMemberAndImageById("test");

        assertThat(result).extracting("memberId").isEqualTo(member1.getMemberId());
        assertThat(result).extracting("email").isEqualTo(member1.getEmail());
        assertThat(result).extracting("nickname").isEqualTo(member1.getNickname());
        assertThat(result).extracting("role").isEqualTo(member1.getRole().getValue());
        assertThat(result).extracting("image").isEqualTo("test1234");
    }


    @DisplayName("회원 권한 변경 테스트")
    @Test
    void testUpdateMemberRole() {
        //given
        Member member = Member.builder().role(Role.MEMBER).build();
        Member verifiedMember = Member.builder().role(Role.VERIFIED_MEMBER).build();
        Member sellerMember = Member.builder().role(Role.SELLER).build();
        Member adminMember = Member.builder().role(Role.ADMIN).build();

        doReturn(Optional.of(member)).when(memberRepository).findById("1");
        doReturn(Optional.of(verifiedMember)).when(memberRepository).findById("2");
        doReturn(Optional.of(sellerMember)).when(memberRepository).findById("3");
        doReturn(Optional.of(adminMember)).when(memberRepository).findById("4");

        //when
        memberService.updateMemberRole("1");
        memberService.updateMemberRole("2");
        memberService.updateMemberRole("3");
        memberService.updateMemberRole("4");

        //then
        assertThat(member.getRole()).isEqualTo(Role.VERIFIED_MEMBER);
        assertThat(verifiedMember.getRole()).isEqualTo(Role.MEMBER);
        assertThat(sellerMember.getRole()).isEqualTo(Role.SELLER);
        assertThat(adminMember.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void deleteMember() {
        doReturn(member1).when(memberService).findMemberById(anyString());
        boolean result = memberService.deleteMember("test");

        assertThat(result).isTrue();
        assertThat(member1.getIsDeleted()).isTrue();
    }

    @Test
    void recoverMember() {
        doReturn(member3).when(memberService).findMemberById(anyString());
        boolean result = memberService.recoverMember("test");

        assertThat(result).isTrue();
        assertThat(member3.getIsDeleted()).isFalse();
    }

    @Test
    void searchMemberSlice() {
        List<MemberDTO.FindMemberDTO> list = List.of(new MemberDTO.FindMemberDTO(member1));
        doReturn(new SliceImpl<>(list, pageable, true)).when(memberQueryRepository).searchMemberByRoleAndNicknameSlice(anyString(), anyString(), any(Pageable.class));

        Slice<MemberDTO.FindMemberDTO> result = memberService.searchMemberSlice("test", "test", pageable);

        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent()).extracting("memberId").contains(member1.getMemberId());
        assertThat(result.getContent()).extracting("email").contains(member1.getEmail());
        assertThat(result.getContent()).extracting("nickname").contains(member1.getNickname());
        assertThat(result.getContent()).extracting("phoneNum").contains(member1.getPhoneNum());
        assertThat(result.getContent()).extracting("role").contains(String.valueOf(member1.getRole()));
        assertThat(result.getContent()).extracting("isDeleted").contains(member1.getIsDeleted());
    }

    @Test
    void addMember() {
        doReturn("1234").when(passwordEncoder).encode(anyString());
        doReturn(member1).when(memberRepository).save(any(Member.class));

        MemberDTO.MemberRequestDto dto = new MemberDTO.MemberRequestDto();
        dto.setEmail("email");
        dto.setPassword("12");
        dto.setNickname("name");
        dto.setPhoneNum("12345");
        boolean result = memberService.addMember(dto);

        assertThat(result).isTrue();
        verify(memberRepository, times(1)).save(any(Member.class));
    }

    @Test
    void updateMember_match() {
        doReturn(Optional.of(member1)).when(memberRepository).findById(anyString());
        doReturn(true).when(passwordEncoder).matches(anyString(), anyString());

        doReturn("1234").when(passwordEncoder).encode(anyString());
        doNothing().when(memberRepository).updateMemberInfo(anyString(), anyString(), anyString(), anyString());

        MemberDTO.MemberUpdateRequestDto requestDto = new MemberDTO.MemberUpdateRequestDto();
        requestDto.setMemberId("id");
        requestDto.setNickname("name");
        requestDto.setPassword("12");
        requestDto.setPhoneNum("12345");
        requestDto.setNowPassword("4321");
        boolean result = memberService.updateMember(requestDto);

        assertThat(result).isTrue();
    }

    @Test
    void updateMember_notMatch() {
        doReturn(Optional.of(member1)).when(memberRepository).findById(anyString());
        doReturn(false).when(passwordEncoder).matches(anyString(), anyString());

        MemberDTO.MemberUpdateRequestDto requestDto = new MemberDTO.MemberUpdateRequestDto();
        requestDto.setMemberId("id");
        requestDto.setNickname("name");
        requestDto.setPassword("12");
        requestDto.setPhoneNum("12345");
        requestDto.setNowPassword("4321");

        assertThatThrownBy(() -> memberService.updateMember(requestDto)).isInstanceOf(IllegalArgumentException.class).hasMessage("현재 비밀번호가 일치하지 않습니다.");
    }

    @Test
    void checkEmail() {
        doReturn(true).when(memberRepository).existsByEmail(anyString());

        boolean result = memberService.checkEmail("test");

        assertThat(result).isTrue();
    }

    @Test
    void checkNickname() {
        doReturn(true).when(memberRepository).existsByNickname(anyString());

        boolean result = memberService.checkNickname("test");

        assertThat(result).isTrue();
    }

    @Test
    void sendCodeToEmail() {
        doNothing().when(mailService).sendEmail(anyString(), anyString(), anyString());
        doNothing().when(redisUtil).set(anyString(), anyString(), anyInt());

        boolean result = memberService.sendCodeToEmail("test");

        assertThat(result).isTrue();
    }

    @Test
    void verifiedCode() {
        doReturn("test").when(redisUtil).get(anyString());

        boolean result = memberService.verifiedCode("test", "test");

        assertThat(result).isTrue();
    }

    @Test
    void changePassword() {
        doReturn(member1).when(memberService).findMemberByEmail(anyString());
        doReturn("1234").when(passwordEncoder).encode(anyString());
        doNothing().when(mailService).sendEmail(anyString(), anyString(), anyString());

        boolean result = memberService.changePassword("test");

        assertThat(result).isTrue();
    }

    @Test
    void existEmail() {
        doReturn(true).when(memberRepository).existsByEmail(anyString());

        boolean result = memberService.existEmail("test");

        assertThat(result).isTrue();
    }

    @Test
    void searchMember() {
        MemberDTO.FindMemberAndImageDTO findMember = new MemberDTO.FindMemberAndImageDTO(member1, "test");
        Slice<MemberDTO.FindMemberAndImageDTO> findMemberDto = new SliceImpl<>(List.of(findMember), pageable, true);

        doReturn(findMemberDto).when(searchQueryRepository).searchMembersByNicknameOrEmail(anyString(), any(Pageable.class));
        Slice<MemberDTO.SearchMemberDTO> result = memberService.searchMember("test", "test", pageable);

        assertThat(result).extracting("followId").contains("test");
        assertThat(result).extracting("followMemberId").contains(findMember.getMemberId());
        assertThat(result).extracting("followNickname").contains(findMember.getNickname());
        assertThat(result).extracting("followEmail").contains(findMember.getEmail());
        assertThat(result).extracting("followRole").contains(findMember.getRole());
        assertThat(result).extracting("followImage").contains(findMember.getImage());
    }
}
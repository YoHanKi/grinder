package com.grinder.service.implement;

import com.grinder.domain.dto.HeartDTO;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.Role;
import com.grinder.repository.HeartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.ArgumentCaptor.forClass;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class HeartServiceImplTest {
    @Spy
    @InjectMocks
    HeartServiceImpl heartService;
    @Mock
    HeartRepository heartRepository;
    @Mock
    MemberServiceImpl memberService;

    private Member member;
    private Cafe cafe;
    private Feed feed;
    private Comment comment;
    private Comment cComment;
    private Heart feedHeart;
    @BeforeEach
    void setUp() {
        member = Member.builder().email("member1@example.com").nickname("user1").password("password1").role(Role.MEMBER).phoneNum("1234567890").build();
        cafe = Cafe.builder().name("Cafe A").address("123 Main St, City A").phoneNum("1112223333").averageGrade(4).build();
        feed = Feed.builder().member(member).cafe(cafe).content("Great coffee and atmosphere!").isVisible(true).grade(5).build();
        comment = Comment.builder().commentId("testId").member(member).feed(feed).content("good").build();
        cComment = Comment.builder().commentId("testId").parentComment(comment).member(member).feed(feed).content("good").build();
        feedHeart = Heart.builder().heartId("heartId").member(member).contentType(ContentType.FEED).contentId(feed.getFeedId()).build();
    }

    @Test
    void addHeart() {
        doReturn(member).when(memberService).findMemberByEmail(anyString());
        doReturn(feedHeart).when(heartRepository).save(any(Heart.class));

        HeartDTO.HeartRequestDTO requestDTO = new HeartDTO.HeartRequestDTO();
        requestDTO.setContentId("setId");
        requestDTO.setContentType("FEED");
        Heart result = heartService.addHeart("test", requestDTO);

        assertThat(result).extracting("heartId").isEqualTo("heartId");
    }

    @Test
    void deleteHeart() {
        HeartDTO.HeartRequestDTO requestDTO = new HeartDTO.HeartRequestDTO();
        requestDTO.setContentId("setId");
        requestDTO.setContentType("FEED");
        doNothing().when(heartRepository).deleteAllByMember_EmailAndContentIdAndContentType(anyString(), anyString(), any(ContentType.class));

        heartService.deleteHeart("test", requestDTO);

        verify(heartRepository).deleteAllByMember_EmailAndContentIdAndContentType("test", "setId", ContentType.FEED);
    }

    @Test
    void findHeart() {
        doReturn(List.of(feedHeart)).when(heartRepository).findAllByMember_EmailAndContentIdAndContentType(anyString(), anyString(), any(ContentType.class));

        HeartDTO.HeartRequestDTO requestDTO = new HeartDTO.HeartRequestDTO();
        requestDTO.setContentId("setId");
        requestDTO.setContentType("FEED");
        List<Heart> result = heartService.findHeart("test", requestDTO);

        assertThat(result).isNotNull();
        assertThat(result).extracting("heartId").contains("heartId");
    }

    @Test
    void findHeartList() {
        doReturn(List.of(feedHeart)).when(heartRepository).findByContentIdAndContentType(anyString(), any(ContentType.class));

        HeartDTO.HeartRequestDTO requestDTO = new HeartDTO.HeartRequestDTO();
        requestDTO.setContentId("setId");
        requestDTO.setContentType("FEED");
        List<Heart> result = heartService.findHeartList(requestDTO);

        assertThat(result).isNotNull();
        assertThat(result).extracting("heartId").contains("heartId");
    }

    @Test
    void isHeart() {
        doReturn(List.of(feedHeart)).when(heartService).findHeart(anyString(), any(HeartDTO.HeartRequestDTO.class));

        HeartDTO.HeartRequestDTO requestDTO = new HeartDTO.HeartRequestDTO();
        requestDTO.setContentId("setId");
        requestDTO.setContentType("FEED");
        boolean result = heartService.isHeart("test", requestDTO);

        assertThat(result).isTrue();
    }
}
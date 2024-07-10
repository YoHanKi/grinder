package com.grinder.service.implement;

import com.grinder.domain.dto.FeedDTO;
import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.Feed;
import com.grinder.domain.entity.Member;
import com.grinder.domain.entity.Tag;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.Role;
import com.grinder.domain.enums.TagName;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.FeedRepository;
import com.grinder.repository.MemberRepository;
import com.grinder.repository.queries.FeedQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class FeedServiceImplTest {
    @Spy
    @InjectMocks
    FeedServiceImpl feedService;
    @Mock
    FeedRepository feedRepository;
    @Mock
    CafeRepository cafeRepository;
    @Mock
    ImageServiceImpl imageService;
    @Mock
    TagServiceImpl tagService;
    @Mock
    MemberServiceImpl memberService;
    @Mock
    FeedQueryRepository feedQueryRepository;
    @Mock
    Pageable pageable;


    private Member member1;
    private Member member2;
    private Cafe cafe1;
    private Cafe cafe2;
    private Cafe cafe3;
    private Feed feed1;
    private Feed feed2;
    private Feed feed3;
    private Tag tag1;

    @BeforeEach
    public void makeExample() {
        member1 = Member.builder().email("member1@example.com").nickname("user1").password("password1").role(Role.MEMBER).phoneNum("1234567890").build();
        member2 = Member.builder().email("member2@example.com").nickname("user2").password("password2").role(Role.MEMBER).phoneNum("9876543210").build();

        cafe1 = Cafe.builder().name("Cafe A").address("123 Main St, City A").phoneNum("1112223333").averageGrade(4).build();
        cafe2 = Cafe.builder().name("Cafe B").address("456 Elm St, City B").phoneNum("4445556666").averageGrade(3).build();
        cafe3 = Cafe.builder().name("Cafe C").address("789 Oak St, City C").phoneNum("7778889999").averageGrade(5).build();

        feed1 = Feed.builder().feedId("test1").member(member1).cafe(cafe1).content("Great").isVisible(true).grade(5).build();
        feed2 = Feed.builder().feedId("test2").member(member1).cafe(cafe2).content("Nice").isVisible(true).grade(4).build();
        feed3 = Feed.builder().feedId("test3").member(member2).cafe(cafe3).content("Excellent").isVisible(false).grade(5).build();

        tag1 = Tag.builder().tagId("testTagId").feed(feed1).tagName(TagName.ACCESSIBLE).build();
    }

    @Test
    void findFeed() {
        doReturn(Optional.of(feed1)).when(feedRepository).findById(anyString());

        Feed result = feedService.findFeed(anyString());

        assertThat(result).extracting("feedId").isEqualTo(feed1.getFeedId());
        assertThat(result).extracting("content").isEqualTo(feed1.getContent());
    }

    @Test
    void saveFeed() {
        doReturn(member1).when(memberService).findMemberByEmail(anyString());
        doReturn(Optional.of(cafe1)).when(cafeRepository).findById(anyString());
        doReturn(feed1).when(feedRepository).save(any(Feed.class));
        doNothing().when(tagService).saveTag(any(), any());

        FeedDTO.FeedRequestDTO requestDTO = new FeedDTO.FeedRequestDTO();
        requestDTO.setCafeId("test");
        requestDTO.setContent("content");
        requestDTO.setGrade(2);
        Feed result = feedService.saveFeed(requestDTO, "test", new ArrayList<>());

        assertThat(result).extracting("content").isEqualTo(feed1.getContent());
    }

    @Test
    void findAllFeed() {
        doReturn(List.of(feed1)).when(feedRepository).findAllByIsVisibleTrue();

        List<Feed> result = feedService.findAllFeed();

        assertThat(result.size()).isEqualTo(1);
    }

    @Test
    void updateFeed() {
        doReturn(feed1).when(feedService).findFeed(anyString());
        doReturn(Optional.of(cafe1)).when(cafeRepository).findById(anyString());

        doNothing().when(tagService).deleteTag(anyString());
        doNothing().when(tagService).saveTag(any(Feed.class), anyList());
        doNothing().when(imageService).deleteFeedImage(anyString(), any(ContentType.class));

        FeedDTO.FeedRequestDTO requestDTO = new FeedDTO.FeedRequestDTO();
        requestDTO.setCafeId("id");
        requestDTO.setTagNameList(new ArrayList<>());
        requestDTO.setContent("setContent");
        Feed result = feedService.updateFeed("test", requestDTO, new ArrayList<>());

        assertThat(result).extracting("content").isEqualTo("setContent");
    }

    @Test
    void deleteFeed() {
        doReturn(feed1).when(feedService).findFeed(anyString());
        Feed saveFeed = Feed.builder().feedId(feed1.getFeedId()).isVisible(true).build();
        doReturn(saveFeed).when(feedRepository).save(any(Feed.class));

        feedService.deleteFeed(anyString());

        ArgumentCaptor<Feed> feedCaptor = ArgumentCaptor.forClass(Feed.class);
        verify(feedRepository).save(feedCaptor.capture());

        Feed deleteFeed = feedCaptor.getValue();
        assertThat(deleteFeed.getIsVisible()).isFalse();
    }

    @Test
    void findFeedForAdmin() {
        FeedDTO.FindFeedDTO feedDTO = new FeedDTO.FindFeedDTO();
        feedDTO.setFeedId("setFeedId");
        feedDTO.setGrade(3);
        feedDTO.setContent("setContent");
        feedDTO.setName("setName");
        feedDTO.setNickname("NickName");
        feedDTO.setImageUrls(new ArrayList<>());
        feedDTO.setTagNames(new ArrayList<>());
        doReturn(Optional.of(feedDTO)).when(feedQueryRepository).findFeed(anyString());

        FeedDTO.FindFeedDTO result = feedService.findFeedForAdmin("test");

        assertThat(result).extracting("feedId").isEqualTo(feedDTO.getFeedId());
        assertThat(result).extracting("nickname").isEqualTo(feedDTO.getNickname());
        assertThat(result).extracting("name").isEqualTo(feedDTO.getName());
        assertThat(result).extracting("content").isEqualTo(feedDTO.getContent());
        assertThat(result).extracting("grade").isEqualTo(feedDTO.getGrade());
        assertThat(result.getImageUrls()).isEmpty();
        assertThat(result.getTagNames()).isEmpty();
    }

    @Test
    void findMyPageFeedWithImage() {
        FeedDTO.FeedWithImageResponseDTO feedDTO = new FeedDTO.FeedWithImageResponseDTO(feed1, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, 1L, "test");
        List<FeedDTO.FeedWithImageResponseDTO> feedList = List.of(feedDTO);
        doReturn(new SliceImpl<>(feedList, pageable , true)).when(feedQueryRepository).FindMemberFeedWithImage(anyString(), anyString(), any(org.springframework.data.domain.Pageable.class));

        Slice<FeedDTO.FeedWithImageResponseDTO> result = feedService.findMyPageFeedWithImage("test", "test", pageable);

        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent()).extracting("feedId").contains(feedDTO.getFeedId());
        assertThat(result.getContent()).extracting("memberNickname").contains(feedDTO.getMemberNickname());
        assertThat(result.getContent()).extracting("memberEmail").contains(feedDTO.getMemberEmail());
        assertThat(result.getContent()).extracting("memberImage").contains(feedDTO.getMemberImage());
        assertThat(result.getContent()).extracting("cafe").contains(feedDTO.getCafe());
        assertThat(result.getContent()).extracting("content").contains(feedDTO.getContent());
        assertThat(result.getContent()).extracting("isVisible").contains(feedDTO.getIsVisible());
        assertThat(result.getContent()).extracting("grade").contains(feedDTO.getGrade());
        assertThat(result.getContent()).extracting("createdAt").contains(feedDTO.getCreatedAt());
        assertThat(result.getContent()).extracting("updatedAt").contains(feedDTO.getUpdatedAt());
        assertThat(result.getContent()).extracting("isHeart").contains(false);
        assertThat(result.getContent()).extracting("heartNum").contains(1);
        assertThat(result.getContent().get(0).getTagNameList()).isEmpty();
        assertThat(result.getContent().get(0).getParentCommentList()).isEmpty();
        assertThat(result.getContent().get(0).getImageUrls()).isEmpty();
    }

    @Test
    void findCafeFeedWithImage() {
        FeedDTO.FeedWithImageResponseDTO feedDTO = new FeedDTO.FeedWithImageResponseDTO(feed1, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, 1L, "test");
        List<FeedDTO.FeedWithImageResponseDTO> feedList = List.of(feedDTO);
        doReturn(new SliceImpl<>(feedList, pageable , true)).when(feedQueryRepository).FindCafeFeedWithImage(anyString(), anyString(), any(org.springframework.data.domain.Pageable.class));

        Slice<FeedDTO.FeedWithImageResponseDTO> result = feedService.findCafeFeedWithImage("test", "test", pageable);

        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent()).extracting("feedId").contains(feedDTO.getFeedId());
    }

    @Test
    void searchFeed() {
        FeedDTO.FeedWithImageResponseDTO feedDTO = new FeedDTO.FeedWithImageResponseDTO(feed1, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, 1L, "test");
        List<FeedDTO.FeedWithImageResponseDTO> feedList = List.of(feedDTO);
        doReturn(new SliceImpl<>(feedList, pageable , true)).when(feedQueryRepository).findSearchRecentFeedWithImage(anyString(), anyString(), any(org.springframework.data.domain.Pageable.class));

        Slice<FeedDTO.FeedWithImageResponseDTO> result = feedService.searchFeed("test", "test", pageable);

        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent()).extracting("feedId").contains(feedDTO.getFeedId());
    }

    @Test
    void findRecentFeedWithImage() {
        FeedDTO.FeedWithImageResponseDTO feedDTO = new FeedDTO.FeedWithImageResponseDTO(feed1, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, 1L, "test");
        List<FeedDTO.FeedWithImageResponseDTO> feedList = List.of(feedDTO);
        doReturn(new SliceImpl<>(feedList, pageable , true)).when(feedQueryRepository).findRecentFeedWithImage(anyString(), any(org.springframework.data.domain.Pageable.class));

        Slice<FeedDTO.FeedWithImageResponseDTO> result = feedService.findRecentFeedWithImage("test", pageable);

        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent()).extracting("feedId").contains(feedDTO.getFeedId());
    }

    @Test
    void recommendFeedWithImage() {
        FeedDTO.FeedWithImageResponseDTO feedDTO = new FeedDTO.FeedWithImageResponseDTO(feed1, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, 1L, "test");
        List<FeedDTO.FeedWithImageResponseDTO> feedList = List.of(feedDTO);
        doReturn(new SliceImpl<>(feedList, pageable , true)).when(feedQueryRepository).RecommendFeedWithImage(anyString(), any(org.springframework.data.domain.Pageable.class));

        Slice<FeedDTO.FeedWithImageResponseDTO> result = feedService.RecommendFeedWithImage("test", pageable);

        assertThat(result.getSize()).isEqualTo(1);
        assertThat(result.hasNext()).isTrue();
        assertThat(result.getContent()).extracting("feedId").contains(feedDTO.getFeedId());
    }

    @Test
    void findFeedForComment() {
        FeedDTO.FeedWithImageResponseDTO feedDTO = new FeedDTO.FeedWithImageResponseDTO(feed1, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false, 1L, "test");
        List<FeedDTO.FeedWithImageResponseDTO> feedList = List.of(feedDTO);
        doReturn(feedList).when(feedQueryRepository).findFeedWithImage(anyString(), anyString());

        List<FeedDTO.FeedWithImageResponseDTO> result = feedService.findFeedForComment("test", "test");

        assertThat(result.size()).isEqualTo(1);
        assertThat(result).extracting("feedId").contains(feedDTO.getFeedId());
    }
}
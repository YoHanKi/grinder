package com.grinder.service.implement;

import com.grinder.controller.entity.CafeController;
import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.Feed;
import com.grinder.domain.entity.Member;
import com.grinder.domain.entity.Tag;
import com.grinder.domain.enums.Role;
import com.grinder.domain.enums.TagName;
import com.grinder.repository.TagRepository;
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
class TagServiceImplTest {
    @Spy
    @InjectMocks
    TagServiceImpl tagService;
    @Mock
    TagRepository tagRepository;

    Member member;
    Cafe cafe;
    Feed feed;
    Tag tag;

    @BeforeEach
    void setUp() {
        member = Member.builder().memberId("testmemberId").email("member1@example.com").nickname("user1").password("password1").role(Role.MEMBER).phoneNum("1234567890").build();
        cafe = Cafe.builder().cafeId("testcafeId").name("Cafe A").address("123 Main St, City A").phoneNum("1112223333").averageGrade(4).build();
        feed = Feed.builder().feedId("testFeedId").member(member).cafe(cafe).content("Great coffee and atmosphere!").isVisible(true).grade(5).build();
        tag = Tag.builder().tagId("testTagId").feed(feed).tagName(TagName.ACCESSIBLE).build();
    }

    @Test
    void testFindTag() {
        when(tagRepository.findById(anyString())).thenReturn(Optional.of(tag));

        Tag result = tagService.findTag("testTagId");

        assertThat(result).isNotNull();
        assertThat(result.getTagId()).isEqualTo("testTagId");
        verify(tagRepository, times(1)).findById("testTagId");
    }

    @Test
    void testFindAllTag() {
        when(tagRepository.findByFeed_FeedId(anyString())).thenReturn(List.of(tag));

        List<Tag> result = tagService.findAllTag("testFeedId");

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTagId()).isEqualTo("testTagId");
        verify(tagRepository, times(1)).findByFeed_FeedId("testFeedId");
    }

    @Test
    void testSaveTag() {
        List<String> tagNames = List.of("ACCESSIBLE", "QUIET");

        tagService.saveTag(feed, tagNames);

        verify(tagRepository, times(2)).save(any(Tag.class));
    }

    @Test
    void testSaveTagWithEmptyList() {
        tagService.saveTag(feed, null);

        verify(tagRepository, times(0)).save(any(Tag.class));
    }

    @Test
    void testDeleteTag() {
        when(tagRepository.findByFeed_FeedId(anyString())).thenReturn(List.of(tag));

        tagService.deleteTag("testFeedId");

        verify(tagRepository, times(1)).findByFeed_FeedId("testFeedId");
        verify(tagRepository, times(1)).deleteAll(anyList());
    }
}
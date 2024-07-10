package com.grinder.repository.queries;

import com.grinder.config.TestConfig;
import com.grinder.domain.dto.FeedDTO;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.TagName;
import com.grinder.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class FeedQueryRepositoryTest {
    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private CafeRepository cafeRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private FeedRepository feedRepository;
    @Autowired
    private HeartRepository heartRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BlacklistRepository blacklistRepository;
    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private FeedQueryRepository feedQueryRepository;

    @BeforeEach
    public void setUp() {
        Member member = memberRepository.save(Member.builder().email("test@test.com").nickname("test").phoneNum("01012341234").password("1234").build());
        Member member1 = memberRepository.save(Member.builder().email("test1@test.com").nickname("test1").phoneNum("01012341234").password("1234").build());
        Member member2 = memberRepository.save(Member.builder().email("test2@test.com").nickname("test2").phoneNum("01012341234").password("1234").build());
        Blacklist blacklist = blacklistRepository.save(Blacklist.builder().member(member).blockedMember(member1).build());
        Blacklist blacklist1 = blacklistRepository.save(Blacklist.builder().member(member).blockedMember(member2).build());
        Image image = imageRepository.save(Image.builder().contentType(ContentType.MEMBER).contentId(member.getMemberId()).imageUrl("1234").build());
        Image image1 = imageRepository.save(Image.builder().contentType(ContentType.MEMBER).contentId(member1.getMemberId()).imageUrl("1234").build());
        Cafe cafe = cafeRepository.save(Cafe.builder().name("그라인더0").phoneNum("01012341234").address("서울시 강남구").build());
        Cafe cafe1 = cafeRepository.save(Cafe.builder().name("그라인더1").phoneNum("01012341234").address("서울시 강남구").build());
        Bookmark bookmark = bookmarkRepository.save(Bookmark.builder().cafe(cafe).member(member).build());
        Feed feed = feedRepository.save(Feed.builder().cafe(cafe).member(member).content("내용").grade(5).build());
        Feed feed1 = feedRepository.save(Feed.builder().cafe(cafe).member(member).content("내용1").grade(4).build());
        Feed feed2 = feedRepository.save(Feed.builder().cafe(cafe).member(member).content("내용2").grade(4).build());
        Comment Pcomment = commentRepository.save(Comment.builder().member(member).feed(feed).content("댓글 내용1").build());
        Comment Pcomment1 = commentRepository.save(Comment.builder().member(member).feed(feed).content("댓글 내용2").build());
        Comment Ccomment = commentRepository.save(Comment.builder().member(member).feed(feed).content("대댓글 내용1").parentComment(Pcomment).build());
        Tag tag = tagRepository.save(Tag.builder().tagName(TagName.ACCESSIBLE).feed(feed).build());
        Tag tag1 = tagRepository.save(Tag.builder().tagName(TagName.CLEAN).feed(feed).build());
        Tag tag2 = tagRepository.save(Tag.builder().tagName(TagName.FAST_WIFI).feed(feed).build());
        Heart heart = heartRepository.save(Heart.builder().member(member).contentType(ContentType.FEED).contentId(feed.getFeedId()).build());
    }

    @Test
    void findFeed() {
        String feedId = feedRepository.findAll().get(0).getFeedId();
        FeedDTO.FindFeedDTO result = feedQueryRepository.findFeed(feedId).orElse(null);

        assertThat(result).isNotNull();
        assertThat(result).extracting("content").isEqualTo("내용");
        assertThat(result.getTagNames().size()).isEqualTo(3);
    }

    @Test
    void findFeedWithImage() {
        String feedId = feedRepository.findAll().get(0).getFeedId();
        List<Tag> tagList = tagRepository.findByFeed_FeedId(feedId);
        List<FeedDTO.FeedWithImageResponseDTO> result = feedQueryRepository.findFeedWithImage("test@test.com", feedId);

        assertThat(result).isNotNull();
        assertThat(result).extracting("memberNickname").contains("test");
        assertThat(result).extracting("content").contains("내용");
        assertThat(result.get(0).getTagNameList().size()).isEqualTo(tagList.size());
    }

    @Test
    void recommendFeedWithImage() {
        Slice<FeedDTO.FeedWithImageResponseDTO> result = feedQueryRepository.RecommendFeedWithImage("test1@test.com", Pageable.ofSize(2));

        assertThat(result).isNotNull();
        assertThat(result).extracting("memberNickname").contains("test");
    }

    @Test
    void findRecentFeedWithImage() {
        Slice<FeedDTO.FeedWithImageResponseDTO> result = feedQueryRepository.findRecentFeedWithImage("test1@test.com", Pageable.ofSize(3));

        assertThat(result).isNotNull();
        assertThat(result).extracting("memberNickname").contains("test");
        assertThat(result).extracting("content").contains("내용1");
    }

    @Test
    void findSearchRecentFeedWithImage() {
        Slice<FeedDTO.FeedWithImageResponseDTO> result = feedQueryRepository.findSearchRecentFeedWithImage("test1@test.com","내용", Pageable.ofSize(2));

        assertThat(result).isNotNull();
        assertThat(result).extracting("memberNickname").contains("test");
    }

    @Test
    void findMemberFeedWithImage() {
        Slice<FeedDTO.FeedWithImageResponseDTO> result = feedQueryRepository.FindMemberFeedWithImage("test1@test.com","test@test.com", Pageable.ofSize(3));

        assertThat(result).isNotNull();
        assertThat(result).extracting("memberNickname").contains("test");
        assertThat(result).extracting("content").contains("내용1");
    }

    @Test
    void findCafeFeedWithImage() {
        String cafeId = cafeRepository.findAll().get(0).getCafeId();
        Slice<FeedDTO.FeedWithImageResponseDTO> result = feedQueryRepository.FindCafeFeedWithImage("test1@test.com",cafeId, Pageable.ofSize(2));

        assertThat(result).extracting("memberNickname").contains("test");
    }
}
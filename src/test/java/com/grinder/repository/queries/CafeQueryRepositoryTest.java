package com.grinder.repository.queries;

import com.grinder.config.TestConfig;
import com.grinder.domain.dto.CafeDTO;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.TagName;
import com.grinder.repository.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class CafeQueryRepositoryTest {
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
    private CafeQueryRepository cafeQueryRepository;

    @BeforeEach
    public void setUp() {
        Member member = memberRepository.save(Member.builder().email("test@test.com").nickname("test").phoneNum("01012341234").password("1234").build());
        Member member1 = memberRepository.save(Member.builder().email("test1@test.com").nickname("test1").phoneNum("01012341234").password("1234").build());
        Member member2 = memberRepository.save(Member.builder().email("test2@test.com").nickname("test2").phoneNum("01012341234").password("1234").build());
        Blacklist blacklist = blacklistRepository.save(Blacklist.builder().member(member).blockedMember(member1).build());
        Blacklist blacklist1 = blacklistRepository.save(Blacklist.builder().member(member).blockedMember(member2).build());
        Image image = imageRepository.save(Image.builder().contentType(ContentType.MEMBER).contentId(member.getMemberId()).imageUrl("1234").build());
        Image image1 = imageRepository.save(Image.builder().contentType(ContentType.MEMBER).contentId(member1.getMemberId()).imageUrl("1234").build());
        Cafe cafe = cafeRepository.save(Cafe.builder().cafeId("test").name("그라인더0").phoneNum("01012341234").address("서울시 강남구").averageGrade(3).build());
        Cafe cafe1 = cafeRepository.save(Cafe.builder().name("그라인더1").phoneNum("01012341234").address("서울시 강남구").build());
        Image cafeImage = imageRepository.save(Image.builder().contentType(ContentType.CAFE).contentId(cafe.getCafeId()).imageUrl("1234").build());
        Bookmark bookmark = bookmarkRepository.save(Bookmark.builder().cafe(cafe).member(member).build());
        Feed feed = feedRepository.save(Feed.builder().cafe(cafe).member(member).content("내용").grade(5).build());
        Feed feed1 = feedRepository.save(Feed.builder().cafe(cafe).member(member1).content("내용1").grade(4).build());
        Tag tag = tagRepository.save(Tag.builder().tagName(TagName.ACCESSIBLE).feed(feed).build());
        Tag tag1 = tagRepository.save(Tag.builder().tagName(TagName.CLEAN).feed(feed).build());
        Tag tag2 = tagRepository.save(Tag.builder().tagName(TagName.FAST_WIFI).feed(feed).build());
        Heart heart = heartRepository.save(Heart.builder().member(member).contentType(ContentType.FEED).contentId(feed.getFeedId()).build());
    }

    @Test
    @DisplayName("이름, 주소, 전화번호로 카페 찾기")
    void searchCafeByNameAndAddressAndPhoneNum() {
        Slice<Cafe> find = cafeQueryRepository.searchCafeByNameAndAddressAndPhoneNum("그라인더", Pageable.ofSize(2));

        List<String> list = Arrays.asList("그라인더0", "그라인더1");

        assertThat(find).extracting("name").containsAll(list);
    }

    @Test
    @DisplayName("이름, 주소로 카페 찾기")
    void searchCafes() {
        Slice<CafeDTO.findAllWithImageAndTagResponse> find = cafeQueryRepository.searchCafes("그라", Pageable.ofSize(2));

        List<String> nameList = Arrays.asList("그라인더0", "그라인더1");

        assertThat(find).extracting("cafeName").containsAll(nameList);
    }

    @Test
    void findTop3CafesReferencedThisWeek() {
        List<CafeDTO.findAllWithImageAndTagResponse> find = cafeQueryRepository.findTop3CafesReferencedThisWeek();

        assertThat(find).extracting("cafeId").contains("test");
        assertThat(find).extracting("cafeName").contains("그라인더0");
        assertThat(find).extracting("cafeAddress").contains("서울시 강남구");
        assertThat(find).extracting("cafePhoneNum").contains("01012341234");
        assertThat(find).extracting("averageGrade").contains(3);
        assertThat(find).extracting("cafeImageUrl").contains("1234");
        assertThat(find.get(0).getTagList().size()).isEqualTo(2);
    }
}
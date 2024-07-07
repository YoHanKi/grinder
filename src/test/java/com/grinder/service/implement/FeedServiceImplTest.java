package com.grinder.service.implement;

import com.grinder.domain.entity.Cafe;
import com.grinder.domain.entity.Feed;
import com.grinder.domain.entity.Member;
import com.grinder.domain.enums.Role;
import com.grinder.repository.CafeRepository;
import com.grinder.repository.FeedRepository;
import com.grinder.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class FeedServiceImplTest {
    @Autowired
    public FeedRepository feedRepository;
    @Autowired
    public CafeRepository cafeRepository;
    @Autowired
    public MemberRepository memberRepository;

    private Member member1;
    private Member member2;
    private Cafe cafe1;
    private Cafe cafe2;
    private Cafe cafe3;
    private Feed feed1;
    private Feed feed2;
    private Feed feed3;

    @BeforeEach
    public void makeExample() {
        member1 = Member.builder()
                .email("member1@example.com")
                .nickname("user1")
                .password("password1")
                .role(Role.MEMBER)
                .phoneNum("1234567890")
                .build();

        member2 = Member.builder()
                .email("member2@example.com")
                .nickname("user2")
                .password("password2")
                .role(Role.MEMBER)
                .phoneNum("9876543210")
                .build();

        cafe1 = Cafe.builder()
                .name("Cafe A")
                .address("123 Main St, City A")
                .phoneNum("1112223333")
                .averageGrade(4)
                .build();

        cafe2 = Cafe.builder()
                .name("Cafe B")
                .address("456 Elm St, City B")
                .phoneNum("4445556666")
                .averageGrade(3)
                .build();

        cafe3 = Cafe.builder()
                .name("Cafe C")
                .address("789 Oak St, City C")
                .phoneNum("7778889999")
                .averageGrade(5)
                .build();

        feed1 = Feed.builder()
                .member(member1)
                .cafe(cafe1)
                .content("Great coffee and atmosphere!")
                .isVisible(true)
                .grade(5)
                .build();

        feed2 = Feed.builder()
                .member(member1)
                .cafe(cafe2)
                .content("Nice place to hang out with friends.")
                .isVisible(true)
                .grade(4)
                .build();

        feed3 = Feed.builder()
                .member(member2)
                .cafe(cafe3)
                .content("Excellent service and desserts.")
                .isVisible(false)
                .grade(5)
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
        cafeRepository.save(cafe1);
        cafeRepository.save(cafe2);
        cafeRepository.save(cafe3);
        feedRepository.save(feed1);
        feedRepository.save(feed2);
        feedRepository.save(feed3);
    }

    @Test
    @DisplayName("피드 조회 테스트")
    void findFeed() {
        String feed1Id = feed1.getFeedId();
        String feed2Id = feed2.getFeedId();
        String feed3Id = feed3.getFeedId();

        Feed findFeed1 = feedRepository.findById(feed1Id).orElseThrow(() -> new IllegalArgumentException("feed id(" + feed1Id + ")를 찾울 수 없습니다."));
        Feed findFeed2 = feedRepository.findById(feed2Id).orElseThrow(() -> new IllegalArgumentException("feed id(" + feed2Id + ")를 찾울 수 없습니다."));
        Feed findFeed3 = feedRepository.findById(feed3Id).orElseThrow(() -> new IllegalArgumentException("feed id(" + feed3Id + ")를 찾울 수 없습니다."));

        assertEquals(feed1, findFeed1);
        assertEquals(feed2, findFeed2);
        assertEquals(feed3, findFeed3);
    }

    @Test
    @DisplayName("피드 전체 조회 테스트")
    void findAllFeed() {
        List<Feed> feedList = new ArrayList<>();
        feedList.add(feed1);
        feedList.add(feed2);
        // feedList.add(feed3); feed3은 isVisible==false 이므로 제외

        List<Feed> findFeedList = feedRepository.findAllByIsVisibleTrue();
        assertThat(findFeedList.size()).isEqualTo(feedList.size());
    }

    @Test
    @DisplayName("피드 수정 테스트")
    void updateFeed() {
        String feed1Id = feed1.getFeedId();
        Cafe updateCafe = cafe2;
        String updateContent = "update content!";
        Integer updateGrade = 4;

        feed1.updateFeed(updateCafe, updateContent, updateGrade);
        feedRepository.save(feed1);
        Feed updateFeed1 = feedRepository.findById(feed1Id).orElseThrow(() -> new IllegalArgumentException("feed id(" + feed1Id + ")를 찾울 수 없습니다."));

        assertEquals(updateFeed1.getCafe(), updateCafe);
        assertEquals(updateFeed1.getContent(), updateContent);
        assertEquals(updateFeed1.getGrade(), updateGrade);
    }

    @Test
    @DisplayName("피드 삭제 테스트")
    void deleteFeed() {
        feed1.notVisible();
        feed2.notVisible();
        List<Feed> feedList = new ArrayList<>();

        List<Feed> findFeedList = feedRepository.findAllByIsVisibleTrue();

        assertEquals(feedList, findFeedList);
    }
}
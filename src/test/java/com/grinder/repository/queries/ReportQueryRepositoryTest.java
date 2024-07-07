package com.grinder.repository.queries;

import com.grinder.config.TestConfig;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.grinder.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Import(TestConfig.class)
class ReportQueryRepositoryTest {
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private CafeRepository cafeRepository;
    @Autowired
    private ReportRepository reportRepository;
    @Autowired
    private FeedRepository feedRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ReportQueryRepository reportQueryRepository;


    @BeforeEach
    public void setUp() {
        Member member = memberRepository.save(Member.builder().email("test@test.com").nickname("test").phoneNum("01012341234").password("1234").build());
        Member member1 = memberRepository.save(Member.builder().email("test1@test.com").nickname("test1").phoneNum("01012341234").password("1234").build());
        Cafe cafe = cafeRepository.save(Cafe.builder().name("그라인더0").phoneNum("01012341234").address("서울시 강남구").build());
        Feed feed = feedRepository.save(Feed.builder().cafe(cafe).member(member).content("내용").grade(5).build());
        Comment Pcomment = commentRepository.save(Comment.builder().member(member).feed(feed).content("댓글 내용1").build());
        reportRepository.save(Report.builder().contentType(ContentType.FEED).contentId(feed.getFeedId()).member(member1).build());
        reportRepository.save(Report.builder().contentType(ContentType.FEED).contentId(feed.getFeedId()).member(member1).build());
        reportRepository.save(Report.builder().contentType(ContentType.COMMENT).contentId(Pcomment.getCommentId()).member(member1).build());
        reportRepository.save(Report.builder().contentType(ContentType.COMMENT).contentId(Pcomment.getCommentId()).member(member1).build());
    }
    @Test
    void searchReport() {
        Slice<Report> feedSlice = reportQueryRepository.searchReport("내용", "FEED", Pageable.ofSize(1));

        assertThat(feedSlice.getSize()).isEqualTo(1);
        assertThat(feedSlice.hasNext()).isTrue();
        assertThat(feedSlice).extracting("member").extracting("email").contains("test1@test.com");
    }
}
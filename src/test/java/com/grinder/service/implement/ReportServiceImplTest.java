package com.grinder.service.implement;

import com.grinder.domain.dto.ReportDTO;
import com.grinder.domain.entity.*;
import com.grinder.domain.enums.ContentType;
import com.grinder.domain.enums.Role;
import com.grinder.repository.CommentRepository;
import com.grinder.repository.ReportRepository;
import com.grinder.repository.queries.ReportQueryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest {
    @InjectMocks
    @Spy
    ReportServiceImpl reportService;
    @Mock
    ReportRepository reportRepository;
    @Mock
    ReportQueryRepository reportQueryRepository;
    @Mock
    CommentServiceImpl commentService;
    @Mock
    FeedServiceImpl feedService;
    @Mock
    CommentRepository commentRepository;
    @Mock
    MemberServiceImpl memberService;
    @Mock
    Pageable pageable;

    private Member member;
    private Cafe cafe;
    private Feed feed;
    private Comment comment;
    private Comment cComment;
    private Report report1;
    private Report report2;
    @BeforeEach
    void setUp() {
        member = Member.builder().email("member1@example.com").nickname("user1").password("password1").role(Role.MEMBER).phoneNum("1234567890").build();
        cafe = Cafe.builder().name("Cafe A").address("123 Main St, City A").phoneNum("1112223333").averageGrade(4).build();
        feed = Feed.builder().member(member).cafe(cafe).content("Great coffee and atmosphere!").isVisible(true).grade(5).build();
        comment = Comment.builder().commentId("testId").member(member).feed(feed).content("good").build();
        cComment = Comment.builder().commentId("testId").parentComment(comment).member(member).feed(feed).content("good").build();
        report1 = Report.builder().reportId("reportId").contentType(ContentType.COMMENT).member(member).contentId("contentId").build();
        report2 = Report.builder().reportId("reportIdFeed").contentType(ContentType.FEED).member(member).contentId("contentFeed").build();
    }

    @DisplayName("신고 내역 조회")
    @Test
    void testFindAllReports() {
        //given
        List<Report> reportList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            reportList.add(Report.builder().member(new Member()).contentType(ContentType.FEED).contentId(UUID.randomUUID().toString()).build());
        }
        Feed feed = Feed.builder().cafe(new Cafe()).member(new Member()).content("피드내용").build();

        doReturn(reportList).when(reportRepository).findAll();
        doReturn(feed).when(feedService).findFeed(anyString());

        //when
        List<ReportDTO.FindReportDTO> reportDTOList = reportService.findAllReports();

        //then
        assertThat(reportDTOList.size()).isEqualTo(2);
    }
    @Test
    void testFindAllReports_comment() {
        //given
        List<Report> reportList = new ArrayList<>();
        reportList.add(report1);

        doReturn(reportList).when(reportRepository).findAll();
        doReturn(comment).when(commentService).findComment(anyString());

        //when
        List<ReportDTO.FindReportDTO> reportDTOList = reportService.findAllReports();

        //then
        assertThat(reportDTOList.size()).isEqualTo(1);
        assertThat(reportDTOList).extracting("reportId").contains(report1.getReportId());
        assertThat(reportDTOList).extracting("nickname").contains(report1.getMember().getNickname());
        assertThat(reportDTOList).extracting("contentId").contains(report1.getContentId());
        assertThat(reportDTOList).extracting("contentType").contains(report1.getContentType());
        assertThat(reportDTOList).extracting("content").contains(comment.getContent());
    }

    @DisplayName("신고 내역 삭제")
    @Test
    void testDeleteReport() {
        Report report = Report.builder().reportId(UUID.randomUUID().toString()).member(new Member()).build();

        doReturn(Optional.of(report)).when(reportRepository).findById(report.getReportId());
        doNothing().when(reportRepository).delete(report);

        reportService.deleteReport(report.getReportId());

        verify(reportRepository, times(1)).delete(report);
    }

    @DisplayName("신고된 컨텐츠 가리기")
    @Test
    void testDeleteContent() {
        //given
        String feedReportId = UUID.randomUUID().toString();

        Report feedReport = Report.builder().reportId(feedReportId).member(new Member()).contentType(ContentType.FEED).contentId(UUID.randomUUID().toString()).build();

        doNothing().when(feedService).deleteFeed(anyString());
        doReturn(feedReport).when(reportService).findReportById(anyString());
        doNothing().when(reportService).deleteAllReportByContentId(any(Report.class));

        //when
        reportService.deleteContent(feedReportId);

        //then
        verify(feedService, times(1)).deleteFeed(anyString());
        verify(commentService, times(0)).deleteComment(anyString());
        verify(reportService, times(1)).deleteAllReportByContentId(any(Report.class));
    }

    @Test
    void testDeleteContent_Comment() {
        //given
        String feedReportId = UUID.randomUUID().toString();

        Report feedReport = Report.builder().reportId(feedReportId).member(new Member()).contentType(ContentType.COMMENT).contentId(UUID.randomUUID().toString()).build();

        doNothing().when(commentService).deleteComment(anyString());
        doReturn(feedReport).when(reportService).findReportById(anyString());
        doNothing().when(reportService).deleteAllReportByContentId(any(Report.class));

        //when
        reportService.deleteContent(feedReportId);

        //then
        verify(feedService, times(0)).deleteFeed(anyString());
        verify(commentService, times(1)).deleteComment(anyString());
        verify(reportService, times(1)).deleteAllReportByContentId(any(Report.class));
    }

    @Test
    void deleteAllReportByContentId() {
        List<Report> list = List.of(report1);
        doReturn(list).when(reportRepository).findByContentId(anyString());
        doNothing().when(reportRepository).deleteAll(anyList());

        reportService.deleteAllReportByContentId(report1);
        verify(reportRepository).deleteAll(list);
    }

    @Test
    void searchReportByContentAndType() {
        List<Report> list = List.of(report1, report2);
        doReturn(new SliceImpl<>(list, pageable, true)).when(reportQueryRepository).searchReport(anyString(), anyString(), any(Pageable.class));
        doReturn(feed).when(feedService).findFeed(anyString());
        doReturn(comment).when(commentService).findComment(anyString());

        Slice<ReportDTO.FindReportDTO> result = reportService.searchReportByContentAndType("test", "test", pageable);

        assertThat(result).extracting("nickname").contains(member.getNickname());
    }

    @Test
    void addReport() {
        doReturn(member).when(memberService).findMemberByEmail(anyString());
        doReturn(true).when(commentRepository).existsById(anyString());
        doReturn(report1).when(reportRepository).save(any(Report.class));

        boolean result = reportService.addReport("test", "test");

        assertThat(result).isTrue();
    }
}
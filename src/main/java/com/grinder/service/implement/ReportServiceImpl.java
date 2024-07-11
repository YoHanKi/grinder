package com.grinder.service.implement;

import com.grinder.domain.entity.Comment;
import com.grinder.domain.entity.Feed;
import com.grinder.domain.entity.Member;
import com.grinder.domain.entity.Report;
import com.grinder.domain.enums.ContentType;
import com.grinder.exception.ContentTypeException;
import com.grinder.repository.CommentRepository;
import com.grinder.repository.ReportRepository;
import com.grinder.repository.queries.ReportQueryRepository;
import com.grinder.service.CommentService;
import com.grinder.service.FeedService;
import com.grinder.service.MemberService;
import com.grinder.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

import static com.grinder.domain.dto.ReportDTO.*;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final CommentService commentService;
    private final FeedService feedService;
    private final ReportQueryRepository reportQueryRepository;
    private final CommentRepository commentRepository;
    private final MemberService memberService;

    @Override
    public List<FindReportDTO> findAllReports() {
        List<Report> reportList = reportRepository.findAll();

        List<FindReportDTO> reportDTOList = reportList
                .stream()
                .map(report -> {
                    if (report.getContentType() == ContentType.FEED) {
                        Feed feed = feedService.findFeed(report.getContentId());
                        return new FindReportDTO(report, feed);
                    } else if(report.getContentType() == ContentType.COMMENT) {
                        Comment comment = commentService.findComment(report.getContentId());
                        return new FindReportDTO(report, comment);
                    } else {
                        throw new ContentTypeException("신고된 컨텐츠의 타입이 잘못되었습니다");
                    }
                })
                .toList();

        return reportDTOList;
    }

    @Override
    public Report findReportById(String reportId) throws NoSuchElementException {
        Report report = reportRepository.findById(reportId).orElseThrow(() -> new NoSuchElementException("존재하지 않는 신고글입니다."));
        return report;
    }

    @Override
    @Transactional
    public void deleteReport(String reportId) {
        Report report = findReportById(reportId);
        reportRepository.delete(report);
    }

    @Override
    @Transactional
    public void deleteAllReportByContentId(Report report) {
        List<Report> reportList = reportRepository.findByContentId(report.getContentId());
        reportRepository.deleteAll(reportList);
    }

    @Override
    @Transactional
    public void deleteContent(String reportId) {
        Report report = findReportById(reportId);
        switch (report.getContentType()) {
            case COMMENT :
                commentService.deleteComment(report.getContentId());
                break;

            case FEED :
                feedService.deleteFeed(report.getContentId());
                break;

            default :
                throw new ContentTypeException("신고된 컨텐츠의 타입이 잘못되었습니다.");
        }

        deleteAllReportByContentId(report);
    }

    @Override
    public Slice<FindReportDTO> searchReportByContentAndType(String keyword, String contentType, Pageable pageable) {
        Slice<Report> reportSlice = reportQueryRepository.searchReport(keyword, contentType, pageable);

        return new SliceImpl<>(reportSlice.getContent().stream().map(report -> {
            if (report.getContentType() == ContentType.FEED) {
                Feed feed = feedService.findFeed(report.getContentId());
                return new FindReportDTO(report, feed);
            } else if(report.getContentType() == ContentType.COMMENT) {
                Comment comment = commentService.findComment(report.getContentId());
                return new FindReportDTO(report, comment);
            } else {
                throw new ContentTypeException("신고된 컨텐츠의 타입이 잘못되었습니다");
            }
        }).toList(), reportSlice.getPageable(), reportSlice.hasNext());
    }

    @Override
    public boolean addReport(String contentId, String email) {
        try {
            Member member = memberService.findMemberByEmail(email);
            Report.ReportBuilder builder = Report.builder()
                    .contentId(contentId)
                    .member(member);

            if (commentRepository.existsById(contentId)) {
                builder.contentType(ContentType.COMMENT);
            } else builder.contentType(ContentType.FEED);
            reportRepository.save(builder.build());
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}

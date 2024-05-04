package com.grinder.domain.dto;

import com.grinder.domain.entity.Report;
import com.grinder.domain.enums.ContentType;
import lombok.Getter;
import lombok.Setter;

public class ReportDTO {

    @Getter
    @Setter
    public static class FindReportDTO {
        private String reportId;
        private String memberId;
        private String contentId;
        private ContentType contentType;

        public FindReportDTO(Report report) {
            this.reportId = report.getReportId();
            this.memberId = report.getMember().getMemberId();
            this.contentId = report.getContentId();
            this.contentType = report.getContentType();
        }
    }
}

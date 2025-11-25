package com.gdg.slbackend.service.report;

import com.gdg.slbackend.domain.report.Report;
import com.gdg.slbackend.domain.report.ReportRepository;
import com.gdg.slbackend.domain.user.User;
import com.gdg.slbackend.domain.user.UserRepository;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;

    public ReportService(
            ReportRepository reportRepository,
            UserRepository userRepository
    ) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
    }

    public void reportPost(Long reporterId, Long reportedUserId, Long postId, String reason) {
        createReport(reporterId, reportedUserId, postId, null, reason);
    }

    public void reportComment(Long reporterId, Long reportedUserId, Long commentId, String reason) {
        createReport(reporterId, reportedUserId, null, commentId, reason);
    }

    private void createReport(
            Long reporterId,
            Long reportedUserId,
            Long postId,
            Long commentId,
            String reason
    ) {
        if ((postId == null && commentId == null) || (postId != null && commentId != null)) {
            throw new GlobalException(ErrorCode.INVALID_REPORT_TARGET);
        }

        User reporter = userRepository.findById(reporterId)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        User reportedUser = userRepository.findById(reportedUserId)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));

        Report report = new Report(reporter, reportedUser, postId, commentId, reason);
        reportRepository.save(report);

        long count = reportRepository.countByReportedUserId(reportedUserId);

        // 5회 이상 신고 누적 → 영구 정지
        if (count >= 5) {
            reportedUser.ban();
        }
    }
}

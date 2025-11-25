package com.gdg.slbackend.api.report;

import com.gdg.slbackend.global.response.ApiResponse;
import com.gdg.slbackend.global.security.UserPrincipal;
import com.gdg.slbackend.service.report.ReportService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/reports")
public class ReportController {

    private final ReportService service;

    public ReportController(ReportService service) {
        this.service = service;
    }

    // 게시글 신고
    @PostMapping("/posts/{postId}")
    public ApiResponse<Void> reportPost(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long postId,
            @RequestParam Long reportedUserId,
            @RequestParam(required = false) String reason
    ) {
        service.reportPost(principal.getId(), reportedUserId, postId, reason);
        return ApiResponse.success();
    }

    // 댓글 신고
    @PostMapping("/comments/{commentId}")
    public ApiResponse<Void> reportComment(
            @AuthenticationPrincipal UserPrincipal principal,
            @PathVariable Long commentId,
            @RequestParam Long reportedUserId,
            @RequestParam(required = false) String reason
    ) {
        service.reportComment(principal.getId(), reportedUserId, commentId, reason);
        return ApiResponse.success();
    }
}

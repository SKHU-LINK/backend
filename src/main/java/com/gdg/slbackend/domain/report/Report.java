package com.gdg.slbackend.domain.report;

import com.gdg.slbackend.domain.user.User;
import com.gdg.slbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "reports")
public class Report extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 신고한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private User reporter;

    // 신고 당한 사람
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_user_id", nullable = false)
    private User reportedUser;

    @Column
    private Long postId;

    @Column
    private Long commentId;

    @Column
    private String reason;

    protected Report() {}

    public Report(
            User reporter,
            User reportedUser,
            Long postId,
            Long commentId,
            String reason
    ) {
        this.reporter = reporter;
        this.reportedUser = reportedUser;
        this.postId = postId;
        this.commentId = commentId;
        this.reason = reason;
    }

    public Long getId() { return id; }
    public User getReporter() { return reporter; }
    public User getReportedUser() { return reportedUser; }
    public Long getPostId() { return postId; }
    public Long getCommentId() { return commentId; }
    public String getReason() { return reason; }
}

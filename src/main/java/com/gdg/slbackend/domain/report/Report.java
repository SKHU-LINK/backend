package com.gdg.slbackend.domain.report;

import com.gdg.slbackend.domain.user.User;
import com.gdg.slbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Table(name = "reports")
@NoArgsConstructor(access = PROTECTED)
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

    @Builder
    private Report(
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
}

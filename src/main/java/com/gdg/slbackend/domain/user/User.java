package com.gdg.slbackend.domain.user;

import com.gdg.slbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static lombok.AccessLevel.PROTECTED;

@Getter
@Entity
@Table(name = "users")
@NoArgsConstructor(access = PROTECTED)
public class User extends BaseTimeEntity {

    /**
     * MS OAuth 사용자 정보를 저장하는 엔티티임.
     * 서비스 전역의 계정 역할을 담당하며,
     * 커뮤니티별 권한은 CommunityMembership에서 관리함.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String oauthProvider; // "MICROSOFT"

    @Column(nullable = false, unique = true)
    private String oauthSubject; // MS의 sub/oid

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String displayName;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private int mileage;

    @Column(nullable = false)
    private boolean isBanned;

    @Column
    private LocalDateTime lastLoginAt;

    @Builder
    private User(
            String oauthProvider,
            String oauthSubject,
            String email,
            String displayName,
            String nickname,
            UserRole role
    ) {
        this.oauthProvider = oauthProvider;
        this.oauthSubject = oauthSubject;
        this.email = email;
        this.displayName = displayName;
        this.nickname = nickname;
        this.role = role;
        this.mileage = 0;
        this.isBanned = false;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void increaseMileage(int amount) {
        this.mileage += amount;
    }

    public void useMileage(int amount) {
        this.mileage -= amount;
    }

    public void ban() {
        this.isBanned = true;
    }

    public void updateLastLoginAt(LocalDateTime time) {
        this.lastLoginAt = time;
    }
}

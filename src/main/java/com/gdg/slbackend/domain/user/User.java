package com.gdg.slbackend.domain.user;

import com.gdg.slbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
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

    @Column
    private String lastLoginAt;

    protected User() {
        // JPA 기본 생성자
    }

    public User(
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
    }

    public Long getId() {
        return id;
    }

    public String getOauthProvider() {
        return oauthProvider;
    }

    public String getOauthSubject() {
        return oauthSubject;
    }

    public String getEmail() {
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getNickname() {
        return nickname;
    }

    public UserRole getRole() {
        return role;
    }

    public String getLastLoginAt() {
        return lastLoginAt;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateLastLoginAt(String time) {
        this.lastLoginAt = time;
    }
}

package com.gdg.slbackend.domain.community;

import com.gdg.slbackend.domain.user.User;
import com.gdg.slbackend.global.enums.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CommunityMembership {
    /**
     * This is Entity the information of Authority and Whether Pin between Users and Communities.
     * Relationship
     * - ManyToOne User
     * - ManyToOne Community
     * */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "community_id")
    private Community community;

    private Role role;

    private boolean isPinned;

    private boolean isBanned;

    private LocalDate joinedAt;

    @Builder
    protected CommunityMembership(Role role, boolean isPinned, boolean isBanned, LocalDate joinedAt, User user, Community community) {
        this.role = role;
        this.isPinned = isPinned;
        this.isBanned = isBanned;
        this.joinedAt = joinedAt;
        this.user = user;
        this.community = community;
    }

    public void updateRole(Role role) {
        this.role = role;
    }

    public void updateIsPinned(boolean isPinned) {
        this.isPinned = isPinned;
    }

    public void updateIsBanned(boolean isBanned) {
        this.isBanned = isBanned;
    }
}

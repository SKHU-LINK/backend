package com.gdg.slbackend.domain.community;

import com.gdg.slbackend.domain.user.User;
import com.gdg.slbackend.global.entity.BaseTimeEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@Table(name = "communities")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Community extends BaseTimeEntity {
    /**
     * This is Entity the information of communities.
     * Each Communities Authority Managed by CommunityMembership.
     * */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private int year;

    private int semester;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User admin;

    @Builder
    protected Community(String name, int year, int semester, LocalDate createdAt, LocalDate updateAt, User admin) {
        this.name = name;
        this.year = year;
        this.semester = semester;
        this.admin = admin;
    }

    public void updateAdmin(User admin) {
        this.admin = admin;
    }
}

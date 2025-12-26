package com.gdg.slbackend.domain.community;

import com.gdg.slbackend.global.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityMembershipRepository
        extends JpaRepository<CommunityMembership, Long> {
    Optional<CommunityMembership> findByCommunityIdAndUserId(
            Long communityId,
            Long userId
    );

    List<CommunityMembership> findAllByUserId(Long userId);

    List<CommunityMembership> findByCommunityOrderByIsPinnedDescIdAsc(
            Community community
    );

    boolean existsByCommunityIdAndUserIdAndRole(Long communityId, Long userId, Role role);
}

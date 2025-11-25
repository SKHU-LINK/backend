package com.gdg.slbackend.domain.community;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityMembershipRepository extends JpaRepository<CommunityMembership, Long> {
    Optional<CommunityMembership> findByUserIdAndCommunityId(Long userId, Long communityId);
    List<CommunityMembership> findByCommunityOrderByIsPinnedDescIdAsc(Community community);
    List<CommunityMembership> findAllByUserId(Long userId);
}

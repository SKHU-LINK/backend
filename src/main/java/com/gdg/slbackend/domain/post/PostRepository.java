package com.gdg.slbackend.domain.post;

import com.gdg.slbackend.global.enums.Category;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Optional<Post> findByTitle(String title);
    Optional<Post> findByAuthorNickname(String authorNickname);
    Optional<Post> findByCategory(Category category);

    Optional<Post> findByCommunityIdAndPinnedTrue(Long communityId);

    @Query("""
            SELECT p FROM Post p
            WHERE p.communityId = :communityId
            AND (:lastId IS NULL OR p.id < :lastId)
            ORDER BY p.id DESC
            """)
    List<Post> findNextPosts(
            @Param("communityId") Long communityId,
            @Param("lastId") Long lastId,
            Pageable pageable
    );

    void deleteByCommunityId(Long communityId);
}

package com.gdg.slbackend.domain.resource;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    List<Resource> findByCommunityIdAndIdLessThanOrderByIdDesc(
            Long communityId,
            Long lastId,
            Pageable pageable
    );

    List<Resource> findByCommunityIdOrderByIdDesc(
            Long communityId,
            Pageable pageable
    );
}


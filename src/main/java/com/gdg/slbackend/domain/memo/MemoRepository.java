package com.gdg.slbackend.domain.memo;

import com.gdg.slbackend.domain.memo.Memo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MemoRepository extends JpaRepository<Memo, Long> {

    List<Memo> findByCreatedAtGreaterThanEqualOrderByCreatedAtAsc(LocalDateTime createdAt);

    List<Memo> findAllByOrderByCreatedAtAsc();

    @Modifying(clearAutomatically = true)
    @Query("delete from Memo m where m.createdAt < :cutoff")
    int deleteOlderThan(@Param("cutoff") LocalDateTime cutoff);
}
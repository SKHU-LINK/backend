package com.gdg.slbackend.domain.memo;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemoBoardRepository extends JpaRepository<MemoBoard, Long> {

    Optional<MemoBoard> findFirstByOrderByIdAsc();
}

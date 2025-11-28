package com.gdg.slbackend.domain.memo;

import com.gdg.slbackend.domain.memo.MemoBoard;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoBoardRepository extends JpaRepository<MemoBoard, Long> {
}
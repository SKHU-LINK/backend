package com.gdg.slbackend.api.memo;

import com.gdg.slbackend.api.memo.dto.MemoRequest;
import com.gdg.slbackend.api.memo.dto.MemoResponse;
import com.gdg.slbackend.service.memo.MemoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/memos")
@Tag(name = "Memo", description = "완전 익명 메모 API")
public class MemoController {

    private final MemoService memoService;

    @GetMapping
    @Operation(
            summary = "오늘의 메모 조회",
            description = "기본적으로 오늘 생성된 메모만 조회하며, all=true로 전체 조회가 가능합니다."
    )
    public ResponseEntity<List<MemoResponse>> getMemos(
            @Parameter(description = "true면 생성일과 관계없이 전체 메모 조회")
            @RequestParam(defaultValue = "false") boolean all
    ) {
        return ResponseEntity.ok(memoService.findMemos(all));
    }

    @PostMapping
    @Operation(
            summary = "메모 작성",
            description = "로그인 없이 누구나 메모를 작성할 수 있으며 색상은 서버에서 무작위로 지정됩니다."
    )
    public ResponseEntity<MemoResponse> createMemo(@Valid @RequestBody MemoRequest request) {
        MemoResponse memoResponse = memoService.createMemo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(memoResponse);
    }
}
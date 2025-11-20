package com.gdg.slbackend.service.mileage;

import com.gdg.slbackend.service.user.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 댓글 작성 시 마일리지 적립 등 User 연관 처리를 담당함.
 */
@Service
public class MileageService {

    private final UserService userService;

    public MileageService(UserService userService) {
        this.userService = userService;
    }

    /**
     * 질문 게시글에 대한 답변 댓글 작성 시 호출하여 mileage를 +1 증가시킴.
     */
    @Transactional
    public void earnForQuestionAnswer(Long commenterId) {
        userService.increaseMileage(commenterId, 1);
    }
}
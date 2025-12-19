package com.gdg.slbackend.service.user;

import com.gdg.slbackend.domain.user.User;
import com.gdg.slbackend.domain.user.UserRepository;
import com.gdg.slbackend.domain.user.UserRole;
import com.gdg.slbackend.global.exception.ErrorCode;
import com.gdg.slbackend.global.exception.GlobalException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UserFinder {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public User findByIdOrThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new GlobalException(ErrorCode.USER_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public String findUserNameByIdOrThrow(Long userId) {
        return findByIdOrThrow(userId).getNickname();
    }

    @Transactional(readOnly = true)
    public boolean isSystemAdmin(Long userId) {
        return userRepository.existsByIdAndRole(userId, UserRole.SYSTEM_ADMIN);
    }
}

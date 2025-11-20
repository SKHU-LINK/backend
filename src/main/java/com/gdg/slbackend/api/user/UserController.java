package com.gdg.slbackend.api.user;

import com.gdg.slbackend.api.user.dto.UserResponse;
import com.gdg.slbackend.global.response.ApiResponse;
import com.gdg.slbackend.service.user.UserService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    /**
     * User 도메인 관련 단일 조회 및 업데이트 기능을 담당함.
     * 로그인 정보 조회(/auth/me) 기능은 AuthController에서 구현 예정임.
     */

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/{userId}")
    public ApiResponse<UserResponse> getUser(@PathVariable Long userId) {
        return ApiResponse.success(userService.getUserById(userId));
    }

    @PostMapping("/me/nickname")
    public ApiResponse<Void> updateNickname(
            @RequestParam Long userId,
            @RequestParam String nickname
    ) {
        userService.updateNickname(userId, nickname);
        return ApiResponse.success();
    }
}

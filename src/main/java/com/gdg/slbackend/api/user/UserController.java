package com.gdg.slbackend.api.user;

import com.gdg.slbackend.api.user.dto.UserMileageResponse;
import com.gdg.slbackend.api.user.dto.UserResponse;
import com.gdg.slbackend.global.response.ApiResponse;
import com.gdg.slbackend.global.security.UserPrincipal;
import com.gdg.slbackend.service.user.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "사용자 조회 및 마일리지 관리 API")
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
    @Operation(summary = "사용자 단건 조회")
    public ApiResponse<UserResponse> getUser(@PathVariable Long userId) {
        return ApiResponse.success(userService.getUserById(userId));
    }

    @PostMapping("/me/nickname")
    @Operation(summary = "닉네임 변경")
    public ApiResponse<Void> updateNickname(
            @RequestParam Long userId,
            @AuthenticationPrincipal com.gdg.slbackend.global.security.UserPrincipal principal,
            @RequestParam String nickname
    ) {
        userService.updateNickname(principal.getId(), nickname);
        return ApiResponse.success();
    }

    /**
     * 현재 로그인한 사용자의 마일리지를 조회함.
     */
    @GetMapping("/me/mileage")
    @Operation(summary = "내 마일리지 조회")
    public ApiResponse<UserMileageResponse> getMyMileage(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        int mileage = userService.getMileage(principal.getId());
        return ApiResponse.success(new UserMileageResponse(mileage));
    }

    /**
     * 현재 로그인한 사용자의 마일리지를 사용(차감)함.
     * amount 만큼 차감하며, 부족할 경우 예외가 발생함.
     */
    @PostMapping("/me/mileage/use")
    @Operation(summary = "내 마일리지 사용")
    public ApiResponse<UserMileageResponse> useMyMileage(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam int amount
    ) {
        int mileage = userService.useMileage(principal.getId(), amount);
        return ApiResponse.success(new UserMileageResponse(mileage));
    }
}
package com.gdg.slbackend.api.community;

import com.gdg.slbackend.api.community.dto.CommunityRequest;
import com.gdg.slbackend.api.community.dto.CommunityResponse;
import com.gdg.slbackend.api.community.dto.CommunityUpdateAdminRequest;
import com.gdg.slbackend.global.response.ApiResponse;
import com.gdg.slbackend.global.security.UserPrincipal;
import com.gdg.slbackend.service.community.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/communities")
@Tag(name = "Community", description = "Communities of classes")
@SecurityRequirement(name = "bearerAuth")
public class CommunityController {
    private final CommunityService communityService;

    @PostMapping
    @Operation(
            summary = "Create the community",
            description = "Create the community by name, year and semester of classes"
    )
    public ResponseEntity<CommunityResponse> createCommunity(
            @RequestBody CommunityRequest communityRequest,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(communityService.createCommunity(communityRequest, principal));
    }

    @GetMapping("/{communityId}")
    @Operation(
            summary = "Read the community",
            description = "Read the community by community's id"
    )
    public ResponseEntity<CommunityResponse> getCommunity(
            @PathVariable Long communityId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(communityService.getCommunity(communityId, principal));
    }

    @GetMapping
    @Operation(
            summary = "Read the communities",
            description = "Read the communities by user's id"
    )
    public ResponseEntity<List<CommunityResponse>> getAllCommunity(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(
                communityService.getCommunityAll(principal)
        );
    }

    @PatchMapping("/{communityId}/admin")
    @Operation(
            summary = "Update the community admin",
            description = "Update the admin of the community"
    )
    public ResponseEntity<CommunityResponse> updateCommunityAdmin(
            @PathVariable Long communityId,
            @Valid @RequestBody CommunityUpdateAdminRequest updateAdminRequest,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(communityService.updateCommunityAdmin(communityId, principal, updateAdminRequest.getNewAdminUserId()));
    }

    @PatchMapping("/{communityId}/pinned")
    @Operation(
            summary = "Update the community pinned",
            description = "Update the pinned of the community"
    )
    public ResponseEntity<CommunityResponse> updateCommunityPinned(
            @PathVariable Long communityId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(communityService.updateCommunityPinned(communityId, principal));
    }

    @DeleteMapping("/{communityId}")
    @Operation(
            summary = "Delete the community",
            description = "Delete the community by community's id"
    )
    public ApiResponse<Void> deleteCommunity(
            @PathVariable Long communityId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        communityService.deleteCommunity(communityId, principal);
        return ApiResponse.success();
    }

    @GetMapping("/test")
    public ResponseEntity<String> test(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(
                principal == null ? "principal is null" : "principal id = " + principal.getId()
        );
    }
}

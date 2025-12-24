package com.gdg.slbackend.api.resource;

import com.gdg.slbackend.api.resource.dto.ResourceDownloadResponse;
import com.gdg.slbackend.api.resource.dto.ResourceRequest;
import com.gdg.slbackend.api.resource.dto.ResourceResponse;
import com.gdg.slbackend.global.security.UserPrincipal;
import com.gdg.slbackend.service.resource.ResourceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/communities")
@SecurityRequirement(name = "bearerAuth")
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping("/{communityId}/resources")
    public ResponseEntity<List<ResourceResponse>> getResources(
            @PathVariable Long communityId,
            @RequestParam(required = false) Long lastId
    ) {
        return ResponseEntity.ok(
                resourceService.getResources(communityId, lastId)
        );
    }

    @PostMapping(
            value = "/{communityId}/resources",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<ResourceResponse> createResource(
            @PathVariable Long communityId,
            @AuthenticationPrincipal UserPrincipal principal,
            @ModelAttribute @Valid ResourceRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resourceService.createResource(
                        communityId,
                        principal.getId(),
                        request
                ));
    }

    @GetMapping("/{resourceId}/download")
    public ResponseEntity<ResourceDownloadResponse> downloadResource(
            @PathVariable Long resourceId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ResourceDownloadResponse response = resourceService.downloadResource(resourceId, principal.getId());
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/resources/{resourceId}")
    public ResponseEntity<ResourceResponse> updateResource(
            @PathVariable Long resourceId,
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody @Valid ResourceRequest request
    ) {
        return ResponseEntity.ok(
                resourceService.updateResource(
                        resourceId, principal.getId(), request
                )
        );
    }

    @DeleteMapping("/resources/{resourceId}")
    public ResponseEntity<Void> deleteResource(
            @PathVariable Long resourceId,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        resourceService.deleteResource(resourceId, principal.getId());
        return ResponseEntity.noContent().build();
    }
}


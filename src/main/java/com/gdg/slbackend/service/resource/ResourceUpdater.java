package com.gdg.slbackend.service.resource;

import com.gdg.slbackend.domain.resource.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ResourceUpdater {

    @Transactional
    public void update(Resource resource, String title) {
        resource.updateTitle(title);
    }
}

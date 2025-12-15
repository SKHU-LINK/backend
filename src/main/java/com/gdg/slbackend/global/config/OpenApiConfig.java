package com.gdg.slbackend.global.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "SL Backend API",
                version = "v1",
                description = "익명 메모 기능을 포함한 SKHU Link 백엔드 API",
                contact = @Contact(name = "GDG",
                        email = "support@example.com")
        ),
        servers = {
                @Server(url = "http://localhost:8080", description = "로컬 환경"),
                @Server(url = "http://shku-link.duckdns.org", description = "배포 환경")
        }
)
public class OpenApiConfig {
}
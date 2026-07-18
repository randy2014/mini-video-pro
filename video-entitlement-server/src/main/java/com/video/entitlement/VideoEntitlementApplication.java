package com.video.entitlement;

import com.video.entitlement.module.admin.service.AdminService;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class VideoEntitlementApplication {
    public static void main(String[] args) {
        SpringApplication.run(VideoEntitlementApplication.class, args);
    }

    @Bean
    ApplicationRunner initDefaultAdmin(AdminService adminService) {
        return args -> adminService.initDefaultAdmin();
    }
}

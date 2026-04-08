package org.soipan.ilas.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "file.storage")
public class FileStorageProperties {
    private String provider = "local";
    private String uploadDir = "storage/exam-assessments";
    private Azure azure = new Azure();

    @Getter
    @Setter
    public static class Azure {
        private String connectionString;
        private String container = "exam-assessments";
    }
}

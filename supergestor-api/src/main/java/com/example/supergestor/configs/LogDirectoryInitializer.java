package com.example.supergestor.configs;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class LogDirectoryInitializer {

    private static final String LOG_DIR = "logs";

    @PostConstruct
    public void init() {
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}

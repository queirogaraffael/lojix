package com.example.lojix.service;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
@Primary
public class MockStorageService implements StorageService {
    @Override
    public String uploadFoto(UUID userId, MultipartFile file) {
        return "mock-key.jpg";
    }

    @Override
    public String gerarPresignedUrl(String objectKey) {
        return "https://mock.s3.url/" + objectKey;
    }

    @Override
    public void deletarFoto(String objectKey) {
    }
}

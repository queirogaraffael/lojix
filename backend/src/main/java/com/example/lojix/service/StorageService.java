package com.example.lojix.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

@Service
public interface StorageService {
    String uploadFoto(UUID usuarioId, MultipartFile file);
    String gerarPresignedUrl(String fotoKey);
    void deletarFoto(String fotoKey);
}

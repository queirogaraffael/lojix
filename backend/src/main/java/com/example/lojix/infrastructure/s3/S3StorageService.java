package com.example.lojix.infrastructure.s3;

import com.example.lojix.service.StorageService;
import com.example.lojix.common.exception.InvalidFileException;
import com.example.lojix.common.exception.StorageException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class S3StorageService implements StorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.presigned-url-expiration-minutes}")
    private long expirationMinutes;

    @Value("${aws.s3.max-file-size-mb}")
    private long maxFileSizeMb;

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of("image/jpeg", "image/png", "image/webp");

    public S3StorageService(S3Client s3Client, S3Presigner s3Presigner) {
        this.s3Client = s3Client;
        this.s3Presigner = s3Presigner;
    }

    @Override
    public String uploadFoto(UUID usuarioId, MultipartFile file) {
        validateFile(file);
        
        String extensao = resolverExtensao(file.getContentType());
        String key = "usuarios/" + usuarioId + "/foto-perfil" + extensao;

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));
            return key;
        } catch (Exception e) {
            log.error("Falha ao fazer upload para o S3. bucket={}, key={}, causa={}", bucketName, key, e.getMessage(), e);
            throw new StorageException("Erro ao fazer upload da foto para o S3: " + e.getMessage(), e);
        }
    }

    @Override
    public String gerarPresignedUrl(String fotoKey) {
        if (fotoKey == null || fotoKey.trim().isEmpty()) {
            return null;
        }

        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fotoKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(expirationMinutes))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (Exception e) {
            throw new StorageException("Erro ao gerar URL da foto no S3: " + e.getMessage(), e);
        }
    }

    @Override
    public void deletarFoto(String fotoKey) {
        if (fotoKey == null || fotoKey.trim().isEmpty()) {
            return;
        }
        
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fotoKey)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            throw new StorageException("Erro ao deletar foto do S3: " + e.getMessage(), e);
        }
    }

    private String resolverExtensao(String contentType) {
        return switch (contentType) {
            case "image/png"  -> ".png";
            case "image/webp" -> ".webp";
            default           -> ".jpg";
        };
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("O arquivo de foto não pode ser vazio.");
        }
        
        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new InvalidFileException("Formato de imagem inválido. Use JPEG, PNG ou WEBP.");
        }

        long maxBytes = maxFileSizeMb * 1024 * 1024;
        if (file.getSize() > maxBytes) {
            throw new InvalidFileException("O tamanho da imagem excede o limite de " + maxFileSizeMb + "MB.");
        }

        try {
            validarMagicBytes(file);
        } catch (IOException e) {
            throw new StorageException("Erro ao ler o arquivo para validação.", e);
        }
    }

    private void validarMagicBytes(MultipartFile file) throws IOException {
        byte[] header = file.getBytes();
        if (isJpeg(header) || isPng(header) || isWebp(header)) return;
        throw new InvalidFileException("Conteúdo do arquivo não corresponde a uma imagem válida.");
    }

    private boolean isJpeg(byte[] header) {
        return header.length >= 3 && 
               (header[0] & 0xFF) == 0xFF && 
               (header[1] & 0xFF) == 0xD8 && 
               (header[2] & 0xFF) == 0xFF;
    }

    private boolean isPng(byte[] header) {
        return header.length >= 4 && 
               (header[0] & 0xFF) == 0x89 && 
               (header[1] & 0xFF) == 0x50 && 
               (header[2] & 0xFF) == 0x4E && 
               (header[3] & 0xFF) == 0x47;
    }

    private boolean isWebp(byte[] header) {
        return header.length >= 12 && 
               (header[0] & 0xFF) == 0x52 && 
               (header[1] & 0xFF) == 0x49 && 
               (header[2] & 0xFF) == 0x46 && 
               (header[3] & 0xFF) == 0x46 && 
               (header[8] & 0xFF) == 0x57 && 
               (header[9] & 0xFF) == 0x45 && 
               (header[10] & 0xFF) == 0x42 && 
               (header[11] & 0xFF) == 0x50;
    }
}

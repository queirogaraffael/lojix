package com.example.lojix.infrastructure.s3;

import com.example.lojix.common.exception.InvalidFileException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3StorageServiceTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    private S3StorageService s3StorageService;

    private final String bucketName = "test-bucket";

    @BeforeEach
    void setup() {
        s3StorageService = new S3StorageService(s3Client, s3Presigner);
        org.springframework.test.util.ReflectionTestUtils.setField(s3StorageService, "bucketName", bucketName);
        org.springframework.test.util.ReflectionTestUtils.setField(s3StorageService, "maxFileSizeMb", 5L);
    }

    @Test
    void uploadFoto_Success() {
        UUID userId = UUID.randomUUID();
        byte[] pngMagicBytes = new byte[] { (byte) 0x89, 0x50, 0x4E, 0x47, 0x00 };
        MockMultipartFile file = new MockMultipartFile("foto", "foto.png", "image/png", pngMagicBytes);

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        String key = s3StorageService.uploadFoto(userId, file);

        assertNotNull(key);
        assertTrue(key.startsWith("usuarios/" + userId + "/foto-perfil"));
        assertTrue(key.endsWith(".png"));

        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
        PutObjectRequest capturedRequest = requestCaptor.getValue();

        assertEquals(bucketName, capturedRequest.bucket());
        assertEquals(key, capturedRequest.key());
        assertEquals("image/png", capturedRequest.contentType());
    }

    @Test
    void uploadFoto_InvalidType() {
        UUID userId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("foto", "doc.pdf", "application/pdf", "pdf content".getBytes());

        assertThrows(InvalidFileException.class, () -> s3StorageService.uploadFoto(userId, file));
        verify(s3Client, never()).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void gerarPresignedUrl_Success() throws Exception {
        String key = "test-key.jpg";
        String expectedUrl = "https://s3.amazonaws.com/test-bucket/test-key.jpg";

        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(new URL(expectedUrl));

        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class)))
                .thenReturn(presignedRequest);

        String result = s3StorageService.gerarPresignedUrl(key);

        assertEquals(expectedUrl, result);
    }

    @Test
    void deletarFoto_Success() {
        String key = "test-key.jpg";

        s3StorageService.deletarFoto(key);

        ArgumentCaptor<DeleteObjectRequest> requestCaptor = ArgumentCaptor.forClass(DeleteObjectRequest.class);
        verify(s3Client).deleteObject(requestCaptor.capture());

        assertEquals(bucketName, requestCaptor.getValue().bucket());
        assertEquals(key, requestCaptor.getValue().key());
    }
}

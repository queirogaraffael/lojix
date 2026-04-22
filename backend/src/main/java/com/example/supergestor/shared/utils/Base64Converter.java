package com.example.supergestor.shared.utils;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
public class Base64Converter {

    @Named("toBytes")
    public byte[] toBytes(String base64String) {
        if (base64String == null || base64String.isEmpty()) return null;
        return Base64.getDecoder().decode(base64String);
    }

    @Named("toBase64")
    public String toBase64(byte[] bytes) {
        if (bytes == null) return null;
        return Base64.getEncoder().encodeToString(bytes);
    }
}

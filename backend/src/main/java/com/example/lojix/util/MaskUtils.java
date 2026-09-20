package com.example.lojix.util;

public class MaskUtils {

    public static String maskCpf(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return "***.***.***-**";
        }

        String cleanCpf = cpf.replaceAll("\\D", "");

        if (cleanCpf.length() != 11) {
            return "***.***.***-**";
        }

        return cleanCpf.substring(0, 3) + ".***.***-" + cleanCpf.substring(9, 11);
    }
}

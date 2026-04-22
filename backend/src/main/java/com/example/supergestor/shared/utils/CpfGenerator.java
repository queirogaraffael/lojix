package com.example.supergestor.shared.utils;

import java.util.Random;

public class CpfGenerator {

    public static String generate() {

        Random random = new Random();

        int[] cpf = new int[11];

        for (int i = 0; i < 9; i++) {
            cpf[i] = random.nextInt(10);
        }

        cpf[9] = calculateDigit(cpf, 9);
        cpf[10] = calculateDigit(cpf, 10);

        StringBuilder result = new StringBuilder();

        for (int i = 0; i < 11; i++) {
            result.append(cpf[i]);
        }

        return result.toString();
    }

    private static int calculateDigit(int[] cpf, int length) {

        int sum = 0;
        int weight = length + 1;

        for (int i = 0; i < length; i++) {
            sum += cpf[i] * weight--;
        }

        int digit = 11 - (sum % 11);

        return digit >= 10 ? 0 : digit;
    }
}
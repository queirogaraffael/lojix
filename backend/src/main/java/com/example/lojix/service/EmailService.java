package com.example.lojix.service;

import com.example.lojix.dto.email.ClienteCadastradoEmailDTO;
import org.springframework.stereotype.Service;

@Service
public interface EmailService {
    void sendWelcomeEmailCliente(ClienteCadastradoEmailDTO dto);
}


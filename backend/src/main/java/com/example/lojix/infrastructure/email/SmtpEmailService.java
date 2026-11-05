package com.example.lojix.infrastructure.email;

import com.example.lojix.dto.email.ClienteCadastradoEmailDTO;
import com.example.lojix.service.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Slf4j
@Service
public class SmtpEmailService implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@lojix.com}")
    private String remetente;

    @Value("${app.mail.enabled:false}")
    private boolean mailEnabled;

    public SmtpEmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    @Override
    public void sendWelcomeEmailCliente(ClienteCadastradoEmailDTO dto) {
        log.info("Processando notificação de boas-vindas para o cliente: {}", dto.getEmail());

        if (!mailEnabled || mailSender == null) {
            log.info("[MOCK EMAIL] Envio de e-mail desativado ou servidor SMTP não configurado. Notificação simulada com sucesso para: {}", dto.getEmail());
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(remetente, "Lojix");
            helper.setTo(dto.getEmail());
            helper.setSubject("Bem-vindo à Lojix! Seu cadastro foi realizado com sucesso");
            helper.setText(buildHtmlTemplate(dto), true);

            mailSender.send(message);
            log.info("E-mail de boas-vindas enviado com sucesso para: {}", dto.getEmail());

        } catch (Exception ex) {
            log.error("Falha ao enviar e-mail de boas-vindas para o cliente {}: {}", dto.getEmail(), ex.getMessage());
        }
    }

    private String buildHtmlTemplate(ClienteCadastradoEmailDTO dto) {
        String dataFormatada = dto.getMembroDesde() != null
                ? dto.getMembroDesde().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                : "-";

        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Bem-vindo à Lojix</title>
              <style>
                body { margin: 0; padding: 0; background-color: #f0f2f5; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; color: #333333; }
                .email-container { max-width: 600px; margin: 30px auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 12px rgba(0,0,0,0.08); }
                .header { background-color: #007bff; padding: 35px 20px; text-align: center; color: #ffffff; }
                .header h1 { margin: 0; font-size: 30px; font-weight: 700; letter-spacing: 1px; }
                .header p { margin: 8px 0 0; font-size: 15px; opacity: 0.95; }
                .content { padding: 35px 30px; line-height: 1.6; }
                .welcome-title { font-size: 20px; font-weight: 600; color: #007bff; margin-bottom: 15px; }
                .lead-text { font-size: 15px; color: #444444; margin-bottom: 25px; }
                .info-card { background-color: #f8f9fa; border: 1px solid #e9ecef; border-left: 5px solid #007bff; border-radius: 6px; padding: 20px; margin: 25px 0; }
                .info-item { margin-bottom: 12px; font-size: 14px; }
                .info-item:last-child { margin-bottom: 0; }
                .info-label { font-weight: 600; color: #555555; width: 140px; display: inline-block; }
                .info-value { color: #222222; font-weight: 500; }
                .callout { background-color: #e7f3ff; border: 1px solid #b8daff; border-radius: 6px; padding: 15px 20px; font-size: 14px; color: #004085; margin-top: 25px; }
                .footer { background-color: #f8f9fa; border-top: 1px solid #eeeeee; padding: 25px 20px; text-align: center; font-size: 12px; color: #777777; }
                .footer p { margin: 4px 0; }
              </style>
            </head>
            <body>
              <div class="email-container">
                <div class="header">
                  <h1>Lojix</h1>
                  <p>Clube de Clientes & Vantagens</p>
                </div>
                <div class="content">
                  <div class="welcome-title">Olá, %s!</div>
                  <p class="lead-text">
                    É um imenso prazer ter você com a gente! Seu cadastro no sistema da <strong>Lojix</strong> foi realizado com sucesso pela nossa equipe de atendimento.
                  </p>
                  
                  <div class="info-card">
                    <div class="info-item"><span class="info-label">Nome:</span><span class="info-value">%s</span></div>
                    <div class="info-item"><span class="info-label">CPF (Protegido):</span><span class="info-value">%s</span></div>
                    <div class="info-item"><span class="info-label">Membro Desde:</span><span class="info-value">%s</span></div>
                  </div>

                  <div class="callout">
                    🎉 <strong>Vantagem exclusiva:</strong> Ao passar no caixa de qualquer unidade, basta informar o seu CPF para aproveitar automaticamente nossas promoções e descontos especiais da semana!
                  </div>
                </div>
                <div class="footer">
                  <p><strong>Lojix Mercado & Varejo</strong></p>
                  <p>Este é um comunicado oficial do sistema. Por favor, não responda a esta mensagem.</p>
                </div>
              </div>
            </body>
            </html>
            """.formatted(dto.getNomeCliente(), dto.getNomeCliente(), dto.getCpf(), dataFormatada);
    }
}


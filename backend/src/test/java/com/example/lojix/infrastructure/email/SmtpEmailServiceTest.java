package com.example.lojix.infrastructure.email;

import com.example.lojix.dto.email.ClienteCadastradoEmailDTO;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class SmtpEmailServiceTest {

    private StubJavaMailSender mailSender;
    private SmtpEmailService smtpEmailService;

    private static class StubJavaMailSender extends JavaMailSenderImpl {
        int sendCount = 0;
        boolean shouldFail = false;
        MimeMessage lastSentMessage;

        @Override
        public void send(MimeMessage mimeMessage) throws MailException {
            sendCount++;
            if (shouldFail) {
                throw new MailSendException("Falha de conexão SMTP");
            }
            this.lastSentMessage = mimeMessage;
        }
    }

    @BeforeEach
    void setUp() {
        mailSender = new StubJavaMailSender();
        smtpEmailService = new SmtpEmailService(mailSender);
        ReflectionTestUtils.setField(smtpEmailService, "remetente", "noreply@lojix.com");
        ReflectionTestUtils.setField(smtpEmailService, "mailEnabled", true);
    }

    @Test
    @DisplayName("Deve montar template HTML e enviar e-mail de boas-vindas quando servico estiver habilitado")
    void sendWelcomeEmailCliente_Success() throws Exception {
        ClienteCadastradoEmailDTO dto = ClienteCadastradoEmailDTO.builder()
                .nomeCliente("Maria Oliveira")
                .email("maria@cliente.com")
                .cpf("***.456.789-**")
                .membroDesde(LocalDate.of(2026, 11, 13))
                .build();

        smtpEmailService.sendWelcomeEmailCliente(dto);

        assertThat(mailSender.sendCount).isEqualTo(1);
        assertThat(mailSender.lastSentMessage).isNotNull();
        assertThat(mailSender.lastSentMessage.getSubject()).contains("Bem-vindo à Lojix");
        assertThat(mailSender.lastSentMessage.getAllRecipients()[0].toString()).isEqualTo("maria@cliente.com");
    }

    @Test
    @DisplayName("Deve simular envio sem chamar JavaMailSender quando mailEnabled for false ou mailSender for nulo")
    void sendWelcomeEmailCliente_DisabledOrNullSender() {
        ReflectionTestUtils.setField(smtpEmailService, "mailEnabled", false);

        ClienteCadastradoEmailDTO dto = ClienteCadastradoEmailDTO.builder()
                .nomeCliente("João Silva")
                .email("joao@cliente.com")
                .cpf("***.123.456-**")
                .membroDesde(LocalDate.of(2026, 11, 13))
                .build();

        smtpEmailService.sendWelcomeEmailCliente(dto);
        assertThat(mailSender.sendCount).isZero();

        SmtpEmailService serviceWithoutSender = new SmtpEmailService(null);
        ReflectionTestUtils.setField(serviceWithoutSender, "mailEnabled", true);
        assertDoesNotThrow(() -> serviceWithoutSender.sendWelcomeEmailCliente(dto));
    }

    @Test
    @DisplayName("Deve absorver falha de rede SMTP sem propagar excecao quando envio falhar")
    void sendWelcomeEmailCliente_WhenSendFails_ShouldCatchExceptionGracefully() {
        mailSender.shouldFail = true;

        ClienteCadastradoEmailDTO dto = ClienteCadastradoEmailDTO.builder()
                .nomeCliente("Carlos Souza")
                .email("carlos@cliente.com")
                .cpf("***.987.654-**")
                .membroDesde(null)
                .build();

        assertDoesNotThrow(() -> smtpEmailService.sendWelcomeEmailCliente(dto));
        assertThat(mailSender.sendCount).isEqualTo(1);
    }
}

package integrador2.helpdesk.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mail;

    public void enviarBoasVindas(String destino) throws MessagingException {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(destino);
        msg.setSubject("Bem‑vindo ao HelpDesk");
        msg.setText("Sua conta foi criada com sucesso!\nAcesse o sistema e abra seus chamados.");
        mail.send(msg);
    }

    public void enviarCodigo(String destino, String codigo) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(destino);
        msg.setSubject("Seu código de verificação");
        msg.setText("Digite o código a seguir para ativar a conta: " + codigo);
        mail.send(msg);
    }

}

package integrador2.helpdesk;

import integrador2.helpdesk.service.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService service;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void enviarBoasVindas_buildsCorrectMessage() throws Exception {
        service.enviarBoasVindas("dest@e");
        ArgumentCaptor<SimpleMailMessage> cap = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(cap.capture());

        SimpleMailMessage msg = cap.getValue();
        assertArrayEquals(new String[]{"dest@e"}, msg.getTo());
        assertEquals("Bem‑vindo ao HelpDesk", msg.getSubject());
        assertTrue(msg.getText().contains("Sua conta foi criada"));
    }

    @Test
    void enviarCodigo_includesCodeInBody() {
        service.enviarCodigo("u@e", "123456");
        ArgumentCaptor<SimpleMailMessage> cap = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(cap.capture());

        assertTrue(cap.getValue().getText().contains("123456"));
    }

    @Test
    void enviarMensagem_setsSubjectAndBody() {
        service.enviarMensagem("a@b", "T", "C");
        ArgumentCaptor<SimpleMailMessage> cap = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(cap.capture());
        assertEquals("T", cap.getValue().getSubject());
        assertEquals("C", cap.getValue().getText());
    }
}

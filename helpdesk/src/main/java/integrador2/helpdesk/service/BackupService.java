package integrador2.helpdesk.service;

import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
public class BackupService {

    private final JavaMailSender mailSender;

    @Value("${DB_URL}")
    private String dbUrl;
    @Value("${DB_USER}")
    private String dbUser;
    @Value("${DB_PASS}")
    private String dbPass;

    @Value("${BACKUP_MAIL_TO}")
    private String backupMailTo;

    private String pgDumpCmd;

    public BackupService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @PostConstruct
    public void init() {
        this.pgDumpCmd = "pg_dump";
    }

    /** Roda todo dia à meia-noite */
    @Scheduled(cron = "0 0 0 * * *")
    public void performBackup() {
        String ts = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        Path dump = Paths.get(System.getProperty("java.io.tmpdir"),
                "backup_" + ts + ".dump");

        try {
            runPgDump(dump);
            sendBackupByEmail(dump);
        } catch (Exception e) {
            log.error("Falha no backup automático:", e);
        } finally {
            try { Files.deleteIfExists(dump); }
            catch (IOException e) { log.warn("Não consegui apagar dump:", e); }
        }
    }

    private void runPgDump(Path dumpPath) throws IOException, InterruptedException {
        // extrai host:porta/dbname do JDBC URL
        String url = dbUrl.replaceFirst("^jdbc:postgresql://", "");
        String[] parts = url.split("/", 2);
        String hostPort = parts[0], dbName = parts[1];
        String[] hp = hostPort.split(":", 2);
        String host = hp[0], port = hp.length>1?hp[1]:"5432";

        List<String> cmd = List.of(
                pgDumpCmd,
                "-h", host,
                "-p", port,
                "-U", dbUser,
                "-F", "c",
                "-b",
                "-f", dumpPath.toString(),
                dbName
        );

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.environment().put("PGPASSWORD", dbPass);
        pb.inheritIO();

        log.info("Iniciando pg_dump...");
        Process p = pb.start();
        if (p.waitFor() != 0) {
            throw new IOException("pg_dump retornou código " + p.exitValue());
        }
        log.info("pg_dump concluído: {}", dumpPath);
    }

    private void sendBackupByEmail(Path dumpPath) throws MessagingException {
        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
        helper.setTo(backupMailTo.split("\\s*,\\s*"));
        helper.setSubject("Backup automático — " +
                LocalDateTime.now().format(DateTimeFormatter.ISO_DATE));
        helper.setText("Segue em anexo o dump do banco gerado em " +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));

        FileSystemResource file = new FileSystemResource(dumpPath.toFile());
        helper.addAttachment(dumpPath.getFileName().toString(), file);

        mailSender.send(msg);
        log.info("Backup enviado para {}", backupMailTo);
    }
}

package br.com.findpark.email;

import br.com.findpark.exceptions.email.EnvioEmailException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Service
public class EmailService {

    @Value("${spring.mail.username}")
    private String sender;

    @Value("${api.web.domain}")
    private String dominio;

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    public void enviarEmail(String subject, String receiver, EmailTemplate template, Context context, Optional<MultipartFile> file) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setFrom(sender);
            helper.setSubject(subject);
            helper.setTo(receiver);

            context.setVariable("receiver", receiver);
            context.setVariable("subject", subject);
            context.setVariable("domain", dominio);
            String htmlBody = templateEngine.process(template.getName(), context);
            helper.setText(htmlBody, true);

            if (file.isPresent()) {
                MultipartFile attachment = file.get();
                String fileName = attachment.getOriginalFilename();
                File tempFile = convertMultipartFileToFile(attachment);

                assert fileName != null;
                helper.addAttachment(fileName, tempFile);
                tempFile.deleteOnExit();
            }

            emailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new EnvioEmailException("Erro ao enviar o email!");
        }
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        Path tempFilePath = Files.createTempFile("attachment_", ".tmp");
        File tempFile = tempFilePath.toFile();
        file.transferTo(tempFile);
        return tempFile;
    }
}

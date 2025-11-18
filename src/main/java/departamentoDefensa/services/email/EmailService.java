package departamentoDefensa.services.email;

import com.sendgrid.*;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class EmailService {

    //@Value("${SENDGRID_API_KEY}")
    private String sendGridApiKey="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";


    public void sendPasswordEmail(String to, String generatedPassword) {
        try {

            // ---- Cargar logo como Base64 para SendGrid ----
            ClassPathResource logo = new ClassPathResource("logo.png");
            byte[] logoBytes = logo.getInputStream().readAllBytes();
            String logoBase64 = Base64.getEncoder().encodeToString(logoBytes);

            // ---- Construir HTML (igual que el tuyo) ----
            String html = """
        <div style="font-family: Arial, sans-serif; background: #0d1b2a; padding: 20px;">
            <div style="max-width: 600px; margin: 0 auto; background: #ffffff;
                        border-radius: 12px; overflow: hidden; box-shadow: 0 4px 18px rgba(0,0,0,0.25);">
    
                <div style="background: #0d1b2a; padding: 25px; text-align: center;">
                    <img src="cid:logoDefensa">
                    <h2 style="color: #ffffff; margin: 0; font-weight: 600; font-size: 22px;">
                        Departamento de Defensa Nacional
                    </h2>
                    <p style="color: #d1d5db; margin-top: 5px; font-size: 14px;">
                        Fuerza Pública de Colombia
                    </p>
                </div>
    
                <div style="padding: 30px; background: #f8fafc;">
                    <p style="font-size: 16px; color: #1a1a1a;">
                        Estimado usuario,
                    </p>
    
                    <p style="font-size: 15px; color: #1a1a1a;">
                        Su registro en la plataforma del <strong>Departamento de Defensa</strong> ha sido validado exitosamente.
                    </p>
    
                    <p style="font-size: 16px; margin-bottom: 10px; color: #1a1a1a;">
                        Esta es su contraseña temporal:
                    </p>
    
                    <div style="
                        background: #1d4ed8;
                        color: white;
                        padding: 15px;
                        border-radius: 8px;
                        text-align: center;
                        font-size: 22px;
                        font-weight: bold;
                        letter-spacing: 2px;
                        margin-bottom: 25px;
                        box-shadow: 0 3px 8px rgba(0,0,0,0.2);
                    ">
                        """ + generatedPassword + """
                    </div>
    
                    <p style="font-size: 15px; color: #1a1a1a;">
                        Por motivos de seguridad, por favor cambie esta contraseña después de iniciar sesión.
                    </p>
    
                    <p style="font-size: 14px; color: #555; margin-top: 30px; text-align: center;">
                        Saludos,<br>
                        <strong>Departamento de Defensa Nacional</strong>
                    </p>
                </div>
            </div>
        </div>
        """;

            // ---- Construir correo SendGrid ----
            Email from = new Email("colombiafuerzapublica@gmail.com");
            Email toEmail = new Email(to);
            Content content = new Content("text/html", html);
            Mail mail = new Mail(from, "Registro Exitoso - Departamento de Defensa", toEmail, content);

            // ---- Adjuntar el logo como inline ----
            Attachments attachment = new Attachments();
            attachment.setContent(logoBase64);
            attachment.setType("image/png");
            attachment.setFilename("logo.png");
            attachment.setDisposition("inline");
            attachment.setContentId("logoDefensa");

            mail.addAttachments(attachment);

            // ---- Enviar ----
            SendGrid sg = new SendGrid(sendGridApiKey);
            Request request = new Request();

            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());

            sg.api(request);

        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo: " + e.getMessage());
        }
    }
}

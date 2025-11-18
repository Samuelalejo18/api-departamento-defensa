package departamentoDefensa.services.email;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendPasswordEmail(String to, String generatedPassword) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("colombiafuerzapublica@gmail.com");
            helper.setTo(to);
            helper.setSubject("Registro Exitoso - Departamento de Defensa");

            String html = """
    <div style="font-family: Arial, sans-serif; background: #0d1b2a; padding: 20px;">
        <div style="max-width: 600px; margin: 0 auto; background: #ffffff;
                    border-radius: 12px; overflow: hidden; box-shadow: 0 4px 18px rgba(0,0,0,0.25);">

            <!-- ENCABEZADO -->
            <div style="background: #0d1b2a; padding: 25px; text-align: center;">
                <img src="cid:logoDefensa" style="width: 120px; margin-bottom: 10px;">
                <h2 style="color: #ffffff; margin: 0; font-weight: 600; font-size: 22px;">
                    Departamento de Defensa Nacional
                </h2>
                <p style="color: #d1d5db; margin-top: 5px; font-size: 14px;">
                    Fuerza Pública de Colombia
                </p>
            </div>

            <!-- CONTENIDO -->
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

                <!-- BLOQUE DE CONTRASEÑA ESTILO BOTÓN AZUL -->
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

                <!-- PIE DE PÁGINA -->
                <p style="font-size: 14px; color: #555; margin-top: 30px; text-align: center;">
                    Saludos,<br>
                    <strong>Departamento de Defensa Nacional</strong>
                </p>
            </div>
        </div>
    </div>
    """;




            helper.setText(html, true);

            // ✔ Cargar imagen correctamente desde resources
            ClassPathResource logo = new ClassPathResource("logo.png");

            // ✔ Asociar el logo al CID
            helper.addInline("logoDefensa", logo);

            mailSender.send(mimeMessage);

        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo: " + e.getMessage());
        }
    }
}

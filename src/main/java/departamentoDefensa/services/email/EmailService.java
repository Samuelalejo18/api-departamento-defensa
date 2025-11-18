package departamentoDefensa.services.email;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

@Service
public class EmailService {

    private final String apiKey = "ecb0247e842267efb397c43cf3989caa";
    private final String secretKey = "dc151f225713fbe16de35529d26431c2";

    public void sendPasswordEmail(String to, String generatedPassword) {
        try {

            // ---------------------------
            //  CARGAR LOGO COMO BASE64
            // ---------------------------
            ClassPathResource logo = new ClassPathResource("logo.png");
            byte[] logoBytes = logo.getInputStream().readAllBytes();
            String logoBase64 = Base64.getEncoder().encodeToString(logoBytes);

            // ---------------------------
            //  HTML EXACTO COMO EL TUYO
            // ---------------------------
            String html = """
            <div style="font-family: Arial, sans-serif; background: #0d1b2a; padding: 20px;">
                <div style="max-width: 600px; margin: 0 auto; background: #ffffff;
                            border-radius: 12px; overflow: hidden; box-shadow: 0 4px 18px rgba(0,0,0,0.25);">

                    <div style="background: #0d1b2a; padding: 25px; text-align: center;">
                        <img src="cid:logoDefensa" style="width: 120px; margin-bottom: 10px;">
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

            // ---------------------------
            // BODY JSON MAILJET
            // ---------------------------
            JSONObject message = new JSONObject()
                    .put("From", new JSONObject()
                            .put("Email", "colombiafuerzapublica@gmail.com")
                            .put("Name", "Departamento de Defensa Nacional"))
                    .put("To", new JSONArray()
                            .put(new JSONObject()
                                    .put("Email", to)
                                    .put("Name", to)))
                    .put("Subject", "Registro Exitoso - Departamento de Defensa")
                    .put("HTMLPart", html)
                    .put("InlineAttachments", new JSONArray()
                            .put(new JSONObject()
                                    .put("ContentType", "image/png")
                                    .put("Filename", "logo.png")
                                    .put("ContentID", "logoDefensa")
                                    .put("Base64Content", logoBase64)
                            )
                    );

            JSONObject body = new JSONObject()
                    .put("Messages", new JSONArray().put(message));

            // ---------------------------
            //  PETICIÓN HTTP A MAILJET
            // ---------------------------
            URL url = new URL("https://api.mailjet.com/v3.1/send");
            HttpURLConnection con = (HttpURLConnection) url.openConnection();

            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");

            String auth = apiKey + ":" + secretKey;
            String encodedAuth = Base64.getEncoder().encodeToString(auth.getBytes());
            con.setRequestProperty("Authorization", "Basic " + encodedAuth);

            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                os.write(body.toString().getBytes());
            }

            int responseCode = con.getResponseCode();
            if (responseCode != 200) {
                throw new RuntimeException("Error enviando correo. HTTP code: " + responseCode);
            }

        } catch (Exception e) {
            throw new RuntimeException("Error enviando correo: " + e.getMessage(), e);
        }
    }
}

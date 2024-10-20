package co.edu.uniquindio.CRUD.servicios.implementaciones;

import co.edu.uniquindio.CRUD.dtos.general.EmailDTO;
import co.edu.uniquindio.CRUD.excepciones.LimiteEnvioAlcanzadoException;
import co.edu.uniquindio.CRUD.excepciones.ServidorCorreoNoDisponibleException;
import co.edu.uniquindio.CRUD.excepciones.CorreoInvalidoException;
import co.edu.uniquindio.CRUD.servicios.interfaces.EmailService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;

    @Override
    public void enviarEmail(EmailDTO emailDTO) throws Exception {
        if (!isValidEmail(emailDTO.para())) {
            throw new CorreoInvalidoException("El formato del correo electrónico es inválido");
        }

        try {
            MimeMessage mensaje = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje);
            helper.setSubject(emailDTO.asunto());
            helper.setText(emailDTO.mensaje(), true);
            helper.setTo(emailDTO.para());
            helper.setFrom("no_reply@dominio.com");
            javaMailSender.send(mensaje);
        } catch (MailAuthenticationException e) {
            throw new ServidorCorreoNoDisponibleException("No se pudo autenticar con el servidor de correo");
        } catch (MailSendException e) {
            if (e.getMessage().contains("Sending rate exceeded")) {
                throw new LimiteEnvioAlcanzadoException("Se ha alcanzado el límite de envío de correos");
            }
            throw new ServidorCorreoNoDisponibleException("No se pudo enviar el correo");
        } catch (Exception e) {
            throw new Exception("Error al enviar el correo: " + e.getMessage());
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}

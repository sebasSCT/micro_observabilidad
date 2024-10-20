package co.edu.uniquindio.talleruno.services.implementacion;

import co.edu.uniquindio.talleruno.services.interfaces.AutenticacionServicio;
import co.edu.uniquindio.talleruno.utils.JWTUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AutenticacionServicioImpl implements AutenticacionServicio {

    private final JWTUtils jwtUtils;

    @Override
    public String autenticacion(String nombre) throws Exception {

        return jwtUtils.generarToken(nombre);
    }
}

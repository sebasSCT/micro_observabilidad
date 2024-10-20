package co.edu.uniquindio.CRUD.servicios.interfaces;

import co.edu.uniquindio.CRUD.dtos.general.LoginDTO;
import co.edu.uniquindio.CRUD.dtos.autenticacionJwt.TokenDTO;

public interface AutenticacionService {

    TokenDTO login(LoginDTO loginDTO) throws Exception;
}

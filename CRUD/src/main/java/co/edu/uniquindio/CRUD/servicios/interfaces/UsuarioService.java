package co.edu.uniquindio.CRUD.servicios.interfaces;

import co.edu.uniquindio.CRUD.dtos.general.CambioPasswordDTO;
import co.edu.uniquindio.CRUD.dtos.usuario.ActualizarUsuarioDTO;
import co.edu.uniquindio.CRUD.dtos.usuario.DetalleUsuarioDTO;
import co.edu.uniquindio.CRUD.dtos.usuario.RegistroUsuarioDTO;

public interface UsuarioService{

    String registrarUsuario(RegistroUsuarioDTO usuario)throws Exception;

    void actualizarUsuario(String idUsuario, ActualizarUsuarioDTO usuario, String idtoken)throws Exception;

    DetalleUsuarioDTO obtenerUsuario(String idCuenta, String idtoken) throws Exception;

    void eliminarUsuario(String idCuenta, String idtoken)throws Exception;

    void cambiarPassword(CambioPasswordDTO cambioPasswordDTO, String idUsuario) throws Exception;

}


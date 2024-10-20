package co.edu.uniquindio.CRUD.servicios.interfaces;

import co.edu.uniquindio.CRUD.dtos.usuario.ItemUsuarioDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GeneralService {

    Page<ItemUsuarioDTO> listarUsuarios(Pageable pageable) throws Exception;

    void enviarLinkRecuperacion(String email) throws Exception;
}

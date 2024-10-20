package co.edu.uniquindio.CRUD.servicios.interfaces;


import co.edu.uniquindio.CRUD.dtos.general.EmailDTO;
import co.edu.uniquindio.CRUD.excepciones.ServidorCorreoNoDisponibleException;

public interface EmailService {

    void enviarEmail(EmailDTO emailDTO) throws Exception, ServidorCorreoNoDisponibleException;
}
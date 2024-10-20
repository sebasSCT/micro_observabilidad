package co.edu.uniquindio.CRUD.controladores;

import co.edu.uniquindio.CRUD.dtos.general.MensajeDTO;
import co.edu.uniquindio.CRUD.dtos.usuario.ItemUsuarioDTO;
import co.edu.uniquindio.CRUD.dtos.usuario.PageItemUsuarioDTO;
import co.edu.uniquindio.CRUD.excepciones.*;
import co.edu.uniquindio.CRUD.servicios.interfaces.GeneralService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Pageable;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/general")
@Tag(name = "General", description = "Controlador para operaciones generales")
public class GeneralController {

    private final GeneralService generalService;

    /**
     Respuestas informativas (100-199):

     100 Continue
     101 Switching Protocols

     Respuestas satisfactorias (200-299):

     200 OK
     201 Created
     204 No Content

     Redirecciones (300-399):

     301 Moved Permanently
     302 Found
     304 Not Modified

     Errores del cliente (400-499):

     400 Bad Request
     401 Unauthorized
     403 Forbidden
     404 Not Found
     405 Method Not Allowed

     Errores del servidor (500-599):

     500 Internal Server Error
     501 Not Implemented
     503 Service Unavailable

     Pageable:

     pageNumber: 0 (indica que es la primera página)
     pageSize: 2 (muestra 2 elementos por página)
     sort: Información sobre el ordenamiento (en este caso, no se aplica ningún orden)
     offset: 0 (el desplazamiento desde el inicio de los datos)
     paged: true (indica que la respuesta está paginada)
     unpaged: false (lo contrario de paged)

     Información general de la paginación:

     last: true (indica que es la última página)
     totalElements: 2 (total de elementos en toda la colección)
     totalPages: 1 (número total de páginas)
     size: 2 (tamaño de la página actual)
     number: 0 (número de la página actual, empezando desde 0)
     first: true (indica que es la primera página)
     numberOfElements: 2 (número de elementos en la página actual)
     empty: false (indica que la página no está vacía)

     Sort:

     empty: true
     sorted: false
     unsorted: true
     (Estos campos indican que no se ha aplicado ningún ordenamiento a los resultados)

     */

    @Operation(summary = "Listar todos los usuarios",
            description = "Obtiene una lista paginada de los usuarios registrados en el sistema")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios obtenida exitosamente",
            content = @Content(schema = @Schema(implementation = PageItemUsuarioDTO.class)))
    @ApiResponse(responseCode = "404", description = "No se encontraron usuarios",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @GetMapping("/usuarios")
    public ResponseEntity<?> listarUsuarios(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "1") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<ItemUsuarioDTO> usuariosPage = generalService.listarUsuarios(pageable);
            return ResponseEntity.ok(usuariosPage);
        } catch (NoHayUsuariosException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }
    }

    @Operation(summary = "Enviar email de recuperación de contraseña",
            description = "Envía un email al usuario que solicitó recuperar la contraseña, incluyendo un token de autenticación")
    @ApiResponse(responseCode = "200", description = "Correo enviado exitosamente", content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "404", description = "Correo electrónico no encontrado en la base de datos", content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "400", description = "Formato de correo electrónico inválido", content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "503", description = "Servidor de correo no disponible", content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "429", description = "Límite de envío de correos alcanzado", content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @GetMapping("/email/password/{email}")
    public ResponseEntity<MensajeDTO<String>> enviarLinkRecuperacion(
            @Parameter(description = "Correo electrónico del usuario", required = true)
            @PathVariable String email)  {
        try {
            generalService.enviarLinkRecuperacion(email);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, "Revisa el correo y copia el token de autenticación"));
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensajeDTO<>(true, "Correo electrónico no encontrado en la base de datos"));
        } catch (CorreoInvalidoException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MensajeDTO<>(true, "Formato de correo electrónico inválido"));
        } catch (LimiteEnvioAlcanzadoException e) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(new MensajeDTO<>(true, "Límite de envío de correos alcanzado. Intente más tarde"));
        }catch (ServidorCorreoNoDisponibleException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(new MensajeDTO<>(true, "Servidor de correo no disponible. Intente más tarde"));
        }  catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MensajeDTO<>(true, "Error interno del servidor"));
        }
    }
}
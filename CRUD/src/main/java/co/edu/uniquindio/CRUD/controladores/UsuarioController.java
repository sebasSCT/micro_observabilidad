package co.edu.uniquindio.CRUD.controladores;

import co.edu.uniquindio.CRUD.excepciones.DatosIncompletosException;
import co.edu.uniquindio.CRUD.excepciones.NoAutorizadoException;
import co.edu.uniquindio.CRUD.excepciones.UsuarioExisteException;
import co.edu.uniquindio.CRUD.excepciones.UsuarioNoEncontradoException;
import co.edu.uniquindio.CRUD.utils.JWTUtils;
import co.edu.uniquindio.CRUD.dtos.general.MensajeDTO;
import co.edu.uniquindio.CRUD.dtos.usuario.ActualizarUsuarioDTO;
import co.edu.uniquindio.CRUD.servicios.interfaces.UsuarioService;
import co.edu.uniquindio.rabbitmq.RabbitMQSender;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@SecurityRequirement(name = "bearerAuth")
@RequestMapping("/api/usuarios/{codigo}")
@RequiredArgsConstructor
@Tag(name = "Usuario", description = "Controlador para operaciones propias del usuario")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final JWTUtils jwtUtils;
    private final RabbitMQSender rabbitMQSender;

    @Operation(summary = "Actualizar perfil de usuario",
            description = "Actualiza la información del perfil de un usuario existente")
    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente", content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos de actualización inválidos o incompletos", content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "401", description = "No autorizado", content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado", content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "409", description = "El usuario ya existe", content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor", content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @PutMapping
    public ResponseEntity<MensajeDTO<String>> actualizarUsuario(
            @Parameter(description = "Código del usuario a actualizar", required = true)
            @PathVariable String codigo,
            @Valid @RequestBody ActualizarUsuarioDTO actualizarUsuarioDTO, HttpServletRequest request) throws Exception {
        String token = getToken(request);
        String idToken = jwtUtils.extractIdFromToken(token);

        try{
            usuarioService.actualizarUsuario(codigo,actualizarUsuarioDTO, idToken);

            rabbitMQSender.sendMessage("update", actualizarUsuarioDTO.email());

            return ResponseEntity.ok().body(new MensajeDTO<>(false, "Usuario actualizado correctamente"));
        }catch (DatosIncompletosException e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }catch(NoAutorizadoException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }catch(UsuarioNoEncontradoException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }catch(UsuarioExisteException e){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }
    }

    @Operation(summary = "Eliminar cuenta de usuario",
            description = "Elimina la cuenta de un usuario del sistema")
    @ApiResponse(responseCode = "200", description = "Usuario eliminado correctamente",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @DeleteMapping
    public ResponseEntity<MensajeDTO<String>> eliminarCuenta(
            @Parameter(description = "Código del usuario a eliminar", required = true)
            @PathVariable String codigo, HttpServletRequest request) throws Exception {
        String token = getToken(request);
        String idToken = jwtUtils.extractIdFromToken(token);

        try{
            usuarioService.eliminarUsuario(codigo, idToken);

            rabbitMQSender.sendMessage("delete", usuarioService.obtenerUsuario(codigo,idToken).email());

            return ResponseEntity.ok().body(new MensajeDTO<>(false, "Usuario eliminado correctamente"));
        }catch(NoAutorizadoException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }catch(UsuarioNoEncontradoException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }

    }

    @Operation(summary = "Buscar un usuario usuario",
            description = "Si el usuario dado el código, existe, entonces se devuelve el usuario sino el respectivo mensaje de la excepción")
    @ApiResponse(responseCode = "401", description = "No autorizado",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @GetMapping
    public ResponseEntity<MensajeDTO<?>> obtenerCliente(
            @Parameter(description = "Código del usuario", required = true)
            @PathVariable String codigo, HttpServletRequest request) throws Exception {
        String token = getToken(request);
        String idToken = jwtUtils.extractIdFromToken(token);
        try{
            return ResponseEntity.ok().body(new MensajeDTO<>(false, usuarioService.obtenerUsuario(codigo,idToken)));
        }catch(NoAutorizadoException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }catch(UsuarioNoEncontradoException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }
    }

    private String getToken(HttpServletRequest req) {
        String header = req.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer "))
            return header.replace("Bearer ", "");
        return null;
    }
}
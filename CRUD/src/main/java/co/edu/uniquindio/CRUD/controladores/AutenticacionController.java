package co.edu.uniquindio.CRUD.controladores;

import co.edu.uniquindio.CRUD.dtos.autenticacionJwt.TokenDTO;
import co.edu.uniquindio.CRUD.dtos.general.CambioPasswordDTO;
import co.edu.uniquindio.CRUD.dtos.general.LoginDTO;
import co.edu.uniquindio.CRUD.dtos.general.MensajeDTO;
import co.edu.uniquindio.CRUD.dtos.usuario.RegistroUsuarioDTO;
import co.edu.uniquindio.CRUD.excepciones.*;
import co.edu.uniquindio.CRUD.servicios.interfaces.AutenticacionService;
import co.edu.uniquindio.CRUD.servicios.interfaces.UsuarioService;
import co.edu.uniquindio.CRUD.utils.JWTUtils;
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
@RequestMapping("/api/auth/usuarios")
@RequiredArgsConstructor
@Tag(name = "Autenticacion", description = "Controlador para operaciones de autenticacion y de registro")
public class AutenticacionController {

    private final AutenticacionService autenticacionServicio;
    private final UsuarioService usuarioService;
    private final JWTUtils jwtUtils;
    private final RabbitMQSender rabbitMQSender;

    @Operation(summary = "Iniciar sesión",
            description = "Autentica un usuario y devuelve un token JWT o un mensaje de error")
    @ApiResponse(responseCode = "200", description = "Login exitoso",
            content = @Content(schema = @Schema(implementation = TokenDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos de entrada incompletos",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "401", description = "Credenciales inválidas",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "403", description = "Usuario inactivo",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @PostMapping("/login")
    public ResponseEntity<MensajeDTO<?>> login(@Valid @RequestBody LoginDTO loginDTO) {
        try {
            TokenDTO tokenDTO = autenticacionServicio.login(loginDTO);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, tokenDTO));
        } catch (DatosIncompletosException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        } catch (CredencialesInvalidasException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        } catch (UsuarioInactivoException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        } catch (UsuarioNoEncontradoException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }
    }

    @Operation(summary = "Registrar nuevo usuario",
            description = "Crea un nuevo usuario en el sistema")
    @ApiResponse(responseCode = "201", description = "Usuario registrado correctamente",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos de registro inválidos o incompletos",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "409", description = "El usuario ya existe",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @PostMapping
    public ResponseEntity<MensajeDTO<String>> registrarse(
            @Valid @RequestBody RegistroUsuarioDTO usuarioDTO) throws Exception {
        try{
            usuarioService.registrarUsuario(usuarioDTO);

            rabbitMQSender.sendMessage("create", usuarioDTO.email());

            return ResponseEntity.status(HttpStatus.CREATED).body(new MensajeDTO<>(false, "Usuario registrado correctamente"));
        }catch (DatosIncompletosException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        } catch (UsuarioExisteException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new MensajeDTO<>(true, e.getMessage()));
        }
    }

    @Operation(summary = "Cambiar contraseña",
            description = "Cambia la contraseña de un usuario existente")
    @ApiResponse(responseCode = "200", description = "Contraseña actualizada con éxito",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "400", description = "Datos de cambio de contraseña inválidos o incompletos",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "403", description = "Usuario inactivo",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @ApiResponse(responseCode = "500", description = "Error interno del servidor",
            content = @Content(schema = @Schema(implementation = MensajeDTO.class)))
    @SecurityRequirement(name = "bearerAuth")
    @PatchMapping("/password/{codigo}")
    public ResponseEntity<MensajeDTO<String>> cambiarPassword(
            @Parameter(description = "Código del usuario", required = true)
            @PathVariable String codigo,
            @Valid @RequestBody CambioPasswordDTO cambioPasswordDTO,
            HttpServletRequest request) throws Exception {
        try{
            String token = getToken(request);
            String idToken = jwtUtils.parseJwt(token).getBody().getSubject();

            if(!idToken.equals(codigo)){
                throw new NoAutorizadoException("No puedes actualizar éste usuario");
            }
            usuarioService.cambiarPassword(cambioPasswordDTO,codigo);
            return ResponseEntity.ok().body(new MensajeDTO<>(false, "Contraseña actualizada con éxito"));
        }catch(NoAutorizadoException e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new MensajeDTO<>(true, e.getMessage()));
        }catch (DatosIncompletosException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }catch(UsuarioNoEncontradoException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }catch(UsuarioInactivoException e){
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new MensajeDTO<>(true, e.getMessage()));
        }catch (Exception e) {
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
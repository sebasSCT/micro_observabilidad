package co.edu.uniquindio.CRUD.servicios.implementaciones;

import co.edu.uniquindio.CRUD.documentos.Usuario;
import co.edu.uniquindio.CRUD.dtos.general.CambioPasswordDTO;
import co.edu.uniquindio.CRUD.dtos.usuario.ActualizarUsuarioDTO;
import co.edu.uniquindio.CRUD.dtos.usuario.DetalleUsuarioDTO;
import co.edu.uniquindio.CRUD.dtos.usuario.RegistroUsuarioDTO;
import co.edu.uniquindio.CRUD.excepciones.*;
import co.edu.uniquindio.CRUD.repositorios.UsuarioRepository;
import co.edu.uniquindio.CRUD.servicios.interfaces.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public String registrarUsuario(RegistroUsuarioDTO usuario) throws Exception {

        if (existeEmail(usuario.email()))
            throw new UsuarioExisteException("El usuario ya existe");

        if(usuario.email().isEmpty() ||
        usuario.nombre().isEmpty() || usuario.password().isEmpty() ||
        usuario.apellido().isEmpty()){
            throw new DatosIncompletosException("Datos de registro inválidos o incompletos");
        }

        Usuario usuarioDB = new Usuario();

        usuarioDB.setNombre(usuario.nombre());
        usuarioDB.setApellido(usuario.apellido());
        usuarioDB.setEmail(usuario.email());
        usuarioDB.setEstado(true);

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String passwordEncriptada = passwordEncoder.encode( usuario.password() );

        usuarioDB.setPassword(passwordEncriptada);

        Usuario usuarioGuardado = usuarioRepository.save(usuarioDB);

        return usuarioGuardado.getCodigo();
    }

    private boolean existeEmail(String email) {
        return usuarioRepository.findByEmail(email).isPresent();
    }


    @Override
    public void actualizarUsuario(String idUsuario,ActualizarUsuarioDTO usuario, String idtoken) throws Exception {

        if(!(usuario.codigo().equals(idtoken)) || !(idUsuario.equals(idtoken)) ){
            throw new NoAutorizadoException("No puedes realizar ésta operación");
        }

        if(usuario.email().isEmpty() ||
                usuario.nombre().isEmpty() ||
                usuario.apellido().isEmpty()){
            throw new DatosIncompletosException("Datos de registro inválidos o incompletos");
        }

        Optional<Usuario> usuarioDB = usuarioRepository.findById(usuario.codigo());

        if (usuarioDB.isEmpty()) {
            throw new UsuarioNoEncontradoException("El usuario no existe");
        }

        if (existeEmail(usuario.email()) && !usuarioDB.get().getCodigo().equals(idUsuario))
            throw new UsuarioExisteException("El usuario ya existe");

        if (!usuarioDB.get().isEstado()) {
            throw new UsuarioInactivoException("El usuario está inactivo");
        }

        Usuario usuarioActual = usuarioDB.get();
        usuarioActual.setNombre(usuario.nombre());
        usuarioActual.setApellido(usuario.apellido());
        usuarioActual.setEmail(usuario.email());
        usuarioActual.setEstado(true);

        usuarioRepository.save(usuarioActual);
    }

    @Override
    public DetalleUsuarioDTO obtenerUsuario(String idCuenta, String idtoken) throws Exception {

        if(!(idCuenta.equals(idtoken))){
            throw new Exception("No puedes realizar ésta operación, porque no eres tú");
        }

        Optional<Usuario> usuarioDB = usuarioRepository.findById(idCuenta);

        if (usuarioDB.isEmpty()) {
            throw new Exception("El usuario no existe");
        }

        Usuario usuarioActual = usuarioDB.get();

        return new DetalleUsuarioDTO(usuarioActual.getCodigo(), usuarioActual.getEmail(), usuarioActual.getNombre(), usuarioActual.getApellido());
    }

    @Override
    public void eliminarUsuario(String idCuenta, String idtoken) throws Exception {

        if(!(idCuenta.equals(idtoken))){
            throw new NoAutorizadoException("No puedes realizar ésta operación");
        }

        Optional<Usuario> usuarioDB = usuarioRepository.findById(idCuenta);

        if (usuarioDB.isEmpty()) {
            throw new UsuarioNoEncontradoException("El usuario no existe");
        }

        if(!usuarioDB.get().isEstado()){
            throw new UsuarioInactivoException("El usuario está inactivo");
        }

        Usuario usuarioActual = usuarioDB.get();

        usuarioActual.setEstado(false);

        Usuario usuarioGuardado = usuarioRepository.save(usuarioActual);
    }

    @Override
    public void cambiarPassword(CambioPasswordDTO cambioPasswordDTO, String idUsuario) throws Exception {

        Optional<Usuario> usuario = usuarioRepository.findById(idUsuario);

        if(cambioPasswordDTO.nuevaPassword().isEmpty()){
            throw new DatosIncompletosException("Datos de cambio de contraseña inválidos o incompletos");
        }

        if(!usuario.get().isEstado()){
            throw new UsuarioInactivoException("El usuario no está activo");
        }

        if (usuario.isEmpty()) {
            throw new UsuarioExisteException("El usuario no existe");
        } else {
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String passwordEncriptada = passwordEncoder.encode(cambioPasswordDTO.nuevaPassword());

            usuario.get().setPassword(passwordEncriptada);
            usuarioRepository.save(usuario.get());
        }
    }
}

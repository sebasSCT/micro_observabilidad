package co.edu.uniquindio.CRUD.documentos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.extern.java.Log;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.io.Serializable;

@Document("usuarios")
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Usuario implements Serializable {

    @Id
    @EqualsAndHashCode.Include
    private String codigo;

    @NotNull(message = "El email no puede ser nulo")
    @Size(max = 50, message = "El email no puede tener más de 50 caracteres")
    @Indexed(unique = true)
    private String email;

    @NotNull(message = "La contraseña no puede ser nula")
    private String password;

    @NotNull(message = "El nombre no puede ser nulo")
    private String nombre;

    @NotNull(message = "El apellido no puede ser nulo")
    private String apellido;

    private boolean estado;

}
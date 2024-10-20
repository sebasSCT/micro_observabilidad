package co.edu.uniquindio.talleruno.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import co.edu.uniquindio.talleruno.services.interfaces.AutenticacionServicio;

@RestController
@RequiredArgsConstructor
public class Reto3Controller {

    private final AutenticacionServicio autenticacionServicio;

    @PostMapping("/autenticacion")
    public ResponseEntity<String> autenticar(@RequestBody String nombre) throws Exception{

        String token = autenticacionServicio.autenticacion(nombre);
        return ResponseEntity.ok(token);

    }
}

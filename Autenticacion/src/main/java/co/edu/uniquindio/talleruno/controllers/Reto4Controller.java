package co.edu.uniquindio.talleruno.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Reto4Controller {
    
    @GetMapping("/saludo")
    public ResponseEntity<String> saludo (@RequestParam(value="nombre", required = false) String nombre) {

        return ResponseEntity.ok("Hola " + nombre);

    }
}


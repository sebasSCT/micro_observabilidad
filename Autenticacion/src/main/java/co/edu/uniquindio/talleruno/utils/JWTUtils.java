package co.edu.uniquindio.talleruno.utils;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;

@Component
public class JWTUtils {

    @Value("${jwt.secret}")
    private String claveSecreta;

    public String generarToken(String nombre) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(nombre)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(20L, ChronoUnit.DAYS)))
                .setIssuer("ingesis.uniquindio.edu.co")
                .signWith(getKey())
                .setHeaderParam("typ", "JWT")
                .compact();
    }

    private Key getKey(){
        return new SecretKeySpec(Base64.getDecoder().decode(claveSecreta),
                SignatureAlgorithm.HS256.getJcaName());
    }
}
package com.ebank.ebankbackend.security;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class SecurityController {

    @Autowired
    private AuthenticationManager authenticationManager ;
    @Autowired
    private JwtEncoder jwtEncoder;
    //un controller pour consulter le profil de l'utilisateur
    @GetMapping("/profile")
    public Authentication authentication (Authentication authentication) {
        return authentication ;
    }
    //Authentification d'un user avec spring security
    @PostMapping("/login")
    public Map<String,String> login(String username,String password){
        org.springframework.security.core.Authentication authentication = authenticationManager.
                authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );

        //On genere le jwt
        Instant instant = Instant.now();
        String scope =  authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).
                collect(Collectors.joining(" "));
        JwtClaimsSet jwtClaimsSet = JwtClaimsSet.builder()
                .issuedAt(instant)
                .expiresAt(instant.plus(60, ChronoUnit.MINUTES))
                .subject(username)
                .claim("scope",scope)
                .build();
        JwtEncoderParameters jwtEncoderParameters =
                JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS512).build(),
                 jwtClaimsSet
        );
        String jwt = jwtEncoder.encode(jwtEncoderParameters).getTokenValue();
        return  Map.of("access-token",jwt);
    }
}

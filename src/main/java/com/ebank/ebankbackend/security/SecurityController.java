package com.ebank.ebankbackend.security;

import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class SecurityController {

    //un controller pour consulter le profil de l'utilisateur
    @GetMapping("/profile")
    public Authentication authentication (Authentication authentication) {
        return authentication ;
    }
}

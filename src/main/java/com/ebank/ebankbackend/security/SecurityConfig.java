package com.ebank.ebankbackend.security;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    //L'anotation @Value() permet de recuperer une variable d'environnement
    @Value("${jwt.secret}")
    private String secretKey ;

    @Bean
    public InMemoryUserDetailsManager inMemoryUserDetailsManager () {
        PasswordEncoder passwordEncoder = passwordEncoder();
        return new InMemoryUserDetailsManager(
                User.withUsername("user1").password(passwordEncoder.encode("12345")).authorities("USER").build(),
                User.withUsername("admin").password(passwordEncoder.encode("12345")).authorities("USER","ADMIN").build()

        );
    }

    @Bean
    public PasswordEncoder passwordEncoder () {
        return new BCryptPasswordEncoder();
    }

    //On protege l'application
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(ar->ar.anyRequest().authenticated())
                .oauth2ResourceServer(oa->oa.jwt(Customizer.withDefaults()))
                .build() ;
    }

    @Bean
    JwtEncoder jwtEncoder () {
        //String secretKey = "d98jjkdfsda09dfdskfsdjfsaldjfkdsdj8h7s7hbvxc3209azdhd12j485jkff7";
        return new NimbusJwtEncoder(new ImmutableSecret<>(secretKey.getBytes()));
    }
    @Bean
    JwtDecoder jwtDecoder () {
        //String secretKey = "d98jjkdfsda09dfdskfsdjfsaldjfkdsdj8h7s7hbvxc3209azdhd12j485jkff7";
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(),"RSA");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec).macAlgorithm(MacAlgorithm.HS512).build();
    }

}

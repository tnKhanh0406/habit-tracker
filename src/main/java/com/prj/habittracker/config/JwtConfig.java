package com.prj.habittracker.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class JwtConfig {
    @Bean
    SecretKey jwtSecretKey(@Value("${app.jwt.secret}") String encodedSecret) {
        byte[] bytes = Base64.getDecoder().decode(encodedSecret);
        if (bytes.length < 32) {
            throw new IllegalArgumentException(
                    "JWT_SECRET must contain at least 32 bytes");
        }

        return new SecretKeySpec(bytes, "HmacSHA256");
    }

    @Bean
    JwtEncoder jwtEncoder(SecretKey key) {
        return new NimbusJwtEncoder(new ImmutableSecret<SecurityContext>(key));
    }

    @Bean
    JwtDecoder jwtDecoder(SecretKey key, @Value("${app.jwt.issuer}") String issuer) {
        NimbusJwtDecoder decoder = NimbusJwtDecoder
                .withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();

        decoder.setJwtValidator(JwtValidators.createDefaultWithIssuer(issuer));

        return decoder;
    }
}

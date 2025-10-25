package com.pacifique.resourceserver.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
@Component
public class JwtAuthenticationConverter implements Converter<Jwt, CustomAuthentication> {

    @Override
    public CustomAuthentication convert(Jwt source) {
        List<GrantedAuthority> authorities = List.of(() -> "read");

        String subject = String.valueOf(source.getSubject());
        System.out.println("subject = " + subject);

        String priority = String.valueOf(source.getClaims().get("priority"));

        return new CustomAuthentication(source,authorities,priority);
    }

}


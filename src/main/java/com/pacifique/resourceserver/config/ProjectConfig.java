package com.pacifique.resourceserver.config;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.authentication.OpaqueTokenAuthenticationProvider;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;
import org.springframework.security.oauth2.server.resource.introspection.SpringOpaqueTokenIntrospector;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.client.RestTemplate;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ProjectConfig {
//    private final JwtAuthenticationConverter converter;
    @Value("${jwt.key.url}")
    private String jwtKeySetUri;

    @Value("${jwt.key.introspectionUri}")
    private String introspectionUri;
    @Value("${resourceserver.clientID}")
    private String resourceServerClientID;
    @Value("${resourceserver.secret}")
    private String resourceServerSecret;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // 1 AUTHORIZATION_CODE = nonopaque
//        http.oauth2ResourceServer((c)
//                -> c.jwt(j -> {
//                    j.jwtAuthenticationConverter(converter);
//        }));


        // 2  CLIENT_CREDENTIALS opaque

//        http.oauth2ResourceServer(c->{
//            c.opaqueToken(o->{
//                o.introspectionUri(introspectionUri);
//                o.introspectionClientCredentials(resourceServerClientID, resourceServerSecret);
//            });
//        });

        http.oauth2ResourceServer(j -> {
                    j.authenticationManagerResolver(authenticationManagerResolver(jwtDecoder(), opaqueTokenIntrospector()));
                }
        );



        http.authorizeHttpRequests(au -> au.anyRequest().authenticated());
        return http.build();
    }

    @Bean
    public AuthenticationManagerResolver<HttpServletRequest> authenticationManagerResolver(
            JwtDecoder jwtDecoder,
            OpaqueTokenIntrospector opaqueToken
    ) {

        AuthenticationManager jwtAuth = new ProviderManager(new JwtAuthenticationProvider(jwtDecoder));
        AuthenticationManager opaqueAuth = new ProviderManager(new OpaqueTokenAuthenticationProvider(opaqueToken));



        return (request) -> {
            if ("jwt".equals(request.getHeader("type"))) {
                return jwtAuth;
            }
            return opaqueAuth;

        };
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwtKeySetUri).build();
    }

    @Bean
    public OpaqueTokenIntrospector opaqueTokenIntrospector() {
        RestTemplate restTemplate = new RestTemplate();
        boolean added = restTemplate.getInterceptors()
                .add(new BasicAuthenticationInterceptor(resourceServerClientID, resourceServerSecret));

        log.info("Added RestTemplate interceptor: {}", added);
        return new SpringOpaqueTokenIntrospector(
                introspectionUri, restTemplate
        );
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }



    @Bean
    public JwtAuthenticationConverter authenticationConverter() {
        return new JwtAuthenticationConverter();
    }


}

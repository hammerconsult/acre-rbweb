package br.com.webpublico.seguranca.firebase;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiServiceFirebase {

    protected static final Logger logger = LoggerFactory.getLogger(ApiServiceFirebase.class);
    private TokenFirebase token;

    public TokenFirebase getToken() {
        if (token == null || token.isExpired()) {
            buscarToken();
        }
        return token;
    }

    private void buscarToken() {
        String url = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=AIzaSyCdH3M665uRki6evdhPGFk5LhAKtseyPHk";
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        UsuarioFirebase usuarioFirebase = new UsuarioFirebase();
        usuarioFirebase.setEmail("app@webpublico.com.br");
        usuarioFirebase.setPassword("Senha10#@2023");
        usuarioFirebase.setReturnSecureToken(Boolean.TRUE);

        HttpEntity<UsuarioFirebase> entity = new HttpEntity<UsuarioFirebase>(usuarioFirebase, headers);
        ResponseEntity<TokenFirebase> response = restTemplate.exchange(url, HttpMethod.POST, entity, TokenFirebase.class);
        token = response.getBody();
        logger.debug("GET TOKEN FIREBASE --- {}", token);
    }
}

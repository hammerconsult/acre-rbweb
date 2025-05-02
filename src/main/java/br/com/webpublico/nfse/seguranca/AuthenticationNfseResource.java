package br.com.webpublico.nfse.seguranca;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by Marinzeck on 23/01/2017.
 */
@RequestMapping("/nfse")
@Controller
public class AuthenticationNfseResource {
    private final Logger logger = LoggerFactory.getLogger(AuthenticationNfseResource.class);

    // Todo deve-se manter comentário até finalização do ticket sobre criação da estrutura dos dados
//    /**
//     * GET  /auth
//     */
//    @RequestMapping(value = "/auth",
//        method = RequestMethod.GET,
//        produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<UsuarioNfse> auth(@RequestParam(value = "login") String login) {
//        UsuarioNfse usuario = new UsuarioNfse();

//        usuario.setLogin("admin");
//        usuario.setPassword("$2a$10$gSAhZrxMllrbgj/kkK9UceBPpChGWJA7SYIb1Mqo.n5aNLq1/oRrC");
//
//        AuthorityNfse auth = new AuthorityNfse();
//        auth.setName("ROLE_ADMIN");
//
//        AuthorityNfse auth2 = new AuthorityNfse();
//        auth2.setName("ROLE_USER");


//        usuario.setAuthorities(new ArrayList<Authority>());
//        usuario.getAuthorities().add(auth);
//        usuario.getAuthorities().add(auth2);

//        usuario.setActivated(false);

//        return new ResponseEntity<>(usuario, HttpStatus.OK);
//    }
}

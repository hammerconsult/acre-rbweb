package br.com.webpublico.nfse.rest;


import br.com.webpublico.entidades.UF;
import br.com.webpublico.negocios.UFFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.io.Serializable;
import java.util.List;

@RequestMapping("/nfse")
@Controller
public class UFResource implements Serializable {
    private UFFacade ufFacade;
    private final Logger logger = LoggerFactory.getLogger(UFResource.class);

    @PostConstruct
    public void init() {
        try {
            ufFacade = (UFFacade) new InitialContext().lookup("java:module/UFFacade");
        } catch (NamingException e) {
            logger.error("Não foi possivel criar a instancia: " + e.getMessage());
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/ufs-brasil",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public <T> ResponseEntity<List<T>> recuperarUfsDoBrasil() {

        List<UF> ufs = ufFacade.buscarUFsDoBrasil();
        return new ResponseEntity(ufs, HttpStatus.OK);
    }
}

package br.com.webpublico.nfse.rest;

import br.com.webpublico.nfse.domain.Cosif;
import br.com.webpublico.nfse.domain.dtos.CosifNfseDTO;
import br.com.webpublico.nfse.facades.CosifFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.Serializable;

@RequestMapping("/nfse/cosif")
@Controller
public class CosifResource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(CosifResource.class);
    private CosifFacade cosifFacade;

    @PostConstruct
    public void init() {
        try {
            cosifFacade = (CosifFacade) new InitialContext().lookup("java:module/CosifFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/por-conta",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<CosifNfseDTO> buscarCosifPorConta(@RequestParam String conta) throws Exception {
        Cosif cosif = cosifFacade.buscarPorConta(conta);
        return new ResponseEntity(cosif != null ? cosif.toNfseDto() : null, HttpStatus.OK);
    }
}

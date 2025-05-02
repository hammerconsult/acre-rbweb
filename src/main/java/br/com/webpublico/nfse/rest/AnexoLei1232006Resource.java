package br.com.webpublico.nfse.rest;


import br.com.webpublico.nfse.domain.AnexoLei1232006;
import br.com.webpublico.nfse.domain.dtos.AnexoLei1232006NfseDTO;
import br.com.webpublico.nfse.domain.perguntasrespostas.AssuntoNfse;
import br.com.webpublico.nfse.facades.AnexoLei1232006Facade;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.List;

@RequestMapping("/nfse")
@Controller
public class AnexoLei1232006Resource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(AnexoLei1232006Resource.class);
    private AnexoLei1232006Facade anexoLei1232006Facade;

    @PostConstruct
    public void init() {
        try {
            anexoLei1232006Facade = (AnexoLei1232006Facade) Util.getFacadeViaLookup("java:module/AnexoLei1232006Facade");
        } catch (Exception e) {
            logger.error("Erro ao iniciar AIDFEResource", e);
        }
    }

    @RequestMapping(value = "/anexos-lei-123-2006",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AssuntoNfse>> recuperarAnessos() {
        List<AnexoLei1232006> anexos = anexoLei1232006Facade.lista();
        List<AnexoLei1232006NfseDTO> dtos = Lists.newArrayList();
        for (AnexoLei1232006 anexo : anexos) {
            dtos.add(anexo.toNfseDto());
        }
        return new ResponseEntity(dtos, HttpStatus.OK);
    }

}

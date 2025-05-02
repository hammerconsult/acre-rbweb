package br.com.webpublico.nfse.rest;


import br.com.webpublico.entidades.NaturezaJuridica;
import br.com.webpublico.enums.TipoNaturezaJuridica;
import br.com.webpublico.negocios.NaturezaJuridicaFacade;
import br.com.webpublico.nfse.domain.dtos.NaturezaJuridicaNfseDTO;
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
public class NaturezaJuridicaResource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(NaturezaJuridicaResource.class);

    private NaturezaJuridicaFacade naturezaJuridicaFacade;

    @PostConstruct
    public void init() {
        try {
            naturezaJuridicaFacade = (NaturezaJuridicaFacade) Util.getFacadeViaLookup("java:module/NaturezaJuridicaFacade");
        } catch (Exception e) {
            logger.error("Erro ao iniciar AIDFEResource", e);
        }
    }


    @RequestMapping(value = "/naturezas-juridicas",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NaturezaJuridicaNfseDTO>> recuperarJuridicas() {
        List<NaturezaJuridicaNfseDTO> dtos = Lists.newArrayList();
        List<NaturezaJuridica> naturezaJuridicas = naturezaJuridicaFacade.listaNaturezaJuridicaPorTipo(TipoNaturezaJuridica.JURIDICA);
        for (NaturezaJuridica naturezaJuridica : naturezaJuridicas) {
            dtos.add(naturezaJuridica.toNfseDto());
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @RequestMapping(value = "/naturezas-fisicas",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NaturezaJuridicaNfseDTO>> recuperarFisicas() {
        List<NaturezaJuridicaNfseDTO> dtos = Lists.newArrayList();
        List<NaturezaJuridica> naturezaJuridicas = naturezaJuridicaFacade.listaNaturezaJuridicaPorTipo(TipoNaturezaJuridica.FISICA);
        for (NaturezaJuridica naturezaJuridica : naturezaJuridicas) {
            dtos.add(naturezaJuridica.toNfseDto());
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
}

package br.com.webpublico.nfse.rest;


import br.com.webpublico.entidades.CNAE;
import br.com.webpublico.negocios.CNAEFacade;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.nfse.domain.dtos.CnaeNfseDTO;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.List;

@RequestMapping("/nfse")
@Controller
public class CnaeResource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(CnaeResource.class);

    private CNAEFacade cnaeFacade;
    private CadastroEconomicoFacade cadastroEconomicoFacade;

    @PostConstruct
    public void init() {
        try {
            cnaeFacade = (CNAEFacade) new InitialContext().lookup("java:module/CNAEFacade");
            cadastroEconomicoFacade = (CadastroEconomicoFacade) new InitialContext().lookup("java:module/CadastroEconomicoFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/cnae-por-cadastro/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<CnaeNfseDTO>> recuperarJuridicas(@PathVariable("id") Long id) {
        List<CNAE> cnaes = cnaeFacade.getCnaePorCadastroEconomico(cadastroEconomicoFacade.recuperar(id));
        List<CnaeNfseDTO> dtos = Lists.newArrayList();
        for (CNAE cnae : cnaes) {
            dtos.add(cnae.toNfseDto());
        }
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }
//
//    @RequestMapping(value = "/cnae-por-servico/{filtro}",
//        method = RequestMethod.GET,
//        produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<List<CnaeNfseDTO>> buscarCnaePorServico(Pageable pageable, @PathVariable("filtro") String filtro) throws URISyntaxException {
//        Page<CnaeNfseDTO> retorno = cnaeFacade.buscarCnaePorServico(pageable, filtro);
//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());
//        return new ResponseEntity(retorno.getContent(), headers, HttpStatus.OK);
//    }
}

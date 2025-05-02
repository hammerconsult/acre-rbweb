package br.com.webpublico.nfse.rest;


import br.com.webpublico.entidades.EscritorioContabil;
import br.com.webpublico.negocios.EscritorioContabilFacade;
import br.com.webpublico.nfse.domain.dtos.EscritorioContabilNfseDTO;
import br.com.webpublico.nfse.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

@RequestMapping("/nfse")
@Controller
public class EscritorioContabilResource implements Serializable {
    private EscritorioContabilFacade escritorioContabilFacade;
    private final Logger logger = LoggerFactory.getLogger(EscritorioContabilResource.class);

    @PostConstruct
    public void init() {
        try {
            escritorioContabilFacade = (EscritorioContabilFacade) new InitialContext().lookup("java:module/EscritorioContabilFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/escritorio-contabil/listar",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EscritorioContabilNfseDTO>> pesquisaGenerica(Pageable pageable,
                                                                            @RequestParam(value = "query", required = false) String query)
        throws URISyntaxException, IOException {
        if (query == null) {
            query = "";
        }
        List<EscritorioContabilNfseDTO> retorno = escritorioContabilFacade.listaEscritorioPorNomeRazaoSocialCPFCNPJ(query, pageable);
        Integer totalCadastros = escritorioContabilFacade.contarEscritorioPorNomeRazaoSocialCPFCNPJ(query);

        Page returnPage = new PageImpl(retorno, pageable, totalCadastros);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(returnPage, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(returnPage.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/escritorios-contabeis",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<EscritorioContabil>> recuperarEscritoriosContabeis() {
        List<EscritorioContabil> escritorios = escritorioContabilFacade.recuperarEscritoriosOrderByNome();
        return new ResponseEntity(escritorios, HttpStatus.OK);
    }
}

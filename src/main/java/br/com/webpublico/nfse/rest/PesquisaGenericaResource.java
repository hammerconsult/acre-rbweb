package br.com.webpublico.nfse.rest;


import br.com.webpublico.nfse.domain.dtos.NfseDTO;
import br.com.webpublico.nfse.facades.PesquisaGenericaNfseFacade;
import br.com.webpublico.nfse.util.PaginationUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by Marinzeck on 23/01/2017.
 */
@RequestMapping("/nfse")
@Controller
public class PesquisaGenericaResource implements Serializable {
    private PesquisaGenericaNfseFacade pesquisaGenericaNfseFacade;
    private final Logger logger = LoggerFactory.getLogger(PesquisaGenericaResource.class);

    @PostConstruct
    public void init() {
        try {
            pesquisaGenericaNfseFacade = (PesquisaGenericaNfseFacade) new InitialContext().lookup("java:module/PesquisaGenericaNfseFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    /**
     * GET  /pesquisa-generica/registro/
     */
    @RequestMapping("/pesquisa-generica/registro")
    public ResponseEntity<NfseDTO> getById(@RequestParam(value = "table") String table,
                                           @RequestParam(value = "id") Long id) {
        logger.error("REST request to get {} : {}", table, id);
        NfseDTO registro = pesquisaGenericaNfseFacade.recuperarRegistro(table, id);
        logger.error("Retorno do get por ID [{}]", registro);
        return new ResponseEntity<>(registro, HttpStatus.OK);
    }

    /**
     * GET  /pesquisa-generica -> Método generico para busca de entidades no webpublico
     */
    @RequestMapping(value = "/pesquisa-generica",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NfseDTO>> pesquisaGenerica(Pageable pageable,
                                                          @RequestParam(value = "table") String table,
                                                          @RequestParam(value = "searchFields", required = false) String searchFields,
                                                          @RequestParam(value = "query", required = false) String query)
        throws URISyntaxException, IOException {
        Page<NfseDTO> retorno = pesquisarRegistros(pageable, table, searchFields, query);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(retorno.getContent(), headers, HttpStatus.OK);
    }

    public Page<NfseDTO> pesquisarRegistros(Pageable pageable, String table, String searchFields, String query) throws UnsupportedEncodingException {
        if (!StringUtils.isBlank(table)) {
            table = PaginationUtil.decode(table);
        }
        if (!StringUtils.isBlank(searchFields)) {
            searchFields = PaginationUtil.decode(searchFields);
        }
        if (!StringUtils.isBlank(query)) {
            query = PaginationUtil.decode(query);
        }
        return pesquisaGenericaNfseFacade.pesquisar(pageable, table, searchFields, query);
    }

    @RequestMapping(value = "/pesquisa-generica/first",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NfseDTO> pesquisaFirst(Pageable pageable,
                                                 @RequestParam(value = "table") String table,
                                                 @RequestParam(value = "searchFields", required = false) String searchFields,
                                                 @RequestParam(value = "query", required = false) String query)
        throws URISyntaxException, IOException {
        Page<NfseDTO> retorno = pesquisarRegistros(pageable, table, searchFields, query);
        if (!retorno.getContent().isEmpty()) {
            return new ResponseEntity<>(retorno.getContent().get(0), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


}

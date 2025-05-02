package br.com.webpublico.nfse.rest;

import br.com.webpublico.nfse.domain.Legislacao;
import br.com.webpublico.nfse.domain.dtos.LegislacaoDTO;
import br.com.webpublico.nfse.exceptions.NfseValidacaoException;
import br.com.webpublico.nfse.facades.LegislacaoFacade;
import br.com.webpublico.nfse.util.HeaderUtil;
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
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

/**
 * Created by wellington on 25/08/17.
 */
@RequestMapping("/nfse")
@Controller
public class LegislacaoResource {

    private static Logger logger = LoggerFactory.getLogger(LegislacaoResource.class);

    private LegislacaoFacade legislacaoFacade;

    @PostConstruct
    public void init() {
        try {
            legislacaoFacade = (LegislacaoFacade) new InitialContext().lookup("java:module/LegislacaoFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/legislacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LegislacaoDTO>> getAll(Pageable pageable,
                                                      @RequestParam(value = "searchFields", required = false) String searchFields,
                                                      @RequestParam(value = "query", required = false) String query)
        throws UnsupportedEncodingException, URISyntaxException {

        if (!StringUtils.isBlank(searchFields)) {
            searchFields = PaginationUtil.decode(searchFields);
        }
        if (!StringUtils.isBlank(query)) {
            query = PaginationUtil.decode(query);
        }

        Page<LegislacaoDTO> retorno = legislacaoFacade.pesquisar(pageable, searchFields, query);


        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());

        return new ResponseEntity(retorno.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/legislacao/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LegislacaoDTO> getById(@PathVariable("id") Long id) throws URISyntaxException, IOException {
        return new ResponseEntity(legislacaoFacade.getById(id).toLegislacaoDTO(), HttpStatus.OK);
    }

    @RequestMapping(value = "/legislacao", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<LegislacaoDTO> create(@RequestBody LegislacaoDTO legislacaoDTO) {
        try {
            Legislacao legislacao = legislacaoFacade.salvarRetornando(Legislacao.toLegislacao(legislacaoDTO));
            return new ResponseEntity(legislacao.toLegislacaoDTO(), HttpStatus.OK);
        } catch (NfseValidacaoException ex) {
            HttpHeaders headers = HeaderUtil.setMessageError(ex.getMensagens().toArray(new String[0]));
            return new ResponseEntity(null, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Erro {}", e);
            return new ResponseEntity(null, setMessageError(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/legislacao", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<LegislacaoDTO> update(@RequestBody LegislacaoDTO legislacaoDTO) {
        try {
            Legislacao legislacao = legislacaoFacade.salvarRetornando(Legislacao.toLegislacao(legislacaoDTO));
            return new ResponseEntity(legislacao.toLegislacaoDTO(), HttpStatus.OK);
        } catch (NfseValidacaoException ex) {
            HttpHeaders headers = HeaderUtil.setMessageError(ex.getMensagens().toArray(new String[0]));
            return new ResponseEntity(null, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/legislacao/{id}")
    public ResponseEntity<LegislacaoDTO> delete(@PathVariable("id") Long id) {
        try {
            Legislacao legislacao = legislacaoFacade.getById(id);
            legislacaoFacade.remover(legislacao);
            return new ResponseEntity(legislacao.toLegislacaoDTO(), null, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/legislacoes-para-exibicao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<LegislacaoDTO>> buscarLegislacoesParaExibicao() {
        return new ResponseEntity(Legislacao.toListLegislacaoDTO(legislacaoFacade.buscarLegislacaoParaExibicao()), HttpStatus.OK);
    }

}

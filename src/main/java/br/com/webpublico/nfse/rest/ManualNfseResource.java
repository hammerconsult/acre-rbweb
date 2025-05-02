package br.com.webpublico.nfse.rest;

import br.com.webpublico.nfse.domain.ManualNfse;
import br.com.webpublico.nfse.domain.dtos.ManualDTO;
import br.com.webpublico.nfse.exceptions.NfseValidacaoException;
import br.com.webpublico.nfse.facades.ManualNfseFacade;
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
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.List;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

/**
 * Created by william on 29/08/17.
 */
@Controller
@RequestMapping("/nfse")
public class ManualNfseResource implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(ManualNfseResource.class);

    private ManualNfseFacade manualNfseFacade;

    @PostConstruct
    public void init() {
        try {
            InitialContext initialContext = new InitialContext();
            manualNfseFacade = (ManualNfseFacade) initialContext.lookup("java:module/ManualNfseFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/manual-nfse", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ManualDTO>> getAll(Pageable pageable,
                                                  @RequestParam(value = "searchFields", required = false) String searchFields,
                                                  @RequestParam(value = "query", required = false) String query)
        throws UnsupportedEncodingException, URISyntaxException {

        if (!StringUtils.isBlank(searchFields)) {
            searchFields = PaginationUtil.decode(searchFields);
        }
        if (!StringUtils.isBlank(query)) {
            query = PaginationUtil.decode(query);
        }

        Page<ManualDTO> retorno = manualNfseFacade.pesquisar(pageable, searchFields, query);


        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());

        return new ResponseEntity(retorno.getContent(), headers, HttpStatus.OK);
    }


    @ResponseBody
    @RequestMapping(value = "/manual-nfse", method = RequestMethod.POST)
    public ResponseEntity<ManualDTO> create(@RequestBody ManualDTO manualDTO) {
        return salvar(manualDTO);
    }

    @ResponseBody
    @RequestMapping(value = "/manual-nfse", method = RequestMethod.PUT)
    public ResponseEntity<ManualDTO> update(@RequestBody ManualDTO manualDTO) {
        return salvar(manualDTO);
    }

    private ResponseEntity<ManualDTO> salvar(ManualDTO manualDTO) {
        try {
            ManualNfse manualNfse = manualNfseFacade.salvarRetornando(ManualNfse.toManual(manualDTO));
            return new ResponseEntity<>(manualNfse.toManualDTO(), HttpStatus.OK);
        } catch (NfseValidacaoException ex) {
            HttpHeaders headers = HeaderUtil.setMessageError(ex.getMensagens().toArray(new String[0]));
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, setMessageError(e.getCause().getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "manual-nfse/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<ManualDTO> delete(@PathVariable Long id) {
        try {
            ManualNfse manualNfse = manualNfseFacade.recuperar(id);
            manualNfseFacade.remover(manualNfse);
            return new ResponseEntity<>(manualNfse.toManualDTO(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/manual-nfse/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ManualDTO> getById(@PathVariable("id") Long id) {
        return new ResponseEntity(manualNfseFacade.recuperar(id).toManualDTO(), HttpStatus.OK);
    }

    @RequestMapping(value = "/manuais-para-exibicao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ManualDTO>> buscarManuaisParaExibicao() {
        return new ResponseEntity(ManualNfse.toListManualNfseDTO(manualNfseFacade.buscarManualParaExibicao()), HttpStatus.OK);
    }

    @RequestMapping(value = "/manuais-por-tag", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<ManualDTO>> buscarManuaisPorTag(@RequestParam(value = "tag", required = false) String tag) {
        return new ResponseEntity(ManualNfse.toListManualSimpleNfseDTO(manualNfseFacade.buscarManualPorTag(tag)), HttpStatus.OK);
    }

}




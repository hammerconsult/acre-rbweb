package br.com.webpublico.nfse.rest;

import br.com.webpublico.nfse.domain.TipoLegislacao;
import br.com.webpublico.nfse.domain.dtos.TipoLegislacaoDTO;
import br.com.webpublico.nfse.domain.dtos.TipoLegislacaoLegislacaoDTO;
import br.com.webpublico.nfse.exceptions.NfseValidacaoException;
import br.com.webpublico.nfse.facades.TipoLegislacaoFacade;
import br.com.webpublico.nfse.util.HeaderUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

/**
 * Created by wellington on 25/08/17.
 */
@RequestMapping("/nfse")
@Controller
public class TipoLegislacaoResource {

    private static Logger logger = LoggerFactory.getLogger(TipoLegislacaoResource.class);

    private TipoLegislacaoFacade tipoLegislacaoFacade;

    @PostConstruct
    public void init() {
        try {
            tipoLegislacaoFacade = (TipoLegislacaoFacade) new InitialContext().lookup("java:module/TipoLegislacaoFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/tipo-legislacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TipoLegislacaoDTO>> loadAll() {
        List<TipoLegislacao> tiposLegislacao = tipoLegislacaoFacade.getAll();
        return new ResponseEntity(TipoLegislacao.toListTipoLegislacaoDTO(tiposLegislacao), HttpStatus.OK);
    }

    @RequestMapping(value = "/tipo-legislacao/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TipoLegislacaoDTO> getById(@PathVariable("id") Long id) throws URISyntaxException, IOException {
        return new ResponseEntity(tipoLegislacaoFacade.getById(id).toTipoLegislacaoDTO(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tipo-legislacao", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TipoLegislacaoDTO> create(@RequestBody TipoLegislacaoDTO tipoLegislacaoDTO) {
        try {
            tipoLegislacaoFacade.salvar(TipoLegislacao.toTipoLegislacao(tipoLegislacaoDTO));
            return new ResponseEntity(tipoLegislacaoDTO, HttpStatus.OK);
        } catch (NfseValidacaoException ex) {
            HttpHeaders headers = HeaderUtil.setMessageError(ex.getMensagens().toArray(new String[0]));
            return new ResponseEntity(null, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/tipo-legislacao", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<TipoLegislacaoDTO> update(@RequestBody TipoLegislacaoDTO tipoLegislacaoDTO) {
        try {
            tipoLegislacaoFacade.salvar(TipoLegislacao.toTipoLegislacao(tipoLegislacaoDTO));
            return new ResponseEntity(tipoLegislacaoDTO, HttpStatus.OK);
        } catch (NfseValidacaoException ex) {
            HttpHeaders headers = HeaderUtil.setMessageError(ex.getMensagens().toArray(new String[0]));
            return new ResponseEntity(null, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/tipo-legislacao/{id}")
    public ResponseEntity<TipoLegislacaoDTO> delete(@PathVariable("id") Long id) {
        logger.debug("id {}", id);
        try {
            TipoLegislacao tipoLegislacao = tipoLegislacaoFacade.getById(id);
            tipoLegislacaoFacade.remover(tipoLegislacao);
            return new ResponseEntity(tipoLegislacao.toTipoLegislacaoDTO(), null, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/tipo-legislacao-com-legislacoes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TipoLegislacaoLegislacaoDTO>> buscarTipoLegislacoComLegiscoes() {
        return new ResponseEntity(tipoLegislacaoFacade.buscarTipoLegislacoComLegiscoes(), HttpStatus.OK);
    }

}

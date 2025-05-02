package br.com.webpublico.nfse.rest;

import br.com.webpublico.nfse.domain.TipoManual;
import br.com.webpublico.nfse.domain.dtos.TipoManualDTO;
import br.com.webpublico.nfse.domain.dtos.TipoManualManualDTO;
import br.com.webpublico.nfse.exceptions.NfseValidacaoException;
import br.com.webpublico.nfse.facades.TipoManualFacade;
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
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

/**
 * Created by william on 24/08/17.
 */
@Controller
@RequestMapping("/nfse")
public class TipoManualResource implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(TipoManualResource.class);

    private TipoManualFacade tipoManualFacade;

    @PostConstruct
    public void init() {
        try {
            tipoManualFacade = (TipoManualFacade) new InitialContext().lookup("java:module/TipoManualFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/tipo-manual", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TipoManualDTO> create(@RequestBody TipoManualDTO tipoManualDTO) {
        return salvar(tipoManualDTO);
    }


    @RequestMapping(value = "/tipo-manual", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<TipoManualDTO> update(@RequestBody TipoManualDTO tipoManualDTO) {
        return salvar(tipoManualDTO);
    }

    private ResponseEntity<TipoManualDTO> salvar(@RequestBody TipoManualDTO tipoManualDTO) {
        try {
            tipoManualFacade.salvar(TipoManual.toTipoManual(tipoManualDTO));
            return new ResponseEntity<>(tipoManualDTO, HttpStatus.OK);
        } catch (NfseValidacaoException ex) {
            HttpHeaders headers = HeaderUtil.setMessageError(ex.getMensagens().toArray(new String[0]));
            return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getCause().getMessage()), HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/tipo-manual/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<TipoManualDTO> delete(@PathVariable("id") Long id) {
        try {
            TipoManual tipoManual = tipoManualFacade.recuperar(id);
            tipoManualFacade.remover(tipoManual);
            return new ResponseEntity<>(tipoManual.toTipoManualDTO(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/tipo-manual/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TipoManualDTO> getById(@PathVariable("id") Long id) throws
        URISyntaxException, IOException {
        TipoManual tipoManual = tipoManualFacade.recuperar(id);
        System.out.println("tipoManual " + tipoManual.getDescricao());
        return new ResponseEntity(tipoManual.toTipoManualDTO(), HttpStatus.OK);
    }

    @RequestMapping(value = "/tipo-manual-com-manuais", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<TipoManualManualDTO>> buscarTipoLegislacoComLegiscoes() {
        return new ResponseEntity(tipoManualFacade.buscarTipoManualComManuais(), HttpStatus.OK);
    }
}

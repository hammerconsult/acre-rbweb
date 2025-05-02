package br.com.webpublico.nfse.rest;

import br.com.webpublico.negocios.MemorandoFacade;
import br.com.webpublico.nfse.domain.dtos.MemorandoDTO;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Created by William on 24/01/2019.
 */
@RequestMapping("/nfse")
@Controller
public class MemorandoResource {

    private static Logger logger = LoggerFactory.getLogger(MemorandoResource.class);

    private MemorandoFacade memorandoFacade;

    @PostConstruct
    public void init() {
        try {
            memorandoFacade = (MemorandoFacade) Util.getFacadeViaLookup("java:module/MemorandoFacade");
        } catch (Exception e) {
            logger.error("Erro ao iniciar AIDFEResource", e);
        }
    }

    @RequestMapping(value = "/memorando-por-pessoa-cmc", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MemorandoDTO>> buscarMemorandoPessoaAndCMC(@RequestParam(value = "login") String login) {
        List<MemorandoDTO> memorandos = memorandoFacade.buscarMemorandoPessoaAndCMC(login);
        return new ResponseEntity(memorandos, HttpStatus.OK);
    }

    @RequestMapping(value = "/memorando-resposta", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<MemorandoDTO>> respostaMemorando(@RequestBody MemorandoDTO memorandoDTO) {
        memorandoFacade.criarMemorandoNFSE(memorandoDTO);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

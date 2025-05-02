package br.com.webpublico.nfse.rest;

import br.com.webpublico.nfse.domain.perguntasrespostas.AssuntoNfse;
import br.com.webpublico.nfse.facades.AssuntoNfseFacade;
import br.com.webpublico.nfse.util.PaginationUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.tratamentoerros.BuscaCausa;
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
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

@RequestMapping("/nfse")
@Controller
public class AssuntoResource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(AssuntoResource.class);
    private AssuntoNfseFacade assuntoNfseFacade;

    @PostConstruct
    public void init() {
        try {
            assuntoNfseFacade = (AssuntoNfseFacade) Util.getFacadeViaLookup("java:module/AssuntoNfseFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/assuntos-por-perguntas", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AssuntoNfse>> recuperarAssuntosPorPerguntas(Pageable pageable, @RequestParam(value = "table") String table, @RequestParam(value = "searchFields", required = false) String searchFields, @RequestParam(value = "query", required = false) String query) throws URISyntaxException, IOException {

        Page<AssuntoNfse> retorno;
        if (query == null || query.isEmpty()) {
            retorno = assuntoNfseFacade.recuperarAssuntos(pageable);
        } else {
            retorno = assuntoNfseFacade.recuperarAssuntosPorBuscaPerguntas(pageable, query);
        }

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(retorno.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/assunto", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<AssuntoNfse> createAssunto(@RequestBody AssuntoNfse assunto) {
        try {
            assuntoNfseFacade.salvar(assunto);
            return new ResponseEntity<AssuntoNfse>(assunto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<AssuntoNfse>(null, setMessageError(e.getCause().getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/assunto", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<AssuntoNfse> updateAssunto(@RequestBody AssuntoNfse assunto) {
        try {
            assuntoNfseFacade.salvar(assunto);
            return new ResponseEntity<AssuntoNfse>(assunto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<AssuntoNfse>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/assunto/{id}")
    public ResponseEntity<AssuntoNfse> deleteAssunto(@PathVariable("id") Long id) {
        try {
            AssuntoNfse assunto = assuntoNfseFacade.recuperar(id);
            assuntoNfseFacade.remover(assunto);
            return new ResponseEntity<AssuntoNfse>(assunto, HttpStatus.OK);
        } catch (Exception e) {
            Throwable exception = BuscaCausa.desenrolarException(e);
            if (exception.getMessage().contains("child record found")) {
                return new ResponseEntity<AssuntoNfse>(new AssuntoNfse(), setMessageError("Erro ao excluir. Assunto possui perguntas relacionadas."), HttpStatus.OK);
            }
            return new ResponseEntity<AssuntoNfse>(new AssuntoNfse(), setMessageError("Erro desconhecido ao tentar excluir assunto."), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/assuntos-ordem-by-ordem",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<AssuntoNfse>> recuperarAssuntosOrdenadoPorOrdem() {
        List<AssuntoNfse> assuntos = assuntoNfseFacade.recuperarAssuntosOrdenadoPorOrdem();
        return new ResponseEntity(assuntos, HttpStatus.OK);
    }
}

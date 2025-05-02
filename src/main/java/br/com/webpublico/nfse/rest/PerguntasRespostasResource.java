package br.com.webpublico.nfse.rest;


import br.com.webpublico.nfse.domain.perguntasrespostas.PerguntasRespostas;
import br.com.webpublico.nfse.domain.dtos.PerguntasRespostasDTO;
import br.com.webpublico.nfse.facades.PerguntasRespostasFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.Serializable;
import java.util.List;

@RequestMapping("/nfse")
@Controller
public class PerguntasRespostasResource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(PerguntasRespostasResource.class);
    private PerguntasRespostasFacade perguntasRespostasFacade;

    @PostConstruct
    public void init() {
        try {
            perguntasRespostasFacade = (PerguntasRespostasFacade) new InitialContext().lookup("java:module/PerguntasRespostasFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/perguntas-respostas-para-exibicao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PerguntasRespostasDTO>> recuperarAll() {
        List<PerguntasRespostas> perguntasRespostas = perguntasRespostasFacade.buscarPergunstasRespostasParaExibicao();
        return new ResponseEntity(PerguntasRespostas.toListPerguntasRespostasDTO(perguntasRespostas), HttpStatus.OK);
    }
}

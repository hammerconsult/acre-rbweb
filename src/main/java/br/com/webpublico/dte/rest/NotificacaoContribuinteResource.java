package br.com.webpublico.dte.rest;

import br.com.webpublico.dte.facades.NotificacaoContribuinteDteFacade;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import java.io.Serializable;

@RequestMapping("/nfse")
@Controller
public class NotificacaoContribuinteResource implements Serializable {

    private static final Logger log = LoggerFactory.getLogger(NotificacaoContribuinteResource.class);

    private NotificacaoContribuinteDteFacade facade;

    @PostConstruct
    public void init() {
        try {
            facade = (NotificacaoContribuinteDteFacade) Util.getFacadeViaLookup("java:module/NotificacaoContribuinteDteFacade");
        } catch (Exception e){
            log.error("Erro {}", e);
        }
    }

    @RequestMapping(value = "/notificacao-contribuinte/gerar-documento", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<byte[]> gerarDocumento(@RequestParam Long idNotificacaoDoc, @RequestParam Long idUsuarioWeb) throws Exception {
        byte[] dados = facade.gerarDocumento(idNotificacaoDoc, idUsuarioWeb);
        return new ResponseEntity<>(dados, HttpStatus.OK);
    }

}

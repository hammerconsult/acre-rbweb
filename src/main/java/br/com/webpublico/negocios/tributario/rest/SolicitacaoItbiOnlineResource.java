package br.com.webpublico.negocios.tributario.rest;

import br.com.webpublico.entidades.SolicitacaoItbiOnline;
import br.com.webpublico.negocios.SolicitacaoItbiOnlineFacade;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@RequestMapping("/integracao/tributario/solicitacao-itbi-online")
@Controller
public class SolicitacaoItbiOnlineResource {

    private final static Logger logger = LoggerFactory.getLogger(SolicitacaoItbiOnlineResource.class);

    private SolicitacaoItbiOnlineFacade solicitacaoItbiOnlineFacade;

    @PostConstruct
    public void init() {
        try {
            solicitacaoItbiOnlineFacade = (SolicitacaoItbiOnlineFacade) Util.getFacadeViaLookup("java:module/SolicitacaoItbiOnlineFacade");
        } catch (Exception e) {
            logger.error("Erro ao recuperar facades na SolicitacaoItbiOnlineResource. ", e);
        }
    }

    @RequestMapping(value = "/homologar", method = RequestMethod.GET)
    public void homologarSolicitacao(@RequestParam Long idSolicitacao,
                                     @RequestParam String usuario) {
        try {
            SolicitacaoItbiOnline solicitacaoItbiOnline = solicitacaoItbiOnlineFacade.recuperar(idSolicitacao);
            AssistenteBarraProgresso assistente = new AssistenteBarraProgresso();
            assistente.setSelecionado(solicitacaoItbiOnline);
            solicitacaoItbiOnlineFacade.homologarSolicitacao(assistente, usuario, null, null, null);
        } catch (Exception e) {
            logger.error("Erro ao homologar solicitação de itbi online.", e);
        }
    }


    @RequestMapping(value = "/imprimir-laudo-itbi", method = RequestMethod.GET)
    public ResponseEntity<byte[]> imprimirLaudoItbi(@RequestParam Long idSolicitacao) {
        try {
            return ResponseEntity.ok(solicitacaoItbiOnlineFacade.imprimirLaudoItbi(idSolicitacao));
        } catch (Exception e) {
            logger.error("Erro ao gerar laudo de itbi da solicitação de itbi online.", e);
            return ResponseEntity.badRequest().body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

}

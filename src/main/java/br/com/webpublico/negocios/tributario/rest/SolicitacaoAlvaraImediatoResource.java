package br.com.webpublico.negocios.tributario.rest;

import br.com.webpublico.entidades.AlvaraConstrucao;
import br.com.webpublico.entidades.SolicitacaoAlvaraImediato;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.AlvaraConstrucaoFacade;
import br.com.webpublico.negocios.SolicitacaoAlvaraImediatoFacade;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;

@RequestMapping("/integracao/tributario/solicitacao-alvara-imediato")
@Controller
public class SolicitacaoAlvaraImediatoResource {

    private final static Logger logger = LoggerFactory.getLogger(SolicitacaoAlvaraImediatoResource.class);

    private SolicitacaoAlvaraImediatoFacade solicitacaoAlvaraImediatoFacade;
    private AlvaraConstrucaoFacade alvaraConstrucaoFacade;

    @PostConstruct
    public void init() {
        try {
            solicitacaoAlvaraImediatoFacade = (SolicitacaoAlvaraImediatoFacade) Util.getFacadeViaLookup("java:module/SolicitacaoAlvaraImediatoFacade");
            alvaraConstrucaoFacade = (AlvaraConstrucaoFacade) Util.getFacadeViaLookup("java:module/AlvaraConstrucaoFacade");
        } catch (Exception e) {
            logger.error("Erro ao recuperar facades na SolicitacaoAlvaraImediatoResource. ", e);
        }
    }

    @RequestMapping(value = "/aprovar", method = RequestMethod.GET)
    public ResponseEntity<byte[]> aprovarSolicitacao(@RequestParam Long idSolicitacao) {
        try {
            SolicitacaoAlvaraImediato solicitacao = solicitacaoAlvaraImediatoFacade.recuperar(idSolicitacao);
            AlvaraConstrucao alvaraConstrucao = solicitacaoAlvaraImediatoFacade.aprovarSolicitacao(solicitacao, null);
            alvaraConstrucao = solicitacaoAlvaraImediatoFacade.gerarCalculoAlvaraConstrucao(alvaraConstrucao);
            byte[] dams = solicitacaoAlvaraImediatoFacade.gerarDAMs(alvaraConstrucao);
            return new ResponseEntity<>(dams, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro ao aprovar solicitação de alvará imediato.", e);
        }
        return null;
    }

    @RequestMapping(value = "/imprimir-dam", method = RequestMethod.GET)
    public ResponseEntity<byte[]> imprimirDam(@RequestParam Long idAlvaraConstrucao) {
        try {
            AlvaraConstrucao alvaraConstrucao = alvaraConstrucaoFacade.recuperar(idAlvaraConstrucao);
            return ResponseEntity.ok().body(alvaraConstrucaoFacade.gerarBytesDAM(alvaraConstrucao));
        } catch (ValidacaoException ve) {
            logger.error("Erro ao imprimir dam da solicitação de alvará imediato.", ve);
            return ResponseEntity.badRequest().body(ve.getMensagens().get(0).getDetail().getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Erro ao imprimir dam da solicitação de alvará imediato.", e);
            return ResponseEntity.badRequest().body("Não foi possivel emitir o dam.".getBytes(StandardCharsets.UTF_8));
        }
    }

    @RequestMapping(value = "/imprimir-alvara", method = RequestMethod.GET)
    public ResponseEntity<byte[]> imprimirAlvara(@RequestParam Long idAlvaraConstrucao) {
        try {
            AlvaraConstrucao alvaraConstrucao = alvaraConstrucaoFacade.recuperar(idAlvaraConstrucao);
            return new ResponseEntity<>(alvaraConstrucaoFacade.gerarBytesAlvara(alvaraConstrucao), HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro ao imprimir alvará da solicitação de alvará imediato.", e);
        }
        return null;
    }
}

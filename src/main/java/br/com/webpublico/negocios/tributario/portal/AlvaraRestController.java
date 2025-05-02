package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.ProcessoCalculoAlvara;
import br.com.webpublico.negocios.CalculoAlvaraFacade;
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

@Controller
@RequestMapping("/integracao/tributario")
public class AlvaraRestController {

    private static final Logger logger = LoggerFactory.getLogger(AlvaraRestController.class);

    private CalculoAlvaraFacade calculoAlvaraFacade;

    @PostConstruct
    public void init() {
        try {
            calculoAlvaraFacade = (CalculoAlvaraFacade) Util.getFacadeViaLookup("java:module/CalculoAlvaraFacade");
        } catch (Exception e) {
            logger.error("Erro ao recuperar facades na AlvaraRestController. {}", e.getMessage());
            logger.debug("Detalhes do erro ao recuperar facades na AlvaraRestController. ", e);
        }
    }

    @RequestMapping(value = "/imprimir-declaracao-dispensa-licenciamento", method = RequestMethod.GET)
    public ResponseEntity<byte[]> imprimirDeclaracaoDispensaLicenciamento(@RequestParam Long idProcessoCalculo) {
        try {
            ProcessoCalculoAlvara processoCalculoAlvara = calculoAlvaraFacade.recuperar(idProcessoCalculo);
            byte[] dados = calculoAlvaraFacade.gerarDeclaracaoDispensaLicenciamento(processoCalculoAlvara);
            return new ResponseEntity<>(dados, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro ao imprimir declaração de dispensa de licenciamento. {}", e.getMessage());
            logger.debug("Detalhes do erro ao imprimir declaração de dispensa de licenciamento.", e);
        }
        return null;
    }
}

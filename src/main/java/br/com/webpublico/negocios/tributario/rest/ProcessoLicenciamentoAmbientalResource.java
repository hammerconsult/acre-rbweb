package br.com.webpublico.negocios.tributario.rest;

import br.com.webpublico.DateUtils;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.tributario.ProcessoLicenciamentoAmbiental;
import br.com.webpublico.entidades.tributario.ProcessoLicenciamentoAmbientalUnidadeMedida;
import br.com.webpublico.negocios.AlvaraConstrucaoFacade;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.SolicitacaoAlvaraImediatoFacade;
import br.com.webpublico.negocios.tributario.ProcessoLicenciamentoAmbientalFacade;
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
import java.math.BigDecimal;
import java.util.Date;

@RequestMapping("/integracao/tributario/processo-licenciamento-ambiental")
@Controller
public class ProcessoLicenciamentoAmbientalResource {

    private final static Logger logger = LoggerFactory.getLogger(ProcessoLicenciamentoAmbientalResource.class);

    private ProcessoLicenciamentoAmbientalFacade processoLicenciamentoAmbientalFacade;
    private ExercicioFacade exercicioFacade;

    @PostConstruct
    public void init() {
        try {
            processoLicenciamentoAmbientalFacade = (ProcessoLicenciamentoAmbientalFacade) Util.getFacadeViaLookup("java:module/ProcessoLicenciamentoAmbientalFacade");
            exercicioFacade = (ExercicioFacade) Util.getFacadeViaLookup("java:module/ExercicioFacade");
        } catch (Exception e) {
            logger.error("Erro ao recuperar facades na ProcessoLicenciamentoAmbientalResource. {}", e.getMessage());
            logger.debug("Detalhes do erro ao recuperar facades na ProcessoLicenciamentoAmbientalResource. ", e);
        }
    }

    @RequestMapping(value = "/finalizar-processo-portal", method = RequestMethod.GET)
    public ResponseEntity<Long> finalizarProcessoPortal(@RequestParam Long idProcesso) {
        try {
            ProcessoLicenciamentoAmbiental processoLicenciamentoAmbiental = processoLicenciamentoAmbientalFacade.recuperar(idProcesso);
            processoLicenciamentoAmbientalFacade.finalizarProcessoPortal(processoLicenciamentoAmbiental);
            return ResponseEntity.ok(idProcesso);
        } catch (Exception e) {
            logger.error("Erro ao finalizar processo de licenciamento ambiental do portal. {}", e.getMessage());
            logger.debug("Detalhes do erro ao finalizar processo de licenciamento ambiental do portal. ", e);
        }
        return ResponseEntity.badRequest().body(null);
    }

    @RequestMapping(value = "/imprimir", method = RequestMethod.GET)
    public ResponseEntity<byte[]> imprimir(@RequestParam Long idProcesso) {
        try {
            ProcessoLicenciamentoAmbiental processo = processoLicenciamentoAmbientalFacade.recuperar(idProcesso);
            processo = processoLicenciamentoAmbientalFacade.gerarDocumentoOficial(processo);
            byte[] bytes = processo.getDocumentoOficial().getConteudo().getBytes();
            return new ResponseEntity<>(bytes, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro ao imprimir documento do licenciamento ambiental. {}", e.getMessage());
            logger.debug("Detalhes do erro ao imprimir documento do licenciamento ambiental.", e);
        }
        return null;
    }
}

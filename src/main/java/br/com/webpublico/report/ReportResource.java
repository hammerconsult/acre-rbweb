package br.com.webpublico.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;

@Controller
@RequestMapping("/report")
public class ReportResource {

    private final Logger log = LoggerFactory.getLogger(ReportResource.class);

    @RequestMapping(value = "/publicar", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void publicar(@RequestParam("uuid") String uuid) {
        log.debug("REST imprimir .....");
        try {
            ReportService.getInstance().publicarRelatorio(uuid);
        } catch (Exception e) {
            log.error("Erro ao publicar o relatório ", e.getMessage());
        }
    }

    @RequestMapping(value = "/porcentagem", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void publicar(@RequestParam("uuid") String uuid, @RequestParam("porcentagem") BigDecimal porcentagem) {
        log.debug("REST porcentagem .....");
        try {
            ReportService.getInstance().porcentagemRelatorio(uuid, porcentagem);
        } catch (Exception e) {
            log.error("Erro ao atribuir a porcentagem do relatório ", e.getMessage());
        }
    }

    @RequestMapping(value = "/error", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public void error(@RequestParam("uuid") String uuid, @RequestParam("error") String error) {
        log.debug("REST error .....");
        try {
            ReportService.getInstance().alterarErro(uuid, error);
        } catch (Exception e) {
            log.error("Erro ao atribuir error do relatório ", e.getMessage());
        }
    }

}

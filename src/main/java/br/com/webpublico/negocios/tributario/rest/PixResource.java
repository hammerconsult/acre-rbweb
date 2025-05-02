package br.com.webpublico.negocios.tributario.rest;

import br.com.webpublico.entidadesauxiliares.WrapperPayloadPixDTO;
import br.com.webpublico.negocios.PixFacade;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import java.io.Serializable;

@RequestMapping("/pix")
@Controller
public class PixResource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(PixResource.class);

    private PixFacade pixFacade;

    @PostConstruct
    public void init() {
        try {
            pixFacade = (PixFacade) Util.getFacadeViaLookup("java:module/PixFacade");
        } catch (Exception e) {
            logger.error("Erro ao recuperar pixFacade. ", e);
        }
    }

    @RequestMapping(value = "/webhook/processar-pagamento", method = RequestMethod.POST)
    public void processarPagamentoPix(@RequestBody String payload) {
        try {
            WrapperPayloadPixDTO wrapperPix = WrapperPayloadPixDTO.toObject(payload);
            pixFacade.processarPagamentoPix(wrapperPix.getPayloads());
            logger.debug("Processando pagamento pix. {}", payload);
        } catch (Exception e) {
            logger.error("Erro ao processar pagamento pix. ", e);
        }
    }
}

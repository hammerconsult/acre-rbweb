package br.com.webpublico.nfse.rest;

import br.com.webpublico.nfse.facades.ConfiguracaoNfseFacade;
import br.com.webpublico.util.Html2Pdf;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by William on 13/09/2017.
 */
@RequestMapping("/nfse")
@Controller
public class ConfiguracaoNfseResource implements Serializable {

    private final Logger logger = LoggerFactory.getLogger(ConfiguracaoNfseResource.class);

    private ConfiguracaoNfseFacade configuracaoNfseFacade;

    @PostConstruct
    public void init() {
        try {
            configuracaoNfseFacade = (ConfiguracaoNfseFacade) new InitialContext().lookup("java:module/ConfiguracaoNfseFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/gerar-pdf", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<byte[]> imprimir(@RequestBody String conteudo) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Html2Pdf.convert(conteudo, baos);
        return new ResponseEntity(baos.toByteArray(), HttpStatus.OK);
    }

}

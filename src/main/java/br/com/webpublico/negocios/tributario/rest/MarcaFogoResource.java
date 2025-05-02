package br.com.webpublico.negocios.tributario.rest;

import br.com.webpublico.entidades.MarcaFogo;
import br.com.webpublico.negocios.MarcaFogoFacade;
import br.com.webpublico.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/integracao/tributario/marca-fogo")
@Controller
public class MarcaFogoResource {

    private final static Logger logger = LoggerFactory.getLogger(MarcaFogoResource.class);

    private MarcaFogoFacade marcaFogoFacade;

    public MarcaFogoFacade getMarcaFogoFacade() {
        if (marcaFogoFacade == null) {
            marcaFogoFacade = (MarcaFogoFacade) Util.getFacadeViaLookup("java:module/MarcaFogoFacade");
        }
        return marcaFogoFacade;
    }

    @RequestMapping(value = "/imprimir-certidao", method = RequestMethod.GET)
    public ResponseEntity<byte[]> imprimirCertidao(@RequestParam Long idCertidao) {
        try {
            MarcaFogo marcaFogo = getMarcaFogoFacade().recuperar(idCertidao);
            marcaFogo = getMarcaFogoFacade().gerarDocumentoOficial(marcaFogo);
            byte[] bytes = marcaFogo.getDocumentoOficial().getConteudo().getBytes();
            return new ResponseEntity<>(bytes, HttpStatus.OK);
        } catch (Exception e) {
            logger.error("Erro ao imprimir certidão de marca a fogo. {}", e.getMessage());
            logger.debug("Detalhes do erro ao imprimir certidão de marca a fogo.", e);
        }
        return null;
    }
}

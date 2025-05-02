package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.ws.model.WSDocumentoPortalServidor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/integracao/documentos")
public class DocumentoPortalRestController extends PortalRestController {

    @RequestMapping(value = "/get-documentos-portal",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSDocumentoPortalServidor>> buscarDocumentosPortal() {
        try {
            List<WSDocumentoPortalServidor> documentos = getPortalContribunteFacade().buscarDocumentosParaPortal();
            return new ResponseEntity<>(documentos, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

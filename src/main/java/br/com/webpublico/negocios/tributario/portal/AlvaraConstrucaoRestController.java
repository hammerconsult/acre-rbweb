package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.ws.model.WsAlvaraConstrucao;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/integracao/tributario")
public class AlvaraConstrucaoRestController extends PortalRestController {

    @RequestMapping(value = "/autenticidade-documento/alvara-construcao/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WsAlvaraConstrucao> buscarInformacoesAlvaraConstrucao(@PathVariable("id") Long id) {
        try {
            WsAlvaraConstrucao wsAlvaraConstrucao = getPortalContribunteFacade().buscarAlvaraDeConstrucao(id);
            if (wsAlvaraConstrucao != null) {
                return new ResponseEntity<>(wsAlvaraConstrucao, HttpStatus.OK);
            }
            return null;
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}

package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.ws.model.WsAlvaraConstrucao;
import br.com.webpublico.ws.model.WsTermoUso;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/integracao/tributario")
public class TermoUsoRestController extends PortalRestController {

    @RequestMapping(value = "/termo-uso-vigente",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WsTermoUso> buscarTermoUsoVigente() {
        try {
            WsTermoUso wsTermoUso = getPortalContribunteFacade().buscarTermoUsoVigente();
            if (wsTermoUso != null) {
                return new ResponseEntity<>(wsTermoUso, HttpStatus.OK);
            }
            return null;
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(value = "/has-termo-uso-para-aceite",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> hasTermoUsoParaAceite(@RequestParam Long idPessoa) {
        try {
            return new ResponseEntity<>(getPortalContribunteFacade().hasTermoUsoVigente(idPessoa), HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }


    @RequestMapping(value = "/aceitar-termo-uso",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void aceitarTermoUsso(@RequestParam Long idPessoa, @RequestParam Long idTermo) {
        getPortalContribunteFacade().aceitarTermoUso(idPessoa, idTermo);
    }
}

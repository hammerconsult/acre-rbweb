package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.ContadorAcessosPortal;
import br.com.webpublico.entidades.FaleConosco;
import br.com.webpublico.ws.model.WSFaleConosco;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/integracao/geral/")
public class FaleConoscoRestController extends PortalRestController {

    @RequestMapping(value = "/fale-conosco",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSFaleConosco> criarFaleConosco(@RequestBody WSFaleConosco faleConosco) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.FALE_CONOSCO, faleConosco.getNome());
            getPortalContribunteFacade().salvarFaleConosco(new FaleConosco(
                    faleConosco.getAssunto(),faleConosco.getNome(),faleConosco.getEmail(),faleConosco.getTelefone(),faleConosco.getMensagem(),Boolean.FALSE));
            return new ResponseEntity<>(faleConosco, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

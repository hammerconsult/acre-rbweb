package br.com.webpublico.negocios.comum.portal;

import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.portal.PortalRestController;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/integracao/webreport/")
public class WebreportRestController extends PortalRestController {

    @RequestMapping(value = "/get-url-webreport",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> buscarUrlWebreport() {
        try {
            ConfiguracaoDeRelatorio configuracaoDeRelatorio = getConfiguracaoDeRelatorioFacade().getConfiguracaoPorChave();
            if (configuracaoDeRelatorio != null) {
                return new ResponseEntity<>(configuracaoDeRelatorio.getUrl(), HttpStatus.OK);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (ValidacaoException ve) {
            return getResponseEntity(ve.getMensagens().get(0).getDetail());
        } catch (Exception e) {
            return getResponseEntity("Não foi possível recuperar o endereço do webreport");
        }
    }

    private ResponseEntity<String> getResponseEntity(String msg) {
        HttpHeaders header = new HttpHeaders();
        header.add("message-error", msg);
        return new ResponseEntity<>(null, header, HttpStatus.BAD_REQUEST);
    }
}

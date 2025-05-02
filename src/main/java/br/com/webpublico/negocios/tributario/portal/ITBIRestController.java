package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.ContadorAcessosPortal;
import br.com.webpublico.ws.model.WSItbi;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/integracao/tributario/itbis")
public class ITBIRestController extends PortalRestController {


    @RequestMapping(value = "/get-itbi-por-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> buscarImoveisPorCpfCnpj(@RequestParam(value = "cpf") String cpf,
                                                     @RequestParam(value = "first", required = false) Integer first,
                                                     @RequestParam(value = "max", required = false) Integer max) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.ITBI, cpf);
            sistemaService.setUsuarioAlternativo(cpf);
            List<WSItbi> itbis = getPortalContribunteFacade().buscarCalculosItbi(cpf, first, max);
            return ResponseEntity.ok().body(itbis);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @RequestMapping(value = "/count-itbi-por-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Integer> contarImoveisPorCpfCnpj(@RequestParam(value = "cpf") String cpf) {
        try {
            sistemaService.setUsuarioAlternativo(cpf);
            Integer itbi = getPortalContribunteFacade().contarCalculosItbi(cpf);
            return new ResponseEntity<>(itbi, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/gerar-itbi",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> geraRelatorio(@RequestBody Long idProcesso) {
        try {
            byte[] bytes = getPortalContribunteFacade().imprimirLaudoItbi(idProcesso).toByteArray();
            return ResponseEntity.ok().body(bytes);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return ResponseEntity.badRequest().body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

    @RequestMapping(value = "/gerar-itbi-base64",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity geraRelatorioBase64(@RequestParam("id") Long id) {
        try {
            byte[] bytes = getPortalContribunteFacade().imprimirLaudoItbi(id).toByteArray();
            Base64 codec = new Base64();
            String encoded = codec.encodeBase64String(bytes);
            return new ResponseEntity<>(encoded, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

}

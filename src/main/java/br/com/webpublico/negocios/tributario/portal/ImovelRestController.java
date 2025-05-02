package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.ContadorAcessosPortal;
import br.com.webpublico.entidades.comum.relatorio.ConfiguracaoDeRelatorio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.ws.model.WSImovel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Controller
@RequestMapping("/integracao/tributario/cadastro-imobiliario")
public class ImovelRestController extends PortalRestController {


    @RequestMapping(value = "/get-imoveis-por-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSImovel>> buscarImoveisPorCpfCnpj(@RequestParam(value = "cpf") String cpf,
                                                                  @RequestParam(value = "first", required = false) Integer first,
                                                                  @RequestParam(value = "max", required = false) Integer max) {
        sistemaService.setUsuarioAlternativo(cpf);
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.IMOVEL, cpf);
            List<WSImovel> imoveis = getPortalContribunteFacade().buscarCadastrosImobiliarios(cpf, first, max);
            return new ResponseEntity<>(imoveis, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-imoveis-por-pessoa-para-pesquisa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSImovel>> buscarImoveisPorCpfCnpjParaPesquisa(@RequestParam(value = "cpf", required = true) String cpf) {
        sistemaService.setUsuarioAlternativo(cpf);
        try {
            List<WSImovel> imoveis = getPortalContribunteFacade().buscarCadastrosImobiliariosParaPesquisa(cpf);
            return new ResponseEntity<>(imoveis, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/count-imoveis-por-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Integer> contarImoveisPorCpfCnpj(@RequestParam(value = "cpf", required = true) String cpf) {
        sistemaService.setUsuarioAlternativo(cpf);
        try {
            Integer imoveis = getPortalContribunteFacade().contarImoveisPorPessoa(cpf);
            return new ResponseEntity<>(imoveis, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/gerar-relatorio",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> gerarRelatorio(@RequestParam(value = "cpf", required = true) String cpf,
                                                 @RequestParam(value = "inscricao", required = true) String inscricao) {
        sistemaService.setUsuarioAlternativo(cpf);
        byte[] bytes = getPortalContribunteFacade().gerarRelatorioCadastroImobiliario(inscricao);
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }

    private ResponseEntity<byte[]> getResponseEntity(String msg) {
        HttpHeaders header = new HttpHeaders();
        header.add("message-error", msg);
        return new ResponseEntity<>(null, header, HttpStatus.BAD_REQUEST);
    }
}

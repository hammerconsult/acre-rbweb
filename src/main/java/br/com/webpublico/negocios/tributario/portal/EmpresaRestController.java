package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.ContadorAcessosPortal;
import br.com.webpublico.ws.model.WSAlvara;
import br.com.webpublico.ws.model.WSEmpresa;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/integracao/tributario/cadastro-economico")
public class EmpresaRestController extends PortalRestController {


    @RequestMapping(value = "/get-empresas-por-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSEmpresa>> buscarEmpresasPorCpfCnpj(@RequestParam(value = "cpf") String cpf,
                                                                    @RequestParam(value = "first", required = false) Integer first,
                                                                    @RequestParam(value = "max", required = false) Integer max) {
        sistemaService.setUsuarioAlternativo(cpf);
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.EMPRESA, cpf);
            List<WSEmpresa> empresas = getPortalContribunteFacade().buscarCadastrosEconomicos(cpf, first, max);
            return new ResponseEntity<>(empresas, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-empresas-por-pessoa-para-pesquisa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSEmpresa>> buscarEmpresasPorCpfCnpj(@RequestParam(value = "cpf") String cpf) {
        sistemaService.setUsuarioAlternativo(cpf);

        try {
            List<WSEmpresa> empresas = getPortalContribunteFacade().buscarCadastrosEconomicos(cpf);
            return new ResponseEntity<>(empresas, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-alvaras-por-cmc",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> buscarAlvaras(@RequestParam(value = "cpf") String cpf,
                                           @RequestParam(value = "inscricao") String inscricao) {
        try {
            sistemaService.setUsuarioAlternativo(cpf);
            List<WSAlvara> alvaras = getPortalContribunteFacade().buscarAlvarasPorCmc(inscricao);
            return ResponseEntity.ok().body(alvaras);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return ResponseEntity.ok().body(e.getMessage());
        }
    }

    @RequestMapping(value = "/count-empresas-por-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Integer> contarEmpresasPorCpfCnpj(@RequestParam(value = "cpf") String cpf) {
        sistemaService.setUsuarioAlternativo(cpf);
        try {
            Integer empresas = getPortalContribunteFacade().contarEmpresasPorPessoa(cpf);
            return new ResponseEntity<>(empresas, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/gerar-relatorio",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> gerarRelatorio(@RequestParam(value = "cpf") String cpf,
                                                 @RequestParam(value = "inscricao") String inscricao) throws IOException {
        sistemaService.setUsuarioAlternativo(cpf);
        byte[] bytes = getPortalContribunteFacade().gerarRelatorioCadastroEconomico(inscricao).toByteArray();
        return new ResponseEntity<>(bytes, HttpStatus.OK);
    }

    @RequestMapping(value = "/gerar-alvara",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> gerarAlvara(@RequestParam(value = "cpf") String cpf,
                                         @RequestParam(value = "alvara") Long alvara) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.ALVARA, cpf);
            sistemaService.setUsuarioAlternativo(cpf);
            ByteArrayOutputStream byteArrayOutputStream = getPortalContribunteFacade().gerarRelatorioAlvaraPortal(cpf, alvara);
            if (byteArrayOutputStream == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(byteArrayOutputStream.toByteArray());
        } catch (Exception e) {
            getLogger().error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }
}

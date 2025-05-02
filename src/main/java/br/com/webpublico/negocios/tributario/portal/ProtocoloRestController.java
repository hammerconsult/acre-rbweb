package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.ContadorAcessosPortal;
import br.com.webpublico.entidades.DocumentoOficial;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.Processo;
import br.com.webpublico.enums.TipoDocumentoProtocolo;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.ProcessoFacade;
import br.com.webpublico.util.Html2Pdf;
import br.com.webpublico.ws.model.WSProcesso;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.naming.InitialContext;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Controller
@RequestMapping("/integracao/tributario/protocolo")
public class ProtocoloRestController extends PortalRestController {


    private ProcessoFacade processoFacade;
    private PessoaFacade pessoaFacade;

    public ProcessoFacade getProcessoFacade() {
        if (processoFacade == null) {
            try {
                processoFacade = (ProcessoFacade) new InitialContext().lookup("java:module/ProcessoFacade");
            } catch (Exception e) {
                getLogger().error(e.getMessage());
            }
        }
        return processoFacade;
    }

    public PessoaFacade getPessoaFacade() {
        if (pessoaFacade == null) {
            try {
                pessoaFacade = (PessoaFacade) new InitialContext().lookup("java:module/PessoaFacade");
            } catch (Exception e) {
                getLogger().error(e.getMessage());
            }
        }
        return pessoaFacade;
    }

    @RequestMapping(value = "/get-protocolos-por-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> buscarProtocolosPorCpfCnpj(@RequestParam(value = "cpf") String cpf,
                                                                    @RequestParam(value = "first", required = false) Integer first,
                                                                    @RequestParam(value = "max", required = false) Integer max) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.PROTOCOLO, cpf);
            List<WSProcesso> protocolos = getPortalContribunteFacade().buscarProtocolosPorPessoaPortalWeb(cpf, first, max);
            return ResponseEntity.ok().body(protocolos);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @RequestMapping(value = "/count-protocolos-por-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Integer> contarImoveisPorCpfCnpj(@RequestParam(value = "cpf") String cpf) {
        try {
            Integer protocolos = getPortalContribunteFacade().contarProtocolosPorPessoaPortalWeb(cpf);
            return new ResponseEntity<>(protocolos, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-protocolos-por-senha",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WSProcesso> getDocumentoProtocoloPorSenha(@RequestParam(value = "senha", required = true) String senha,
                                                                    @RequestParam(value = "numero", required = true) Integer numero,
                                                                    @RequestParam(value = "ano", required = true) Integer ano) {
        Processo p = getProcessoFacade().getDocumentoProtocoloPorSenhaNumeroEAno(senha, numero, ano);
        singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.PROTOCOLO, "");
        if (p != null) {
            WSProcesso processo = new WSProcesso(p);
            return new ResponseEntity<>(processo, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/gerar-pdf-base64",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> gerarProtocolo(@RequestParam(value = "numero", required = true) Integer numero, @RequestParam(value = "ano", required = true) Integer ano) {
        Processo selecionado = getProcessoFacade().recuperarProcessoPorNumeroAndAno(numero, ano);
        selecionado = getProcessoFacade().recuperar(selecionado.getId());
        Pessoa pessoa = getPessoaFacade().recuperar(selecionado.getPessoa().getId());
        try {
            DocumentoOficial documentoOficial = getProcessoFacade().geraDocumento(TipoDocumentoProtocolo.PROTOCOLO_WEB, selecionado, null, pessoa);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Html2Pdf.convert(documentoOficial.getConteudo(), baos);
            byte[] bytes = baos.toByteArray();
            Base64 codec = new Base64();
            String encoded = codec.encodeBase64String(bytes);
            return new ResponseEntity<>(encoded, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

}

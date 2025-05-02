package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.ContadorAcessosPortal;
import br.com.webpublico.enums.TipoCadastroDoctoOficial;
import br.com.webpublico.ws.model.WSSolicitacaoDocumentoOficial;
import br.com.webpublico.ws.model.WSTipoDocumentoOficial;
import br.com.webpublico.ws.model.WsValidacaoSolicitacaoDoctoOficial;
import org.apache.commons.codec.binary.Base64;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Created by Wellington Abdo on 29/08/2016.
 */
@Controller
@RequestMapping("/integracao/tributario/documento-oficial")
public class DocumentoOficialRestController extends PortalRestController {


    @RequestMapping(value = "/solicitar-docto-oficial-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WsValidacaoSolicitacaoDoctoOficial> solicitarDoctoOficialDaPessoa(@RequestParam(value = "cpf") String cpf,
                                                                                            @RequestParam(value = "tipo", required = false) Long tipo) {
        WsValidacaoSolicitacaoDoctoOficial validacao = new WsValidacaoSolicitacaoDoctoOficial();
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.SOLICITACAO_DOCUMENTO, cpf);

            sistemaService.setUsuarioAlternativo(cpf);
            validacao = getPortalContribunteFacade().gerarSolicitacaoParaPessoa(cpf, tipo);
            return new ResponseEntity<>(validacao, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(validacao, HttpStatus.OK);
    }

    @RequestMapping(value = "/solicitar-docto-oficial-economico",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WsValidacaoSolicitacaoDoctoOficial> solicitarDoctoOficialDaCadastroEconomico(@RequestParam(value = "cpf", required = true) String cpf,
                                                                                                       @RequestParam(value = "cmc", required = true) String cmc,
                                                                                                       @RequestParam(value = "tipo", required = false) Long tipo) {

        WsValidacaoSolicitacaoDoctoOficial validacao = new WsValidacaoSolicitacaoDoctoOficial();
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.SOLICITACAO_DOCUMENTO, cpf);
            sistemaService.setUsuarioAlternativo(cpf);
            validacao = getPortalContribunteFacade().gerarSolicitacaoParaCadastroEconomico(cmc, tipo);
            return new ResponseEntity<>(validacao, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(validacao, HttpStatus.OK);
    }

    @RequestMapping(value = "/solicitar-docto-oficial-imobiliario",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<WsValidacaoSolicitacaoDoctoOficial> solicitarDoctoOficialDaCadastroImobiliario(@RequestParam(value = "cpf", required = true) String cpf,
                                                                                                         @RequestParam(value = "inscricao", required = true) String inscricao,
                                                                                                         @RequestParam(value = "tipo", required = false) Long tipo) {
        WsValidacaoSolicitacaoDoctoOficial validacao = new WsValidacaoSolicitacaoDoctoOficial();
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.SOLICITACAO_DOCUMENTO, cpf);
            sistemaService.setUsuarioAlternativo(cpf);
            validacao = getPortalContribunteFacade().gerarSolicitacaoParaCadastroImobiliario(inscricao, tipo);
            return new ResponseEntity<>(validacao, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(validacao, HttpStatus.OK);
    }

    @RequestMapping(value = "/get-solicitacoes-docto-oficial-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSSolicitacaoDocumentoOficial>> buscarSolicitacoesDoctoOficialDaPessoa(@RequestParam(value = "cpf", required = true) String cpf,
                                                                                                      @RequestParam(value = "first", required = false) Integer first,
                                                                                                      @RequestParam(value = "max", required = false) Integer max) {
        try {
            List<WSSolicitacaoDocumentoOficial> solicitacoes = getPortalContribunteFacade().buscarSolicitacoesPortalWeb(cpf, first, max);
            return new ResponseEntity<>(solicitacoes, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/count-solicitacoes-docto-oficial-pessoa",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Integer> buscarQuantidadeSolicitacoesDoctoOficialDaPessoa(@RequestParam(value = "cpf", required = true) String cpfCnpj) {
        try {
            Integer quantidade = getPortalContribunteFacade().buscarQuantidadeSolicitacoesPortalWeb(cpfCnpj);
            return new ResponseEntity<>(quantidade, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-tipos-docto-oficial-pessoa-fisica",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSTipoDocumentoOficial>> buscarTiposDocumentoOficialPessoaFisica() {
        try {
            List<WSTipoDocumentoOficial> tipos = getPortalContribunteFacade().buscarTiposDocumentoPorTipoCadastro(TipoCadastroDoctoOficial.PESSOAFISICA);
            return new ResponseEntity<>(tipos, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-tipos-docto-oficial-pessoa-juridica",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSTipoDocumentoOficial>> buscarTiposDocumentoOficialPessoaJuridica() {
        try {
            List<WSTipoDocumentoOficial> imoveis = getPortalContribunteFacade().buscarTiposDocumentoPorTipoCadastro(TipoCadastroDoctoOficial.PESSOAJURIDICA);
            return new ResponseEntity<>(imoveis, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-tipos-docto-oficial-cadastro-economico",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSTipoDocumentoOficial>> buscarTiposDocumentoOficialCadastroEconomico() {
        try {
            List<WSTipoDocumentoOficial> imoveis = getPortalContribunteFacade().buscarTiposDocumentoPorTipoCadastro(TipoCadastroDoctoOficial.CADASTROECONOMICO);
            return new ResponseEntity<>(imoveis, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-tipos-docto-oficial-cadastro-imobiliario",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSTipoDocumentoOficial>> buscarTiposDocumentoOficialCadastroImobiliario() {
        try {
            List<WSTipoDocumentoOficial> imoveis = getPortalContribunteFacade().buscarTiposDocumentoPorTipoCadastro(TipoCadastroDoctoOficial.CADASTROIMOBILIARIO);
            return new ResponseEntity<>(imoveis, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-tipos-docto-oficial-cadastro-rural",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<WSTipoDocumentoOficial>> buscarTiposDocumentoOficialCadastroRural() {
        try {
            List<WSTipoDocumentoOficial> imoveis = getPortalContribunteFacade().buscarTiposDocumentoPorTipoCadastro(TipoCadastroDoctoOficial.CADASTRORURAL);
            return new ResponseEntity<>(imoveis, HttpStatus.OK);
        } catch (Exception e) {
            getLogger().error("Exception", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/gerar-documento",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> getConteudoDocumentoOficial(@RequestBody WSSolicitacaoDocumentoOficial sol) {
        try {
            byte[] bytes = getPortalContribunteFacade().gerarConteudoDocumentoOficial(sol).getBytes(StandardCharsets.UTF_8);
            return ResponseEntity.ok().body(bytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

    @RequestMapping(value = "/gerar-documento-base64",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<String> getConteudoDocumentoOficialBase64(@RequestParam(value = "id") Long id) {
        try {
            WSSolicitacaoDocumentoOficial ws = new WSSolicitacaoDocumentoOficial();
            ws.setId(id);
            String conteudo = getPortalContribunteFacade().gerarConteudoDocumentoOficial(ws);
            byte[] bytes = conteudo.getBytes(StandardCharsets.UTF_8);
            String encoded = Base64.encodeBase64String(bytes);
            return ResponseEntity.ok(encoded);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}

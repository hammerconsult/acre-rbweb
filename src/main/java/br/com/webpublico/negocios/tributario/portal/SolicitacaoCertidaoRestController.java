package br.com.webpublico.negocios.tributario.portal;

import br.com.webpublico.entidades.ContadorAcessosPortal;
import br.com.webpublico.entidadesauxiliares.DadosSolicitacaoCertidaoPortal;
import br.com.webpublico.entidadesauxiliares.RetornoCertidaoDebitos;
import br.com.webpublico.entidadesauxiliares.ValidacaoSolicitacaoDoctoOficial;
import br.com.webpublico.tributario.SolicitacaoDocumentoOficialDTO;
import br.com.webpublico.ws.model.WSImovel;
import br.com.webpublico.ws.model.WSPessoa;
import br.com.webpublico.ws.model.WsDadosPessoaisSolicitacaoCertidao;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.nio.charset.StandardCharsets;

@Controller
@RequestMapping("/integracao/tributario/documento-oficial")
public class SolicitacaoCertidaoRestController extends PortalRestController {

    @RequestMapping(value = "/buscar-wsimovel-certidao",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> buscarWsImovel(@RequestBody WsDadosPessoaisSolicitacaoCertidao dados) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.SOLICITACAO_DOCUMENTO, dados.getCpfCnpj());
            sistemaService.setUsuarioAlternativo(dados.getCpfCnpj());
            WSImovel imovel = getPortalContribunteFacade().getSolicitacaoCertidaoPortalFacade().buscarWsImovel(dados);
            if (imovel == null) throw new Exception("Imóvel não encontrado.");
            String mensagemRetorno;
            if (dados.isCadastro()) {
                mensagemRetorno = "Não é possivel emitir a " + dados.getTipoDocumentoOficialPortal().getDescricao() + " para o cadastro " + dados.getInscricaoCadastral();
            } else {
                mensagemRetorno = "Não é possivel emitir a " + dados.getTipoDocumentoOficialPortal().getDescricao() + " para pessoa " + (dados.isPessoaFisica() ? "fisica." : "jurídica.");
            }
            if (!getPortalContribunteFacade().getSolicitacaoCertidaoPortalFacade().certidaoLiberadaParaEmissaoPortal(dados)) {
                throw new Exception(mensagemRetorno);
            }
            return ResponseEntity.ok(imovel);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "/buscar-wspessoa-certidao",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<?> buscarWsPessoa(@RequestBody WsDadosPessoaisSolicitacaoCertidao dados) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.SOLICITACAO_DOCUMENTO, dados.getCpfCnpj());
            sistemaService.setUsuarioAlternativo(dados.getCpfCnpj());
            WSPessoa pessoa = getPortalContribunteFacade().getSolicitacaoCertidaoPortalFacade().buscarWsPessoa(dados);
            if (pessoa == null) throw new Exception("Pessoa não encontrada.");
            String mensagemRetorno;
            if (dados.isCadastro()) {
                mensagemRetorno = "Não é possivel emitir a " + dados.getTipoDocumentoOficialPortal().getDescricao() + " para o cadastro " + dados.getInscricaoCadastral();
            } else {
                mensagemRetorno = "Não é possivel emitir a " + dados.getTipoDocumentoOficialPortal().getDescricao() + " para pessoa " + (dados.isPessoaFisica() ? "fisica." : "jurídica.");
            }
            if (!getPortalContribunteFacade().getSolicitacaoCertidaoPortalFacade().certidaoLiberadaParaEmissaoPortal(dados)) {
                throw new Exception(mensagemRetorno);
            }
            return ResponseEntity.ok(pessoa);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @RequestMapping(value = "/imprimir-certidao-portalweb",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<byte[]> imprimirCertidao(@RequestBody WsDadosPessoaisSolicitacaoCertidao wsDados) {
        try {
            byte[] bytes = new byte[0];
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.SOLICITACAO_DOCUMENTO, wsDados.getCpfCnpj());
            sistemaService.setUsuarioAlternativo(wsDados.getCpfCnpj());
            ValidacaoSolicitacaoDoctoOficial validacao = getPortalContribunteFacade().getSolicitacaoCertidaoPortalFacade().imprimirCertidao(wsDados.getId(), montarDadosSolicitacao(wsDados));

            if (validacao != null) {
                if (!StringUtils.isBlank(validacao.getMensagemValidacao())) {
                    return ResponseEntity.badRequest().body(validacao.getMensagemValidacao().getBytes(StandardCharsets.UTF_8));
                } else {
                    bytes = validacao.getCertidao();
                }
            }
            return ResponseEntity.ok(bytes);
        } catch (Exception e) {
            getLogger().error("Exception", e);
            return ResponseEntity.badRequest().body(e.getMessage().getBytes(StandardCharsets.UTF_8));
        }
    }

    @RequestMapping(value = "/get-certidao-debitos",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RetornoCertidaoDebitos> getCertidaoDebitos(@RequestBody WsDadosPessoaisSolicitacaoCertidao wsDados) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.SOLICITACAO_DOCUMENTO, wsDados.getCpfCnpj());
            sistemaService.setUsuarioAlternativo(wsDados.getCpfCnpj());
            ValidacaoSolicitacaoDoctoOficial validacao = getPortalContribunteFacade().getSolicitacaoCertidaoPortalFacade()
                .imprimirCertidao(wsDados.getId(), montarDadosSolicitacao(wsDados));
            RetornoCertidaoDebitos retornoCertidaoDebitos = new RetornoCertidaoDebitos();
            retornoCertidaoDebitos.setMensagem(validacao.getMensagemValidacao());
            retornoCertidaoDebitos.setSolicitacaoDocumentoOficial(validacao.getSolicitacaoDoctoOficial() != null ?
                validacao.getSolicitacaoDoctoOficial().toDto() : null);
            return ResponseEntity.ok(retornoCertidaoDebitos);
        } catch (Exception e) {
            getLogger().error("Erro na api /get-certidao-debitos. Erro: {}", e.getMessage());
            getLogger().debug("Detalhes do erro na api /get-certidao-debitos.", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/get-certidao-portalweb",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<SolicitacaoDocumentoOficialDTO> getCertidao(@RequestBody WsDadosPessoaisSolicitacaoCertidao wsDados) {
        try {
            singletonMetricas.contarAcessoPortal(ContadorAcessosPortal.TipoAcesso.SOLICITACAO_DOCUMENTO, wsDados.getCpfCnpj());
            sistemaService.setUsuarioAlternativo(wsDados.getCpfCnpj());
            ValidacaoSolicitacaoDoctoOficial validacao = getPortalContribunteFacade().getSolicitacaoCertidaoPortalFacade().imprimirCertidao(wsDados.getId(), montarDadosSolicitacao(wsDados));

            if (validacao != null) {
                if (!StringUtils.isBlank(validacao.getMensagemValidacao())) {
                    return new ResponseEntity<>(HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(validacao.getSolicitacaoDoctoOficial().toDto(), HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            getLogger().error("Erro na api /get-certidao-portalweb. Erro: {}", e.getMessage());
            getLogger().debug("Detalhes do erro na api /get-certidao-portalweb.", e);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private DadosSolicitacaoCertidaoPortal montarDadosSolicitacao(WsDadosPessoaisSolicitacaoCertidao wsDados) {
        DadosSolicitacaoCertidaoPortal.TipoCadastro tipoCadastro;

        if (wsDados.isCadastro()) {
            tipoCadastro = DadosSolicitacaoCertidaoPortal.TipoCadastro.IMOBILIARIO;
        } else if (wsDados.isPessoaFisica()) {
            tipoCadastro = DadosSolicitacaoCertidaoPortal.TipoCadastro.PESSOA_FISICA;
        } else {
            tipoCadastro = DadosSolicitacaoCertidaoPortal.TipoCadastro.PESSOA_JURIDICA;
        }

        return new DadosSolicitacaoCertidaoPortal(wsDados.getTipoDocumentoOficialPortal(), tipoCadastro);
    }
}

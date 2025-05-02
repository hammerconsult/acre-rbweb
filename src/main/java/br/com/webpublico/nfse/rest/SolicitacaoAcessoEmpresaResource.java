package br.com.webpublico.nfse.rest;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.nfse.domain.SituacaoSolicitacaoAcesso;
import br.com.webpublico.nfse.domain.SolicitacaoAcessoEmpresa;
import br.com.webpublico.nfse.domain.dtos.SolicitacaoAcessoEmpresaAvaliacaoDTO;
import br.com.webpublico.nfse.domain.dtos.SolicitacaoAcessoEmpresaDTO;
import br.com.webpublico.nfse.facades.SolicitacaoAcessoEmpresaFacade;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.nfse.util.PaginationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.util.List;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

@RequestMapping("/nfse")
@Controller
public class SolicitacaoAcessoEmpresaResource implements Serializable {
    private final Logger logger = LoggerFactory.getLogger(SolicitacaoAcessoEmpresaResource.class);
    private SolicitacaoAcessoEmpresaFacade solicitacaoAcessoEmpresaFacade;
    private UsuarioWebFacade usuarioWebFacade;

    @PostConstruct
    public void init() {
        try {
            solicitacaoAcessoEmpresaFacade = (SolicitacaoAcessoEmpresaFacade) new InitialContext().lookup("java:module/SolicitacaoAcessoEmpresaFacade");
            usuarioWebFacade = (UsuarioWebFacade) new InitialContext().lookup("java:module/UsuarioWebFacade");
        } catch (Exception ex) {
            logger.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/solicitacao-acesso-empresa", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<SolicitacaoAcessoEmpresaDTO> createSolicitacaoAcessoEmpresa(@RequestBody SolicitacaoAcessoEmpresaDTO solicitacao) throws ValidacaoException {
        try {
            String cnpjEmpresa = solicitacao.getCnpj();
//            String usuarioLogin = solicitacao.getNfseUser().getLogin();
//            if (solicitacaoAcessoEmpresaFacade.buscarSolicitacaoEmAvaliacao(cnpjEmpresa, usuarioLogin).size() > 0) {
//                return getResponseEntityError("Já existe uma solicitação sua para a empresa portadora do CNPJ: " + cnpjEmpresa, "Sua solicitação está aguardando avaliação e com isso esta operação não pode ser feita novamente.");
//            }

            SolicitacaoAcessoEmpresa novaSolicitacao = solicitacaoAcessoEmpresaFacade.criarSolicitacaoAcessoEmpresa(solicitacao);

            solicitacao.setId(novaSolicitacao.getId());

            return new ResponseEntity<SolicitacaoAcessoEmpresaDTO>(solicitacao, HttpStatus.OK);
        } catch (Exception e) {
            return getResponseEntityError(e.getCause().getMessage());
        }
    }

    @RequestMapping(value = "/recuperar-solicitacoes-do-usuario-logado", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SolicitacaoAcessoEmpresa>> recuperarSolicitacoesDoUsuarioLogado(Pageable pageable, @RequestParam(value = "usuario") String usuario, @RequestParam(value = "query", required = false) String query) throws URISyntaxException {
        Page<SolicitacaoAcessoEmpresa> retorno;
        retorno = solicitacaoAcessoEmpresaFacade.recuperarSolicitacoesPorUsuarioEQuery(pageable, usuario, query);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(retorno.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/recuperar-solicitacoes-aguardando-avaliacao", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<SolicitacaoAcessoEmpresa>> recuperarSolicitacoesAguardandoAvaliacao(Pageable pageable, @RequestParam(value = "query", required = false) String query) throws URISyntaxException {
        Page<SolicitacaoAcessoEmpresa> retorno;
        retorno = solicitacaoAcessoEmpresaFacade.recuperarSolicitacoesAguardandoAvaliacaoPorQuery(pageable, query);

        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(retorno, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(retorno.getContent(), headers, HttpStatus.OK);
    }

    @RequestMapping(value = "/solicitacao-acesso-empresa", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SolicitacaoAcessoEmpresaAvaliacaoDTO> recuperarPerguntasPorAssuntoId(@RequestParam(value = "id") Long id) throws URISyntaxException, IOException {
        SolicitacaoAcessoEmpresa solicitacao = solicitacaoAcessoEmpresaFacade.recuperarPorId(id);

        UsuarioWeb user = usuarioWebFacade.recuperar(solicitacao.getNfseUser().getId());

        SolicitacaoAcessoEmpresaAvaliacaoDTO solicitacaoDto = new SolicitacaoAcessoEmpresaAvaliacaoDTO(solicitacao, user);

        return new ResponseEntity(solicitacaoDto, HttpStatus.OK);

    }

    @RequestMapping(value = "/solicitacao-acesso-empresa-aprovar", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<SolicitacaoAcessoEmpresaAvaliacaoDTO> aprovarSolicitacaoAcessoEmpresa(@RequestBody SolicitacaoAcessoEmpresaAvaliacaoDTO solicitacao) throws ValidacaoException {
        solicitacao.setSituacao(SituacaoSolicitacaoAcesso.APROVADO);
        try {
            solicitacaoAcessoEmpresaFacade.atualizarSolicitacaoAcessoEmpresaAvaliacao(solicitacao);
            return new ResponseEntity<>(solicitacao, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getCause().getMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @RequestMapping(value = "/solicitacao-acesso-empresa-rejeitar", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<SolicitacaoAcessoEmpresaAvaliacaoDTO> rejeitarSolicitacaoAcessoEmpresa(@RequestBody SolicitacaoAcessoEmpresaAvaliacaoDTO solicitacao) throws ValidacaoException {
        solicitacao.setSituacao(SituacaoSolicitacaoAcesso.REJEITADO);
        try {
            solicitacaoAcessoEmpresaFacade.atualizarSolicitacaoAcessoEmpresaAvaliacao(solicitacao);
            return new ResponseEntity<>(solicitacao, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getCause().getMessage()), HttpStatus.NOT_ACCEPTABLE);
        }
    }

    @RequestMapping(value = "/solicitacao-acesso-empresa", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<SolicitacaoAcessoEmpresaDTO> updateSolicitacaoAcessoEmpresa(@RequestBody SolicitacaoAcessoEmpresaDTO solicitacao) throws ValidacaoException {
        try {
            solicitacaoAcessoEmpresaFacade.atualizarSolicitacaoAcessoEmpresa(solicitacao);
            return new ResponseEntity<>(solicitacao, HttpStatus.OK);
        } catch (Exception e) {
            return getResponseEntityError(e.getCause().getMessage());
        }
    }

    private ResponseEntity<SolicitacaoAcessoEmpresaDTO> getResponseEntityError(String... erroMessage) {
        return new ResponseEntity<>(null, setMessageError(erroMessage), HttpStatus.NOT_ACCEPTABLE);
    }
}

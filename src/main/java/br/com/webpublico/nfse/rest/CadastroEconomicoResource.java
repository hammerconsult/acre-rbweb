package br.com.webpublico.nfse.rest;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Notificacao;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.enums.TipoNotificacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.nfse.domain.TributosFederais;
import br.com.webpublico.nfse.domain.UserNfseCadastroEconomico;
import br.com.webpublico.nfse.domain.dtos.ImagemUsuarioNfseDTO;
import br.com.webpublico.nfse.domain.dtos.PrestadorServicoNfseDTO;
import br.com.webpublico.nfse.domain.dtos.TributosFederaisNfseDTO;
import br.com.webpublico.nfse.util.PaginationUtil;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.mifmif.common.regex.Generex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.List;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

/**
 * Created by daniel on 26/04/17.
 */
@Controller
@RequestMapping("/nfse")
public class CadastroEconomicoResource implements Serializable {


    private static final Logger log = LoggerFactory.getLogger(CadastroEconomicoResource.class);
    private CadastroEconomicoFacade cadastroEconomicoFacade;
    private UsuarioWebFacade usuarioWebFacade;

    @PostConstruct
    public void init() {
        try {
            cadastroEconomicoFacade = (CadastroEconomicoFacade) Util.getFacadeViaLookup("java:module/CadastroEconomicoFacade");
            usuarioWebFacade = (UsuarioWebFacade) Util.getFacadeViaLookup("java:module/UsuarioWebFacade");
        } catch (Exception ex) {
            log.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping("/cadastro-economico/buscar")
    public ResponseEntity<PrestadorServicoNfseDTO> getById(@RequestParam(value = "id") Long id) {
        CadastroEconomico cadastro = cadastroEconomicoFacade.recuperar(id);
        return new ResponseEntity<>(cadastro.toNfseDto(), HttpStatus.OK);
    }

    @RequestMapping("/cadastro-economico/buscar-por-login")
    public ResponseEntity<List<PrestadorServicoNfseDTO>> getByUser(Pageable pageable,
                                                                   @RequestParam(value = "filtro") String filtro,
                                                                   @RequestParam(value = "login") String login) throws URISyntaxException, UnsupportedEncodingException {
        filtro = URLDecoder.decode(filtro, "UTF-8");
        filtro = URLDecoder.decode(filtro, "UTF-8");
        Page<PrestadorServicoNfseDTO> cadastros = cadastroEconomicoFacade.buscarcadastrosPorUsuarioNfse(pageable, filtro, login);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(cadastros, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity<>(cadastros.getContent(), headers, HttpStatus.OK);
    }


    @RequestMapping("/cadastro-economico/buscar-por-cnpj")
    public ResponseEntity<PrestadorServicoNfseDTO> getByCnpj(@RequestParam(value = "cnpj") String cnpj) {
        CadastroEconomico cadastro = null;
        List<CadastroEconomico> cadastroEconomicos = cadastroEconomicoFacade.buscarCadastrosEconomicosPorCnpj(cnpj);
        if (!cadastroEconomicos.isEmpty()) {
            cadastro = cadastroEconomicoFacade.recuperar(cadastroEconomicos.get(0).getId());
            return new ResponseEntity<>(cadastro.toNfseDto(), HttpStatus.OK);

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @RequestMapping("/cadastros-economicos/buscar-por-cnpj")
    public ResponseEntity<List<PrestadorServicoNfseDTO>> buscarByCnpj(@RequestParam(value = "cnpj") String cnpj) {
        List<CadastroEconomico> cadastroEconomicos = cadastroEconomicoFacade.buscarCadastrosEconomicosPorCnpj(cnpj);
        if (!cadastroEconomicos.isEmpty()) {
            List<PrestadorServicoNfseDTO> dtos = Lists.newArrayList();
            for (CadastroEconomico cadastroEconomico : cadastroEconomicos) {
                dtos.add(cadastroEconomico.toNfseDto());
            }
            return new ResponseEntity<>(dtos, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping("/cadastro-economico/buscar-por-inscricao-municipal")
    public ResponseEntity<PrestadorServicoNfseDTO> getByInscricaoMunicipal(@RequestParam(value = "inscricaoMunicipal") String inscricaoMunicipal) {
        CadastroEconomico cadastro = null;
        CadastroEconomico cadastroEconomicos = cadastroEconomicoFacade.buscarCadastroEconomicoPorInscricaoMunicipal(inscricaoMunicipal);
        if (cadastroEconomicos != null) {
            cadastro = cadastroEconomicoFacade.recuperar(cadastroEconomicos.getId());
            return new ResponseEntity<>(cadastro.toNfseDto(), HttpStatus.OK);

        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @RequestMapping("/cadastro-economico/buscar-na-receita-por-cnpj")
    public ResponseEntity<PrestadorServicoNfseDTO> getReceitaByCnpj(@RequestParam(value = "cnpj") String cnpj) {
        //TODO SUBSTITUIR POR REDESIM
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }

    /**
     * GET  /pesquisa-generica -> Método generico para busca de entidades no webpublico
     */
    @RequestMapping(value = "/cadastro-economico/listar",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<PrestadorServicoNfseDTO>> pesquisaGenerica(Pageable pageable,
                                                                          @RequestParam(value = "query", required = false) String query)
        throws URISyntaxException {
        if (query == null) {
            query = "";
        }
        List<PrestadorServicoNfseDTO> retorno = cadastroEconomicoFacade.buscarCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJ(query, pageable);
        Integer totalCadastros = cadastroEconomicoFacade.contarCadastroEconomicoPorCMCNomeRazaoSocialCPFCNPJ(query);

        Page returnPage = new PageImpl(retorno, pageable, totalCadastros);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(returnPage, "", pageable.getPageNumber(), pageable.getPageSize());
        return new ResponseEntity(returnPage.getContent(), headers, HttpStatus.OK);
    }


    @RequestMapping(value = "/cadastro-economico/imagem",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImagemUsuarioNfseDTO> getImagemUsuario(@RequestParam("id") Long id) {
        try {
            ImagemUsuarioNfseDTO imagemUsuarioNfseDTO = cadastroEconomicoFacade.buscarImagemDoCadastro(id);
            imagemUsuarioNfseDTO.setId(id);
            return new ResponseEntity<>(imagemUsuarioNfseDTO, HttpStatus.OK);
        } catch (Exception e) {
            log.debug("Não foi encontrada nenhuma pessoa, Exception {} ", e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/cadastro-economico",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PrestadorServicoNfseDTO> saveCadastroEconomico(@RequestBody PrestadorServicoNfseDTO dto) {
        try {
            CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.buscarCadastroEconomicoPorInscricaoMunicipal(dto.getInscricaoMunicipal());
            if (cadastroEconomico == null) {
                CadastroEconomico save = cadastroEconomicoFacade.salvar(dto);
                notificarNovoCMC(save);
                if (dto.getCriarUsuario()) {
                    usuarioWebFacade.criarUsuarioWeb(save, dto.getPassword(), Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
                }
                return new ResponseEntity<>(save.toSimpleNfseDto(), HttpStatus.OK);
            }
            return new ResponseEntity<>(null, setMessageError("Já existe um cadastro econômico com essa Inscrição Municipal"), HttpStatus.BAD_REQUEST);
        } catch (ValidacaoException e) {
            String[] mensagens = new String[e.getMensagens().size()];
            mensagens = e.getMensagens().toArray(mensagens);
            return new ResponseEntity<>(null, setMessageError(mensagens), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/perfil-cadastro-economico",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PrestadorServicoNfseDTO> updateCadastroEconomico(@RequestBody PrestadorServicoNfseDTO dto) {
        try {
            CadastroEconomico save = cadastroEconomicoFacade.atualizarCadastroViaNfse(dto);
            return new ResponseEntity<>(save.toNfseDto(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
    }

    public void notificarNovoCMC(CadastroEconomico save) {
        String titulo = "Novo Cadastro Econômico realizado via Sistema Iss Online!";
        String descricao = "O C.M.C. " + save.getCmcNomeCpfCnpj() + " está aguarando ser analisado.";
        String link = "/cadastro-economico/editar/" + save.getId() + "/";

        NotificacaoService.getService().notificar(
            new Notificacao(titulo, descricao,
                link, Notificacao.Gravidade.INFORMACAO, TipoNotificacao.COMUNICACAO_NFS_ELETRONICA));
    }

    @RequestMapping("/cadastro-economico/tributos-federais")
    public void salvarTributosFederais(@RequestBody TributosFederaisNfseDTO dto) {
        try {
            cadastroEconomicoFacade.salvarConfiguracoesTributosFederais(dto);
        } catch (Exception e) {
            log.error("Erro: ", e);
        }
    }

    @RequestMapping(value = "/cadastro-economico/buscar-tributos-federais",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TributosFederaisNfseDTO> buscarTributosFederais(@RequestParam(value = "prestadorId") Long prestadorId) {
        TributosFederais tributosFederais = cadastroEconomicoFacade.buscarTributosFederais(prestadorId);
        if (tributosFederais != null) {
            return new ResponseEntity<>(tributosFederais.toNfseDto(), HttpStatus.OK);
        }
        return new ResponseEntity<>(new TributosFederaisNfseDTO(), HttpStatus.OK);
    }

    @RequestMapping(value = "/cadastro-economico/gerar-chave-acesso",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<PrestadorServicoNfseDTO> gerarChaveAcesso(@RequestParam(value = "id") Long id) {
        try {
            CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.recuperar(id);
            cadastroEconomico.setChaveAcesso(new Generex("[0-9a-fA-F]{8}").random());
            CadastroEconomico save = cadastroEconomicoFacade.salvarRetornado(cadastroEconomico);
            return new ResponseEntity<>(save.toNfseDto(), HttpStatus.OK);
        } catch (ValidacaoException e) {
            String[] mensagens = new String[e.getMensagens().size()];
            mensagens = e.getMensagens().toArray(mensagens);
            return new ResponseEntity<>(null, setMessageError(mensagens), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping("/cadastro-economico/criar-usuario")
    public void criarUsuario(@RequestBody PrestadorServicoNfseDTO prestador) {
        CadastroEconomico cadastroEconomico = cadastroEconomicoFacade.recuperar(prestador.getId());
        usuarioWebFacade.criarUsuarioWeb(cadastroEconomico, prestador.getPassword(), Boolean.FALSE, Boolean.FALSE, Boolean.TRUE);
    }
}

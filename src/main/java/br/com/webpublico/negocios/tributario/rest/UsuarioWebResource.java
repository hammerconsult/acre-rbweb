package br.com.webpublico.negocios.tributario.rest;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.negocios.CadastroEconomicoFacade;
import br.com.webpublico.negocios.PessoaFacade;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.nfse.domain.ConfiguracaoNfse;
import br.com.webpublico.nfse.domain.UserNfseCadastroEconomico;
import br.com.webpublico.nfse.domain.dtos.*;
import br.com.webpublico.nfse.domain.dtos.enums.PermissaoUsuarioEmpresaNfse;
import br.com.webpublico.nfse.exceptions.NfseOperacaoNaoPermitidaException;
import br.com.webpublico.nfse.exceptions.NfseValidacaoException;
import br.com.webpublico.nfse.facades.ConfiguracaoNfseFacade;
import br.com.webpublico.nfse.util.HeaderUtil;
import br.com.webpublico.nfse.util.RandomUtil;
import br.com.webpublico.util.Util;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import javax.naming.InitialContext;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static br.com.webpublico.nfse.util.HeaderUtil.setMessageError;

/**
 * Created by daniel on 26/04/17.
 */
@Controller
@RequestMapping("/nfse")
public class UsuarioWebResource implements Serializable {


    private final Logger log = LoggerFactory.getLogger(UsuarioWebResource.class);

    private UsuarioWebFacade usuarioWebFacade;
    private PessoaFacade pessoaService;
    private CadastroEconomicoFacade cadastroEconomicoService;
    private ConfiguracaoNfseFacade configuracaoNfseService;

    @PostConstruct
    public void init() {
        try {
            usuarioWebFacade = (UsuarioWebFacade) new InitialContext().lookup("java:module/UsuarioWebFacade");
            pessoaService = (PessoaFacade) new InitialContext().lookup("java:module/PessoaFacade");
            cadastroEconomicoService = (CadastroEconomicoFacade) new InitialContext().lookup("java:module/CadastroEconomicoFacade");
            configuracaoNfseService = (ConfiguracaoNfseFacade) new InitialContext().lookup("java:module/ConfiguracaoNfseFacade");
        } catch (Exception ex) {
            log.error("Não foi possivel criar a instancia: " + ex.getMessage());
        }
    }

    @RequestMapping(value = "/wp-online", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> isWpOnline() {
        return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
    }


    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserNfseDTO> login(@RequestParam(value = "login") String login) {
        try {
            UsuarioWeb usuario = usuarioWebFacade.efetuarLogin(login);
            usuarioWebFacade.tratarPermissoesContador(usuario);
            usuarioWebFacade.atribuirEmpresa(usuario);
            usuario = usuarioWebFacade.recuperar(usuario.getId());
            if (usuario.getUserNfseCadastroEconomico() != null) {
                usuario.getUserNfseCadastroEconomico().setCadastroEconomico(
                    cadastroEconomicoService.recuperar(usuario.getUserNfseCadastroEconomico().getCadastroEconomico().getId()));
            }
            return new ResponseEntity<>(usuario.toNfseDto(), HttpStatus.OK);
        } catch (NfseOperacaoNaoPermitidaException ex) {
            return new ResponseEntity<>(null, setMessageError(ex.getMessage()), HttpStatus.OK);
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>(null, setMessageError(ex.getMessage()), HttpStatus.OK);
        }
    }


    @RequestMapping(value = "/usuario", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserNfseDTO> buscarUsuario(@RequestParam(value = "login") String login) {
        try {
            UsuarioWeb usuario = usuarioWebFacade.recuperarUsuarioPorLogin(login);
            return new ResponseEntity<>(usuario != null ? usuario.toNfseDto() : null, HttpStatus.OK);
        } catch (NfseOperacaoNaoPermitidaException ex) {
            return new ResponseEntity<>(null, setMessageError(ex.getMessage()), HttpStatus.OK);
        }
    }

    @RequestMapping(value = "/usuario/acrescentar-tentativa-login", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserNfseDTO> acrescentarTentativaLogin(@RequestParam(value = "login") String login) {
        try {
            UsuarioWeb usuario = usuarioWebFacade.recuperarUsuarioPorLogin(login);
            ConfiguracaoNfse config = configuracaoNfseService.recuperarConfiguracao();
            if (usuario != null) {
                if (config.getTentativaAcessoIndevido() != null && config.getTentativaAcessoIndevido() > 0 &&
                    config.getTentativaAcessoIndevido() <= usuario.getTentativaLogin()) {
                    usuario.setActivated(false);
                } else {
                    usuario.setTentativaLogin(usuario.getTentativaLogin() + 1);
                }
                try {
                    usuarioWebFacade.salvar(usuario);
                } catch (NfseOperacaoNaoPermitidaException e) {
                    e.printStackTrace();
                }
                return new ResponseEntity<>(usuario.toNfseDto(), HttpStatus.OK);
            }
        } catch (NfseOperacaoNaoPermitidaException e) {
            return new ResponseEntity<>(null, setMessageError(e.getMessage()), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @RequestMapping(value = "/usuario/zerar-tentativa-login", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<UserNfseDTO> zerarTentativaLogin(@RequestParam(value = "login") String login) {
        UsuarioWeb usuario = usuarioWebFacade.recuperarUsuarioPorLogin(login);
        if (usuario != null) {
            usuario.setTentativaLogin(0);
            try {
                usuarioWebFacade.salvar(usuario);
            } catch (NfseOperacaoNaoPermitidaException e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity<>(usuario.toNfseDto(), HttpStatus.OK);
    }


    @RequestMapping(value = "/usuario",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveAccount(@RequestBody UserNfseDTO userDTO) throws NfseOperacaoNaoPermitidaException {
        UsuarioWeb usuario = usuarioWebFacade.recuperarUsuarioPorLogin(userDTO.getLogin());
        if (userDTO != null) {
            usuario.setEmail(userDTO.getEmail());
            usuarioWebFacade.salvar(usuario);
            if (usuario.getPessoa() != null) {
                if (usuario.getPessoa() instanceof PessoaFisica) {
                    ((PessoaFisica) usuario.getPessoa()).setNome(userDTO.getNome());
                } else {
                    ((PessoaJuridica) usuario.getPessoa()).setRazaoSocial(userDTO.getNome());
                }
                usuario.getPessoa().setEmail(userDTO.getEmail());
                pessoaService.salvar(usuario.getPessoa());
            }
            return new ResponseEntity<>(HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }


    @RequestMapping(value = "/register",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserNfseDTO> registerAccount(@RequestBody RegisterNfseDTO registerDTO) {
        log.debug("registerDTO [{}] ", registerDTO);
        try {
            hasDadosUsuarioRegistrado(registerDTO);
            registerDTO.validarDadosPessoaisUsuario();
            UsuarioWeb userInformation = usuarioWebFacade.registrarUsuario(registerDTO);
            UserNfseDTO nfseDTO = userInformation.toNfseDto();
            enviarEmailAtivacaoUsuario(userInformation);
            return new ResponseEntity<>(nfseDTO, HttpStatus.CREATED);
        } catch (NfseOperacaoNaoPermitidaException ex) {
            return nfseExceptionToResponseEntity(ex);
        }
    }

    private void enviarEmailAtivacaoUsuario(UsuarioWeb userInformation) {
        usuarioWebFacade.enviarEmailAtivacaoUsuario(userInformation);
    }

    @RequestMapping(value = "/activate",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) throws NfseOperacaoNaoPermitidaException {
        UsuarioWeb nfseUser = usuarioWebFacade.ativarRegistro(key);
        if (nfseUser != null) {
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @RequestMapping(value = "/account/reset_password/init",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SimpleValidationSuccessNfseDTO> requestPasswordReset(@RequestParam(value = "cpf") String cpf) {
        UsuarioWeb nfseUser = usuarioWebFacade.buscarNfseUserPorLogin(Util.removerCaracteresEspeciais(cpf));
        if (nfseUser != null) {
            nfseUser.setResetKey(RandomUtil.generateResetKey());
            nfseUser.setResetDate(new Date());
            nfseUser = usuarioWebFacade.salvarRetornando(nfseUser);
            if (nfseUser.isActivated()) {
                usuarioWebFacade.enviarEmailResetarSenha(nfseUser);
                return new ResponseEntity<>(new SimpleValidationSuccessNfseDTO("Operação Realizada",
                    "Verifique a caixa de entrada do seu e-mail (" + nfseUser.getEmail() + "), você receberá os detalhes para gerar uma nova senha."), HttpStatus.OK);
            } else {
                usuarioWebFacade.enviarEmailAtivacaoUsuario(nfseUser);
                return new ResponseEntity<>(new SimpleValidationSuccessNfseDTO("Operação Realizada",
                    "Verifique a caixa de entrada do seu e-mail (" + nfseUser.getEmail() + "), você receberá os detalhes para ativação da sua conta."), HttpStatus.OK);
            }
        }
        throw new NfseOperacaoNaoPermitidaException("Verifique o cpf ou cnpj informado, o mesmo não se encontra cadastrado");
    }

    @RequestMapping(value = "/account/reset_password/finish",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> finishPasswordReset(@RequestParam(value = "key") String key, @RequestParam(value = "newPassword") String newPassword) {
        if (!checkPasswordLength(newPassword)) {
            return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
        }
        UsuarioWeb user = usuarioWebFacade.completePasswordReset(newPassword, key);
        if (user != null) {
            return new ResponseEntity<String>(HttpStatus.OK);
        } else {
            return nfseExceptionToResponseEntity(new NfseOperacaoNaoPermitidaException("Não foi encontrada nenhuma solicitação de alteração de senha para essa acesso"));
        }
    }

    @RequestMapping(value = "/account/change-password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> changePassword(@RequestParam String login, @RequestParam String newPassword) {
        usuarioWebFacade.changePassword(login, newPassword);
        return new ResponseEntity(HttpStatus.OK);
    }

    private boolean checkPasswordLength(String password) {
        return (!StringUtils.isEmpty(password) && password.length() >= UserNfseDTO.PASSWORD_MIN_LENGTH && password.length() <= UserNfseDTO.PASSWORD_MAX_LENGTH);
    }


    private ResponseEntity<UserNfseDTO> nfseExceptionToResponseEntity(NfseOperacaoNaoPermitidaException ex) {
        HttpHeaders headers = HeaderUtil.setMessageError(ex.getMensagens().toArray(new String[0]));
        return new ResponseEntity<>(null, headers, HttpStatus.BAD_REQUEST);

    }


    private void hasDadosUsuarioRegistrado(UserNfseDTO usuario) throws NfseValidacaoException {
        if (usuarioWebFacade.buscarNfseUserPorLogin(usuario.getLogin()) != null) {
            throw new NfseValidacaoException("O CPF informado já está em uso.", "Verifique se o CPF informado está correto.", "Caso o CPF informado esteja correto, entre em contato com o suporte ou siga os passos do esqueci minha senha na página inicial.");
        }

//        if (usuarioWebFacade.buscarNfseUserPorEmail(usuario.getEmail()) != null) {
//            throw new NfseValidacaoException("O email informado já está em uso.", "Verifique se o email informado está correto.", "Caso o email informado esteja correto, entre em contato com o suporte ou siga os passos do esqueci minha senha na página inicial.");
//        }
//      TODO Será que vai ficar assim?
//        if (pessoaFacade.buscarPessoaPorCpfOrCnpj(usuario.getLogin()) != null) {
//            throw new NfseValidacaoException("Seu cadastro já consta na base de dados unificada do municipio.", "Verifique se o CPF informado está correto.", "Caso o CPF informado esteja correto, entre em contato com o suporte para continuar o cadastro do seu usuário.");
//        }
    }


    @RequestMapping(value = "/usuario/imagem",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> saveImagemUsuario(@RequestBody ImagemUsuarioNfseDTO imagemDto) {
        log.debug("REST request to POST Pessoa Do Usuário ");
        try {
            pessoaService.atualizarImagemPessoa(imagemDto);
        } catch (Exception e) {
            log.debug("Não foi encontrada nenhuma pessoa, Exception {} ", e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(value = "/usuario/imagem",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ImagemUsuarioNfseDTO> getImagemUsuario(@RequestParam("login") String login) {
        log.debug("REST request to get Imagem Do Usuário ");
        try {
            return new ResponseEntity<>(usuarioWebFacade.buscarImagemDoUsuario(login), HttpStatus.OK);
        } catch (Exception e) {
            log.debug("Não foi encontrada nenhuma pessoa, Exception {} ", e.getMessage());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, value = "/usuario/imagem/{id}")
    public void deleteArquivo(@PathVariable Long id) throws IOException {
        Pessoa pessoa = pessoaService.recuperar(id);
        pessoa.setArquivo(null);
        pessoaService.salvar(pessoa);
    }


    @RequestMapping("/cadastro-economico/vincular-usuario")
    public void vincularUsuario(@RequestParam(value = "id") Long id,
                                @RequestParam(value = "loginResponsavel") String loginResponsavel,
                                @RequestParam(value = "login") String login) {

        UsuarioWeb nfseUserResponsavel = usuarioWebFacade.buscarNfseUserPorLogin(loginResponsavel);
        UsuarioWeb nfseUser = usuarioWebFacade.buscarNfseUserPorLogin(login);

        CadastroEconomico cadastro = cadastroEconomicoService.recuperar(id);
        for (UserNfseCadastroEconomico userNfseCadastroEconomico : nfseUser.getUserNfseCadastroEconomicos()) {
            if (userNfseCadastroEconomico.getCadastroEconomico().equals(cadastro)) {
                return;
            }
        }
        usuarioWebFacade.adicionarCadastroEconomicoAoUsuario(nfseUser, cadastro,
            PermissaoUsuarioEmpresaNfse.getTodasMenosAdmin(), nfseUserResponsavel, UserNfseCadastroEconomico.Situacao.PENDENTE);
        usuarioWebFacade.salvarRetornando(nfseUser);
    }

    @RequestMapping(value = "/usuario/por-cpf", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<UserNfseDTO>> buscarUsuarioPorCpf(@RequestParam(value = "cpf") String cpf) {
        List<UserNfseDTO> usuarios = usuarioWebFacade.buscarNfseUserPorCpf(cpf);
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }


    @RequestMapping(value = "/cadastro-economico/usuarios", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<PrestadorUsuarioNfseDTO>> buscarUsuario(@RequestParam(value = "empresaId") Long empresaId) {
        List<PrestadorUsuarioNfseDTO> usuarios = usuarioWebFacade.buscarNfseUserPorEmpresa(empresaId, false);
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @RequestMapping(value = "/cadastro-economico/usuarios-inativos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<PrestadorUsuarioNfseDTO>> buscarUsuarioInativo(@RequestParam(value = "empresaId") Long empresaId) {
        List<PrestadorUsuarioNfseDTO> usuarios = usuarioWebFacade.buscarNfseUserPorEmpresa(empresaId, true);
        return new ResponseEntity<>(usuarios, HttpStatus.OK);
    }

    @RequestMapping(value = "/cadastro-economico/ativar-desativar-usuario", method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void ativarDesativarUsuario(@RequestParam(value = "empresaId") Long empresaId,
                                       @RequestParam(value = "login") String login,
                                       @RequestParam(value = "loginResponsavel") String loginResponsavel) {
        usuarioWebFacade.ativarOuDesativarUsuario(empresaId, login, loginResponsavel);
    }

    @RequestMapping(value = "/cadastro-economico/adionar-remover-permissoes-usuario", method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void permitirAcessoAoUsuario(@RequestBody PrestadorUsuarioNfseDTO dto) {
        usuarioWebFacade.adicionarOuRemoverRecursoDosUsuarios(dto);
    }

    @RequestMapping(value = "/usuario/convidar",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)

    public ResponseEntity<ConviteUsuarioNfseDTO> registerAccount(@Valid @RequestBody ConviteUsuarioNfseDTO convite, HttpServletRequest request) throws NfseOperacaoNaoPermitidaException {
        log.debug("convite [{}] ", convite);
        try {
            usuarioWebFacade.convidarUsuario(convite);
            return new ResponseEntity<>(convite, HttpStatus.CREATED);
        } catch (NfseOperacaoNaoPermitidaException ex) {
            return new ResponseEntity<>(null, HeaderUtil.setMessageError(ex.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @RequestMapping(value = "/usuario/trocar-empresa", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void trocarEmpresa(@RequestParam(value = "prestadorId") Long prestadorId, @RequestParam(value = "login") String login) {
        usuarioWebFacade.trocarEmpresaLogada(prestadorId, login);
    }

    @RequestMapping(value = "/usuario/acesso-empresa", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<Boolean> hasAcessoEmpresa(@RequestParam String login, @RequestParam String inscricaoMunicipal) {
        Boolean hasAcesso = usuarioWebFacade.hasAcessoEmpresa(login, inscricaoMunicipal);
        return new ResponseEntity<Boolean>(hasAcesso, HttpStatus.OK);
    }


}

package br.com.webpublico.controle.comum;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.entidades.comum.UsuarioWeb;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ConsultaCepFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.comum.UsuarioWebFacade;
import br.com.webpublico.nfse.domain.BloqueioEmissaoNfse;
import br.com.webpublico.nfse.domain.NfseUserAuthority;
import br.com.webpublico.nfse.domain.UserNfseCadastroEconomico;
import br.com.webpublico.nfse.domain.dtos.enums.PermissaoUsuarioEmpresaNfse;
import br.com.webpublico.nfse.enums.SituacaoBloqueioEmissaoNfse;
import br.com.webpublico.nfse.facades.NfseAuthorityFacade;
import br.com.webpublico.nfse.util.RandomUtil;
import br.com.webpublico.solicitacaodispositivo.AuthoritiesConstants;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.StringUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "usuarioWebNovo", pattern = "/usuario-web/novo/", viewId = "/faces/comum/usuarioweb/edita.xhtml"),
    @URLMapping(id = "usuarioWebListar", pattern = "/usuario-web/listar/", viewId = "/faces/comum/usuarioweb/lista.xhtml"),
    @URLMapping(id = "usuarioWebEditar", pattern = "/usuario-web/editar/#{usuarioWebControlador.id}/", viewId = "/faces/comum/usuarioweb/edita.xhtml"),
    @URLMapping(id = "usuarioWebVer", pattern = "/usuario-web/ver/#{usuarioWebControlador.id}/", viewId = "/faces/comum/usuarioweb/visualizar.xhtml"),
})
public class UsuarioWebControlador extends PrettyControlador<UsuarioWeb> implements Serializable, CRUD {

    @EJB
    private UsuarioWebFacade usuarioWebFacade;
    @EJB
    private NfseAuthorityFacade nfseAuthorityFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private String senha2;
    private CadastroEconomico cadastroEconomicoSelecionado;
    private ArrayList<PermissaoUsuarioEmpresaNfse> permissaoUsuarioEmpresaNfses = Lists.newArrayList(PermissaoUsuarioEmpresaNfse.values());
    @EJB
    private ConsultaCepFacade consultaCepFacade;
    private BloqueioEmissaoNfse bloqueioEmissaoNfse;

    public UsuarioWebControlador() {
        super(UsuarioWeb.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return usuarioWebFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/usuario-web/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public BloqueioEmissaoNfse getBloqueioEmissaoNfse() {
        return bloqueioEmissaoNfse;
    }

    public void setBloqueioEmissaoNfse(BloqueioEmissaoNfse bloqueioEmissaoNfse) {
        this.bloqueioEmissaoNfse = bloqueioEmissaoNfse;
    }

    @Override
    @URLAction(mappingId = "usuarioWebNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        selecionado.setActivated(true);
    }

    @Override
    @URLAction(mappingId = "usuarioWebEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "usuarioWebVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public String getSenha2() {
        return senha2;
    }

    public void setSenha2(String senha2) {
        this.senha2 = senha2;
    }

    public void selecionarPessoa(ActionEvent evento) {
        selecionado.setPessoa((Pessoa) evento.getComponent().getAttributes().get("objeto"));
        handlePessoa();
    }

    public void handlePessoa() {
        selecionado.setEmail("");
        selecionado.setLogin("");
        if (selecionado.getPessoa() != null) {
            String login = StringUtil.retornaApenasNumeros(selecionado.getPessoa().getCpf_Cnpj());
            UsuarioWeb usuarioWeb = usuarioWebFacade.recuperarUsuarioPorLogin(login);
            if (usuarioWeb != null) {
                FacesUtil.addOperacaoNaoPermitida("Já existe um usuário cadastrado para a pessoa " + selecionado.getPessoa().getNome());
                return;
            }
            selecionado.setEmail(selecionado.getPessoa().getEmail());
            selecionado.setLogin(login);
            if (usuarioWebFacade.hasEscritorioContabilidadeDaPessoa(selecionado.getPessoa())) {
                FacesUtil.executaJavaScript("confirmaEscritorioContabilidade.show()");
            }
        }
    }

    public boolean isAdmin() {
        return selecionado.isAdmin();
    }

    @Override
    public void salvar() {
        try {
            if (isOperacaoNovo()) {
                selecionado.setPassword(usuarioWebFacade.encripitografarSenha(RandomUtil.generatePassword()));
            }
            Util.validarCampos(selecionado);
            validarInformacoes();
            selecionado.getLogin().replaceAll("\\s+", "");
            if (isOperacaoNovo() || selecionado.getAuthorities().isEmpty()) {
                List<NfseUserAuthority> authorities = Lists.newArrayList();
                authorities.add(new NfseUserAuthority(selecionado, nfseAuthorityFacade.buscarPorTipo(AuthoritiesConstants.USER)));
                authorities.add(new NfseUserAuthority(selecionado, nfseAuthorityFacade.buscarPorTipo(AuthoritiesConstants.CONTRIBUINTE)));
                selecionado.setNfseUserAuthorities(authorities);
                selecionado.setCreatedBy(sistemaFacade.getUsuarioCorrente().getLogin());
            }
            selecionado = usuarioWebFacade.salvarRetornando(selecionado);
            if (isOperacaoNovo()) {
                reiniciarSenha();
            }
            FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "ver/" + selecionado.getId());
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarInformacoes() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getEmail() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo E-mail é obrigatório!");
        }
        if (selecionado.getLogin() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Usuário é obrigatório!");
        } else {
            if (!usuarioWebFacade.verificarUsuarioExistentePorLogin(selecionado).isEmpty()) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Usuário já cadastrado!");
            }
        }
        ve.lancarException();
    }

    public void removerCadastroEconomico(UserNfseCadastroEconomico userNfseCadastroEconomico) {
        if (selecionado.getUserNfseCadastroEconomico() != null && selecionado.getUserNfseCadastroEconomico().equals(userNfseCadastroEconomico)) {
            selecionado.setUserNfseCadastroEconomico(null);
        }
        selecionado.getUserNfseCadastroEconomicos().remove(userNfseCadastroEconomico);
    }

    public void defineContador(UserNfseCadastroEconomico userNfseCadastroEconomico) {
        userNfseCadastroEconomico.setContador(!userNfseCadastroEconomico.getContador());
    }

    public CadastroEconomico getCadastroEconomicoSelecionado() {
        return cadastroEconomicoSelecionado;
    }

    public void setCadastroEconomicoSelecionado(CadastroEconomico cadastroEconomicoSelecionado) {
        this.cadastroEconomicoSelecionado = cadastroEconomicoSelecionado;
    }

    public void adicionarCadastroEconomico() {
        try {
            validarListaCadastroEconomico();
            selecionado = usuarioWebFacade.adicionarCadastroEconomicoAoUsuario(selecionado, cadastroEconomicoSelecionado, permissaoUsuarioEmpresaNfses);
            cadastroEconomicoSelecionado = null;
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarListaCadastroEconomico() {
        ValidacaoException ve = new ValidacaoException();
        if (cadastroEconomicoSelecionado == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Selecione um Cadastro Econômico!");
        }
        for (UserNfseCadastroEconomico cadastro : selecionado.getUserNfseCadastroEconomicos()) {
            if (cadastro.getCadastroEconomico().equals(cadastroEconomicoSelecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Cadastro Econômico já foi adicionado a lista!");
            }
        }
        ve.lancarException();
    }

    public void reiniciarSenha() {
        if (selecionado != null) {
            selecionado = usuarioWebFacade.recuperar(selecionado.getId());
            selecionado.setResetKey(RandomUtil.generateResetKey());
            selecionado.setResetDate(new Date());
            selecionado = usuarioWebFacade.salvarRetornando(selecionado);
            if (selecionado.isActivated()) {
                usuarioWebFacade.enviarEmailResetarSenha(selecionado);
            } else {
                usuarioWebFacade.enviarEmailAtivacaoUsuario(selecionado);
            }
            FacesUtil.addOperacaoRealizada("Verifique a caixa de entrada do email '" + selecionado.getEmail() + "' e siga as instruções");
        }
    }

    public void definirSenhaIgualCpfCnpj() {
        if (selecionado != null) {
            selecionado.setPassword(usuarioWebFacade.encripitografarSenha(
                StringUtil.retornaApenasNumeros(selecionado.getPessoa().getCpf_Cnpj())));
            selecionado = usuarioWebFacade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada("Senha redefinida com sucesso!");
        }
    }

    public void permitirAcesso(UserNfseCadastroEconomico userCadastro) {
        userCadastro.setPermissoes(permissaoUsuarioEmpresaNfses);
        usuarioWebFacade.permitirAcesso(userCadastro);
        FacesUtil.redirecionamentoInterno(getCaminhoPadrao() + "editar/" + selecionado.getId() + "/");
    }

    public List<PermissaoUsuarioEmpresaNfse> getPermissoes() {
        return permissaoUsuarioEmpresaNfses;
    }

    public void adicionarOuRemovePermissaoEmpresa(UserNfseCadastroEconomico cadastro, PermissaoUsuarioEmpresaNfse permissao) {
        if (cadastro.getPermissoes().contains(permissao)) {
            cadastro.getPermissoes().remove(permissao);
        } else {
            cadastro.getPermissoes().add(permissao);
        }
    }

    public void ativarDesativarUsuario() {
        selecionado.setActivated(!selecionado.isActivated());
        selecionado = usuarioWebFacade.criarHistoricoAtivacaoInativacao(selecionado, sistemaFacade.getUsuarioCorrente());
        FacesUtil.addOperacaoRealizada("Usuário " + (selecionado.isActivated() ? "Ativado" : "Inativado") + " com sucesso!");
    }

    public void salvarBloqueioEmissaoNfse() {
        try {
            bloqueioEmissaoNfse.realizarValidacoes();
            selecionado.adicionarBloqueioEmissaoNfse(bloqueioEmissaoNfse);
            cancelarBloqueioEmissaoNfse();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve);
        } catch (Exception e) {
            FacesUtil.addErrorPadrao(e);
        }
    }

    public void cancelarBloqueioEmissaoNfse() {
        bloqueioEmissaoNfse = null;
    }

    public void novoBloqueioEmissaoNfse() {
        bloqueioEmissaoNfse = new BloqueioEmissaoNfse();
        bloqueioEmissaoNfse.setDataRegistro(new Date());
        bloqueioEmissaoNfse.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());
    }

    public void changeCadastroEconomico() {
        bloqueioEmissaoNfse.setSituacao(null);
        if (bloqueioEmissaoNfse.getCadastroEconomico() != null) {
            UserNfseCadastroEconomico userNfseCadastroEconomico = selecionado.getUserNfseCadastroEconomico(bloqueioEmissaoNfse.getCadastroEconomico());
            bloqueioEmissaoNfse.setSituacao(userNfseCadastroEconomico.getBloqueadoEmissaoNfse() ? SituacaoBloqueioEmissaoNfse.DESBLOQUEAR : SituacaoBloqueioEmissaoNfse.BLOQUEAR);
        }
    }

    public List<CadastroEconomico> completarCadastroEconomicoUsuarioNfse(String parte) {
        return usuarioWebFacade.buscarCadastroEconomicoPorUsuarioNfse(selecionado, parte);
    }

    public List<SelectItem> getSituacoesBloqueioEmissaoNfse() {
        return Util.getListSelectItemSemCampoVazio(SituacaoBloqueioEmissaoNfse.values());
    }
}

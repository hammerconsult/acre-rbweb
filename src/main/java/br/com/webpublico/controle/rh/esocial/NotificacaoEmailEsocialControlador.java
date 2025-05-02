package br.com.webpublico.controle.rh.esocial;


import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.rh.esocial.NotificacaoEmailEsocial;
import br.com.webpublico.entidades.rh.esocial.UsuarioEmailEsocial;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.esocial.NotificacaoEmailEsocialFacade;
import br.com.webpublico.util.FacesUtil;
import com.google.common.base.Strings;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Objects;

@ManagedBean(name = "notificacaoEmailEsocialControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novaNotificacaoEmailEsocial", pattern = "/notificacao-email-esocial/novo/", viewId = "/faces/rh/esocial/notificacao-email/edita.xhtml"),
    @URLMapping(id = "editarNotificacaoEmailEsocial", pattern = "/notificacao-email-esocial/editar/#{notificacaoEmailEsocialControlador.id}/", viewId = "/faces/rh/esocial/notificacao-email/edita.xhtml"),
    @URLMapping(id = "listarNotificacaoEmailEsocial", pattern = "/notificacao-email-esocial/listar/", viewId = "/faces/rh/esocial/notificacao-email/lista.xhtml"),
    @URLMapping(id = "verNotificacaoEmailEsocial", pattern = "/notificacao-email-esocial/ver/#{notificacaoEmailEsocialControlador.id}/", viewId = "/faces/rh/esocial/notificacao-email/visualizar.xhtml")
})
public class NotificacaoEmailEsocialControlador extends PrettyControlador<NotificacaoEmailEsocial> implements Serializable, CRUD {

    @EJB
    private NotificacaoEmailEsocialFacade facade;

    private UsuarioEmailEsocial usuarioEmailEsocial;

    public NotificacaoEmailEsocialControlador() {
        super(NotificacaoEmailEsocial.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public UsuarioEmailEsocial getUsuarioEmailEsocial() {
        return usuarioEmailEsocial;
    }

    public void setUsuarioEmailEsocial(UsuarioEmailEsocial usuarioEmailEsocial) {
        this.usuarioEmailEsocial = usuarioEmailEsocial;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/notificacao-email-esocial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "novaNotificacaoEmailEsocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
        usuarioEmailEsocial = new UsuarioEmailEsocial();
    }

    @Override
    @URLAction(mappingId = "editarNotificacaoEmailEsocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
        usuarioEmailEsocial = new UsuarioEmailEsocial();
    }

    @Override
    @URLAction(mappingId = "verNotificacaoEmailEsocial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            realizarValidacoes();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void realizarValidacoes() {
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getEmpregador() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o Empregador.");
            ve.lancarException();
        }

        ve.lancarException();
    }

    public void validarEmpregadorCadastrado() {
        ValidacaoException ve = new ValidacaoException();
        if (facade.hasEmpregadorCadastrado(selecionado.getEmpregador().getId())) {
            FacesUtil.addError("Não foi possível cadastrar esse empregador!", "Empregador já cadastrado.");
            selecionado.setEmpregador(null);
        }
    }

    public void removerUsuario(UsuarioEmailEsocial usuario) {
        selecionado.getUsuarios().remove(usuario);
    }

    public void adicionarUsuario() {
        try {
            validarUsuario();
            usuarioEmailEsocial.setNotificacaoEmailEsocial(selecionado);
            selecionado.getUsuarios().add(usuarioEmailEsocial);
            usuarioEmailEsocial = new UsuarioEmailEsocial();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarUsuario() {
        ValidacaoException ve = new ValidacaoException();
        if (usuarioEmailEsocial.getUsuarioSistema() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o usuário.");
            ve.lancarException();
        }
        if (usuarioEmailEsocial.getUsuarioSistema() != null && Strings.isNullOrEmpty(usuarioEmailEsocial.getUsuarioSistema().getPessoaFisica().getEmail())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Usuário sem e-mail cadastrado.");
        }

        selecionado.getUsuarios().forEach(user -> {
            if (Objects.equals(user.getUsuarioSistema().getId(), usuarioEmailEsocial.getUsuarioSistema().getId())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Usuário já adicionado.");
            }
        });
        ve.lancarException();
    }
}

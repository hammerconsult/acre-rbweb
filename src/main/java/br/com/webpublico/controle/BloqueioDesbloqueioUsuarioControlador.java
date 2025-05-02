package br.com.webpublico.controle;

import br.com.webpublico.entidades.BloqueioDesbloqueioUsuario;
import br.com.webpublico.entidades.BloqueioDesbloqueioUsuarioEmail;
import br.com.webpublico.entidades.BloqueioDesbloqueioUsuarioTipoAfastamento;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BloqueioDesbloqueioUsuarioFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-bloqueio-desbloqueio-usuario", pattern = "/bloqueio-desbloqueio-usuario/novo/", viewId = "/faces/comum/bloqueio-desbloqueio-usuario/edita.xhtml"),
    @URLMapping(id = "lista-bloqueio-desbloqueio-usuario", pattern = "/bloqueio-desbloqueio-usuario/listar/", viewId = "/faces/comum/bloqueio-desbloqueio-usuario/lista.xhtml"),
    @URLMapping(id = "ver-bloqueio-desbloqueio-usuario", pattern = "/bloqueio-desbloqueio-usuario/ver/#{bloqueioDesbloqueioUsuarioControlador.id}/", viewId = "/faces/comum/bloqueio-desbloqueio-usuario/visualizar.xhtml"),
    @URLMapping(id = "editar-bloqueio-desbloqueio-usuario", pattern = "/bloqueio-desbloqueio-usuario/editar/#{bloqueioDesbloqueioUsuarioControlador.id}/", viewId = "/faces/comum/bloqueio-desbloqueio-usuario/edita.xhtml")
})
public class BloqueioDesbloqueioUsuarioControlador extends PrettyControlador<BloqueioDesbloqueioUsuario> implements Serializable, CRUD {

    @EJB
    private BloqueioDesbloqueioUsuarioFacade facade;
    private BloqueioDesbloqueioUsuarioEmail email;
    private BloqueioDesbloqueioUsuarioTipoAfastamento tipoAfastamento;

    public BloqueioDesbloqueioUsuarioControlador() {
        super(BloqueioDesbloqueioUsuario.class);
    }

    @URLAction(mappingId = "novo-bloqueio-desbloqueio-usuario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        cancelarTipoAfastamento();
        cancelarEmail();
        selecionado.setInicioVigencia(facade.getSistemaFacade().getDataOperacao());
    }

    @URLAction(mappingId = "ver-bloqueio-desbloqueio-usuario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-bloqueio-desbloqueio-usuario", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        cancelarTipoAfastamento();
        cancelarEmail();
    }

    public void cancelarEmail() {
        email = null;
    }

    public void instanciarEmail() {
        email = new BloqueioDesbloqueioUsuarioEmail();
        email.setBloqueioDesbloqueioUsuario(selecionado);
    }

    public void editarEmail(BloqueioDesbloqueioUsuarioEmail bloqueioDesbloqueioUsuarioEmail) {
        email = (BloqueioDesbloqueioUsuarioEmail) Util.clonarObjeto(bloqueioDesbloqueioUsuarioEmail);
    }

    public void removerEmail(BloqueioDesbloqueioUsuarioEmail bloqueioDesbloqueioUsuarioEmail) {
        selecionado.getEmails().remove(bloqueioDesbloqueioUsuarioEmail);
    }

    public void adicionarEmail() {
        try {
            validarEmail();
            Util.adicionarObjetoEmLista(selecionado.getEmails(), email);
            cancelarEmail();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarEmail() {
        ValidacaoException ve = new ValidacaoException();
        if (email.getEmail().isEmpty()) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo E-mail deve ser informado!");
        }
        ve.lancarException();
        for (BloqueioDesbloqueioUsuarioEmail bloqueioDesbloqueioUsuarioEmail : selecionado.getEmails()) {
            if (!bloqueioDesbloqueioUsuarioEmail.equals(email) && bloqueioDesbloqueioUsuarioEmail.getEmail().trim().toLowerCase().equals(email.getEmail().trim().toLowerCase())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O e-mail informado já está adicionado!");
            }
        }
        ve.lancarException();
    }

    public void cancelarTipoAfastamento() {
        tipoAfastamento = null;
    }

    public void instanciarTipoAfastamento() {
        tipoAfastamento = new BloqueioDesbloqueioUsuarioTipoAfastamento();
        tipoAfastamento.setBloqueioDesbloqueioUsuario(selecionado);
    }

    public void editarTipoAfastamento(BloqueioDesbloqueioUsuarioTipoAfastamento bloqueioDesbloqueioUsuarioTipoAfastamento) {
        tipoAfastamento = (BloqueioDesbloqueioUsuarioTipoAfastamento) Util.clonarObjeto(bloqueioDesbloqueioUsuarioTipoAfastamento);
    }

    public void removerTipoAfastamento(BloqueioDesbloqueioUsuarioTipoAfastamento bloqueioDesbloqueioUsuarioTipoAfastamento) {
        selecionado.getTiposDeAfastamento().remove(bloqueioDesbloqueioUsuarioTipoAfastamento);
    }

    public void adicionarTipoAfastamento() {
        try {
            validarTipoAfastamento();
            Util.adicionarObjetoEmLista(selecionado.getTiposDeAfastamento(), tipoAfastamento);
            cancelarTipoAfastamento();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        }
    }

    private void validarTipoAfastamento() {
        ValidacaoException ve = new ValidacaoException();
        if (tipoAfastamento.getTipoAfastamento() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Tipo de Afastamento deve ser informado!");
        }
        ve.lancarException();
        for (BloqueioDesbloqueioUsuarioTipoAfastamento bloqueioDesbloqueioUsuarioTipoAfastamento : selecionado.getTiposDeAfastamento()) {
            if (!bloqueioDesbloqueioUsuarioTipoAfastamento.equals(tipoAfastamento) && tipoAfastamento.getTipoAfastamento().equals(bloqueioDesbloqueioUsuarioTipoAfastamento.getTipoAfastamento())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Tipo de Afastamento selecionado já está adicionado!");
            }
        }
        ve.lancarException();
    }

    public List<SelectItem> getTiposDeAfastamento() {
        return Util.getListSelectItem(facade.getTipoAfastamentoFacade().lista(), false);
    }

    @Override
    public void salvar() {
        try {
            validarCampos();
            if (isOperacaoNovo()) {
                facade.salvarNovo(selecionado);
            } else {
                facade.salvar(selecionado);
            }
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            descobrirETratarException(ex);
        }
    }

    private void validarCampos() {
        Util.validarCampos(selecionado);
        ValidacaoException ve = new ValidacaoException();
        if (selecionado.getFimVigencia() != null && selecionado.getFimVigencia().before(selecionado.getInicioVigencia())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo Início de Vigência deve ser inferior ao Fim de Vigência!");
        }
        if (selecionado.getEmails().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("É necessário informar pelo menos um e-mail.");
        }

        ve.lancarException();
        List<BloqueioDesbloqueioUsuario> bloqueios = facade.buscarBloqueiosVigentes(selecionado.getInicioVigencia());
        for (BloqueioDesbloqueioUsuario bloqueio : bloqueios) {
            if (!bloqueio.equals(selecionado)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("Foi encontrado um registro com a vigência informada.");
            }
        }
        ve.lancarException();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/bloqueio-desbloqueio-usuario/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public BloqueioDesbloqueioUsuarioEmail getEmail() {
        return email;
    }

    public void setEmail(BloqueioDesbloqueioUsuarioEmail email) {
        this.email = email;
    }

    public BloqueioDesbloqueioUsuarioTipoAfastamento getTipoAfastamento() {
        return tipoAfastamento;
    }

    public void setTipoAfastamento(BloqueioDesbloqueioUsuarioTipoAfastamento tipoAfastamento) {
        this.tipoAfastamento = tipoAfastamento;
    }
}

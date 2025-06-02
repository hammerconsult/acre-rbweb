package br.com.webpublico.nfse.controladores;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.enums.SummaryMessages;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.nfse.domain.BloqueioCpfCampanhaNfse;
import br.com.webpublico.nfse.domain.CampanhaNfse;
import br.com.webpublico.nfse.enums.TipoCupomCampanhaNfse;
import br.com.webpublico.nfse.facades.CampanhaNfseFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@ManagedBean(name = "campanhaNfseControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(
        id = "campanha-nfse-novo",
        pattern = "/nfse/campanha/novo/",
        viewId = "/faces/tributario/nfse/campanha/edita.xhtml"),
    @URLMapping(
        id = "campanha-nfse-listar",
        pattern = "/nfse/campanha/listar/",
        viewId = "/faces/tributario/nfse/campanha/lista.xhtml"),
    @URLMapping(
        id = "campanha-nfse-editar",
        pattern = "/nfse/campanha/editar/#{campanhaNfseControlador.id}/",
        viewId = "/faces/tributario/nfse/campanha/edita.xhtml"),
    @URLMapping(
        id = "campanha-nfse-ver",
        pattern = "/nfse/campanha/ver/#{campanhaNfseControlador.id}/",
        viewId = "/faces/tributario/nfse/campanha/visualizar.xhtml"),
})
public class CampanhaNfseControlador extends PrettyControlador<CampanhaNfse> implements Serializable, CRUD {

    @EJB
    private CampanhaNfseFacade facade;
    private BloqueioCpfCampanhaNfse bloqueioCpf;

    public CampanhaNfseControlador() {
        super(CampanhaNfse.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/nfse/campanha/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    public BloqueioCpfCampanhaNfse getBloqueioCpf() {
        return bloqueioCpf;
    }

    public void setBloqueioCpf(BloqueioCpfCampanhaNfse bloqueioCpf) {
        this.bloqueioCpf = bloqueioCpf;
    }

    public CampanhaNfseFacade getFacade() {
        return facade;
    }

    @Override
    @URLAction(mappingId = "campanha-nfse-novo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        super.novo();
    }

    @Override
    @URLAction(mappingId = "campanha-nfse-editar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void editar() {
        super.editar();
    }

    @Override
    @URLAction(mappingId = "campanha-nfse-ver", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    public void inserirBloqueioCPF() {
        this.bloqueioCpf = new BloqueioCpfCampanhaNfse();
    }

    public void adicionarBloquieoCPF() {
        try {
            facade.validarCpfBloqueado(bloqueioCpf, selecionado);
            bloqueioCpf.setCampanha(selecionado);
            selecionado.getCpfsBloqueados().add(bloqueioCpf);
            bloqueioCpf = null;
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
        }
    }

    public void cancelarBloquieoCPF() {
        bloqueioCpf = null;
    }

    public void removerBloquieoCPF(BloqueioCpfCampanhaNfse bloqueioCpf) {
        selecionado.getCpfsBloqueados().remove(bloqueioCpf);
    }

    public List<SelectItem> getTiposCupomSorteio() {
        return Util.getListSelectItem(TipoCupomCampanhaNfse.values());
    }

    public void trocouTipoCupom() {
        selecionado.setValorPorCupom(BigDecimal.ZERO);
    }

    @Override
    public void salvar() {
        try {
            if (validaRegrasParaSalvar()) {
                selecionado = facade.salvarRetornando(selecionado);
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(SummaryMessages.OPERACAO_REALIZADA.getDescricao(), getMensagemSucessoAoSalvar()));
                redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId());
            }
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }

    @Override
    public void salvar(Redirecionar redirecionar) {
        try {
            selecionado.validarCamposObrigatorios();
            selecionado = facade.salvarRetornando(selecionado);
            FacesUtil.addOperacaoRealizada(getMensagemSucessoAoSalvar());
            redireciona(getCaminhoPadrao() + "ver/" + selecionado.getId());
        } catch (ValidacaoException ex) {
            FacesUtil.printAllFacesMessages(ex.getMensagens());
        } catch (Exception e) {
            descobrirETratarException(e);
        }
    }
}

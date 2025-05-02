package br.com.webpublico.controle.contabil.emendagoverno;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.contabil.emendagoverno.EmendaGoverno;
import br.com.webpublico.entidades.contabil.emendagoverno.EmendaGovernoParlamentar;
import br.com.webpublico.enums.TipoEmenda;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.contabil.emendagoverno.EmendaGovernoFacade;
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
    @URLMapping(id = "novo-emenda-governo", pattern = "/planejamento/emenda-governo/novo/", viewId = "/faces/financeiro/emenda-governo/emenda/edita.xhtml"),
    @URLMapping(id = "editar-emenda-governo", pattern = "/planejamento/emenda-governo/editar/#{emendaGovernoControlador.id}/", viewId = "/faces/financeiro/emenda-governo/emenda/edita.xhtml"),
    @URLMapping(id = "ver-emenda-governo", pattern = "/planejamento/emenda-governo/ver/#{emendaGovernoControlador.id}/", viewId = "/faces/financeiro/emenda-governo/emenda/visualizar.xhtml"),
    @URLMapping(id = "listar-emenda-governo", pattern = "/planejamento/emenda-governo/listar/", viewId = "/faces/financeiro/emenda-governo/emenda/lista.xhtml")
})
public class EmendaGovernoControlador extends PrettyControlador<EmendaGoverno> implements Serializable, CRUD {
    @EJB
    private EmendaGovernoFacade facade;
    private EmendaGovernoParlamentar emendaGovernoParlamentar;

    public EmendaGovernoControlador() {
        super(EmendaGoverno.class);
    }

    @URLAction(mappingId = "novo-emenda-governo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        cancelarParlamentar();
    }

    @URLAction(mappingId = "editar-emenda-governo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        cancelarParlamentar();
    }

    @URLAction(mappingId = "ver-emenda-governo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/planejamento/emenda-governo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public List<SelectItem> getTiposEmendas() {
        return Util.getListSelectItem(TipoEmenda.values(), false);
    }

    public void removerParlamentar(EmendaGovernoParlamentar egp) {
        selecionado.getParlamentares().remove(egp);
    }

    public void cancelarParlamentar() {
        emendaGovernoParlamentar = null;
    }

    public void novoParlamentar() {
        emendaGovernoParlamentar = new EmendaGovernoParlamentar();
    }

    public void adicionarParlamentar() {
        try {
            validarAdicionarParlamentar();
            emendaGovernoParlamentar.setEmendaGoverno(selecionado);
            Util.adicionarObjetoEmLista(selecionado.getParlamentares(), emendaGovernoParlamentar);
            cancelarParlamentar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        }
    }

    private void validarAdicionarParlamentar() {
        ValidacaoException ve = new ValidacaoException();
        if (emendaGovernoParlamentar.getParlamentar() == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Parlamentar deve ser informado.");
        }
        ve.lancarException();
        for (EmendaGovernoParlamentar egp : selecionado.getParlamentares()) {
            if (!egp.equals(emendaGovernoParlamentar) && egp.getParlamentar().equals(emendaGovernoParlamentar.getParlamentar())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O Parlamentar selecionado já foi adicionado.");
            }
        }
        ve.lancarException();
    }

    public EmendaGovernoParlamentar getEmendaGovernoParlamentar() {
        return emendaGovernoParlamentar;
    }

    public void setEmendaGovernoParlamentar(EmendaGovernoParlamentar emendaGovernoParlamentar) {
        this.emendaGovernoParlamentar = emendaGovernoParlamentar;
    }

    public List<EmendaGoverno> completarEmendasPorNumeroOuDescricao(String parte) {
        return facade.listaFiltrando(parte.trim(), "numero", "descricao");
    }
}

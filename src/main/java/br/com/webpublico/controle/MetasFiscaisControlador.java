/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.MetasFiscais;
import br.com.webpublico.enums.TipoMetasFiscais;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.MetasFiscaisFacade;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.MoneyConverterMoedaSemRs;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@ManagedBean(name = "metasFiscaisControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novometasfiscais", pattern = "/metas-fiscais/novo/", viewId = "/faces/financeiro/ppa/metasfiscais/edita.xhtml"),
    @URLMapping(id = "editametasfiscais", pattern = "/metas-fiscais/editar/#{metasFiscaisControlador.id}/", viewId = "/faces/financeiro/ppa/metasfiscais/edita.xhtml"),
    @URLMapping(id = "vermetasfiscais", pattern = "/metas-fiscais/ver/#{metasFiscaisControlador.id}/", viewId = "/faces/financeiro/ppa/metasfiscais/visualizar.xhtml"),
    @URLMapping(id = "listarmetasfiscais", pattern = "/metas-fiscais/listar/", viewId = "/faces/financeiro/ppa/metasfiscais/lista.xhtml")
})
public class MetasFiscaisControlador extends PrettyControlador<MetasFiscais> implements Serializable, CRUD {

    @EJB
    private MetasFiscaisFacade metasFiscaisFacade;
    private MoneyConverterMoedaSemRs moneyConverter;
    @ManagedProperty(name = "sistemaControlador", value = "#{sistemaControlador}")
    private SistemaControlador sistemaControlador;

    public MetasFiscaisControlador() {
        super(MetasFiscais.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return metasFiscaisFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/metas-fiscais/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


    @URLAction(mappingId = "novometasfiscais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        selecionado.setTipoMetasFiscais(TipoMetasFiscais.PREVISTO);
    }

    @URLAction(mappingId = "editametasfiscais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "vermetasfiscais", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }

    @Override
    public void salvar() {
        try {
            Util.validarCampos(selecionado);
            validarDuplicidade();
            super.salvar();
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        }
    }

    public List<SelectItem> getListaTipoMetasFiscais() {
        List<SelectItem> lista = new ArrayList<>();
        for (TipoMetasFiscais metasFiscais : TipoMetasFiscais.values()) {
            lista.add(new SelectItem(metasFiscais, metasFiscais.toString()));
        }
        return lista;
    }

    public List<SelectItem> getLdos() {
        return Util.getListSelectItem(metasFiscaisFacade.getLdoFacade().lista());
    }

    public void validarDuplicidade() {
        ValidacaoException ve = new ValidacaoException();
        MetasFiscais metasFiscais = metasFiscaisFacade.buscarMetasFiscaisPorExercicioELdo(selecionado);
        if (metasFiscais != null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Já existe Metas Fiscais cadastrada para o Exercício de " + selecionado.getExercicio() + ", LDO " + selecionado.getLdo() + " do Tipo " + selecionado.getTipoMetasFiscais() + ".");
        }
        ve.lancarException();
    }

    public MoneyConverterMoedaSemRs getMoneyConverter() {
        if (moneyConverter == null) {
            moneyConverter = new MoneyConverterMoedaSemRs();
        }
        return moneyConverter;
    }

    public MetasFiscaisFacade getMetasFiscaisFacade() {
        return metasFiscaisFacade;
    }

    public void setMetasFiscaisFacade(MetasFiscaisFacade metasFiscaisFacade) {
        this.metasFiscaisFacade = metasFiscaisFacade;
    }

    public SistemaControlador getSistemaControlador() {
        return sistemaControlador;
    }

    public void setSistemaControlador(SistemaControlador sistemaControlador) {
        this.sistemaControlador = sistemaControlador;
    }
}

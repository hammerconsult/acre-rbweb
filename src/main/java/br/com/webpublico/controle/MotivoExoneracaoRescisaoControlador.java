/*
 * Codigo gerado automaticamente em Tue Feb 07 08:22:09 BRST 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoCAGED;
import br.com.webpublico.enums.rh.esocial.TipoMotivoDesligamentoESocial;
import br.com.webpublico.enums.rh.previdencia.TipoMotivoDemissaoBBPrev;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
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
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoMotivoExoneracao", pattern = "/motivo-exoneracao-rescisao/novo/", viewId = "/faces/rh/administracaodepagamento/motivoexoneracaorescisao/edita.xhtml"),
        @URLMapping(id = "editarMotivoExoneracao", pattern = "/motivo-exoneracao-rescisao/editar/#{motivoExoneracaoRescisaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/motivoexoneracaorescisao/edita.xhtml"),
        @URLMapping(id = "listarMotivoExoneracao", pattern = "/motivo-exoneracao-rescisao/listar/", viewId = "/faces/rh/administracaodepagamento/motivoexoneracaorescisao/lista.xhtml"),
        @URLMapping(id = "verMotivoExoneracao", pattern = "/motivo-exoneracao-rescisao/ver/#{motivoExoneracaoRescisaoControlador.id}/", viewId = "/faces/rh/administracaodepagamento/motivoexoneracaorescisao/visualizar.xhtml")
})
public class MotivoExoneracaoRescisaoControlador extends PrettyControlador<MotivoExoneracaoRescisao> implements Serializable, CRUD {

    @EJB
    private MotivoExoneracaoRescisaoFacade motivoExoneracaoRescisaoFacade;
    @EJB
    private MovimentoCAGEDFacade movimentoCAGEDFacade;
    @EJB
    private MotivoDesligamentoFGTSFacade motivoDesligamentoFGTSFacade;
    @EJB
    private MotivoDesligamentoRAISFacade motivoDesligamentoRAISFacade;
    @EJB
    private TipoSaqueFacade tipoSaqueFacade;
    private ConverterGenerico converterMovimentoCAGED;
    private ConverterGenerico converterTipoSaque;
    private ConverterGenerico converterMotivoDesligamentoFGTS;
    private ConverterGenerico converterMotivoDesligamentoRAIS;

    @URLAction(mappingId = "novoMotivoExoneracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "verMotivoExoneracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "editarMotivoExoneracao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public MotivoExoneracaoRescisaoControlador() {
        metadata = new EntidadeMetaData(MotivoExoneracaoRescisao.class);
    }

    public MotivoExoneracaoRescisaoFacade getFacade() {
        return motivoExoneracaoRescisaoFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return motivoExoneracaoRescisaoFacade;
    }

    @Override
    public void salvar() {
        if (validaCampos())
            super.salvar();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public Boolean validaCampos() {
        if (motivoExoneracaoRescisaoFacade.existeCodigo(selecionado)) {
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção !", "O Código informado já está cadastrado em outro Motivo de Exoneração/Rescisão !");
            FacesContext.getCurrentInstance().addMessage("msg", msg);
            return false;
        }
        return true;
    }

    public List<SelectItem> getMovimentosCAGED() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (MovimentoCAGED object : movimentoCAGEDFacade.listaMovimentoPorTipo(TipoCAGED.DEMISSAO)) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterMovimentoCAGED() {
        if (converterMovimentoCAGED == null) {
            converterMovimentoCAGED = new ConverterGenerico(MovimentoCAGED.class, movimentoCAGEDFacade);
        }
        return converterMovimentoCAGED;
    }

    public List<SelectItem> getMotivosDesligamentoFGTS() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (MotivoDesligamentoFGTS object : motivoDesligamentoFGTSFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterMotivoDesligamentoFGTS() {
        if (converterMotivoDesligamentoFGTS == null) {
            converterMotivoDesligamentoFGTS = new ConverterGenerico(MotivoDesligamentoFGTS.class, motivoDesligamentoFGTSFacade);
        }
        return converterMotivoDesligamentoFGTS;
    }

    public List<SelectItem> getMotivosDesligamentoRAIS() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (MotivoDesligamentoRAIS object : motivoDesligamentoRAISFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterMotivoDesligamentoRAIS() {
        if (converterMotivoDesligamentoRAIS == null) {
            converterMotivoDesligamentoRAIS = new ConverterGenerico(MotivoDesligamentoRAIS.class, motivoDesligamentoRAISFacade);
        }
        return converterMotivoDesligamentoRAIS;
    }

    public List<SelectItem> getTiposSaque() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (TipoSaque object : tipoSaqueFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterTipoSaque() {
        if (converterTipoSaque == null) {
            converterTipoSaque = new ConverterGenerico(TipoSaque.class, tipoSaqueFacade);
        }
        return converterTipoSaque;
    }

    public List<SelectItem> getTipoMotivoDesligamentoEsocial() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        for (TipoMotivoDesligamentoESocial tipo : TipoMotivoDesligamentoESocial.values()) {
            toReturn.add(new SelectItem(tipo, tipo.toString()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposMotivosDemissaoBBPrev() {
        return Util.getListSelectItem(TipoMotivoDemissaoBBPrev.values(), false);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/motivo-exoneracao-rescisao/";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();  //To change body of implemented methods use File | Settings | File Templates.
    }
}

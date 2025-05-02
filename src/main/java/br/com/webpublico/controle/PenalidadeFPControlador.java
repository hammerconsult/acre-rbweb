/*
 * Codigo gerado automaticamente em Tue Apr 03 17:54:41 BRT 2012
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.PenalidadeFP;
import br.com.webpublico.entidades.TipoPenalidadeFP;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import javax.faces.bean.ViewScoped;

@ManagedBean(name = "penalidadeFPControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoPenalidadeFP", pattern = "/penalidade/novo/", viewId = "/faces/rh/administracaodepagamento/penalidadefp/edita.xhtml"),
        @URLMapping(id = "editarPenalidadeFP", pattern = "/penalidade/editar/#{penalidadeFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/penalidadefp/edita.xhtml"),
        @URLMapping(id = "listarPenalidadeFP", pattern = "/penalidade/listar/", viewId = "/faces/rh/administracaodepagamento/penalidadefp/lista.xhtml"),
        @URLMapping(id = "verPenalidadeFP", pattern = "/penalidade/ver/#{penalidadeFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/penalidadefp/visualizar.xhtml")
})
public class PenalidadeFPControlador extends PrettyControlador<PenalidadeFP> implements Serializable, CRUD {

    @EJB
    private PenalidadeFPFacade penalidadeFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterContratoFP;
    @EJB
    private AtoLegalFacade atoLegalFacade;
    private ConverterAutoComplete converterAtoLegal;
    @EJB
    private TipoPenalidadeFacade tipoPenalidadeFacade;
    private ConverterAutoComplete converterTipoPenalidade;

    public PenalidadeFPControlador() {
        super(PenalidadeFP.class);
    }

    public PenalidadeFPFacade getFacade() {
        return penalidadeFPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return penalidadeFPFacade;
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.recuperaContrato(parte.trim());
    }

    public ConverterAutoComplete getConverterContratoFP() {
        if (converterContratoFP == null) {
            converterContratoFP = new ConverterAutoComplete(ContratoFP.class, contratoFPFacade);
        }
        return converterContratoFP;
    }

    public List<AtoLegal> completaAtoLegal(String parte) {
        return atoLegalFacade.listaFiltrandoAtoDePessoal(parte.trim());
    }

    public ConverterAutoComplete getConverterAtoLegal() {
        if (converterAtoLegal == null) {
            converterAtoLegal = new ConverterAutoComplete(AtoLegal.class, atoLegalFacade);
        }
        return converterAtoLegal;
    }

    public List<TipoPenalidadeFP> completaTipoPenalidade(String parte) {
        return tipoPenalidadeFacade.listaFiltrando(parte.trim(), "codigo", "descricao");
    }

    public ConverterAutoComplete getConverterTipoPenalidade() {
        if (converterTipoPenalidade == null) {
            converterTipoPenalidade = new ConverterAutoComplete(TipoPenalidadeFP.class, tipoPenalidadeFacade);
        }
        return converterTipoPenalidade;
    }

    public Boolean validaCampos() {
        boolean retorno = Util.validaCampos(selecionado);
        if (retorno) {
            PenalidadeFP penalidade = (PenalidadeFP) selecionado;
            if (penalidade.getInicioVigencia().getTime() > penalidade.getFinalVigencia().getTime()) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Atenção", "A data final de vigência não pode ser menor que a data inicial!"));
                retorno = false;
            }
            if(penalidade.ultrapassaLimiteDiasPenalidade()){
                FacesUtil.addError("Atenção","Total de dias ultrapassa o valor estipulado no tipo de penalidade. Max. dias permitido: "  +penalidade.getTipoPenalidadeFP().getDiasDescontar() );
                retorno = false;
            }
        }

        return retorno;
    }

    @URLAction(mappingId = "editarPenalidadeFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verPenalidadeFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "novoPenalidadeFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/penalidade/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}

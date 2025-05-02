package br.com.webpublico.controle.rh.pccr;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.rh.pccr.CategoriaPCCR;
import br.com.webpublico.entidades.rh.pccr.EnquadramentoFuncionalPCCR;
import br.com.webpublico.entidades.rh.pccr.ValorProgressaoPCCR;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.rh.pccr.CategoriaPCCRFacade;
import br.com.webpublico.negocios.rh.pccr.EnquadramentoFuncionalPCCRFacade;
import br.com.webpublico.negocios.rh.pccr.ReferenciaProgressaoPCCRFacade;
import br.com.webpublico.negocios.rh.pccr.ValorProgressaoPCCRFacade;
import br.com.webpublico.util.ConverterGenerico;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 27/06/14
 * Time: 14:31
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoEnquadramentoFuncionalPCCR", pattern = "/enquadramento-funcional-pccr/novo/", viewId = "/faces/rh/pccr/enquadramentofuncionalpccr/edita.xhtml"),
        @URLMapping(id = "editarEnquadramentoFuncionalPCCR", pattern = "/enquadramento-funcional-pccr/editar/#{enquadramentoFuncionalPCCRControlador.id}/", viewId = "/faces/rh/pccr/enquadramentofuncionalpccr/edita.xhtml"),
        @URLMapping(id = "listarEnquadramentoFuncionalPCCR", pattern = "/enquadramento-funcional-pccr/listar/", viewId = "/faces/rh/pccr/enquadramentofuncionalpccr/lista.xhtml"),
        @URLMapping(id = "verEnquadramentoFuncionalPCCR", pattern = "/enquadramento-funcional-pccr/ver/#{enquadramentoFuncionalPCCRControlador.id}/", viewId = "/faces/rh/pccr/enquadramentofuncionalpccr/visualizar.xhtml")
})
public class EnquadramentoFuncionalPCCRControlador extends PrettyControlador<EnquadramentoFuncionalPCCR> implements Serializable, CRUD {
    @EJB
    private EnquadramentoFuncionalPCCRFacade enquadramentoFuncionalPCCRFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private CategoriaPCCRFacade categoriaPCCRFacade;
    @EJB
    private ReferenciaProgressaoPCCRFacade referenciaProgressaoPCCRFacade;
    private ConverterGenerico converterGenericoCateroria;
    @EJB
    private ValorProgressaoPCCRFacade valorProgressaoPCCRFacade;

    public EnquadramentoFuncionalPCCRControlador() {
        super(EnquadramentoFuncionalPCCR.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/enquadramento-funcional-pccr/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return enquadramentoFuncionalPCCRFacade;
    }

    @URLAction(mappingId = "novoEnquadramentoFuncionalPCCR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editarEnquadramentoFuncionalPCCR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "verEnquadramentoFuncionalPCCR", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.editar();
    }

    @Override
    public void salvar() {
        try {
            selecionado.valida();
            super.salvar();
        } catch (Exception e) {
            FacesUtil.addError("Atenção", e.getMessage());
        }
         //To change body of overridden methods use File | Settings | File Templates.
    }

    public List<ContratoFP> completaContratoServidor(String parte) {
        return contratoFPFacade.recuperaContrato(parte.trim());
    }

    public List<SelectItem> getCategoriasPCCRs() {
        if (selecionado.getContratoServidor() != null)
            return Util.getListSelectItem(categoriaPCCRFacade.findCategoriasByCargo(selecionado.getContratoServidor().getCargo()));
        else {
            return new ArrayList<SelectItem>();
        }
    }

    public List<SelectItem> getReferenciaProgressoes() {
        if (selecionado.getCategoriaPCCR() != null)
            return Util.getListSelectItem(referenciaProgressaoPCCRFacade.findLetrasByCategoria(selecionado.getCategoriaPCCR()));
        else {
            return new ArrayList<SelectItem>();
        }
    }

    public void preencherCategoria() {
        categoriaPCCRFacade.preencherCategoriaPCCRParaTest();
    }

    public ConverterGenerico getConverterCategoria() {
        if (converterGenericoCateroria == null) {
            converterGenericoCateroria = new ConverterGenerico(CategoriaPCCR.class, categoriaPCCRFacade);
        }

        return converterGenericoCateroria;
    }

    public void carregaValorDoEnquadramento() {
        if (selecionado.getReferenciaProgressaoPCCR() != null && selecionado.getCategoriaPCCR() != null) {
            ValorProgressaoPCCR valor = valorProgressaoPCCRFacade.findValorByProgresaoAndCategoria(selecionado.getReferenciaProgressaoPCCR(), selecionado.getCategoriaPCCR());
            if (valor != null) selecionado.setVencimentoBase(valor.getValor());
        }

    }
}

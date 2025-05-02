/*
 * Codigo gerado automaticamente em Wed Aug 24 16:52:39 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.RecursoDoVinculoFP;
import br.com.webpublico.entidades.RecursoFP;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.ConverterAutoComplete;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "recursoDoVinculoFPControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoRecursoDoVinculoFP", pattern = "/recurso-vinculofp/novo/", viewId = "/faces/rh/administracaodepagamento/recursodovinculofp/edita.xhtml"),
        @URLMapping(id = "editarRecursoDoVinculoFP", pattern = "/recurso-vinculofp/editar/#{recursoDoVinculoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/recursodovinculofp/edita.xhtml"),
        @URLMapping(id = "listarRecursoDoVinculoFP", pattern = "/recurso-vinculofp/listar/", viewId = "/faces/rh/administracaodepagamento/recursodovinculofp/lista.xhtml"),
        @URLMapping(id = "verRecursoDoVinculoFP", pattern = "/recurso-vinculofp/ver/#{recursoDoVinculoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/recursodovinculofp/visualizar.xhtml")
})
public class RecursoDoVinculoFPControlador extends PrettyControlador<RecursoDoVinculoFP> implements Serializable, CRUD {

    @EJB
    private RecursoDoVinculoFPFacade recursoDoVinculoFPFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    private ConverterAutoComplete converterVinculoFP;
    @EJB
    private RecursoFPFacade recursoFPFacade;
    private ConverterAutoComplete converterRecursoFP;
    private Boolean bloqueiaInicioVigencia;
    @EJB
    private SistemaFacade sistemaFacade;

    public RecursoDoVinculoFPControlador() {
        super(RecursoDoVinculoFP.class);
    }

    public RecursoDoVinculoFPFacade getFacade() {
        return recursoDoVinculoFPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return recursoDoVinculoFPFacade;
    }

    public Boolean getBloqueiaInicioVigencia() {
        return bloqueiaInicioVigencia;
    }

    public void setBloqueiaInicioVigencia(Boolean bloqueiaInicioVigencia) {
        this.bloqueiaInicioVigencia = bloqueiaInicioVigencia;
    }

    @URLAction(mappingId = "novoRecursoDoVinculoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        bloqueiaInicioVigencia = Boolean.FALSE;
    }

    @URLAction(mappingId = "editarRecursoDoVinculoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
        bloqueiaInicioVigencia = Boolean.TRUE;
    }

    @URLAction(mappingId = "verRecursoDoVinculoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    public ConverterAutoComplete getConverterVinculoFP() {
        if (converterVinculoFP == null) {
            converterVinculoFP = new ConverterAutoComplete(VinculoFP.class, vinculoFPFacade);
        }
        return converterVinculoFP;
    }

    public ConverterAutoComplete getConverterRecursoFP() {
        if (converterRecursoFP == null) {
            converterRecursoFP = new ConverterAutoComplete(RecursoFP.class, recursoFPFacade);
        }
        return converterRecursoFP;
    }

    public List<VinculoFP> completaVinculoFP(String parte) {
        //return vinculoFPFacade.listaFiltrandoNomeNumero(parte);
        return contratoFPFacade.buscaContratoFiltrandoAtributosVinculoMatriculaFPVigente(parte.trim());
    }

    public List<RecursoFP> completaRecursoFP(String parte) {
        return recursoFPFacade.listaRecursosFPVigente(parte, sistemaFacade.getDataOperacao());
    }

    @Override
    public void salvar() {
        if (validaCampos()) {
            super.salvar();
        }
    }

    public Boolean validaCampos() {
        boolean valido = Util.validaCampos(selecionado);
        if (valido) {
            RecursoDoVinculoFP recursoDoVinculoFP = (RecursoDoVinculoFP) selecionado;
            if (recursoDoVinculoFP.getFinalVigencia() != null) {
                if (recursoDoVinculoFP.getFinalVigencia().before(recursoDoVinculoFP.getInicioVigencia())) {
                    FacesUtil.addWarn("Atenção!", "A data final de vigência não pode ser anterior à data inicial!");
                    valido = false;
                }
            }
        }

        return valido;
    }

    public void setaDataInicioVigencia() {
        if (selecionado.getVinculoFP() != null) {
            RecursoDoVinculoFP r = recursoDoVinculoFPFacade.ultimoRecursoDoVinculo(selecionado.getVinculoFP());
            if (r != null) {
                if (r.getFinalVigencia() == null) {
                    FacesUtil.addWarn("Atenção!", "O servidor selecionado possui recurso vigente.");
                    selecionado.setVinculoFP(null);
                    bloqueiaInicioVigencia = Boolean.FALSE;
                } else {
                    selecionado.setInicioVigencia(DataUtil.adicionaDias(r.getFinalVigencia(), 1));
                    bloqueiaInicioVigencia = Boolean.TRUE;
                }
            } else {
                selecionado.setInicioVigencia(selecionado.getVinculoFP().getInicioVigencia());
                bloqueiaInicioVigencia = Boolean.TRUE;
            }
        }
    }


    @Override
    public String getCaminhoPadrao() {
        return "/recurso-vinculofp/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}

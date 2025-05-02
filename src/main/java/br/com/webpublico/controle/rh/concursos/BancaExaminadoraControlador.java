package br.com.webpublico.controle.rh.concursos;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.Contrato;
import br.com.webpublico.entidades.ContratoFP;
import br.com.webpublico.entidades.rh.concursos.BancaExaminadora;
import br.com.webpublico.entidades.rh.concursos.Concurso;
import br.com.webpublico.entidades.rh.concursos.MembroBancaExaminadora;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.ContratoFPFacade;
import br.com.webpublico.negocios.ContratoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.negocios.rh.concursos.BancaExaminadoraFacade;
import br.com.webpublico.negocios.rh.concursos.ConcursoFacade;
import br.com.webpublico.util.*;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.primefaces.event.SelectEvent;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean(name = "bancaExaminadoraControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novo-banca-examinadora", pattern = "/concursos/banca-examinadora/novo/", viewId = "/faces/rh/concursos/banca-examinadora/edita.xhtml"),
        @URLMapping(id = "editar-banca-examinadora", pattern = "/concursos/banca-examinadora/editar/#{bancaExaminadoraControlador.id}/", viewId = "/faces/rh/concursos/banca-examinadora/edita.xhtml"),
        @URLMapping(id = "ver-banca-examinadora", pattern = "/concursos/banca-examinadora/ver/#{bancaExaminadoraControlador.id}/", viewId = "/faces/rh/concursos/banca-examinadora/edita.xhtml"),
        @URLMapping(id = "listar-banca-examinadora", pattern = "/concursos/banca-examinadora/listar/", viewId = "/faces/rh/concursos/banca-examinadora/lista.xhtml")
})
public class BancaExaminadoraControlador extends PrettyControlador<BancaExaminadora> implements Serializable, CRUD {

    @EJB
    private BancaExaminadoraFacade bancaExaminadoraFacade;
    @EJB
    private ContratoFPFacade contratoFPFacade;
    @EJB
    private ConcursoFacade concursoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    private MembroBancaExaminadora membroBancaExaminadora;
    private ConverterAutoComplete converterContrato;
    @EJB
    private ContratoFacade contratoFacade;

    public MembroBancaExaminadora getMembroBancaExaminadora() {
        return membroBancaExaminadora;
    }

    public void setMembroBancaExaminadora(MembroBancaExaminadora membroBancaExaminadora) {
        this.membroBancaExaminadora = membroBancaExaminadora;
    }

    public BancaExaminadoraControlador() {
        super(BancaExaminadora.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return bancaExaminadoraFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/concursos/banca-examinadora/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "novo-banca-examinadora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-banca-examinadora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-banca-examinadora", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public ConverterAutoComplete getConverterConcurso() {
        return new ConverterAutoComplete(Concurso.class, concursoFacade);
    }

    public Converter converterServidor;

    public Converter getConverterServidor() {
        if (converterServidor == null) {
            converterServidor = new Converter() {
                @Override
                public Object getAsObject (FacesContext facesContext, UIComponent uiComponent, String s){
                    try {
                        return contratoFPFacade.recuperarSomenteContrato(Long.parseLong(s));
                    } catch (Exception e) {
                    }
                    return null;
                }

                @Override
                public String getAsString (FacesContext facesContext, UIComponent uiComponent, Object o){
                    if (o == null) {
                        return null;
                    } else {
                        if (o instanceof Long) {
                            return String.valueOf(o);
                        } else {
                            try {
                                return Persistencia.getAttributeValue(o, Persistencia.getFieldId(o.getClass()).getName()).toString();
                            } catch (Exception e) {
                                return String.valueOf(o);
                            }
                        }
                    }
                }
            } ;
        }
        return converterServidor;
    }

    public List<Contrato> completarContrato(String parte) {
        return contratoFacade.buscarContratoPorNumeroOrExercicio(parte);
    }

    public ConverterAutoComplete getConverterContrato() {
        if (converterContrato == null) {
            converterContrato = new ConverterAutoComplete(Contrato.class, contratoFacade);
        }
        return converterContrato;
    }

    public void definirContrato(SelectEvent evt) {
        Contrato contrato = (Contrato) evt.getObject();
        selecionado.setContrato(contratoFacade.recarregar(contrato));
    }


    public List<SelectItem> getConcursos() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (Concurso c : concursoFacade.getUltimosConcursosAprovadosSemBancaExaminadora()) {
            toReturn.add(new SelectItem(c, "" + c));
        }
        return toReturn;
    }

    public void novoMembroBancaExaminadora() {
        this.membroBancaExaminadora = new MembroBancaExaminadora();
        this.membroBancaExaminadora.setBancaExaminadora(selecionado);
    }

    public void cancelarMembroBancaExaminadora() {
        this.membroBancaExaminadora = null;
    }

    public boolean membroSelecionadoJaAdicionado() {
        if (selecionado.getMembros() == null || selecionado.getMembros().isEmpty()) {
            return false;
        }
        for (MembroBancaExaminadora membro : selecionado.getMembros()) {
            if (membro.getServidor().getMatriculaFP().getPessoa().equals(membroBancaExaminadora.getServidor().getMatriculaFP().getPessoa())) {
                FacesUtil.addOperacaoNaoPermitida("O servidor informado já é um membro da comissão examinadora.");
                return true;
            }
        }
        return false;
    }

    public void confirmarMembroBancaExaminadora() {
        if (!Util.validaCampos(this.membroBancaExaminadora)) {
            return;
        }

        if (membroSelecionadoJaAdicionado()) {
            return;
        }

        selecionado.setMembros(Util.adicionarObjetoEmLista(selecionado.getMembros(), this.membroBancaExaminadora));
        cancelarMembroBancaExaminadora();
    }

    public List<ContratoFP> completaContratoFP(String parte) {
        return contratoFPFacade.recuperarFiltrandoContratosVigentesEm(parte.trim(), UtilRH.getDataOperacao());
    }

    public void removerMembroBancaExaminadora(MembroBancaExaminadora mbe) {
        selecionado.getMembros().remove(mbe);
    }

    public void mudouSelecaoConcurso(){
        selecionado.setMembros(new ArrayList<MembroBancaExaminadora>());
        cancelarMembroBancaExaminadora();
    }

    @Override
    public void salvar() {
        if (!possuiPeloMenosUmMembro()){
            return;
        }
        super.salvar();
    }

    private boolean possuiPeloMenosUmMembro() {
        if(selecionado.getContrato() == null) {
            if (selecionado.getMembros() == null || selecionado.getMembros().isEmpty()) {
                FacesUtil.addOperacaoNaoPermitida("A banca examinadora deve conter pelo menos um membro.");
                return false;
            }
        }
        return true;
    }
}

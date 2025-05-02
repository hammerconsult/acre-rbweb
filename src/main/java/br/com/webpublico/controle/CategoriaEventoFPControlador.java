/*
 * Codigo gerado automaticamente em Mon Sep 05 09:56:38 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.*;
import br.com.webpublico.interfaces.CRUD;
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

@ManagedBean(name = "categoriaEventoFPControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoCategoriaEventoFP", pattern = "/categoriaeventofp/novo/", viewId = "/faces/rh/administracaodepagamento/categoriaeventofp/edita.xhtml"),
        @URLMapping(id = "editarCategoriaEventoFP", pattern = "/categoriaeventofp/editar/#{categoriaEventoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/categoriaeventofp/edita.xhtml"),
        @URLMapping(id = "listarCategoriaEventoFP", pattern = "/categoriaeventofp/listar/", viewId = "/faces/rh/administracaodepagamento/categoriaeventofp/lista.xhtml"),
        @URLMapping(id = "verCategoriaEventoFP", pattern = "/categoriaeventofp/ver/#{categoriaEventoFPControlador.id}/", viewId = "/faces/rh/administracaodepagamento/categoriaeventofp/visualizar.xhtml")
})
public class CategoriaEventoFPControlador extends PrettyControlador<CategoriaEventoFP> implements Serializable, CRUD {

    @EJB
    private CategoriaEventoFPFacade categoriaEventoFPFacade;

    @EJB
    private EventoFPFacade eventoFPFacade;
    @EJB
    private CategoriaPCSFacade categoriaPCSFacade;

    private transient ConverterAutoComplete converterEventoFP;
    private transient ConverterAutoComplete converterCategoria;
    private transient ConverterGenerico converterGenericoCategoria;

    public CategoriaEventoFPControlador() {
        metadata = new EntidadeMetaData(CategoriaEventoFP.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return categoriaEventoFPFacade;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public List<SelectItem> getCategorias() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (CategoriaPCS object : categoriaPCSFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterAutoComplete getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterAutoComplete(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    public ConverterGenerico getConverterCategoria() {
        if (converterGenericoCategoria == null) {
            converterGenericoCategoria = new ConverterGenerico(CategoriaPCS.class, categoriaPCSFacade);
        }
        return converterGenericoCategoria;
    }

//    public void selecionar(ActionEvent evento) {
//        operacao = Operacoes.EDITAR;
//        Medico a = (Medico) evento.getComponent().getAttributes().get("objeto");
//        selecionado = atestadoMedicoFacade.recuperar(a.getId());
//    }

    public List<EventoFP> completaEventoFP(String parte) {
        return eventoFPFacade.listaFiltrandoEventosAtivos(parte.trim());
    }

    @Override
    public void salvar() {
        if (validaCampos())
            super.salvar();    //To change body of overridden methods use File | Settings | File Templates.
    }

    private boolean validaCampos() {
        if (Util.validaCampos(selecionado)) {
            if (selecionado.getFinalVigencia() != null && selecionado.getInicioVigencia().after(selecionado.getFinalVigencia())) {
                FacesUtil.addError("Atenção", "O início de vigência não pode ser maior que o final de vigência.");
                return false;
            }
            CategoriaEventoFP categoriaEventoFP = categoriaEventoFPFacade.existeCategoriaEventoVigente(selecionado);
            if (categoriaEventoFP != null) {
                FacesUtil.addError("Atenção", "Já existe um registro vigente salvo no banco com o evento " + categoriaEventoFP.getEventoFP() + " e com  a categoria " + categoriaEventoFP.getCategoriaPCS() +" dentro de uma vigência. ");
                return false;
            }
        } else return false;
        return true;  //To change body of created methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "novoCategoriaEventoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "verCategoriaEventoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();    //To change body of overridden methods use File | Settings | File Templates.
    }

    @URLAction(mappingId = "editarCategoriaEventoFP", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();    //To change body of overridden methods use File | Settings | File Templates.
    }


    public String getCaminhoPadrao() {
        return "/categoriaeventofp/";
    }


    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}

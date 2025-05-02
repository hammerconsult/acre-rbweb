/*
 * Codigo gerado automaticamente em Wed Oct 05 15:42:34 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.OpcaoSalarialCC;
import br.com.webpublico.enums.Operacoes;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.OpcaoSalarialCCFacade;
import br.com.webpublico.util.*;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;

@ManagedBean(name = "opcaoSalarialCCControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoOpcaoSalarial", pattern = "/opcaosalarial/novo/", viewId = "/faces/rh/administracaodepagamento/opcaosalarialcc/edita.xhtml"),
        @URLMapping(id = "editarOpcaoSalarial", pattern = "/opcaosalarial/editar/#{opcaoSalarialCCControlador.id}/", viewId = "/faces/rh/administracaodepagamento/opcaosalarialcc/edita.xhtml"),
        @URLMapping(id = "listarOpcaoSalarial", pattern = "/opcaosalarial/listar/", viewId = "/faces/rh/administracaodepagamento/opcaosalarialcc/lista.xhtml"),
        @URLMapping(id = "verOpcaoSalarial", pattern = "/opcaosalarial/ver/#{opcaoSalarialCCControlador.id}/", viewId = "/faces/rh/administracaodepagamento/opcaosalarialcc/visualizar.xhtml")
})
public class OpcaoSalarialCCControlador extends PrettyControlador<OpcaoSalarialCC> implements Serializable, CRUD {

    @EJB
    private OpcaoSalarialCCFacade opcaoSalarialCCFacade;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private PercentualConverter percentualConverter;
    private ConverterGenerico converterEventoFp;

    public OpcaoSalarialCCControlador() {
        super(OpcaoSalarialCC.class);
    }

    public PercentualConverter getPercentualConverter() {
        if (percentualConverter == null) {
            percentualConverter = new PercentualConverter();
        }
        return percentualConverter;
    }

    public ConverterGenerico getConverterEventoFP() {
        if (converterEventoFp == null) {
            converterEventoFp = new ConverterGenerico(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFp;
    }

    public List<SelectItem> getEventosFps() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (EventoFP object : eventoFPFacade.listaEventosAtivosFolha()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }


    @Override
    public AbstractFacade getFacede() {
        return opcaoSalarialCCFacade;
    }

    @URLAction(mappingId = "novoOpcaoSalarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
        ((OpcaoSalarialCC) selecionado).setCodigo(opcaoSalarialCCFacade.buscaNovoCodigo());
    }

    @URLAction(mappingId = "verOpcaoSalarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editarOpcaoSalarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public void salvar() {

        if (Util.validaCampos(selecionado)) {
            try {

                if (operacao == Operacoes.NOVO) {

                    String novoCodigo = opcaoSalarialCCFacade.buscaNovoCodigo();

                    if (!novoCodigo.equals(((OpcaoSalarialCC) selecionado).getCodigo())) {
                        FacesUtil.addWarn("Atenção!", "O Código " + getSelecionado().getCodigo() + " já está sendo usado e foi gerado o novo código " + novoCodigo + " !");
                        getSelecionado().setCodigo(novoCodigo);
                    }
                }

                super.salvar();

            } catch (Exception e) {
                FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_FATAL, "Exceção do sistema!", e.getMessage().toString()));
            }
        }
    }

    @Override
    public String getCaminhoPadrao() {
        return "/opcaosalarial/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }
}

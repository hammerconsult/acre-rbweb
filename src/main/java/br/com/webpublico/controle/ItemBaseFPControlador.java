/*
 * Codigo gerado automaticamente em Wed Sep 07 10:28:33 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.BaseFP;
import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.entidades.ItemBaseFP;
import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.BaseFPFacade;
import br.com.webpublico.negocios.EventoFPFacade;
import br.com.webpublico.negocios.ItemBaseFPFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;
import br.com.webpublico.enums.Operacoes;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class ItemBaseFPControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private ItemBaseFPFacade itemBaseFPFacade;
    @EJB
    private BaseFPFacade baseFPFacade;
    private ConverterGenerico converterBaseFP;
    @EJB
    private EventoFPFacade eventoFPFacade;
    private ConverterGenerico converterEventoFP;

    public ItemBaseFPControlador() {
        metadata = new EntidadeMetaData(ItemBaseFP.class);
    }

    public ItemBaseFPFacade getFacade() {
        return itemBaseFPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return itemBaseFPFacade;
    }

    public List<SelectItem> getBaseFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (BaseFP object : baseFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterBaseFP() {
        if (converterBaseFP == null) {
            converterBaseFP = new ConverterGenerico(BaseFP.class, baseFPFacade);
        }
        return converterBaseFP;
    }

    public List<SelectItem> getEventoFP() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        for (EventoFP object : eventoFPFacade.lista()) {
            toReturn.add(new SelectItem(object, object.getDescricao()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterEventoFP() {
        if (converterEventoFP == null) {
            converterEventoFP = new ConverterGenerico(EventoFP.class, eventoFPFacade);
        }
        return converterEventoFP;
    }

    @Override
    public void selecionar(ActionEvent evento) {
        operacao = Operacoes.EDITAR;
        ItemBaseFP ibf = (ItemBaseFP) evento.getComponent().getAttributes().get("objeto");
        selecionado = itemBaseFPFacade.recuperar(ibf.getId());
    }

    public List<SelectItem> getOperacaoFormula() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (OperacaoFormula object : OperacaoFormula.values()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

}

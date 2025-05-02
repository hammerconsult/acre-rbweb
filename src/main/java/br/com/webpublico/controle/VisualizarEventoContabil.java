package br.com.webpublico.controle;

import br.com.webpublico.entidades.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRH;
import com.google.common.collect.Maps;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;

/**
 * Created by renat on 07/06/2016.
 */
@ManagedBean
@ViewScoped
public class VisualizarEventoContabil implements Serializable {

    @EJB
    private RelatorioPesquisaGenericoFacade relatorioPesquisaGenericoFacade;
    @EJB
    private RecuperadorFacade recuperadorFacade;
    @EJB
    private EventoContabilFacade eventoContabilFacade;
    @EJB
    private CLPFacade clpFacade;
    @EJB
    private OrigemOCCFacade origemOCCFacade;
    private EventoContabil eventoContabil;
    private ParametroEvento parametroEvento;
    private CLP clp;
    private Map<String, VisualizarEventoContabil> componente;

    public void novoComponente(String id) {

        if (componente == null) {
            componente = Maps.newHashMap();
        }
        componente.put(id, this);
    }

    public void novo(EventoContabil eventoContabil, ParametroEvento parametroEvento) {
        this.eventoContabil = eventoContabilFacade.recuperar(eventoContabil.getId());
        if (parametroEvento != null) {
            this.parametroEvento = parametroEvento;
        }
        atribuirCLP();
    }

    public Map<String, VisualizarEventoContabil> getComponente() {
        return componente;
    }

    private void atribuirCLP() {
        for (ItemEventoCLP itemEventoCLP : this.eventoContabil.getItemEventoCLPs()) {
            if (itemEventoCLP.getClp().getExercicio().equals(Util.getSistemaControlador().getExercicioCorrente())) {
                Date primeiroDiaAno = DataUtil.getPrimeiroDiaAno(Util.getSistemaControlador().getExercicio());
                if (itemEventoCLP.getClp().getFimVigencia() == null || itemEventoCLP.getClp().getFimVigencia().compareTo(primeiroDiaAno) >= 0) {
                    this.clp = clpFacade.recuperar(itemEventoCLP.getClp().getId());
                    break;
                }
            }
        }
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public CLP getClp() {
        return clp;
    }

    public void setClp(CLP clp) {
        this.clp = clp;
    }

    public ParametroEvento getParametroEvento() {
        return parametroEvento;
    }

    public void setParametroEvento(ParametroEvento parametroEvento) {
        this.parametroEvento = parametroEvento;
    }

    public String recuperarContaPorTag(TagOCC tagOCC) {
        if (parametroEvento == null) {
            return "";
        }
        return eventoContabilFacade.recuperarContaContabilPorParametroTag(parametroEvento, tagOCC, UtilRH.getDataOperacao());
    }
}

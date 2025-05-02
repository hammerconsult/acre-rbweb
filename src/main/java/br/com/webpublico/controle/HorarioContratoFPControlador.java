/*
 * Codigo gerado automaticamente em Wed Sep 14 14:58:10 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.HorarioContratoFP;
import br.com.webpublico.entidades.HorarioDeTrabalho;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.HorarioContratoFPFacade;
import br.com.webpublico.negocios.HorarioDeTrabalhoFacade;
import br.com.webpublico.util.ConverterGenerico;
import br.com.webpublico.util.EntidadeMetaData;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@ManagedBean
@SessionScoped
public class HorarioContratoFPControlador extends SuperControladorCRUD implements Serializable {

    @EJB
    private HorarioContratoFPFacade horarioContratoFPFacade;
    @EJB
    private HorarioDeTrabalhoFacade horarioDeTrabalhoFacade;
    private ConverterGenerico converterHorarioDeTrabalho;
    private HorarioDeTrabalho horarioDeTrabalhoSelecionado;

    public HorarioDeTrabalho getHorarioDeTrabalhoSelecionado() {
        return horarioDeTrabalhoSelecionado;
    }

    public void setHorarioDeTrabalhoSelecionado(HorarioDeTrabalho horarioDeTrabalhoSelecionado) {
        this.horarioDeTrabalhoSelecionado = horarioDeTrabalhoSelecionado;
    }

    public HorarioContratoFPControlador() {
        metadata = new EntidadeMetaData(HorarioContratoFP.class);
    }

    public HorarioContratoFPFacade getFacade() {
        return horarioContratoFPFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return horarioContratoFPFacade;
    }

    public List<SelectItem> getHorarioDeTrabalho() {
        List<SelectItem> toReturn = new ArrayList<SelectItem>();
        toReturn.add(new SelectItem(null, ""));
        for (HorarioDeTrabalho object : horarioDeTrabalhoFacade.lista()) {
            toReturn.add(new SelectItem(object, object.toString()));
        }
        return toReturn;
    }

    public ConverterGenerico getConverterHorarioDeTrabalho() {
        if (converterHorarioDeTrabalho == null) {
            converterHorarioDeTrabalho = new ConverterGenerico(HorarioDeTrabalho.class, horarioDeTrabalhoFacade);
        }
        return converterHorarioDeTrabalho;
    }
}

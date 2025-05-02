package br.com.webpublico.controle;

import br.com.webpublico.entidades.HistoricoContabil;
import br.com.webpublico.negocios.HistoricoContabilFacade;
import br.com.webpublico.util.ConverterAutoComplete;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by mga on 26/07/2017.
 */
@ManagedBean
@ViewScoped
public class ComponenteHistoricoContabilControlador implements Serializable {

    @EJB
    private HistoricoContabilFacade facade;
    private HistoricoContabil historicoContabil;
    private ConverterAutoComplete converterHistoricoContabil;
    private String historico;

    public void novo(String historico) {
        this.historico = historico;
    }

    public List<HistoricoContabil> completarHistoricoContabil(String parte) {
        return facade.buscarFiltrandoHistoricoContabil(parte.trim());
    }

    public ConverterAutoComplete getConverterHistoricoContabil() {
        if (converterHistoricoContabil == null) {
            converterHistoricoContabil = new ConverterAutoComplete(HistoricoContabil.class, facade);
        }
        return converterHistoricoContabil;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public HistoricoContabil getHistoricoContabil() {
        return historicoContabil;
    }

    public void setHistoricoContabil(HistoricoContabil historicoContabil) {
        this.historicoContabil = historicoContabil;
    }
}

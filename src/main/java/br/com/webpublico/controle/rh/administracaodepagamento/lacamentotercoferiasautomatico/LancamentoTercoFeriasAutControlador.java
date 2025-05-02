package br.com.webpublico.controle.rh.administracaodepagamento.lacamentotercoferiasautomatico;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.PeriodoAquisitivoFL;
import br.com.webpublico.entidades.rh.administracaodepagamento.LancamentoTercoFeriasAut;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.rh.administracaodepagamento.LancamentoTercoFeriasAutFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "lancamentoTercoFeriasAutControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "verLancamentoTercoFeriasAut", pattern = "/lancamento-terco-ferias-automatico/ver/#{lancamentoTercoFeriasAutControlador.id}/", viewId = "/faces/rh/administracaodepagamento/lacamentotercoferiasautomatico/visualizar.xhtml"),
    @URLMapping(id = "listaLancamentoTercoFeriasAut", pattern = "/lancamento-terco-ferias-automatico/listar/", viewId = "/faces/rh/administracaodepagamento/lacamentotercoferiasautomatico/lista.xhtml")
})
public class LancamentoTercoFeriasAutControlador extends PrettyControlador<LancamentoTercoFeriasAut> implements Serializable, CRUD {

    @EJB
    private LancamentoTercoFeriasAutFacade facade;

    public LancamentoTercoFeriasAutControlador() {
        super(PeriodoAquisitivoFL.class);
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/lancamento-terco-ferias-automatico/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    @URLAction(mappingId = "verLancamentoTercoFeriasAut", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void ver() {
        super.ver();
    }
}

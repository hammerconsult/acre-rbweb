package br.com.webpublico.controle.contabil.emendagoverno;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.contabil.emendagoverno.Parlamentar;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.contabil.emendagoverno.ParlamentarFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-parlamentar", pattern = "/planejamento/parlamentar/novo/", viewId = "/faces/financeiro/emenda-governo/parlamentar/edita.xhtml"),
    @URLMapping(id = "editar-parlamentar", pattern = "/planejamento/parlamentar/editar/#{parlamentarControlador.id}/", viewId = "/faces/financeiro/emenda-governo/parlamentar/edita.xhtml"),
    @URLMapping(id = "ver-parlamentar", pattern = "/planejamento/parlamentar/ver/#{parlamentarControlador.id}/", viewId = "/faces/financeiro/emenda-governo/parlamentar/visualizar.xhtml"),
    @URLMapping(id = "listar-parlamentar", pattern = "/planejamento/parlamentar/listar/", viewId = "/faces/financeiro/emenda-governo/parlamentar/lista.xhtml")
})
public class ParlamentarControlador extends PrettyControlador<Parlamentar> implements Serializable, CRUD {
    @EJB
    private ParlamentarFacade facade;

    public ParlamentarControlador() {
        super(Parlamentar.class);
    }

    @URLAction(mappingId = "novo-parlamentar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-parlamentar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-parlamentar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/planejamento/parlamentar/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public List<Parlamentar> completarParlamentaresPorNome(String parte) {
        return facade.listaFiltrando(parte.trim(), "nome");
    }
}

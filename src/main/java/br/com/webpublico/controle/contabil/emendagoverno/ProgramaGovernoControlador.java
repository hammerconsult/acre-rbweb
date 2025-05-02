package br.com.webpublico.controle.contabil.emendagoverno;

import br.com.webpublico.controle.PrettyControlador;
import br.com.webpublico.entidades.contabil.emendagoverno.ProgramaGoverno;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.contabil.emendagoverno.ProgramaGovernoFacade;
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
    @URLMapping(id = "novo-programa-governo", pattern = "/planejamento/programa-governo/novo/", viewId = "/faces/financeiro/emenda-governo/programa/edita.xhtml"),
    @URLMapping(id = "editar-programa-governo", pattern = "/planejamento/programa-governo/editar/#{programaGovernoControlador.id}/", viewId = "/faces/financeiro/emenda-governo/programa/edita.xhtml"),
    @URLMapping(id = "ver-programa-governo", pattern = "/planejamento/programa-governo/ver/#{programaGovernoControlador.id}/", viewId = "/faces/financeiro/emenda-governo/programa/visualizar.xhtml"),
    @URLMapping(id = "listar-programa-governo", pattern = "/planejamento/programa-governo/listar/", viewId = "/faces/financeiro/emenda-governo/programa/lista.xhtml")
})
public class ProgramaGovernoControlador extends PrettyControlador<ProgramaGoverno> implements Serializable, CRUD {
    @EJB
    private ProgramaGovernoFacade facade;

    public ProgramaGovernoControlador() {
        super(ProgramaGoverno.class);
    }

    @URLAction(mappingId = "novo-programa-governo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "editar-programa-governo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @URLAction(mappingId = "ver-programa-governo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/planejamento/programa-governo/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    public List<ProgramaGoverno> completarProgramasPorNome(String parte) {
        return facade.listaFiltrando(parte.trim(), "nome");
    }
}

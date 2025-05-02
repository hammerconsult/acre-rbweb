/*
 * Codigo gerado automaticamente em Tue Aug 23 01:14:15 BRT 2011
 * Gerador de Controlador
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.Combustivel;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.CombustivelFacade;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.util.ConverterGenerico;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

@ManagedBean(name = "combustivelControlador")
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "combustivelNovo", pattern = "/frota/combustivel/novo/", viewId = "/faces/administrativo/frota/combustivel/edita.xhtml"),
        @URLMapping(id = "combustivelListar", pattern = "/frota/combustivel/listar/", viewId = "/faces/administrativo/frota/combustivel/lista.xhtml"),
        @URLMapping(id = "combustivelEditar", pattern = "/frota/combustivel/editar/#{combustivelControlador.id}/", viewId = "/faces/administrativo/frota/combustivel/edita.xhtml"),
        @URLMapping(id = "combustivelVer", pattern = "/frota/combustivel/ver/#{combustivelControlador.id}/", viewId = "/faces/administrativo/frota/combustivel/visualizar.xhtml"),
})
public class CombustivelControlador extends PrettyControlador<Combustivel> implements Serializable, CRUD {

    @EJB
    private CombustivelFacade combustivelFacade;
    private ConverterGenerico converterCombustivel;

    public CombustivelControlador() {
        super(Combustivel.class);
    }

    public CombustivelFacade getFacade() {
        return combustivelFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return combustivelFacade;
    }

    @Override
    public String getCaminhoPadrao() {
        return "/frota/combustivel/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @URLAction(mappingId = "combustivelNovo", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "combustivelVer", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "combustivelEditar", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    public ConverterGenerico getConverterCombustivel() {
        if (converterCombustivel == null) {
            converterCombustivel = new ConverterGenerico(Combustivel.class, combustivelFacade);
        }
        return converterCombustivel;
    }

    public List<Combustivel> combustiveis() {
        return combustivelFacade.listaDecrescente();
    }
}

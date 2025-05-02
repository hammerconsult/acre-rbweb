/*
 * Codigo gerado automaticamente em Tue Apr 17 14:27:35 BRT 2012
 * Gerador de Controlador
*/

package br.com.webpublico.controle;


import br.com.webpublico.entidades.UnidadeJudiciaria;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.UnidadeJudiciariaFacade;
import br.com.webpublico.interfaces.CRUD;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import java.io.Serializable;
import javax.faces.bean.ViewScoped;


@ManagedBean(name="unidadeJudiciariaControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-unidade-judiciaria", pattern = "/unidade-judiciaria/novo/", viewId = "/faces/financeiro/orcamentario/dividapublica/unidadejudiciaria/edita.xhtml"),
    @URLMapping(id = "editar-unidade-judiciaria", pattern = "/unidade-judiciaria/editar/#{unidadeJudiciariaControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/unidadejudiciaria/edita.xhtml"),
    @URLMapping(id = "ver-unidade-judiciaria", pattern = "/unidade-judiciaria/ver/#{unidadeJudiciariaControlador.id}/", viewId = "/faces/financeiro/orcamentario/dividapublica/unidadejudiciaria/visualizar.xhtml"),
    @URLMapping(id = "listar-unidade-judiciaria", pattern = "/unidade-judiciaria/listar/", viewId = "/faces/financeiro/orcamentario/dividapublica/unidadejudiciaria/lista.xhtml")
})
public class UnidadeJudiciariaControlador extends PrettyControlador<UnidadeJudiciaria> implements Serializable, CRUD {

    @EJB
    private UnidadeJudiciariaFacade unidadeJudiciariaFacade;

    public UnidadeJudiciariaControlador() {
        super(UnidadeJudiciaria.class);
    }

    public UnidadeJudiciariaFacade getFacade() {
        return unidadeJudiciariaFacade;
    }

    @Override
    public AbstractFacade getFacede() {
        return unidadeJudiciariaFacade;
    }

    @URLAction(mappingId = "novo-unidade-judiciaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void novo() {
        super.novo();
    }

    @URLAction(mappingId = "ver-unidade-judiciaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }

    @URLAction(mappingId = "editar-unidade-judiciaria", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void editar() {
        super.editar();
    }

    @Override
    public String getCaminhoPadrao() {
        return "/unidade-judiciaria/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }


}

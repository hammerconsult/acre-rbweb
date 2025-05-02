/*
 * Codigo gerado automaticamente em Mon Oct 31 21:28:23 BRST 2011
 * Gerador de Controlador
 *
 */
package br.com.webpublico.controle;

import br.com.webpublico.entidades.SolicitacaoEmpenho;
import br.com.webpublico.interfaces.CRUD;
import br.com.webpublico.negocios.AbstractFacade;
import br.com.webpublico.negocios.SolicitacaoEmpenhoFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "listaSolEmp", pattern = "/solicitacao-empenho/listar/", viewId = "/faces/administrativo/licitacao/solicitacaoempenho/lista.xhtml"),
    @URLMapping(id = "verSolEmp", pattern = "/solicitacao-empenho/ver/#{solicitacaoEmpenhoControlador.id}/", viewId = "/faces/administrativo/licitacao/solicitacaoempenho/visualizar.xhtml")
})
public class SolicitacaoEmpenhoControlador extends PrettyControlador<SolicitacaoEmpenho> implements Serializable, CRUD {

    @EJB
    private SolicitacaoEmpenhoFacade facade;

    public SolicitacaoEmpenhoControlador() {
        super(SolicitacaoEmpenho.class);
    }

    @Override
    public String getCaminhoPadrao() {
        return "/solicitacao-empenho/";
    }

    @Override
    public Object getUrlKeyValue() {
        return selecionado.getId();
    }

    @Override
    public AbstractFacade getFacede() {
        return facade;
    }

    @URLAction(mappingId = "verSolEmp", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void ver() {
        super.ver();
    }
}

package br.com.webpublico.controlerelatorio;

import br.com.webpublico.relatoriofacade.RelatorioAcompanhamentoLiberacaoFinanceiraFacade;
import br.com.webpublico.relatoriofacade.RelatorioAcompanhamentoLiberacaoFinanceiraSuperFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Mateus on 27/08/2014.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-acompanhamento-liberacao-financeira", pattern = "/relatorio/acompanhamento-liberacao-financeira/", viewId = "/faces/financeiro/relatorio/relatorioacompliberacaofinanceira.xhtml")
})
@ManagedBean
public class RelatorioAcompanhamentoLiberacaoFinanceira extends RelatorioAcompanhamentoLiberacaoFinanceiraSuperControlador {

    @EJB
    private RelatorioAcompanhamentoLiberacaoFinanceiraFacade facade;

    public RelatorioAcompanhamentoLiberacaoFinanceira() {
        super();
    }

    @URLAction(mappingId = "relatorio-acompanhamento-liberacao-financeira", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void limparCampos() {
        super.limparCampos();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioAcompanhamentoLiberacaoFinanceira.jasper";
    }

    @Override
    public RelatorioAcompanhamentoLiberacaoFinanceiraSuperFacade getFacade() {
        return facade;
    }

    @Override
    public String getNomeRelatorio() {
        return "ACOMPANHAMENTO-LIBERACAO-FINANCEIRA";
    }
}

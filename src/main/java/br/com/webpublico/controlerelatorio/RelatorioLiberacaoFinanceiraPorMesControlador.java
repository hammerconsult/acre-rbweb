package br.com.webpublico.controlerelatorio;

import br.com.webpublico.relatoriofacade.RelatorioAcompanhamentoLiberacaoFinanceiraSuperFacade;
import br.com.webpublico.relatoriofacade.RelatorioLiberacaoFinanceiraPorMesFacade;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by mateus on 11/07/17.
 */
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-liberacao-financeira-por-mes",
        pattern = "/relatorio/liberacao-financeira-por-mes/",
        viewId = "/faces/financeiro/relatorio/relatorioliberacaofinanceirapormes.xhtml")
})
@ManagedBean
public class RelatorioLiberacaoFinanceiraPorMesControlador extends RelatorioAcompanhamentoLiberacaoFinanceiraSuperControlador {

    @EJB
    protected RelatorioLiberacaoFinanceiraPorMesFacade facade;

    public RelatorioLiberacaoFinanceiraPorMesControlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-liberacao-financeira-por-mes", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    @Override
    public void limparCampos() {
        super.limparCampos();
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioLiberacaoFinanceiraPorMes.jasper";
    }

    @Override
    public RelatorioAcompanhamentoLiberacaoFinanceiraSuperFacade getFacade() {
        return facade;
    }

    @Override
    public String getNomeRelatorio() {
        return "LIBERACAO-FINANCEIRA-POR-MES";
    }
}

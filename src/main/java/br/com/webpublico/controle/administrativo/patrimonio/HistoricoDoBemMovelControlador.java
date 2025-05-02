package br.com.webpublico.controle.administrativo.patrimonio;

import br.com.webpublico.controle.RelatorioPatrimonioControlador;
import br.com.webpublico.enums.TipoBem;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

/**
 * Created by Desenvolvimento on 13/03/2017.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "novoHistoricoBemMovel", pattern = "/historico-bem-movel/novo/",
                viewId = "/faces/administrativo/patrimonio/relatorios/historico/historicodobemmovel.xhtml")})
public class HistoricoDoBemMovelControlador extends RelatorioPatrimonioControlador {

    @URLAction(mappingId = "novoHistoricoBemMovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoHistoricoBemMovel() {
        limparCampos();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        gerarRelatorioHistoricoBem(TipoBem.MOVEIS, tipoRelatorioExtensao);
    }
}

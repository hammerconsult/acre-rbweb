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
        @URLMapping(id = "novoHistoricoBemImovel", pattern = "/historico-bem-imovel/novo/",
                viewId = "/faces/administrativo/patrimonio/relatorios/historico/historicodobemimovel.xhtml")})
public class HistoricoDoBemImovelControlador extends RelatorioPatrimonioControlador {

    @URLAction(mappingId = "novoHistoricoBemImovel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoHistoricoBemImovel() {
        limparCampos();
    }

    public void gerarRelatorioHistoricoBemImovel(String tipoRelatorioExtensao) {
        gerarRelatorioHistoricoBem(TipoBem.IMOVEIS, tipoRelatorioExtensao);
    }
}

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
        @URLMapping(id = "novoHistoricoBemIntangivel",
                pattern = "/historico-bem-intangivel/novo/",
                viewId = "/faces/administrativo/patrimonio/relatorios/historico/historicodobemintangivel.xhtml")})
public class HistoricoDoBemIntangivelControlador extends RelatorioPatrimonioControlador {

    @URLAction(mappingId = "novoHistoricoBemIntangivel", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoHistoricoBemIntangivel() {
        limparCampos();
    }

    public void gerarRelatorioHistoricoBemIntangivel(String tipoRelatorioExtensao) {
        gerarRelatorioHistoricoBem(TipoBem.INTANGIVEIS, tipoRelatorioExtensao);
    }
}

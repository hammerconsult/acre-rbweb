/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controlerelatorio.contabil.AbstractRelatorioItemDemonstrativo;
import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.enums.PortalTipoAnexo;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

/**
 * @author Edi
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-receita-orcamento-fiscal", pattern = "/relatorio/receita-orcamento-fiscal-categoria-economica/", viewId = "/faces/financeiro/relatorio/receita-orcamento-fiscal-por-categoria-economica.xhtml")
})
public class RelatorioReceitaOrcamentoFiscalCategoriaEconomicaControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioReceitaOrcamentoFiscalCategoriaEconomicaControlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-receita-orcamento-fiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.RECEITA_ORCAMENTO_FISCAL;
        super.limparCampos();
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        RelatoriosItemDemonst relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Receita dos Orçamentos Fiscal e da Seguridade Social", exercicio, TipoRelatorioItemDemonstrativo.OUTROS);
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("idExCorrente", exercicio.getId());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("relatoriosItemDemonstDTO", relatoriosItemDemonst.toDto());
        dto.setNomeRelatorio("Receita dos Orçamentos Fiscal e da Seguridade Social por Categoria Econômica e Fonte de Recurso");
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioReceitaOrcamentoFiscal";
    }

    @Override
    public String getApi() {
        return "contabil/receita-orcamento-fiscal-categoria-economica/";
    }
}

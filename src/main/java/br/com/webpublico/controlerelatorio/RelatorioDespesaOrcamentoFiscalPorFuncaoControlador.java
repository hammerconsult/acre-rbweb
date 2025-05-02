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
 * @author juggernaut
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-despesa-orcamento-fiscal-funcao", pattern = "/relatorio/despesa-orcamento-fiscal/funcao/", viewId = "/faces/financeiro/relatorio/despesa-orcamento-fiscal-por-funcao.xhtml")
})
public class RelatorioDespesaOrcamentoFiscalPorFuncaoControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioDespesaOrcamentoFiscalPorFuncaoControlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-despesa-orcamento-fiscal-funcao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.DESPESA_ORCAMENTO_FISCAL_POR_FUNCAO;
        super.limparCampos();
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        RelatoriosItemDemonst relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Despesa dos Orçamentos Fiscal e da Seguridade Social por Função", exercicio, TipoRelatorioItemDemonstrativo.OUTROS);
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno().toString());
        dto.adicionarParametro("idExCorrente", exercicio.getId());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("relatoriosItemDemonstDTO", relatoriosItemDemonst.toDto());
        dto.setNomeRelatorio("Despesa dos Orçamentos Fiscal e da Seguridade Social por Função de Governo e Fonte de Recurso");
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioDespesaOrcamentoFiscalPorFuncao";
    }

    @Override
    public String getApi() {
        return "contabil/despesa-orcamento-fiscal-por-funcao/";
    }
}

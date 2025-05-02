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
    @URLMapping(id = "relatorio-despesa-orcamento-fiscal", pattern = "/relatorio/despesa-orcamento-fiscal/orgao/", viewId = "/faces/financeiro/relatorio/despesa-orcamento-fiscal-por-orgao.xhtml")
})
public class RelatorioDespesaOrcamentoFiscalPorOrgaoControlador extends AbstractRelatorioItemDemonstrativo implements Serializable {

    public RelatorioDespesaOrcamentoFiscalPorOrgaoControlador() {
        super();
    }

    @URLAction(mappingId = "relatorio-despesa-orcamento-fiscal", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        portalTipoAnexo = PortalTipoAnexo.DESPESA_ORCAMENTO_FISCAL_POR_ORGAO;
        super.limparCampos();
    }

    @Override
    public void montarDtoSemApi(RelatorioDTO dto) {
        RelatoriosItemDemonst relatoriosItemDemonst = getRelatoriosItemDemonstFacade().recuperaRelatorioPorTipoEDescricao("Despesa dos Orçamentos Fiscal e da Seguridade Social por Órgão", exercicio, TipoRelatorioItemDemonstrativo.OUTROS);
        dto.setNomeParametroBrasao("IMAGEM");
        dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
        dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno());
        dto.adicionarParametro("ID_EXERCICIO", exercicio.getId());
        dto.adicionarParametro("relatoriosItemDemonstDTO", relatoriosItemDemonst.toDto());
        dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
        dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeRelatorio("DESPESA-DOS-ORÇAMENTOS-FISCAL-E-DA-SEGURIDADE-SOCIAL-POR-ÓRGÃO-ORÇAMENTÁRIO-E-FONTE-DE-RECURSO");
    }

    @Override
    public String getNomeArquivo() {
        return "RelatorioDespesaOrcamentoFiscalPorOrgao";
    }

    @Override
    public String getApi() {
        return "contabil/despesa-orcamento-fiscal-por-orgao/";
    }
}

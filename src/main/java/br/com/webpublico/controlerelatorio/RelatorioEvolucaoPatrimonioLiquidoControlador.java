package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.RelatoriosItemDemonst;
import br.com.webpublico.entidadesauxiliares.ItemDemonstrativoComponente;
import br.com.webpublico.enums.TipoRelatorioItemDemonstrativo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.RelatoriosItemDemonstFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.List;

/**
 * Created by Fabio on 10/05/2016.
 */

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-evolucao_patrimonio_liquido", pattern = "/relatorio/evolucao-patrimonio-liquido/", viewId = "/faces/financeiro/relatorio/relatorioevolucaopatrimonioliquido.xhtml")
})
public class RelatorioEvolucaoPatrimonioLiquidoControlador extends AbstractReport implements Serializable {

    @EJB
    private RelatoriosItemDemonstFacade relatoriosItemDemonstFacade;
    private RelatoriosItemDemonst relatoriosItemDemonst;
    private List<ItemDemonstrativoComponente> itens;

    public RelatorioEvolucaoPatrimonioLiquidoControlador() {
        itens = Lists.newArrayList();
    }

    @URLAction(mappingId = "relatorio-evolucao_patrimonio_liquido", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
    }

    public List<ItemDemonstrativoComponente> getItens() {
        return itens;
    }

    public void setItens(List<ItemDemonstrativoComponente> itens) {
        this.itens = itens;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }

    public void gerarRelatorio() {
        try {
            relatoriosItemDemonst = relatoriosItemDemonstFacade.recuperaRelatorioPorTipoEDescricao("Evolução do Patrimônio Líquido", getSistemaFacade().getExercicioCorrente(), TipoRelatorioItemDemonstrativo.LDO);
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("ANOEXERCICIO", getSistemaFacade().getExercicioCorrente().getAno() - 2);
            dto.adicionarParametro("ANOANTERIOR1", getSistemaFacade().getExercicioCorrente().getAno() - 3);
            dto.adicionarParametro("ANOANTERIOR2", getSistemaFacade().getExercicioCorrente().getAno() - 4);
            dto.adicionarParametro("EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno());
            dto.adicionarParametro("itens", ItemDemonstrativoComponente.itensDemonstrativosComponentesToDto(itens));
            dto.adicionarParametro("relatoriosItemDemonst", relatoriosItemDemonst.toDto());
            dto.setNomeRelatorio("Evolução do Patrimônio Líquido");
            dto.setApi("contabil/evolucao-patrimonio-liquido/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            logger.error("Erro ao gerar relatório: ", ex);
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
}

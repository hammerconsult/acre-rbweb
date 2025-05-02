package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.negocios.ExcecaoNegocioGenerica;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.dtos.FiltroRelatorioApuracaoISSQN;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Date;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-apuracao-issqn", pattern = "/nfse/relatorio/apuracao-issqn/",
        viewId = "/faces/tributario/nfse/relatorio/apuracao-issqn.xhtml")
})
public class RelatorioApuracaoISSQNControlador extends AbstractReport {
    private FiltroRelatorioApuracaoISSQN filtro;

    @URLAction(mappingId = "novo-relatorio-apuracao-issqn", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroRelatorioApuracaoISSQN();
        filtro.setExercicioInicial(getSistemaFacade().getExercicioCorrente());
        filtro.setMesInicial(Mes.getMesToInt(DataUtil.getMes(new Date())));
        filtro.setExercicioFinal(filtro.getExercicioInicial());
        filtro.setMesFinal(filtro.getMesInicial());
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "Municipio de Rio Branco");
            dto.adicionarParametro("TITULO", "Secretaria Municipal de Finanças");
            dto.adicionarParametro("USUARIO", SistemaFacade.obtemLogin());
            dto.adicionarParametro("FILTROS", filtro.montarDescricaoFiltros());
            dto.adicionarParametro("WHERE", filtro.montarClausulaWhere());
            dto.setNomeRelatorio("Relatório de Apuração de ISSQN");
            dto.setApi("tributario/nfse/apuracao-issqn/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (ExcecaoNegocioGenerica ex) {
            FacesUtil.addOperacaoNaoRealizada(ex.getMessage());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }
    public FiltroRelatorioApuracaoISSQN getFiltro() {
        return filtro;
    }
    public void setFiltro(FiltroRelatorioApuracaoISSQN filtro) {
        this.filtro = filtro;
    }
}

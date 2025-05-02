package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.nfse.domain.dtos.FiltroRelatorioNotasDivergentesPgdas;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.Calendar;
import java.util.Date;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "novo-relatorio-nota-divergente-pgdas",
        pattern = "/nfse/relatorio/notas-fiscais-com-aliquota-divergente-do-pgdas/",
        viewId = "/faces/tributario/nfse/relatorio/notas-divergentes-pgdas.xhtml")
})
public class RelatorioNotasAliquotaDivergentePgdasControlador {

    @EJB
    private SistemaFacade sistemaFacade;
    private FiltroRelatorioNotasDivergentesPgdas filtro;

    @URLAction(mappingId = "novo-relatorio-nota-divergente-pgdas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        filtro = new FiltroRelatorioNotasDivergentesPgdas();
        filtro.setInscricaoInicial("1");
        filtro.setInscricaoFinal("999999999");
        filtro.setDataInicial(DataUtil.getPrimeiroDiaAno(new Date()));
        filtro.setDataFinal(DataUtil.getUltimoDiaAno(Calendar.getInstance().get(Calendar.YEAR)));
    }

    public void gerarRelatorio() {
        try {
            filtro.validar();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setApi("tributario/nfse/comparativo-pgdas/");
            dto.setNomeParametroBrasao("BRASAO");
            dto.setNomeRelatorio("comparativo-nfse-pgdas");
            filtro.montarParametros(dto);
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    public FiltroRelatorioNotasDivergentesPgdas getFiltro() {
        return filtro;
    }

    public void setFiltro(FiltroRelatorioNotasDivergentesPgdas filtro) {
        this.filtro = filtro;
    }
}

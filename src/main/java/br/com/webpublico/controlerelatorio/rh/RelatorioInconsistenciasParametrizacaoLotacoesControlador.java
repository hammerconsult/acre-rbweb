package br.com.webpublico.controlerelatorio.rh;

import br.com.webpublico.controlerelatorio.AbstractReport;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ManagedBean(name = "relatorioInconsistenciasParametrizacaoLotacoesControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorioInconsistenciasParametrizacaoLotacoes", pattern = "/relatorio-inconsistencias-parametrizacao-lotacoes/", viewId = "/faces/rh/relatorios/inconsistenciasParametrizacaoLotacoes.xhtml"),
})
public class RelatorioInconsistenciasParametrizacaoLotacoesControlador extends AbstractReport implements Serializable {
    protected static final Logger logger = LoggerFactory.getLogger(RelatorioInconsistenciasParametrizacaoLotacoesControlador.class);
    @EJB
    private SistemaFacade sistemaFacade;

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.setApi("rh/inconsistencias-parametrizacao-lotacoes/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("Erro ao gerar o relatório Inconsistências na Parametrização de Lotações {}", e);
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
        dto.setNomeParametroBrasao("BRASAO");
        dto.adicionarParametro("MODULO", "Recursos Humanos");
        dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("NOMERELATORIO", "Relatório Inconsistências na Parametrização de Lotações");
        dto.adicionarParametro("DATAOPERACAO", sistemaFacade.getDataOperacao());
        dto.setNomeRelatorio("Relatório Inconsistências na Parametrização de Lotações");
        return dto;
    }
}

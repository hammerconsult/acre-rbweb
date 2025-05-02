package br.com.webpublico.dashboard.controlador;

import br.com.webpublico.controlerelatorio.AbstractRelatorioAssincronoControlador;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.relatoriofacade.AbstractRelatorioAssincronoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.util.HashMap;

@ManagedBean
@ViewScoped
public class DashboardContratoControlador extends AbstractRelatorioAssincronoControlador {

    private static final Logger logger = LoggerFactory.getLogger(DashboardContratoControlador.class);

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setNomeRelatorio("RELATÓRIO DASHBOARD LICITAÇÕES E CONTRATOS");
            dto.adicionarParametro("MODULO", "Compra e Licitações");
            dto.adicionarParametro("MUNICIPIO", montarNomeDoMunicipio());
            dto.adicionarParametro("NOMERELATORIO", "RELATÓRIO DASHBOARD CONTRATO");
            dto.adicionarParametro("ANO", getSistemaFacade().getExercicioCorrente().getAno(), false);
            dto.adicionarParametro("USUARIO", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setApi("administrativo/contrato-dashboard/");
            ReportService.getInstance().gerarRelatorio(getSistemaFacade().getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            logger.error("DashboardContratoControlador {}", e);
            FacesUtil.addOperacaoNaoRealizada(e.getMessage());
        }
    }

    @Override
    protected AbstractRelatorioAssincronoFacade getFacade() {
        return null;
    }

    @Override
    protected Object clausulasConsulta() {
        return null;
    }

    @Override
    protected String arquivoJasper() {
        return null;
    }

    @Override
    protected void validarCampos() {

    }

    @Override
    protected HashMap getParameters() {
        return null;
    }
}

package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.ReservaObjetoFrota;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import net.sf.jasperreports.engine.JRException;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.IOException;

/**
 * Created by renato on 03/05/17.
 */
@ManagedBean(name = "relatorioReservaObjetoFrotaControlador")
@ViewScoped
public class RelatorioReservaObjetoFrotaControlador extends AbstractReport {

    @EJB
    private SistemaFacade sistemaFacade;

    public void gerarRelatorio(ReservaObjetoFrota reservaObjetoFrota) throws JRException, IOException {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            String nomeRelatorio = "RELATÓRIO DE RESERVA DE FROTAS";
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SECRETARIA", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("condicao", " WHERE reser.ID = " + reservaObjetoFrota.getId());
            dto.adicionarParametro("MODULO", "FROTAS");
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/reserva-objeto-frota/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
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

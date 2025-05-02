    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.controlerelatorio;

    import br.com.webpublico.exception.ValidacaoException;
    import br.com.webpublico.exception.WebReportRelatorioExistenteException;
    import br.com.webpublico.report.ReportService;
    import br.com.webpublico.util.FacesUtil;
    import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
    import com.ocpsoft.pretty.faces.annotation.URLAction;
    import com.ocpsoft.pretty.faces.annotation.URLMapping;
    import com.ocpsoft.pretty.faces.annotation.URLMappings;

    import javax.faces.bean.ManagedBean;
    import javax.faces.bean.ViewScoped;
    import java.io.Serializable;

/**
 * @author wiplash
 */
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorio-medocc", pattern = "/relatorio/medocc/", viewId = "/faces/financeiro/relatorio/relatoriomargemdespesasobrigatorias.xhtml")
})
@ManagedBean
public class RelatorioMargemDespesasObrigatoriasControlador extends AbstractReport implements Serializable {

    @URLAction(mappingId = "relatorio-medocc", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limpaCampos() {
    }

    public void gerarRelatorio() {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO");
            dto.adicionarParametro("ANO_EXERCICIO", getSistemaFacade().getExercicioCorrente().getAno());
            dto.adicionarParametro("idExercicio", getSistemaFacade().getExercicioCorrente().getId());
            dto.setNomeRelatorio("MARGEM DE EXPANSÃO DAS DESPESAS OBRIGATÓRIAS DE CARÁTER CONTINUADO");
            dto.setApi("contabil/margem-despesas-obrigatorias/");
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

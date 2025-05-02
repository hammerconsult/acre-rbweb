package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;

@ViewScoped
@URLMapping(id = "relatorio-demonstrativo-dividas", pattern = "/relatorio/demonstrativo-dividas/", viewId = "/faces/financeiro/relatorio/relatoriodemonstrativodividas.xhtml")
@ManagedBean
public class RelatorioDemonstrativoDividasControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    private Exercicio exercicio;

    @URLAction(mappingId = "relatorio-demonstrativo-dividas", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        exercicio = sistemaFacade.getExercicioCorrente();
    }

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("MUNICIPIO", "Município de Rio Branco - AC");
            dto.adicionarParametro("dataInicial", DataUtil.primeiroDiaMes(1, exercicio.getAno()).getTime());
            dto.adicionarParametro("dataFinal", DataUtil.criarDataUltimoDiaMes(12, exercicio.getAno()).toDate().getTime());
            dto.adicionarParametro("exercicio", exercicio.getId());
            dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno());
            dto.setNomeRelatorio("DEMONSTRATIVO DE DÍVIDAS");
            dto.setApi("contabil/demonstrativo-dividas/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        ve.lancarException();
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }
}

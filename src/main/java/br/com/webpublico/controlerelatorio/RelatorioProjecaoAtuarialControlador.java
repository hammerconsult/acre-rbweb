package br.com.webpublico.controlerelatorio;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 03/10/13
 * Time: 20:04
 * To change this template use File | Settings | File Templates.
 */

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-projecao-atuarial", pattern = "/relatorio-projecao-atuarial/", viewId = "/faces/financeiro/relatorio/relatorioprojecaoatuarial.xhtml"),
})

@ManagedBean(name = "relatorioProjecaoAtuarialControlador")
public class RelatorioProjecaoAtuarialControlador extends AbstractReport implements Serializable {

    @EJB
    private ExercicioFacade exercicioFacade;
    private Exercicio exercicio;

    public RelatorioProjecaoAtuarialControlador() {
    }

    public String getCaminhoPadrao() {
        return "/relatorio-projecao-atuarial/";
    }

    @URLAction(mappingId = "relatorio-projecao-atuarial", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        limparCampos();
    }

    public void limparCampos() {
        exercicio = null;
    }

    public List<SelectItem> getExercicios() {
        List<SelectItem> retorno = new ArrayList<>();
        retorno.add(new SelectItem(null, ""));
        for (Exercicio ex : exercicioFacade.lista()) {
            if (ex.getAno() != null) {
                retorno.add(new SelectItem(ex, ex.getAno().toString()));
            }
        }
        return retorno;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public void geraRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.adicionarParametro("USER", getSistemaFacade().getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("IMAGEM");
            dto.adicionarParametro("MUNICIPIO", "MUNICÍPIO DE RIO BRANCO - AC");
            dto.adicionarParametro("EXERCICIO", exercicio.getId());
            dto.adicionarParametro("ANO_EXERCICIO", exercicio.getAno());
            dto.setNomeRelatorio("Relatório de Projeção Atuarial");
            dto.setApi("contabil/projecao-atuarial/");
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

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Exercício deve ser informado.");
        }
        ve.lancarException();
    }
}

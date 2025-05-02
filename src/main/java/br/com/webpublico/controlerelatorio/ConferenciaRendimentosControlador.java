package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.*;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.DateTime;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by Buzatto on 26/11/2015.
 */
@ManagedBean(name = "conferenciaRendimentosControlador")
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "conferenciaRendimentos", pattern = "/conferencia-rendimentos/", viewId = "/faces/rh/relatorios/conferenciaRendimentos.xhtml")
})
public class ConferenciaRendimentosControlador extends AbstractReport implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private VinculoFPFacade vinculoFPFacade;

    private VinculoFP vinculoFP;
    private Exercicio exercicio;

    public void gerarRelatorio() {
        try {
            validarCampos();
            RelatorioDTO dto = montarRelatorioDTO();
            dto.setApi("rh/relatorio-conferencia-de-rendimentos/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
        }
    }

    private RelatorioDTO montarRelatorioDTO() {
        RelatorioDTO dto = new RelatorioDTO();
        dto.setNomeParametroBrasao("IMAGEM");
        dto.setNomeRelatorio("Conferência de Rendimentos Pagos");
        dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getLogin());
        dto.adicionarParametro("MODULO", "RH");
        dto.adicionarParametro("SECRETARIA", "DEPARTAMENTO DE RECURSOS HUMANOS");
        dto.adicionarParametro("NOMERELATORIO", "Conferência de Rendimentos Pagos");
        dto.adicionarParametro("ANO", exercicio.getAno());
        dto.adicionarParametro("MATRICULA", vinculoFP.getMatriculaFP().getMatricula());
        dto.adicionarParametro("CONTRATO", Integer.valueOf(vinculoFP.getNumero()));
        dto.adicionarParametro("CPF", vinculoFP.getMatriculaFP().getPessoa().getCpf());
        dto.adicionarParametro("NOME", vinculoFP.getMatriculaFP().getPessoa().getNome());
        dto.adicionarParametro("MODALIDADE", vinculoFP.getContratoFP().getModalidadeContratoFP().getDescricao());
        dto.adicionarParametro("VINCULO", vinculoFP.getId());
        dto.adicionarParametro("CONTRATO_ID", vinculoFP.getContratoFP().getId());
        dto.adicionarParametro("DATA", vinculoFP.getFinalVigencia() == null ? new Date(exercicio.getAno(), 12, 31, 0, 0).getTime() : vinculoFP.getFinalVigencia().getTime());
        dto.adicionarParametro("EXERCICIO", exercicio.getAno());

        return dto;
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (vinculoFP == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Servidor deve ser informado!");
        }
        if (exercicio == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Ano Calendário deve ser informado!");
        }
        ve.lancarException();
    }

    public List<VinculoFP> completarVinculo(String s) {
        return vinculoFPFacade.listaTodosVinculosVigentes(s.trim());
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }
}

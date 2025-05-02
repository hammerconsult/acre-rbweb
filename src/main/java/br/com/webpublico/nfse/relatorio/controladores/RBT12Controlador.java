package br.com.webpublico.nfse.relatorio.controladores;

import br.com.webpublico.entidades.CadastroEconomico;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.nfse.domain.dtos.RBT12NfseDTO;
import br.com.webpublico.nfse.facades.DeclaracaoMensalServicoFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
        @URLMapping(id = "relatorioRBT12", pattern = "/nfse/relatorio-rbt12/",
                viewId = "/faces/tributario/nfse/relatorio/rbt12.xhtml")
})
public class RBT12Controlador implements Serializable {

    private static final Logger logger = LoggerFactory.getLogger(RBT12Controlador.class);

    @EJB
    private DeclaracaoMensalServicoFacade declaracaoMensalServicoFacade;

    private Integer exercicioInicial, exercicioFinal;
    private Mes mesInicial, mesFinal;
    private CadastroEconomico cadastroEconomico;
    private Future<List<RBT12NfseDTO>> futureRegistros;
    private List<RBT12NfseDTO> registros;
    private Boolean consultaRealizada = Boolean.FALSE;

    public Boolean getConsultaRealizada() {
        return consultaRealizada;
    }

    @URLAction(mappingId = "relatorioRBT12", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novo() {
        LocalDate agora = LocalDate.now();
        exercicioInicial = agora.getYear();
        exercicioFinal = agora.getYear();
        mesInicial = Mes.getMesToInt(agora.getMonthOfYear() - 1);
        mesFinal = Mes.getMesToInt(agora.getMonthOfYear());
        cadastroEconomico = null;
    }

    public List<SelectItem> getMeses() {
        return Util.getListSelectItem(Mes.values(), false);
    }

    public Integer getExercicioInicial() {
        return exercicioInicial;
    }

    public void setExercicioInicial(Integer exercicioInicial) {
        this.exercicioInicial = exercicioInicial;
    }

    public Integer getExercicioFinal() {
        return exercicioFinal;
    }

    public void setExercicioFinal(Integer exercicioFinal) {
        this.exercicioFinal = exercicioFinal;
    }

    public Mes getMesInicial() {
        return mesInicial;
    }

    public void setMesInicial(Mes mesInicial) {
        this.mesInicial = mesInicial;
    }

    public Mes getMesFinal() {
        return mesFinal;
    }

    public void setMesFinal(Mes mesFinal) {
        this.mesFinal = mesFinal;
    }

    public CadastroEconomico getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(CadastroEconomico cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }

    public List<RBT12NfseDTO> getRegistros() {
        return registros;
    }

    public void validar() {
        ValidacaoException ve = new ValidacaoException();
        if (mesInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a competência inicial");
        }
        if (mesFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe a competência final");
        }
        if (exercicioInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício inicial");
        }
        if (exercicioFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o exercício final");
        }
        if (cadastroEconomico == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Informe o prestador de serviços");
        }

        ve.lancarException();
    }

    public void iniciarConsulta() {
        limparDados();
    }

    private void limparDados() {
        consultaRealizada = Boolean.FALSE;
        registros = null;
    }

    public void consultar() {
        try {
            validar();
            futureRegistros = declaracaoMensalServicoFacade.buscarRegistrosRBT12(cadastroEconomico.getId(),
                    getCompetenciaInicial(), getCompetenciaFinal());
            FacesUtil.executaJavaScript("acompanharConsulta()");
        } catch (ValidacaoException e) {
            FacesUtil.printAllFacesMessages(e);
            FacesUtil.executaJavaScript("aguarde.hide()");
        } catch (Exception e) {
            FacesUtil.addErroAoGerarRelatorio(e.getMessage());
            FacesUtil.executaJavaScript("aguarde.hide()");
        }
    }

    public Date getCompetenciaInicial() {
        return DataUtil.dataSemHorario(DataUtil.getDia(1, mesInicial.getNumeroMes(), exercicioInicial));
    }

    public Date getCompetenciaFinal() {
        return DataUtil.dataSemHorario(DataUtil.ultimoDiaMes(DataUtil
                .getDia(1, mesFinal.getNumeroMes(), exercicioFinal)).getTime());
    }

    public void acompanharConsulta() {
        if (futureRegistros.isDone()) {
            FacesUtil.executaJavaScript("finalizarConsulta()");
        }
    }

    public void finalizarConsulta() {
        try {
            registros = futureRegistros.get();
            consultaRealizada = Boolean.TRUE;
        } catch (InterruptedException | ExecutionException e) {
            logger.error("Erro ao finalizar consulta do livro fiscal", e);
        }
    }

    public void gerarRelatorio() {
        gerarRelatorio("PDF");
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validar();
            UsuarioSistema usuarioCorrente = declaracaoMensalServicoFacade.getSistemaFacade().getUsuarioCorrente();
            RelatorioDTO relatorioDTO = criarRelatorioDTO(usuarioCorrente);
            relatorioDTO.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            ReportService.getInstance().gerarRelatorio(usuarioCorrente, relatorioDTO);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException op) {
            FacesUtil.printAllFacesMessages(op);
        } catch (Exception ex) {
            logger.debug("Erro ao gerar o relatorio de rbt12.", ex);
            logger.error("Erro ao gerar o relatorio de rbt12.");
            FacesUtil.addErrorPadrao(ex);
        }
    }

    private String getFiltrosUtilizados() {
        return "Competência: " + mesInicial.getNumeroMes() +
                "/" + exercicioInicial + " à " + mesFinal.getNumeroMes() + "/" +
                exercicioFinal + "; Prestador: " + cadastroEconomico.toString();
    }

    private RelatorioDTO criarRelatorioDTO(UsuarioSistema usuarioSistema) {
        RelatorioDTO relatorioDTO = new RelatorioDTO();
        relatorioDTO.adicionarParametro("MUNICIPIO", "PREFEITURA DE RIO BRANCO");
        relatorioDTO.adicionarParametro("SECRETARIA", "SECRETARIA MUNICIPAL DE FINANÇAS");
        relatorioDTO.adicionarParametro("FILTROS_UTILIZADOS", getFiltrosUtilizados());
        relatorioDTO.adicionarParametro("PRESTADOR_ID", cadastroEconomico.getId());
        relatorioDTO.adicionarParametro("COMPETENCIA_INICIAL", getCompetenciaInicial());
        relatorioDTO.adicionarParametro("COMPETENCIA_FINAL", getCompetenciaFinal());
        relatorioDTO.adicionarParametro("USUARIO", usuarioSistema.getLogin());
        relatorioDTO.setNomeRelatorio("relatorio-rbt12.pdf");
        relatorioDTO.setApi("tributario/nfse/rbt12/");
        return relatorioDTO;
    }

    public void handleCadastroEconomico() {
        limparDados();
    }
}

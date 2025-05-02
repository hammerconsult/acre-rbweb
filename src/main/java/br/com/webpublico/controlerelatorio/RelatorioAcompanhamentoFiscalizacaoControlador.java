package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.SituacaoAcaoFiscal;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.FiscalDesignadoFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
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
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by HardRock on 07/04/2017.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "acompanhamento-fiscalizacao", pattern = "/acompanhamento-fiscalizacao/",
        viewId = "/faces/tributario/fiscalizacao/acompanhamentofiscalizacao/edita.xhtml")
})
public class RelatorioAcompanhamentoFiscalizacaoControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private FiscalDesignadoFacade fiscalDesignadoFacade;
    private Date dataInicial;
    private Date dataFinal;
    private String inscricaoInicial;
    private String inscricaoFinal;
    private String ordemServicoInicial;
    private String ordemServicoFinal;
    private String programacaoInicial;
    private String programacaoFinal;
    private String autoInfracaoInicial;
    private String autoInfracaoFinal;
    private Integer autoInfracaoAno;
    private UsuarioSistema fiscalDesignado;
    private SituacaoAcaoFiscal situacaoAcaoFiscal;
    private TipoRelatorio tipoRelatorio;

    @URLAction(mappingId = "acompanhamento-fiscalizacao", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorio() {
        inicializarFiltros();
    }

    private void inicializarFiltros() {
        tipoRelatorio = TipoRelatorio.SINTETICO;
        dataInicial = DataUtil.getPrimeiroDiaAno(sistemaFacade.getExercicioCorrente().getAno());
        dataFinal = null;
        inscricaoInicial = "";
        inscricaoFinal = "";
        ordemServicoInicial = "";
        ordemServicoFinal = "";
        programacaoInicial = "";
        programacaoFinal = "";
        autoInfracaoInicial = "";
        autoInfracaoFinal = "";
        autoInfracaoAno = null;
        situacaoAcaoFiscal = null;
        fiscalDesignado = null;
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            String nomeRelatorio = "ACOMPANHAMENTO DAS FISCALIZAÇÕES";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("NOME_PREFEITURA", "Município de Rio Branco");
            dto.adicionarParametro("NOME_RELATORIO", nomeRelatorio);
            dto.adicionarParametro("TIPO_RELATORIO", tipoRelatorio.name());
            dto.adicionarParametro("condicao", montarCondicao());
            dto.adicionarParametro("isAnalitico", TipoRelatorio.ANALITICO.equals(tipoRelatorio));
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("tributario/acompanhamento-fiscalizacoes/");
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

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        ve.lancarException();
        if (dataInicial != null && dataFinal != null && dataFinal.before(dataInicial)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O campo data final deve ser superior ou igual a data inicial.");
        }
        ve.lancarException();
    }

    public StringBuilder montarCondicao() {
        StringBuilder filtro = new StringBuilder();
        String juncao = " where ";
        if (dataInicial != null) {
            filtro.append(juncao).append("acao.dataInicial >= ").append(" to_date('" + DataUtil.getDataFormatada(dataInicial) + "', 'dd/MM/yyyy')");
            juncao = " and ";
        }
        if (dataFinal != null) {
            filtro.append(juncao).append("acao.dataFinal <= ").append(" to_date('" + DataUtil.getDataFormatada(dataFinal) + "', 'dd/MM/yyyy')");
            juncao = " and ";
        }
        if (inscricaoInicial != null && !inscricaoInicial.isEmpty()) {
            filtro.append(juncao).append("to_number(cmc.inscricaocadastral) >= ").append(inscricaoInicial);
            juncao = " and ";
        }
        if (inscricaoFinal != null && !inscricaoFinal.isEmpty()) {
            filtro.append(juncao).append("to_number(cmc.inscricaocadastral) <= ").append(inscricaoFinal);
            juncao = " and ";
        }
        if (ordemServicoInicial != null && !ordemServicoInicial.isEmpty()) {
            filtro.append(juncao).append("acao.ordemservico >= ").append(ordemServicoInicial);
            juncao = " and ";
        }
        if (ordemServicoFinal != null && !ordemServicoFinal.isEmpty()) {
            filtro.append(juncao).append("acao.ordemservico <= ").append(ordemServicoFinal);
            juncao = " and ";
        }
        if (programacaoInicial != null && !programacaoInicial.isEmpty()) {
            filtro.append(juncao).append("prog.numero >= ").append(programacaoInicial);
            juncao = " and ";
        }
        if (programacaoFinal != null && !programacaoFinal.isEmpty()) {
            filtro.append(juncao).append("prog.numero <= ").append(programacaoFinal);
            juncao = " and ";
        }
        if (autoInfracaoInicial != null && !autoInfracaoInicial.isEmpty()) {
            filtro.append(juncao).append("to_number(auto.numero) >= ").append(autoInfracaoInicial);
            juncao = " and ";
        }
        if (autoInfracaoFinal != null && !autoInfracaoFinal.isEmpty()) {
            filtro.append(juncao).append("to_number(auto.numero) <= ").append(autoInfracaoFinal);
            juncao = " and ";
        }
        if (autoInfracaoAno != null) {
            filtro.append(juncao).append("auto.ano = ").append(autoInfracaoAno);
            juncao = " and ";
        }
        if (situacaoAcaoFiscal != null) {
            filtro.append(juncao).append("acao.situacaoAcaoFiscal = ").append("'" + situacaoAcaoFiscal.name() + "'");
            juncao = " and ";
        }
        if (fiscalDesignado != null) {
            filtro.append(juncao).append(" usuario.id = ").append(fiscalDesignado.getId());
        }
        return filtro;
    }

    public List<UsuarioSistema> completarFiscal(String parte) {
        return fiscalDesignadoFacade.buscarFiltrandoFiscaisDesignadosPorNome(parte.trim());
    }

    public List<SelectItem> getSituacoesAcaoFiscal() {
        List<SelectItem> toReturn = new ArrayList<>();
        toReturn.add(new SelectItem(null, "Todas..."));
        for (SituacaoAcaoFiscal obj : SituacaoAcaoFiscal.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public List<SelectItem> getTiposRelatorio() {
        List<SelectItem> toReturn = new ArrayList<>();
        for (TipoRelatorio obj : TipoRelatorio.values()) {
            toReturn.add(new SelectItem(obj, obj.getDescricao()));
        }
        return toReturn;
    }

    public UsuarioSistema getFiscalDesignado() {
        return fiscalDesignado;
    }

    public void setFiscalDesignado(UsuarioSistema fiscalDesignado) {
        this.fiscalDesignado = fiscalDesignado;
    }

    public String getInscricaoFinal() {
        return inscricaoFinal;
    }

    public void setInscricaoFinal(String inscricaoFinal) {
        this.inscricaoFinal = inscricaoFinal;
    }

    public String getOrdemServicoInicial() {
        return ordemServicoInicial;
    }

    public void setOrdemServicoInicial(String ordemServicoInicial) {
        this.ordemServicoInicial = ordemServicoInicial;
    }

    public String getOrdemServicoFinal() {
        return ordemServicoFinal;
    }

    public void setOrdemServicoFinal(String ordemServicoFinal) {
        this.ordemServicoFinal = ordemServicoFinal;
    }

    public String getProgramacaoInicial() {
        return programacaoInicial;
    }

    public void setProgramacaoInicial(String programacaoInicial) {
        this.programacaoInicial = programacaoInicial;
    }

    public String getProgramacaoFinal() {
        return programacaoFinal;
    }

    public void setProgramacaoFinal(String programacaoFinal) {
        this.programacaoFinal = programacaoFinal;
    }

    public String getAutoInfracaoInicial() {
        return autoInfracaoInicial;
    }

    public void setAutoInfracaoInicial(String autoInfracaoInicial) {
        this.autoInfracaoInicial = autoInfracaoInicial;
    }

    public String getAutoInfracaoFinal() {
        return autoInfracaoFinal;
    }

    public void setAutoInfracaoFinal(String autoInfracaoFinal) {
        this.autoInfracaoFinal = autoInfracaoFinal;
    }

    public String getInscricaoInicial() {
        return inscricaoInicial;
    }

    public void setInscricaoInicial(String inscricaoInicial) {
        this.inscricaoInicial = inscricaoInicial;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public SituacaoAcaoFiscal getSituacaoAcaoFiscal() {
        return situacaoAcaoFiscal;
    }

    public void setSituacaoAcaoFiscal(SituacaoAcaoFiscal situacaoAcaoFiscal) {
        this.situacaoAcaoFiscal = situacaoAcaoFiscal;
    }

    public enum TipoRelatorio {
        SINTETICO("Sintético"),
        ANALITICO("Analítico");
        private String descricao;

        TipoRelatorio(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }

        public void setDescricao(String descricao) {
            this.descricao = descricao;
        }
    }

    public TipoRelatorio getTipoRelatorio() {
        return tipoRelatorio;
    }

    public void setTipoRelatorio(TipoRelatorio tipoRelatorio) {
        this.tipoRelatorio = tipoRelatorio;
    }

    public Integer getAutoInfracaoAno() {
        return autoInfracaoAno;
    }

    public void setAutoInfracaoAno(Integer autoInfracaoAno) {
        this.autoInfracaoAno = autoInfracaoAno;
    }
}

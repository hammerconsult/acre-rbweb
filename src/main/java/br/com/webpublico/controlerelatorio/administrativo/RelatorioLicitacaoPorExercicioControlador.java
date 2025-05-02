package br.com.webpublico.controlerelatorio.administrativo;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.enums.TipoAvaliacaoLicitacao;
import br.com.webpublico.enums.TipoSituacaoLicitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-licitacao-por-exercicio", pattern = "/relatorios/licitacao-por-exercicio/", viewId = "/faces/administrativo/relatorios/relatorio-licitacao-por-exercicio.xhtml")
})
public class RelatorioLicitacaoPorExercicioControlador implements Serializable {
    private static final Logger logger = LoggerFactory.getLogger(RelatorioLicitacaoPorExercicioControlador.class);

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ExercicioFacade exercicioFacade;
    private Long numeroProcesso;
    private Long numeroLicitacao;
    private Exercicio exercicio;
    private Date dataInicial;
    private Date dataFinal;
    private ModalidadeLicitacao modalidadeLicitacao;
    private TipoAvaliacaoLicitacao tipoAvaliacaoLicitacao;
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    private String resumoDoObjeto;
    private TipoSituacaoLicitacao statusLicitacao;
    private String filtros;

    @URLAction(mappingId = "relatorio-licitacao-por-exercicio", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        numeroProcesso = null;
        numeroLicitacao = null;
        exercicio = sistemaFacade.getExercicioCorrente();
        dataInicial = null;
        dataFinal = null;
        modalidadeLicitacao = null;
        tipoAvaliacaoLicitacao = null;
        resumoDoObjeto = null;
        hierarquiaOrganizacional = null;
        statusLicitacao = null;
    }

    public void gerarRelatorio(String tipoRelatorio) {
        try {
            validarCampos();
            String nomeRelatorio = "Relatório de Licitação por Exercício";
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorio));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SECRETARIA", hierarquiaOrganizacional != null ? hierarquiaOrganizacional.getDescricao() : sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente().getDescricao());
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("condicao", montarCondicaoEFiltros());
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/licitacao-por-exercicio/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
            logger.error("Erro ao gerar relatório. ", ex);
        }
    }

    private void validarCampos() {
        ValidacaoException ve = new ValidacaoException();
        if (dataInicial != null && dataFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Se a data inicial estiver preenchida, a data final deve ser informada.");
        }
        if (dataFinal != null && dataInicial == null) {
            ve.adicionarMensagemDeCampoObrigatorio("Se a data final estiver preenchida, a data inicial deve ser informada.");
        }
        if (dataInicial != null && dataFinal != null ) {
            if (dataInicial.after(dataFinal)) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A data final do Período deve ser superior a data inicial.");
            }
            if (!DataUtil.getAno(dataInicial).equals(exercicio.getAno()) || !DataUtil.getAno(dataFinal).equals(exercicio.getAno())) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("O ano do período selecionado deve ser igual ao do exercício.");
            }
        }
        ve.lancarException();
    }

    private String montarCondicaoEFiltros() {
        StringBuilder sb = new StringBuilder();
        filtros = "";
        sb.append(" and ex.id = ").append(exercicio.getId());
        filtros += "Exercício: " + exercicio.getAno();
        if (numeroProcesso != null) {
            sb.append(" and pdc.numero = ").append(numeroProcesso);
            filtros += " - Número do Processo: " + numeroProcesso;
        }
        if (numeroLicitacao != null) {
            sb.append(" and lic.numerolicitacao = ").append(numeroLicitacao);
            filtros += " - Número da Licitação: " + numeroLicitacao;
        }
        if (dataInicial != null && dataFinal != null) {
            sb.append(" and trunc(lic.emitidaEm) between to_date('").append(DataUtil.getDataFormatada(dataInicial)).append("','dd/mm/yyyy') and to_date('").append(DataUtil.getDataFormatada(dataFinal)).append("','dd/mm/yyyy') ");
            filtros += " - Período: " + DataUtil.getDataFormatada(dataInicial) + " à " + DataUtil.getDataFormatada(dataFinal);
        }
        if (modalidadeLicitacao != null) {
            sb.append(" and lic.modalidadelicitacao = '").append(modalidadeLicitacao.name()).append("' ");
            filtros += " - Modalidade: " + modalidadeLicitacao.getDescricao();
        }
        if (tipoAvaliacaoLicitacao != null) {
            sb.append(" and lic.tipoavaliacao = '").append(tipoAvaliacaoLicitacao.name()).append("' ");
            filtros += " - Tipo de Avaliação: " + tipoAvaliacaoLicitacao.getDescricao();
        }
        if (this.resumoDoObjeto != null && !this.resumoDoObjeto.isEmpty()) {
            sb.append(" and lower(lic.resumodoobjeto) like lower('%").append(resumoDoObjeto).append("%') ");
            filtros += " - Resumo do Objeto: " + resumoDoObjeto;
        }
        if (hierarquiaOrganizacional != null) {
            sb.append(" and pdc.UNIDADEORGANIZACIONAL_ID =").append(hierarquiaOrganizacional.getSubordinada().getId());
            this.filtros += " - Unidade Administrativa: " + hierarquiaOrganizacional.toString();
        }
        if (statusLicitacao != null) {
            sb.append(" and status.tipoSituacaoLicitacao = '").append(statusLicitacao.name()).append("' ");
            this.filtros += " - Status da licitação: " + statusLicitacao.getDescricao();
        }
        return sb.toString();
    }

    public List<SelectItem> getTiposStatus() {
        return Util.getListSelectItem(TipoSituacaoLicitacao.values());
    }

    public Long getNumeroProcesso() {
        return numeroProcesso;
    }

    public void setNumeroProcesso(Long numeroProcesso) {
        this.numeroProcesso = numeroProcesso;
    }

    public Long getNumeroLicitacao() {
        return numeroLicitacao;
    }

    public void setNumeroLicitacao(Long numeroLicitacao) {
        this.numeroLicitacao = numeroLicitacao;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
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

    public ModalidadeLicitacao getModalidadeLicitacao() {
        return modalidadeLicitacao;
    }

    public void setModalidadeLicitacao(ModalidadeLicitacao modalidadeLicitacao) {
        this.modalidadeLicitacao = modalidadeLicitacao;
    }

    public TipoAvaliacaoLicitacao getTipoAvaliacaoLicitacao() {
        return tipoAvaliacaoLicitacao;
    }

    public void setTipoAvaliacaoLicitacao(TipoAvaliacaoLicitacao tipoAvaliacaoLicitacao) {
        this.tipoAvaliacaoLicitacao = tipoAvaliacaoLicitacao;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public String getResumoDoObjeto() {
        return resumoDoObjeto;
    }

    public void setResumoDoObjeto(String resumoDoObjeto) {
        this.resumoDoObjeto = resumoDoObjeto;
    }

    public TipoSituacaoLicitacao getStatusLicitacao() {
        return statusLicitacao;
    }

    public void setStatusLicitacao(TipoSituacaoLicitacao statusLicitacao) {
        this.statusLicitacao = statusLicitacao;
    }
}

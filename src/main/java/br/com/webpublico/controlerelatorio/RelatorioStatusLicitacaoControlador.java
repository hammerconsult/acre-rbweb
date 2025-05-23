package br.com.webpublico.controlerelatorio;

import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.enums.ModalidadeLicitacao;
import br.com.webpublico.enums.TipoAvaliacaoLicitacao;
import br.com.webpublico.enums.TipoSituacaoLicitacao;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: JoãoPaulo
 * Date: 27/10/14
 * Time: 11:40
 * To change this template use File | Settings | File Templates.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "relatorio-licitacao-em-andamento", pattern = "/licitacao/relatorio-licitacao-em-andamento/", viewId = "/faces/administrativo/relatorios/relatoriolicitacaoemandamento.xhtml"),
    @URLMapping(id = "relatorio-licitacao-finalizada", pattern = "/licitacao/relatorio-licitacao-finalizada/", viewId = "/faces/administrativo/relatorios/relatoriolicitacaofinalizada.xhtml"),
    @URLMapping(id = "relatorio-licitacao-homologada", pattern = "/licitacao/relatorio-licitacao-homologada/", viewId = "/faces/administrativo/relatorios/relatoriolicitacaohomogada.xhtml")
})
public class RelatorioStatusLicitacaoControlador implements Serializable {

    @EJB
    private SistemaFacade sistemaFacade;
    private Long numeroProcesso;
    private Long numeroLicitacao;
    private Date dataDeAbertura;
    private ModalidadeLicitacao modalidadeLicitacaoSelecionada;
    private TipoAvaliacaoLicitacao tipoAvaliacaoLicitacaoSelecionada;
    private UnidadeOrganizacional unidadeOrganizacionalSelecionada;
    private String resumoDoObjeto;
    private String filtros;
    private String arquivoJrxml;
    private String condicao;
    private String nomeRelatorio;

    @URLAction(mappingId = "relatorio-licitacao-em-andamento", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioLicitacaoEmAndamento() {
        limparCampos();
        arquivoJrxml = "Relatorio_Licitacao_Em_Andamento.jrxml";
        condicao = " where sl.tiposituacaolicitacao = '" + TipoSituacaoLicitacao.ANDAMENTO.name() + "'" + montarCondicaoEFiltros();
        nomeRelatorio = "Relatório de Licitação em Andamento";
    }

    @URLAction(mappingId = "relatorio-licitacao-finalizada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioLicitacaoFinalizada() {
        limparCampos();
        arquivoJrxml = "Relatorio_Licitacao_Finalizada.jrxml";
        condicao = " where (sl.tiposituacaolicitacao = '" + TipoSituacaoLicitacao.CANCELADA.name() + "' or sl.tiposituacaolicitacao = '" + TipoSituacaoLicitacao.HOMOLOGADA.name() + "') " + montarCondicaoEFiltros();
        nomeRelatorio = "Relatório de Licitação Finalizada";
    }

    @URLAction(mappingId = "relatorio-licitacao-homologada", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void novoRelatorioLicitacaoHomologada() {
        limparCampos();
        arquivoJrxml = "Relatorio_Licitacao_Homologada.jrxml";
        condicao = " where sl.tiposituacaolicitacao = '" + TipoSituacaoLicitacao.HOMOLOGADA.name() + "'" + montarCondicaoEFiltros();
        nomeRelatorio = "Relatório de Licitação Homologada";
    }

    private void limparCampos() {
        this.numeroProcesso = null;
        this.numeroLicitacao = null;
        this.dataDeAbertura = null;
        this.modalidadeLicitacaoSelecionada = null;
        this.tipoAvaliacaoLicitacaoSelecionada = null;
        this.resumoDoObjeto = null;
        this.unidadeOrganizacionalSelecionada = new UnidadeOrganizacional();
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("SECRETARIA", sistemaFacade.getUnidadeOrganizacionalOrcamentoCorrente().getDescricao());
            dto.adicionarParametro("MODULO", "Administrativo");
            dto.adicionarParametro("arquivoJrxml", arquivoJrxml);
            dto.adicionarParametro("NOMERELATORIO", nomeRelatorio);
            dto.adicionarParametro("condicao", condicao);
            dto.adicionarParametro("FILTROS", filtros);
            dto.setNomeRelatorio(nomeRelatorio);
            dto.setApi("administrativo/status-licitacao/");
            ReportService.getInstance().gerarRelatorio(sistemaFacade.getUsuarioCorrente(), dto);
            FacesUtil.addMensagemRelatorioSegundoPlano();
        } catch (WebReportRelatorioExistenteException e) {
            ReportService.getInstance().abrirDialogConfirmar(e);
        } catch (ValidacaoException ve) {
            FacesUtil.printAllFacesMessages(ve.getAllMensagens());
        } catch (Exception ex) {
            FacesUtil.addErroAoGerarRelatorio(ex.getMessage());
        }
    }

    private String montarCondicaoEFiltros() {
        StringBuilder sb = new StringBuilder();
        filtros = "";
        if (this.numeroProcesso != null) {
            sb.append(" and pdc.numero = ").append(this.numeroProcesso);
            this.filtros += " Número do Processo: " + this.numeroProcesso;
        }
        if (this.numeroLicitacao != null) {
            sb.append(" and lic.numerolicitacao = ").append(this.numeroLicitacao);
            this.filtros += " Número da Licitação: " + this.numeroLicitacao;
        }
        if (this.dataDeAbertura != null) {
            sb.append(" and trunc(lic.abertaem) = to_date('").append(DataUtil.getDataFormatada(this.dataDeAbertura)).append("','dd/mm/yyyy') ");
            this.filtros += " Data de Abertura: " + DataUtil.getDataFormatada(this.dataDeAbertura);
        }
        if (this.modalidadeLicitacaoSelecionada != null) {
            sb.append(" and lic.modalidadelicitacao = '").append(this.modalidadeLicitacaoSelecionada.name()).append("' ");
            this.filtros += " Modalidade: " + this.modalidadeLicitacaoSelecionada.getDescricao();
        }
        if (this.tipoAvaliacaoLicitacaoSelecionada != null) {
            sb.append(" and lic.tipoavaliacao = '").append(this.tipoAvaliacaoLicitacaoSelecionada.name()).append("' ");
            this.filtros += " Tipo de Avaliação: " + this.tipoAvaliacaoLicitacaoSelecionada.getDescricao();
        }
        if (this.resumoDoObjeto != null && !this.resumoDoObjeto.isEmpty()) {
            sb.append(" and lower(lic.resumodoobjeto) like lower('%").append(this.resumoDoObjeto).append("%') ");
            this.filtros += " Resumo do Objeto: " + this.resumoDoObjeto;
        }
        if (this.unidadeOrganizacionalSelecionada != null) {
            sb.append(" and uo.id =").append(this.unidadeOrganizacionalSelecionada.getId());
            this.filtros += " Unidade Administrativa: " + this.unidadeOrganizacionalSelecionada.getDescricao();
        }
        return sb.toString();
    }

    public ModalidadeLicitacao getModalidadeLicitacaoSelecionada() {
        return modalidadeLicitacaoSelecionada;
    }

    public void setModalidadeLicitacaoSelecionada(ModalidadeLicitacao modalidadeLicitacaoSelecionada) {
        this.modalidadeLicitacaoSelecionada = modalidadeLicitacaoSelecionada;
    }

    public String getFiltros() {
        return filtros;
    }

    public void setFiltros(String filtros) {
        this.filtros = filtros;
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

    public TipoAvaliacaoLicitacao getTipoAvaliacaoLicitacaoSelecionada() {
        return tipoAvaliacaoLicitacaoSelecionada;
    }

    public void setTipoAvaliacaoLicitacaoSelecionada(TipoAvaliacaoLicitacao tipoAvaliacaoLicitacaoSelecionada) {
        this.tipoAvaliacaoLicitacaoSelecionada = tipoAvaliacaoLicitacaoSelecionada;
    }

    public String getResumoDoObjeto() {
        return resumoDoObjeto;
    }

    public void setResumoDoObjeto(String resumoDoObjeto) {
        this.resumoDoObjeto = resumoDoObjeto;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalSelecionada() {
        return unidadeOrganizacionalSelecionada;
    }

    public void setUnidadeOrganizacionalSelecionada(UnidadeOrganizacional unidadeOrganizacionalSelecionada) {
        this.unidadeOrganizacionalSelecionada = unidadeOrganizacionalSelecionada;
    }

    public Date getDataDeAbertura() {
        return dataDeAbertura;
    }

    public void setDataDeAbertura(Date dataDeAbertura) {
        this.dataDeAbertura = dataDeAbertura;
    }
}

package br.com.webpublico.controlerelatorio;

import br.com.webpublico.controle.LevantamentoBensPatrimoniaisControlador;
import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidadesauxiliares.FiltrosLevantamentoBem;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.exception.WebReportRelatorioExistenteException;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.report.ReportService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.webreportdto.dto.comum.RelatorioDTO;
import br.com.webpublico.webreportdto.dto.comum.TipoRelatorioDTO;
import com.google.common.collect.Lists;
import com.ocpsoft.pretty.faces.annotation.URLAction;
import com.ocpsoft.pretty.faces.annotation.URLMapping;
import com.ocpsoft.pretty.faces.annotation.URLMappings;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by mateus on 19/03/18.
 */
@ManagedBean
@ViewScoped
@URLMappings(mappings = {
    @URLMapping(id = "emitirRelatorioLevantamentoBem", pattern = "/levantamento-de-bens-patrimoniais/relatorio-de-levantamentos/", viewId = "/faces/administrativo/patrimonio/levantamentodebenspatrimoniais/relatorioLevantamentoBem.xhtml")
})
public class RelatorioLevantamentoBemControlador {

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    public static final String NOME_RELATORIO = "RelatorioDeLevantamentoDeBensMoveis";
    private Date dataInicial;
    private Date dataFinal;
    private LevantamentoBensPatrimoniaisControlador.TipoOrdenacaoRelatorioLevantamentoBem tipoOrdenacaoRelatorioLevantamentoBem;
    private List<LevantamentoBensPatrimoniaisControlador.TipoOrdenacaoRelatorioLevantamentoBem> tiposDeOrdenacao;
    private EstadoConservacaoBem estadoConservacaoBem;
    private SituacaoConservacaoBem situacaoConservacaoBem;
    private TipoAquisicaoBem tipoAquisicaoBem;
    private GrupoBem grupoBem;
    private String numeroNota;
    private Integer numeroEmpenho;
    private Integer anoEmpenho;
    private String observacao;
    private String localizacao;
    private TipoRecursoAquisicaoBem tipoRecursoAquisicaoBem;
    private HierarquiaOrganizacional hierarquiaOrganizacionalAdministrativa;
    private HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria;
    private FiltrosLevantamentoBem filtrosLevBem;
    private String filtros;
    private String descricaoOrderBy;

    @URLAction(mappingId = "emitirRelatorioLevantamentoBem", phaseId = URLAction.PhaseId.RENDER_RESPONSE, onPostback = false)
    public void limparCampos() {
        hierarquiaOrganizacionalAdministrativa = new HierarquiaOrganizacional();
        hierarquiaOrganizacionalOrcamentaria = new HierarquiaOrganizacional();
        tipoOrdenacaoRelatorioLevantamentoBem = LevantamentoBensPatrimoniaisControlador.TipoOrdenacaoRelatorioLevantamentoBem.UNIDADE;
        tiposDeOrdenacao = new ArrayList<>();
        estadoConservacaoBem = null;
        grupoBem = null;
        numeroNota = null;
        numeroEmpenho = null;
        anoEmpenho = null;
        observacao = null;
        localizacao = null;
        filtrosLevBem = new FiltrosLevantamentoBem();
        filtrosLevBem.setDataReferencia(sistemaFacade.getDataOperacao());
    }

    public void gerarRelatorio(String tipoRelatorioExtensao) {
        try {
            validarCampos();
            RelatorioDTO dto = new RelatorioDTO();
            dto.setTipoRelatorio(TipoRelatorioDTO.valueOf(tipoRelatorioExtensao));
            filtrosLevBem.setOrderBy(getColunasOrderBy());
            filtrosLevBem.setClausulas(montarCondicaoEFiltros(dto));
            dto.adicionarParametro("USUARIO", sistemaFacade.getUsuarioCorrente().getNome(), false);
            dto.setNomeParametroBrasao("BRASAO");
            dto.adicionarParametro("ORDENACAO", descricaoOrderBy);
            dto.adicionarParametro("FILTROS", filtros);
            dto.adicionarParametro("consolidado", filtrosLevBem.getConsolidado());
            dto.adicionarParametro("dataReferencia", DataUtil.getDataFormatada(filtrosLevBem.getDataReferencia()));
            dto.adicionarParametro("condicao", filtrosLevBem.getClausulas());
            dto.adicionarParametro("orderBy", filtrosLevBem.getOrderBy());
            dto.adicionarParametro("codigoEDescricaoHierarqAdm", filtrosLevBem.getCodigoEDescricaoHierarquiaAdministrativa());
            dto.setNomeRelatorio(NOME_RELATORIO);
            dto.setApi("administrativo/levantamento-bens-moveis/");
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
        ValidacaoException ex = new ValidacaoException();
        if (hierarquiaOrganizacionalAdministrativa == null && hierarquiaOrganizacionalOrcamentaria == null) {
            ex.adicionarMensagemDeCampoObrigatorio("Informe ao menos uma unidade administrativa ou orçamentária.");
        }
        if (numeroEmpenho != null && anoEmpenho == null) {
            ex.adicionarMensagemDeCampoObrigatorio("Por Favor informe o ano do empenho.");
        } else if (anoEmpenho != null && numeroEmpenho == null) {
            ex.adicionarMensagemDeCampoObrigatorio("Por Favor informe o nº do empenho.");
        }
        if (filtrosLevBem.getDataReferencia() == null) {
            ex.adicionarMensagemDeCampoObrigatorio("Por Favor informe a data de referência.");
        }
        ex.lancarException();
    }

    public List<SelectItem> getEstadosDeConservacao() {
        return Util.getListSelectItem(Arrays.asList(EstadoConservacaoBem.getValoresPossiveisParaLevantamentoDeBemPatrimonial()));
    }

    public List<SelectItem> buscarHierarquiasOrcamentarias() {
        List<SelectItem> toReturn = Lists.newArrayList();
        toReturn.add(new SelectItem(null, ""));
        if (hierarquiaOrganizacionalAdministrativa != null && hierarquiaOrganizacionalAdministrativa.getSubordinada() != null) {
            for (HierarquiaOrganizacional hierarquia : hierarquiaOrganizacionalFacade.retornaHierarquiaOrcamentariaPorUnidadeAdministrativa(hierarquiaOrganizacionalAdministrativa.getSubordinada(), sistemaFacade.getDataOperacao())) {
                toReturn.add(new SelectItem(hierarquia, hierarquia.toString()));
            }
        } else {
            for (HierarquiaOrganizacional hierarquia : hierarquiaOrganizacionalFacade.retornaHierarquiaOrganizacionalOrcamentariaOndeUsuarioEhGestorPatrimonio(sistemaFacade.getUsuarioCorrente(), sistemaFacade.getDataOperacao())) {
                toReturn.add(new SelectItem(hierarquia, hierarquia.toString()));
            }
        }
        return toReturn;
    }

    public String montarCondicaoEFiltros(RelatorioDTO dto) {
        StringBuffer sql = new StringBuffer();
        filtros = "";
        if (hierarquiaOrganizacionalAdministrativa != null && hierarquiaOrganizacionalAdministrativa.getCodigo() != null) {
            sql.append(" and VW.CODIGO LIKE '").append(hierarquiaOrganizacionalAdministrativa.getCodigoSemZerosFinais()).append("%'");
            filtrosLevBem.setCodigoEDescricaoHierarquiaAdministrativa(recuperarHierarquiaPelaDataReferencia(hierarquiaOrganizacionalAdministrativa, TipoHierarquiaOrganizacional.ADMINISTRATIVA).toString());
            dto.adicionarParametro("CODIGO_E_DESCRICAO_HIERARQUIA", filtrosLevBem.getCodigoEDescricaoHierarquiaAdministrativa());
        }
        if (hierarquiaOrganizacionalOrcamentaria != null && hierarquiaOrganizacionalOrcamentaria.getCodigo() != null) {
            sql.append(" and VWORC.CODIGO = '").append(hierarquiaOrganizacionalOrcamentaria.getCodigo()).append("'");
            dto.adicionarParametro("CODIGO_E_DESCRICAO_HIERARQUIA_ORC", recuperarHierarquiaPelaDataReferencia(hierarquiaOrganizacionalOrcamentaria, TipoHierarquiaOrganizacional.ORCAMENTARIA).toString());
        }
        if (dataInicial != null) {
            sql.append(" AND trunc(lev.dataaquisicao) >= to_date('").append(DataUtil.getDataFormatada(dataInicial)).append("', 'dd/MM/yyyy') ");
            filtros = " Data de aquisição inicial: " + DataUtil.getDataFormatada(dataInicial) + ". ";
        }
        if (dataFinal != null) {
            sql.append(" AND trunc(lev.dataaquisicao) <= to_date('").append(DataUtil.getDataFormatada(dataFinal)).append("', 'dd/MM/yyyy') ");
            filtros = " Data de aquisição final: " + DataUtil.getDataFormatada(dataFinal) + ". ";
        }
        if (dataInicial != null && dataFinal != null) {
            filtros = " Data de aquisição: " + DataUtil.getDataFormatada(dataInicial) + " à " + DataUtil.getDataFormatada(dataFinal) + ". ";
        }
        if (estadoConservacaoBem != null) {
            sql.append(" AND lev.ESTADOCONSERVACAOBEM = '").append(getEstadoConservacaoBem().name()).append("'");
            filtros += " Estado de Conservação: " + estadoConservacaoBem.getDescricao() + ". ";
        }
        if (tipoAquisicaoBem != null) {
            sql.append(" AND lev.tipoAquisicaoBem = '").append(getTipoAquisicaoBem().name()).append("'");
            filtros += " Tipo de Aquisição: " + tipoAquisicaoBem.getDescricao() + ". ";
        }
        if (grupoBem != null) {
            sql.append(" and g.codigo = '").append(grupoBem.getCodigo().trim()).append("' ");
            filtros += " Grupo Patrimonial: " + grupoBem.toString() + ". ";
        }
        if (situacaoConservacaoBem != null) {
            sql.append(" and lev.situacaoconservacaobem = '").append(situacaoConservacaoBem.name()).append("' ");
            filtros += " Situação de Conservação: " + situacaoConservacaoBem.getDescricao() + ". ";
        }
        if ((numeroNota != null) && !numeroNota.trim().isEmpty()) {
            sql.append(" and lev.notafiscal = '").append(numeroNota).append("' ");
        }
        if (numeroEmpenho != null && anoEmpenho != null) {
            sql.append(" and lev.notaempenho = ").append(numeroEmpenho).append(" ");
            sql.append(" and extract(year from lev.datanotaempenho) = ").append(anoEmpenho);
        }
        if ((observacao != null) && !observacao.trim().isEmpty())
            sql.append(" and lower(lev.observacao) like '%").append(observacao.trim().toLowerCase()).append("%' ");

        if ((localizacao != null) && !localizacao.trim().isEmpty()) {
            sql.append(" and lower(lev.localizacao) like '%").append(localizacao.trim().toLowerCase()).append("%' ");
        }
        if (tipoRecursoAquisicaoBem != null) {
            sql.append(" and exists(select 1 from origemrecursobem orb where orb.levantamentobempatrimonial_id = lev.id ").
                append(" and orb.tiporecursoaquisicaobem = '").append(tipoRecursoAquisicaoBem.name()).append("') ");
            filtros += " Tipo da Origem do Recurso: " + tipoRecursoAquisicaoBem.getDescricao() + ". ";
        }
        sql.append(" AND NOT EXISTS(SELECT 1 ")
            .append(" FROM EFETIVACAOLEVANTAMENTOBEM EFET ")
            .append(" INNER JOIN EVENTOBEM EVB_EFET ON EVB_EFET.ID = EFET.ID AND TRUNC(EVB_EFET.DATALANCAMENTO) <= to_date('").append(DataUtil.getDataFormatada(filtrosLevBem.getDataReferencia())).append("', 'dd/MM/yyyy')")
            .append(" WHERE EFET.LEVANTAMENTO_ID = LEV.ID ")
            .append(" AND NOT EXISTS(SELECT 1 FROM ESTORNOEFETIVACAOLEVMOVEL ESTORNO ")
            .append(" INNER JOIN EVENTOBEM EVB_ESTORNO ON EVB_ESTORNO.ID = ESTORNO.ID AND TRUNC(EVB_ESTORNO.DATALANCAMENTO) <= to_date('").append(DataUtil.getDataFormatada(filtrosLevBem.getDataReferencia())).append("', 'dd/MM/yyyy')")
            .append(" WHERE ESTORNO.EFETIVACAOLEVANTAMENTOBEM_ID = EFET.ID ")
            .append("  )) ");
        return sql.toString();
    }

    private HierarquiaOrganizacional recuperarHierarquiaPelaDataReferencia(HierarquiaOrganizacional hierarquiaOrganizacional, TipoHierarquiaOrganizacional tipoHierarquiaOrganizacional) {
        return hierarquiaOrganizacionalFacade.getHierarquiaOrganizacionalVigentePorUnidade(hierarquiaOrganizacional.getSubordinada(), tipoHierarquiaOrganizacional, filtrosLevBem.getDataReferencia());
    }

    public void adicionarOrdenacao() {
        if (!tiposDeOrdenacao.contains(tipoOrdenacaoRelatorioLevantamentoBem)) {
            tiposDeOrdenacao.add(tipoOrdenacaoRelatorioLevantamentoBem);
            tipoOrdenacaoRelatorioLevantamentoBem = LevantamentoBensPatrimoniaisControlador.TipoOrdenacaoRelatorioLevantamentoBem.UNIDADE;
        } else {
            FacesUtil.addError(SummaryMessages.OPERACAO_NAO_PERMITIDA.getDescricao(), "Está opção já foi adicionada.");
        }
    }

    public void removerOpcaoTipoOrdenacao(LevantamentoBensPatrimoniaisControlador.TipoOrdenacaoRelatorioLevantamentoBem tipo) {
        tiposDeOrdenacao.remove(tipo);
    }

    private String getColunasOrderBy() {
        String orderby = LevantamentoBensPatrimoniaisControlador.TipoOrdenacaoRelatorioLevantamentoBem.UNIDADE.getColuna();
        for (LevantamentoBensPatrimoniaisControlador.TipoOrdenacaoRelatorioLevantamentoBem to : tiposDeOrdenacao) {
            orderby += ", " + to.getColuna();
            descricaoOrderBy += ", " + to.getDescricao();
        }
        return orderby;
    }

    public List<SelectItem> getSituacoesConservacaoBem() {
        if (estadoConservacaoBem != null) {
            return Util.getListSelectItem(estadoConservacaoBem.getSituacoes());
        }
        return null;
    }

    public List<SelectItem> getTiposDeAquisicoesBens() {
        return Util.getListSelectItem(TipoAquisicaoBem.values());
    }

    public List<SelectItem> getTiposRecursosAquisicoesBens() {
        return Util.getListSelectItem(Arrays.asList(TipoRecursoAquisicaoBem.values()));
    }

    public List<SelectItem> getTiposOrdenacoes() {
        List<SelectItem> listSelectItem = new ArrayList<>();
        for (LevantamentoBensPatrimoniaisControlador.TipoOrdenacaoRelatorioLevantamentoBem tipo : LevantamentoBensPatrimoniaisControlador.TipoOrdenacaoRelatorioLevantamentoBem.values()) {
            if (!tipo.equals(LevantamentoBensPatrimoniaisControlador.TipoOrdenacaoRelatorioLevantamentoBem.UNIDADE)) {
                listSelectItem.add(new SelectItem(tipo, tipo.getDescricao()));
            }
        }
        return listSelectItem;
    }

    public LevantamentoBensPatrimoniaisControlador.TipoOrdenacaoRelatorioLevantamentoBem getTipoOrdenacaoRelatorioLevantamentoBem() {
        return tipoOrdenacaoRelatorioLevantamentoBem;
    }

    public void setTipoOrdenacaoRelatorioLevantamentoBem(LevantamentoBensPatrimoniaisControlador.TipoOrdenacaoRelatorioLevantamentoBem tipoOrdenacaoRelatorioLevantamentoBem) {
        this.tipoOrdenacaoRelatorioLevantamentoBem = tipoOrdenacaoRelatorioLevantamentoBem;
    }

    public List<LevantamentoBensPatrimoniaisControlador.TipoOrdenacaoRelatorioLevantamentoBem> getTiposDeOrdenacao() {
        return tiposDeOrdenacao;
    }

    public void setTiposDeOrdenacao(List<LevantamentoBensPatrimoniaisControlador.TipoOrdenacaoRelatorioLevantamentoBem> tiposDeOrdenacao) {
        this.tiposDeOrdenacao = tiposDeOrdenacao;
    }

    public EstadoConservacaoBem getEstadoConservacaoBem() {
        return estadoConservacaoBem;
    }

    public void setEstadoConservacaoBem(EstadoConservacaoBem estadoConservacaoBem) {
        this.estadoConservacaoBem = estadoConservacaoBem;
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

    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return tipoAquisicaoBem;
    }

    public void setTipoAquisicaoBem(TipoAquisicaoBem tipoAquisicaoBem) {
        this.tipoAquisicaoBem = tipoAquisicaoBem;
    }

    public GrupoBem getGrupoBem() {
        return grupoBem;
    }

    public void setGrupoBem(GrupoBem grupoBem) {
        this.grupoBem = grupoBem;
    }

    public SituacaoConservacaoBem getSituacaoConservacaoBem() {
        return situacaoConservacaoBem;
    }

    public void setSituacaoConservacaoBem(SituacaoConservacaoBem situacaoConservacaoBem) {
        this.situacaoConservacaoBem = situacaoConservacaoBem;
    }

    public String getNumeroNota() {
        return numeroNota;
    }

    public void setNumeroNota(String numeroNota) {
        this.numeroNota = numeroNota;
    }

    public Integer getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(Integer numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public Integer getAnoEmpenho() {
        return anoEmpenho;
    }

    public void setAnoEmpenho(Integer anoEmpenho) {
        this.anoEmpenho = anoEmpenho;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public TipoRecursoAquisicaoBem getTipoRecursoAquisicaoBem() {
        return tipoRecursoAquisicaoBem;
    }

    public void setTipoRecursoAquisicaoBem(TipoRecursoAquisicaoBem tipoRecursoAquisicaoBem) {
        this.tipoRecursoAquisicaoBem = tipoRecursoAquisicaoBem;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalAdministrativa() {
        return hierarquiaOrganizacionalAdministrativa;
    }

    public void setHierarquiaOrganizacionalAdministrativa(HierarquiaOrganizacional hierarquiaOrganizacionalAdministrativa) {
        this.hierarquiaOrganizacionalAdministrativa = hierarquiaOrganizacionalAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacionalOrcamentaria() {
        return hierarquiaOrganizacionalOrcamentaria;
    }

    public void setHierarquiaOrganizacionalOrcamentaria(HierarquiaOrganizacional hierarquiaOrganizacionalOrcamentaria) {
        this.hierarquiaOrganizacionalOrcamentaria = hierarquiaOrganizacionalOrcamentaria;
    }

    public FiltrosLevantamentoBem getFiltrosLevBem() {
        return filtrosLevBem;
    }

    public void setFiltrosLevBem(FiltrosLevantamentoBem filtrosLevBem) {
        this.filtrosLevBem = filtrosLevBem;
    }
}

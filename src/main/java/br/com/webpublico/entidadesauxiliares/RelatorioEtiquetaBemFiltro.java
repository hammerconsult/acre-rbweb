package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.GrupoBem;
import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.entidades.Pessoa;
import br.com.webpublico.enums.EstadoConservacaoBem;
import br.com.webpublico.enums.SituacaoConservacaoBem;
import br.com.webpublico.enums.TipoAquisicaoBem;
import br.com.webpublico.enums.TipoGrupo;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Strings;

import java.io.Serializable;
import java.util.Date;

public class RelatorioEtiquetaBemFiltro implements Serializable {

    private static String MSG_VALIDACAO_DATA_INICIAL = "A data inicial deve ser informada.";

    private static String MSG_VALIDACAO_DATA_FINAL = "A data final deve ser informada.";

    private static String MSG_VALIDACAO_PERIODO = "A data final deve ser posterior ou igual a data inicial.";

    private Date dataAquisicaoInicial;

    private Date dataAquisicaoFinal;

    private HierarquiaOrganizacional hierarquiaAdministrativa;

    private HierarquiaOrganizacional hierarquiaOrcamentaria;

    private TipoAquisicaoBem tipoAquisicaoBem;

    private EstadoConservacaoBem estadoConservacaoBem;

    private SituacaoConservacaoBem situacaoConservacaoBem;

    private TipoGrupo tipoGrupo;

    private GrupoBem grupoPatrimonial;

    private String numeroNotaFiscal;

    private Date dataNotaFiscalInicial;

    private Date dataNotaFiscalFinal;

    private String numeroEmpenho;

    private Date dataEmpenhoInicial;

    private Date dataEmpenhoFinal;

    private Pessoa fornecedor;

    private String registroPatrimonial;

    public RelatorioEtiquetaBemFiltro() {
        limparFiltros();
    }

    public void validarFiltros() {
        ValidacaoException ve = new ValidacaoException();

        validarFiltrosData(ve);

        if (getHierarquiaAdministrativa() == null && getHierarquiaOrcamentaria() == null ) {
            ve.adicionarMensagemDeCampoObrigatorio("Os campos Unidade Administrativa e Unidade Orçamentária estão vazios, uma das duas unidades devem ser informadas.");
        }

        ve.lancarException();
    }

    public String montarCondicao() {
        StringBuilder condicao = new StringBuilder();

        condicao.append("WHERE TRUNC(bem.DATAAQUISICAO) BETWEEN ")
            .append("TO_DATE('").append(DataUtil.getDataFormatada(dataAquisicaoInicial)).append("', 'dd/MM/yyyy') ")
            .append("AND TO_DATE('").append(DataUtil.getDataFormatada(dataAquisicaoFinal)).append("', 'dd/MM/yyyy') ");

        if (hierarquiaAdministrativa != null) {
            condicao.append(" AND vwAdm.ID = " + hierarquiaAdministrativa.getId());
        }

        if (hierarquiaOrcamentaria != null) {
            condicao.append(" AND vwOrc.ID = '").append(hierarquiaOrcamentaria.getId()).append("' ");
        }

        if (tipoAquisicaoBem != null) {
            condicao.append(" AND est.TIPOAQUISICAOBEM = '").append(tipoAquisicaoBem.name()).append("' ");
        }

        if (estadoConservacaoBem != null) {
            condicao.append(" AND est.ESTADOBEM = '").append(estadoConservacaoBem.name()).append("' ");
        }
        if (situacaoConservacaoBem != null) {
            condicao.append(" AND est.SITUACAOCONSERVACAOBEM = '").append(situacaoConservacaoBem.name()).append("' ");
        }

        if (tipoGrupo != null) {
            condicao.append(" AND est.TIPOGRUPO = '").append(tipoGrupo.name()).append("' ");
        }

        if (grupoPatrimonial != null) {
            condicao.append(" AND gb.ID = ").append(grupoPatrimonial.getId());
        }

        if (!Strings.isNullOrEmpty(numeroNotaFiscal)) {
            condicao.append(" AND (");
            condicao.append("     nota.NUMERONOTAFISCAL = '").append(numeroNotaFiscal.trim()).append("' ");
            condicao.append("     OR exists (");
            condicao.append("         SELECT 1 ");
            condicao.append("         FROM AQUISICAO a");
            condicao.append("         INNER JOIN ITEMAQUISICAO ia ON a.ID = ia.AQUISICAO_ID ");
            condicao.append("         INNER JOIN EVENTOBEM eva ON ia.ID = eva.ID ");
            condicao.append("         INNER JOIN BEM ba ON eva.BEM_ID = ba.ID ");
            condicao.append("         INNER JOIN SOLICITACAOAQUISICAO sa ON a.SOLICITACAOAQUISICAO_ID = sa.ID ");
            condicao.append("         INNER JOIN DOCTOFISCALSOLICAQUISICAO dfsa ON sa.ID = dfsa.SOLICITACAOAQUISICAO_ID ");
            condicao.append("         LEFT JOIN DOCTOFISCALLIQUIDACAO dfl ON dfsa.DOCUMENTOFISCAL_ID = dfl.ID ");
            condicao.append("         WHERE ba.ID = bem.ID ");
            condicao.append("         AND dfl.NUMERO = '").append(numeroNotaFiscal.trim()).append("' ");
            condicao.append("     )");
            condicao.append(" )");
        }

        if (dataNotaFiscalInicial != null && dataNotaFiscalFinal != null) {
            condicao.append(" AND TRUNC(nota.DATANOTAFISCAL) BETWEEN ")
                .append("TO_DATE('").append(DataUtil.getDataFormatada(dataNotaFiscalInicial)).append("', 'dd/MM/yyyy') ")
                .append("AND TO_DATE('").append(DataUtil.getDataFormatada(dataNotaFiscalFinal)).append("', 'dd/MM/yyyy') ");
        }

        if (!Strings.isNullOrEmpty(numeroEmpenho)) {
            condicao.append(" AND notaEmp.NUMEROEMPENHO = '").append(numeroEmpenho.trim()).append("' ");
        }

        if (dataEmpenhoInicial != null && dataEmpenhoFinal != null) {
            condicao.append(" AND trunc(notaEmp.DATAEMPENHO) BETWEEN ")
                .append("TO_DATE('").append(DataUtil.getDataFormatada(dataEmpenhoInicial)).append("', 'dd/MM/yyyy') ")
                .append("AND TO_DATE('").append(DataUtil.getDataFormatada(dataEmpenhoFinal)).append("', 'dd/MM/yyyy') ");
        }

        if (fornecedor != null) {
            condicao.append(" AND bem.FORNECEDOR_ID = ").append(fornecedor.getId());
        }

        if (!Strings.isNullOrEmpty(registroPatrimonial)) {
            condicao.append(" AND bem.IDENTIFICACAO = '").append(registroPatrimonial.trim()).append("' ");
        }

        return condicao.toString();
    }

    private void validarFiltrosData(ValidacaoException ve) {
        if (dataAquisicaoInicial == null && dataAquisicaoFinal == null) {
            ve.adicionarMensagemDeCampoObrigatorio("O campo Data de Aquisição é obrigatório.");
        }

        if (verificarSeDataInicialDoPeriodoFoiInformada(dataAquisicaoInicial, dataAquisicaoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_DATA_INICIAL);
        }

        if (verificarSeDataFinalDoPeriodoFoiInformada(dataAquisicaoInicial, dataAquisicaoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_DATA_FINAL);
        }

        if (verificarSeDataInicialDoPeriodoFoiInformada(dataNotaFiscalInicial, dataNotaFiscalFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_DATA_INICIAL);
        }

        if (verificarSeDataFinalDoPeriodoFoiInformada(dataNotaFiscalInicial, dataNotaFiscalFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_DATA_FINAL);
        }

        if (verificarSeDataInicialDoPeriodoFoiInformada(dataEmpenhoInicial, dataEmpenhoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_DATA_INICIAL);
        }

        if (verificarSeDataFinalDoPeriodoFoiInformada(dataEmpenhoInicial, dataEmpenhoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_DATA_FINAL);
        }

        validarFiltrosPeriodo(ve);

        ve.lancarException();
    }

    private void validarFiltrosPeriodo(ValidacaoException ve) {
        if (verificarDataFinalAnteriorAInicial(dataAquisicaoInicial, dataAquisicaoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_PERIODO);
        }

        if (verificarDataFinalAnteriorAInicial(dataNotaFiscalInicial, dataNotaFiscalFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_PERIODO);
        }

        if (verificarDataFinalAnteriorAInicial(dataEmpenhoInicial, dataEmpenhoFinal)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(MSG_VALIDACAO_PERIODO);
        }

        ve.lancarException();
    }

    private boolean verificarDataFinalAnteriorAInicial(Date dataInicial, Date dataFinal) {
        return dataInicial != null && dataFinal != null && dataFinal.before(dataInicial);
    }

    private boolean verificarSeDataInicialDoPeriodoFoiInformada(Date dataInicial, Date dataFinal) {
        return dataFinal != null && dataInicial == null;
    }

    private boolean verificarSeDataFinalDoPeriodoFoiInformada(Date dataInicial, Date dataFinal) {
        return dataInicial != null && dataFinal == null;
    }

    private void limparFiltros() {
        dataAquisicaoInicial = null;
        dataAquisicaoFinal = null;
        hierarquiaAdministrativa = null;
        hierarquiaOrcamentaria = null;
        tipoAquisicaoBem = null;
        estadoConservacaoBem = null;
        situacaoConservacaoBem = null;
        tipoGrupo = null;
        grupoPatrimonial = null;
        numeroNotaFiscal = null;
        dataNotaFiscalInicial = null;
        dataNotaFiscalFinal = null;
        numeroEmpenho = null;
        dataEmpenhoInicial = null;
        dataEmpenhoFinal = null;
        fornecedor = null;
        registroPatrimonial = null;
    }

    public boolean hasFiltrosNotaOrEmpenhoUtilizados() {
        return getDataNotaFiscalInicial() != null
            || !Strings.isNullOrEmpty(getNumeroNotaFiscal())
            || getDataEmpenhoInicial() != null
            || !Strings.isNullOrEmpty(getNumeroEmpenho());
    }

    public Date getDataAquisicaoInicial() {
        return dataAquisicaoInicial;
    }

    public void setDataAquisicaoInicial(Date dataAquisicaoInicial) {
        this.dataAquisicaoInicial = dataAquisicaoInicial;
    }

    public Date getDataAquisicaoFinal() {
        return dataAquisicaoFinal;
    }

    public void setDataAquisicaoFinal(Date dataAquisicaoFinal) {
        this.dataAquisicaoFinal = dataAquisicaoFinal;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public HierarquiaOrganizacional getHierarquiaOrcamentaria() {
        return hierarquiaOrcamentaria;
    }

    public void setHierarquiaOrcamentaria(HierarquiaOrganizacional hierarquiaOrcamentaria) {
        this.hierarquiaOrcamentaria = hierarquiaOrcamentaria;
    }

    public TipoAquisicaoBem getTipoAquisicaoBem() {
        return tipoAquisicaoBem;
    }

    public void setTipoAquisicaoBem(TipoAquisicaoBem tipoAquisicaoBem) {
        this.tipoAquisicaoBem = tipoAquisicaoBem;
    }

    public EstadoConservacaoBem getEstadoConservacaoBem() {
        return estadoConservacaoBem;
    }

    public void setEstadoConservacaoBem(EstadoConservacaoBem estadoConservacaoBem) {
        this.estadoConservacaoBem = estadoConservacaoBem;
    }

    public SituacaoConservacaoBem getSituacaoConservacaoBem() {
        return situacaoConservacaoBem;
    }

    public void setSituacaoConservacaoBem(SituacaoConservacaoBem situacaoConservacaoBem) {
        this.situacaoConservacaoBem = situacaoConservacaoBem;
    }

    public TipoGrupo getTipoGrupo() {
        return tipoGrupo;
    }

    public void setTipoGrupo(TipoGrupo tipoGrupo) {
        this.tipoGrupo = tipoGrupo;
    }

    public GrupoBem getGrupoPatrimonial() {
        return grupoPatrimonial;
    }

    public void setGrupoPatrimonial(GrupoBem grupoPatrimonial) {
        this.grupoPatrimonial = grupoPatrimonial;
    }

    public String getNumeroNotaFiscal() {
        return numeroNotaFiscal;
    }

    public void setNumeroNotaFiscal(String numeroNotaFiscal) {
        this.numeroNotaFiscal = numeroNotaFiscal;
    }

    public Date getDataNotaFiscalInicial() {
        return dataNotaFiscalInicial;
    }

    public void setDataNotaFiscalInicial(Date dataNotaFiscalInicial) {
        this.dataNotaFiscalInicial = dataNotaFiscalInicial;
    }

    public Date getDataNotaFiscalFinal() {
        return dataNotaFiscalFinal;
    }

    public void setDataNotaFiscalFinal(Date dataNotaFiscalFinal) {
        this.dataNotaFiscalFinal = dataNotaFiscalFinal;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public Date getDataEmpenhoInicial() {
        return dataEmpenhoInicial;
    }

    public void setDataEmpenhoInicial(Date dataEmpenhoInicial) {
        this.dataEmpenhoInicial = dataEmpenhoInicial;
    }

    public Date getDataEmpenhoFinal() {
        return dataEmpenhoFinal;
    }

    public void setDataEmpenhoFinal(Date dataEmpenhoFinal) {
        this.dataEmpenhoFinal = dataEmpenhoFinal;
    }

    public Pessoa getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(Pessoa fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getRegistroPatrimonial() {
        return registroPatrimonial;
    }

    public void setRegistroPatrimonial(String registroPatrimonial) {
        this.registroPatrimonial = registroPatrimonial;
    }

}

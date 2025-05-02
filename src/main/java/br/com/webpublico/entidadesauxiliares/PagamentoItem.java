package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 12/06/14
 * Time: 13:57
 * To change this template use File | Settings | File Templates.
 */
public class PagamentoItem {
    private String numeroLiquidacao;
    private String numeroEmpenho;
    private String numeroPagamento;
    private Date dataPagamento;
    private Date dataEmpenho;
    private Date dataLiquidacao;
    private Date dataBaixa;
    private String codigo;
    private BigDecimal valorPagamento;
    private BigDecimal valorRetencao;
    private BigDecimal total;
    private String fornecedor;
    private String tipoEmpenho;
    private String contaCodigo;
    private String contaDescricao;
    private String projetoAtividade;
    private String projetoAtividadeDescricao;
    private String programa;
    private String funcao;
    private String subFuncao;
    private String fonteDescricao;
    private String fonteCodigo;
    private String situacao;
    private String codigoUnidade;
    private String unidade;
    private String orgao;
    private String unidadeGestora;
    private String historicoPagamento;
    private String historicoLiquidacao;
    private String historicoEmpenho;
    private List<PagamentoRetencaoItem> retencoes;
    private String ordemBancaria;
    private String sequenciaOrdem;
    private String situacaoOrdem;
    private String categoriaOrcamentaria;
    private String tipoLancamento;

    public PagamentoItem() {
    }

    public String getNumeroLiquidacao() {
        return numeroLiquidacao;
    }

    public void setNumeroLiquidacao(String numeroLiquidacao) {
        this.numeroLiquidacao = numeroLiquidacao;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
    }

    public String getNumeroPagamento() {
        return numeroPagamento;
    }

    public void setNumeroPagamento(String numeroPagamento) {
        this.numeroPagamento = numeroPagamento;
    }

    public Date getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(Date dataPagamento) {
        this.dataPagamento = dataPagamento;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getValorPagamento() {
        return valorPagamento;
    }

    public void setValorPagamento(BigDecimal valorPagamento) {
        this.valorPagamento = valorPagamento;
    }

    public BigDecimal getValorRetencao() {
        return valorRetencao;
    }

    public void setValorRetencao(BigDecimal valorRetencao) {
        this.valorRetencao = valorRetencao;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public String getFornecedor() {
        return fornecedor;
    }

    public void setFornecedor(String fornecedor) {
        this.fornecedor = fornecedor;
    }

    public String getTipoEmpenho() {
        return tipoEmpenho;
    }

    public void setTipoEmpenho(String tipoEmpenho) {
        this.tipoEmpenho = tipoEmpenho;
    }

    public String getContaCodigo() {
        return contaCodigo;
    }

    public void setContaCodigo(String contaCodigo) {
        this.contaCodigo = contaCodigo;
    }

    public String getContaDescricao() {
        return contaDescricao;
    }

    public void setContaDescricao(String contaDescricao) {
        this.contaDescricao = contaDescricao;
    }

    public String getProjetoAtividade() {
        return projetoAtividade;
    }

    public void setProjetoAtividade(String projetoAtividade) {
        this.projetoAtividade = projetoAtividade;
    }

    public String getProjetoAtividadeDescricao() {
        return projetoAtividadeDescricao;
    }

    public void setProjetoAtividadeDescricao(String projetoAtividadeDescricao) {
        this.projetoAtividadeDescricao = projetoAtividadeDescricao;
    }

    public String getFonteDescricao() {
        return fonteDescricao;
    }

    public void setFonteDescricao(String fonteDescricao) {
        this.fonteDescricao = fonteDescricao;
    }

    public String getFonteCodigo() {
        return fonteCodigo;
    }

    public void setFonteCodigo(String fonteCodigo) {
        this.fonteCodigo = fonteCodigo;
    }

    public String getCodigoUnidade() {
        return codigoUnidade;
    }

    public void setCodigoUnidade(String codigoUnidade) {
        this.codigoUnidade = codigoUnidade;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }

    public String getUnidadeGestora() {
        return unidadeGestora;
    }

    public void setUnidadeGestora(String unidadeGestora) {
        this.unidadeGestora = unidadeGestora;
    }

    public List<PagamentoRetencaoItem> getRetencoes() {
        return retencoes;
    }

    public void setRetencoes(List<PagamentoRetencaoItem> retencoes) {
        this.retencoes = retencoes;
    }

    public Date getDataEmpenho() {
        return dataEmpenho;
    }

    public void setDataEmpenho(Date dataEmpenho) {
        this.dataEmpenho = dataEmpenho;
    }

    public Date getDataLiquidacao() {
        return dataLiquidacao;
    }

    public void setDataLiquidacao(Date dataLiquidacao) {
        this.dataLiquidacao = dataLiquidacao;
    }

    public Date getDataBaixa() {
        return dataBaixa;
    }

    public void setDataBaixa(Date dataBaixa) {
        this.dataBaixa = dataBaixa;
    }

    public String getHistoricoPagamento() {
        return historicoPagamento;
    }

    public void setHistoricoPagamento(String historicoPagamento) {
        this.historicoPagamento = historicoPagamento;
    }

    public String getHistoricoLiquidacao() {
        return historicoLiquidacao;
    }

    public void setHistoricoLiquidacao(String historicoLiquidacao) {
        this.historicoLiquidacao = historicoLiquidacao;
    }

    public String getHistoricoEmpenho() {
        return historicoEmpenho;
    }

    public void setHistoricoEmpenho(String historicoEmpenho) {
        this.historicoEmpenho = historicoEmpenho;
    }

    public String getOrdemBancaria() {
        return ordemBancaria;
    }

    public void setOrdemBancaria(String ordemBancaria) {
        this.ordemBancaria = ordemBancaria;
    }

    public String getSequenciaOrdem() {
        return sequenciaOrdem;
    }

    public void setSequenciaOrdem(String sequenciaOrdem) {
        this.sequenciaOrdem = sequenciaOrdem;
    }

    public String getSituacaoOrdem() {
        return situacaoOrdem;
    }

    public void setSituacaoOrdem(String situacaoOrdem) {
        this.situacaoOrdem = situacaoOrdem;
    }

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(String subFuncao) {
        this.subFuncao = subFuncao;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public String getCategoriaOrcamentaria() {
        return categoriaOrcamentaria;
    }

    public void setCategoriaOrcamentaria(String categoriaOrcamentaria) {
        this.categoriaOrcamentaria = categoriaOrcamentaria;
    }

    public String getTipoLancamento() {
        return tipoLancamento;
    }

    public void setTipoLancamento(String tipoLancamento) {
        this.tipoLancamento = tipoLancamento;
    }
}

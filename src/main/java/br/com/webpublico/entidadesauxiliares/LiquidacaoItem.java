package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: usuario
 * Date: 11/06/14
 * Time: 15:22
 * To change this template use File | Settings | File Templates.
 */
public class LiquidacaoItem {

    private Long idLiquidacao;
    private String numeroLiquidacao;
    private Date dataLiquidacao;
    private String codigo;
    private BigDecimal valorLiquidacao;
    private String numeroEmpenho;
    private Date dataEmpenho;
    private String fornecedor;
    private String tipoEmpenho;
    private String codigoConta;
    private String descricaoConta;
    private String projetoAtividade;
    private String descricaoProjetoAtividade;
    private String fonte;
    private String descricaoFonte;
    private String codigoUnidade;
    private String unidade;
    private String orgao;
    private String unidadeGestora;
    private String historico;
    private List<LiquidacaoDoctoComprobatorio> documentos;
    private List<LiquidacaoDetalhamento> detalhamentos;

    public LiquidacaoItem() {
    }

    public String getNumeroLiquidacao() {
        return numeroLiquidacao;
    }

    public void setNumeroLiquidacao(String numeroLiquidacao) {
        this.numeroLiquidacao = numeroLiquidacao;
    }

    public Date getDataLiquidacao() {
        return dataLiquidacao;
    }

    public void setDataLiquidacao(Date dataLiquidacao) {
        this.dataLiquidacao = dataLiquidacao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public BigDecimal getValorLiquidacao() {
        return valorLiquidacao;
    }

    public void setValorLiquidacao(BigDecimal valorLiquidacao) {
        this.valorLiquidacao = valorLiquidacao;
    }

    public String getNumeroEmpenho() {
        return numeroEmpenho;
    }

    public void setNumeroEmpenho(String numeroEmpenho) {
        this.numeroEmpenho = numeroEmpenho;
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

    public String getCodigoConta() {
        return codigoConta;
    }

    public void setCodigoConta(String codigoConta) {
        this.codigoConta = codigoConta;
    }

    public String getDescricaoConta() {
        return descricaoConta;
    }

    public void setDescricaoConta(String descricaoConta) {
        this.descricaoConta = descricaoConta;
    }

    public String getProjetoAtividade() {
        return projetoAtividade;
    }

    public void setProjetoAtividade(String projetoAtividade) {
        this.projetoAtividade = projetoAtividade;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
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

    public String getDescricaoFonte() {
        return descricaoFonte;
    }

    public void setDescricaoFonte(String descricaoFonte) {
        this.descricaoFonte = descricaoFonte;
    }

    public String getDescricaoProjetoAtividade() {
        return descricaoProjetoAtividade;
    }

    public void setDescricaoProjetoAtividade(String descricaoProjetoAtividade) {
        this.descricaoProjetoAtividade = descricaoProjetoAtividade;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public List<LiquidacaoDoctoComprobatorio> getDocumentos() {
        return documentos;
    }

    public void setDocumentos(List<LiquidacaoDoctoComprobatorio> documentos) {
        this.documentos = documentos;
    }

    public Long getIdLiquidacao() {
        return idLiquidacao;
    }

    public void setIdLiquidacao(Long idLiquidacao) {
        this.idLiquidacao = idLiquidacao;
    }

    public Date getDataEmpenho() {
        return dataEmpenho;
    }

    public void setDataEmpenho(Date dataEmpenho) {
        this.dataEmpenho = dataEmpenho;
    }

    public List<LiquidacaoDetalhamento> getDetalhamentos() {
        return detalhamentos;
    }

    public void setDetalhamentos(List<LiquidacaoDetalhamento> detalhamentos) {
        this.detalhamentos = detalhamentos;
    }
}

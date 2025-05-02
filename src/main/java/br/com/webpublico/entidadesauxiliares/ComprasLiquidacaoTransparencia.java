package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mateus on 16/12/2014.
 */
public class ComprasLiquidacaoTransparencia {
    private BigDecimal quantidade;
    private Date dataLiquidacao;
    private String numeroLiquidacao;
    private String numeroEmpenho;
    private String funcional;
    private String contaCodigo;
    private String fonte;
    private String pessoa;
    private BigDecimal valorLiquidacao;
    private String historicoNota;
    private String unidade;
    private String orgao;
    private List<ComprasDetalhamentoTransparencia> detalhamentos;
    private List<ComprasDoctoTransparencia> doctos;

    public ComprasLiquidacaoTransparencia() {
        detalhamentos = new ArrayList<>();
        doctos = new ArrayList<>();
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public Date getDataLiquidacao() {
        return dataLiquidacao;
    }

    public void setDataLiquidacao(Date dataLiquidacao) {
        this.dataLiquidacao = dataLiquidacao;
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

    public String getFuncional() {
        return funcional;
    }

    public void setFuncional(String funcional) {
        this.funcional = funcional;
    }

    public String getContaCodigo() {
        return contaCodigo;
    }

    public void setContaCodigo(String contaCodigo) {
        this.contaCodigo = contaCodigo;
    }

    public String getFonte() {
        return fonte;
    }

    public void setFonte(String fonte) {
        this.fonte = fonte;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public BigDecimal getValorLiquidacao() {
        return valorLiquidacao;
    }

    public void setValorLiquidacao(BigDecimal valorLiquidacao) {
        this.valorLiquidacao = valorLiquidacao;
    }

    public String getHistoricoNota() {
        return historicoNota;
    }

    public void setHistoricoNota(String historicoNota) {
        this.historicoNota = historicoNota;
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

    public List<ComprasDetalhamentoTransparencia> getDetalhamentos() {
        return detalhamentos;
    }

    public void setDetalhamentos(List<ComprasDetalhamentoTransparencia> detalhamentos) {
        this.detalhamentos = detalhamentos;
    }

    public List<ComprasDoctoTransparencia> getDoctos() {
        return doctos;
    }

    public void setDoctos(List<ComprasDoctoTransparencia> doctos) {
        this.doctos = doctos;
    }
}

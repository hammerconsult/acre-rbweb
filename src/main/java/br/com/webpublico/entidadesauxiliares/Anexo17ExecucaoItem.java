package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoContaExtraorcamentaria;

import java.math.BigDecimal;

/**
 * Created by Mateus on 13/11/2014.
 */
public class Anexo17ExecucaoItem {
    private String descricao;
    private TipoContaExtraorcamentaria tipoContaExtraorcamentaria;
    private BigDecimal saldoAnterior;
    private BigDecimal inscricao;
    private BigDecimal incorporacao;
    private BigDecimal pagamento;
    private BigDecimal baixa;
    private BigDecimal saldoAtual;
    private BigDecimal valor;

    public Anexo17ExecucaoItem() {
        saldoAnterior = BigDecimal.ZERO;
        inscricao = BigDecimal.ZERO;
        incorporacao = BigDecimal.ZERO;
        pagamento = BigDecimal.ZERO;
        baixa = BigDecimal.ZERO;
        saldoAtual = BigDecimal.ZERO;
        valor = BigDecimal.ZERO;
    }

    public TipoContaExtraorcamentaria getTipoContaExtraorcamentaria() {
        return tipoContaExtraorcamentaria;
    }

    public void setTipoContaExtraorcamentaria(TipoContaExtraorcamentaria tipoContaExtraorcamentaria) {
        this.tipoContaExtraorcamentaria = tipoContaExtraorcamentaria;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }

    public BigDecimal getInscricao() {
        return inscricao;
    }

    public void setInscricao(BigDecimal inscricao) {
        this.inscricao = inscricao;
    }

    public BigDecimal getIncorporacao() {
        return incorporacao;
    }

    public void setIncorporacao(BigDecimal incorporacao) {
        this.incorporacao = incorporacao;
    }

    public BigDecimal getPagamento() {
        return pagamento;
    }

    public void setPagamento(BigDecimal pagamento) {
        this.pagamento = pagamento;
    }

    public BigDecimal getBaixa() {
        return baixa;
    }

    public void setBaixa(BigDecimal baixa) {
        this.baixa = baixa;
    }

    public BigDecimal getSaldoAtual() {
        return saldoAtual;
    }

    public void setSaldoAtual(BigDecimal saldoAtual) {
        this.saldoAtual = saldoAtual;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }
}

package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Mateus on 17/08/2015.
 */
public class ReceitaExtraXDespesaExtra {
    private Date data;
    private String numero;
    private String tipo;
    private String pessoa;
    private String contaExtra;
    private String fonteDeRecursos;
    private String historico;
    private BigDecimal saldoAnterior;
    private BigDecimal inscricao;
    private BigDecimal baixa;
    private BigDecimal saldo;
    private List<ReceitaExtraXDespesaExtra> despesas;

    public ReceitaExtraXDespesaExtra() {
        despesas = new ArrayList<>();
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getPessoa() {
        return pessoa;
    }

    public void setPessoa(String pessoa) {
        this.pessoa = pessoa;
    }

    public String getContaExtra() {
        return contaExtra;
    }

    public void setContaExtra(String contaExtra) {
        this.contaExtra = contaExtra;
    }

    public String getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(String fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public BigDecimal getInscricao() {
        return inscricao;
    }

    public void setInscricao(BigDecimal inscricao) {
        this.inscricao = inscricao;
    }

    public BigDecimal getBaixa() {
        return baixa;
    }

    public void setBaixa(BigDecimal baixa) {
        this.baixa = baixa;
    }

    public BigDecimal getSaldo() {
        saldo = inscricao.subtract(baixa);
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public List<ReceitaExtraXDespesaExtra> getDespesas() {
        return despesas;
    }

    public void setDespesas(List<ReceitaExtraXDespesaExtra> despesas) {
        this.despesas = despesas;
    }

    public BigDecimal getSaldoAnterior() {
        return saldoAnterior;
    }

    public void setSaldoAnterior(BigDecimal saldoAnterior) {
        this.saldoAnterior = saldoAnterior;
    }
}

package br.com.webpublico.entidadesauxiliares.contabil.relatoriosaldofinanceiro;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlos on 15/03/17.
 */
public class ContaFinanceiraDoSaldoFinanceiroVO {
    private BigDecimal id;
    private BigDecimal reg;
    private String finan;
    private BigDecimal soma;
    private String vwOrg;
    private BigDecimal vwId;
    private BigDecimal conta;
    private List<FonteDeRecursoDoSaldoFinanceiroVO> fonteDeRecursoDoSaldoFinanceiroVOs;

    public ContaFinanceiraDoSaldoFinanceiroVO() {
        fonteDeRecursoDoSaldoFinanceiroVOs = new ArrayList<>();
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getReg() {
        return reg;
    }

    public void setReg(BigDecimal reg) {
        this.reg = reg;
    }

    public String getFinan() {
        return finan;
    }

    public void setFinan(String finan) {
        this.finan = finan;
    }

    public BigDecimal getSoma() {
        return soma;
    }

    public void setSoma(BigDecimal soma) {
        this.soma = soma;
    }

    public String getVwOrg() {
        return vwOrg;
    }

    public void setVwOrg(String vwOrg) {
        this.vwOrg = vwOrg;
    }

    public BigDecimal getVwId() {
        return vwId;
    }

    public void setVwId(BigDecimal vwId) {
        this.vwId = vwId;
    }

    public List<FonteDeRecursoDoSaldoFinanceiroVO> getFonteDeRecursoDoSaldoFinanceiroVOs() {
        return fonteDeRecursoDoSaldoFinanceiroVOs;
    }

    public void setFonteDeRecursoDoSaldoFinanceiroVOs(List<FonteDeRecursoDoSaldoFinanceiroVO> fonteDeRecursoDoSaldoFinanceiroVOs) {
        this.fonteDeRecursoDoSaldoFinanceiroVOs = fonteDeRecursoDoSaldoFinanceiroVOs;
    }

    public BigDecimal getConta() {
        return conta;
    }

    public void setConta(BigDecimal conta) {
        this.conta = conta;
    }
}

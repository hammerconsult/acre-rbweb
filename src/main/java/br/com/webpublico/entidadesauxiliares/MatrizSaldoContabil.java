package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoMatrizSaldoContabil;

import java.math.BigDecimal;

public class MatrizSaldoContabil {

    private String contaContabilSiconfi;
    private String ic1;
    private String tipo1;
    private String ic2;
    private String tipo2;
    private String ic3;
    private String tipo3;
    private String ic4;
    private String tipo4;
    private String ic5;
    private String tipo5;
    private String ic6;
    private String tipo6;
    private String ic7;
    private String tipo7;
    private BigDecimal valor;
    private TipoMatrizSaldoContabil tipoValor;
    private String naturezaValor;

    public MatrizSaldoContabil() {
        valor = BigDecimal.ZERO;
    }

    public String getContaContabilSiconfi() {
        return contaContabilSiconfi;
    }

    public void setContaContabilSiconfi(String contaContabilSiconfi) {
        this.contaContabilSiconfi = contaContabilSiconfi;
    }

    public String getIc1() {
        return ic1;
    }

    public void setIc1(String ic1) {
        this.ic1 = ic1;
    }

    public String getTipo1() {
        return tipo1;
    }

    public void setTipo1(String tipo1) {
        this.tipo1 = tipo1;
    }

    public String getIc2() {
        return ic2;
    }

    public void setIc2(String ic2) {
        this.ic2 = ic2;
    }

    public String getTipo2() {
        return tipo2;
    }

    public void setTipo2(String tipo2) {
        this.tipo2 = tipo2;
    }

    public String getIc3() {
        return ic3;
    }

    public void setIc3(String ic3) {
        this.ic3 = ic3;
    }

    public String getTipo3() {
        return tipo3;
    }

    public void setTipo3(String tipo3) {
        this.tipo3 = tipo3;
    }

    public String getIc4() {
        return ic4;
    }

    public void setIc4(String ic4) {
        this.ic4 = ic4;
    }

    public String getTipo4() {
        return tipo4;
    }

    public void setTipo4(String tipo4) {
        this.tipo4 = tipo4;
    }

    public String getIc5() {
        return ic5;
    }

    public void setIc5(String ic5) {
        this.ic5 = ic5;
    }

    public String getTipo5() {
        return tipo5;
    }

    public void setTipo5(String tipo5) {
        this.tipo5 = tipo5;
    }

    public String getIc6() {
        return ic6;
    }

    public void setIc6(String ic6) {
        this.ic6 = ic6;
    }

    public String getTipo6() {
        return tipo6;
    }

    public void setTipo6(String tipo6) {
        this.tipo6 = tipo6;
    }

    public String getIc7() {
        return ic7;
    }

    public void setIc7(String ic7) {
        this.ic7 = ic7;
    }

    public String getTipo7() {
        return tipo7;
    }

    public void setTipo7(String tipo7) {
        this.tipo7 = tipo7;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoMatrizSaldoContabil getTipoValor() {
        return tipoValor;
    }

    public void setTipoValor(TipoMatrizSaldoContabil tipoValor) {
        this.tipoValor = tipoValor;
    }

    public String getNaturezaValor() {
        return naturezaValor;
    }

    public void setNaturezaValor(String naturezaValor) {
        this.naturezaValor = naturezaValor;
    }
}

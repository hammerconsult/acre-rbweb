package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

public class RecolhimentoFundoPrevMatricula {

    private String matricula;
    private String numero;
    private String servidor;
    private String pisPasep;
    private String cpf;
    private BigDecimal base;
    private BigDecimal valor;
    private Integer quantidadeSegurados;
    private BigDecimal remuneracaoBruta;
    private BigDecimal percentual;

    public RecolhimentoFundoPrevMatricula() {
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public String getPisPasep() {
        return pisPasep;
    }

    public void setPisPasep(String pisPasep) {
        this.pisPasep = pisPasep;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public BigDecimal getBase() {
        return base;
    }

    public void setBase(BigDecimal base) {
        this.base = base;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getQuantidadeSegurados() {
        return quantidadeSegurados;
    }

    public void setQuantidadeSegurados(Integer quantidadeSegurados) {
        this.quantidadeSegurados = quantidadeSegurados;
    }

    public BigDecimal getRemuneracaoBruta() {
        return remuneracaoBruta;
    }

    public void setRemuneracaoBruta(BigDecimal remuneracaoBruta) {
        this.remuneracaoBruta = remuneracaoBruta;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }
}

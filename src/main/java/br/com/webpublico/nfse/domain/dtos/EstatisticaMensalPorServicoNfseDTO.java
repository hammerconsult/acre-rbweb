package br.com.webpublico.nfse.domain.dtos;

import java.math.BigDecimal;

public class EstatisticaMensalPorServicoNfseDTO implements NfseDTO {

    private Integer mes;
    private Integer ano;
    private String codigo;
    private String nome;
    private BigDecimal valor;

    public EstatisticaMensalPorServicoNfseDTO(Integer mes, Integer ano) {
        this();
        this.mes = mes;
        this.ano = ano;
    }

    public EstatisticaMensalPorServicoNfseDTO() {
        this.valor = BigDecimal.ZERO;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }
}

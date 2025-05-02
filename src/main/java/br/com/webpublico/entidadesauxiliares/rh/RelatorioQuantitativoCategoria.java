package br.com.webpublico.entidadesauxiliares.rh;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author peixe on 03/12/2015  11:34.
 */
public class RelatorioQuantitativoCategoria implements Serializable {

    private String matricula;
    private String contrato;
    private String nome;

    private String descricao;
    private Integer quantidade;
    private BigDecimal valor;
    private String orgao;

    public RelatorioQuantitativoCategoria() {
        quantidade = 0;
        valor = BigDecimal.ZERO;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getContrato() {
        return contrato;
    }

    public void setContrato(String contrato) {
        this.contrato = contrato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getOrgao() {
        return orgao;
    }

    public void setOrgao(String orgao) {
        this.orgao = orgao;
    }
}

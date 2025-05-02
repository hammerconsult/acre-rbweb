package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 16/12/2014.
 */
public class ReceitaTransparencia {
    private String codigo;
    private String descricao;
    private BigDecimal orcadaInicial;
    private BigDecimal orcadaAtual;
    private BigDecimal arrecadado;
    private BigDecimal nivel;

    public ReceitaTransparencia() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getOrcadaInicial() {
        return orcadaInicial;
    }

    public void setOrcadaInicial(BigDecimal orcadaInicial) {
        this.orcadaInicial = orcadaInicial;
    }

    public BigDecimal getOrcadaAtual() {
        return orcadaAtual;
    }

    public void setOrcadaAtual(BigDecimal orcadaAtual) {
        this.orcadaAtual = orcadaAtual;
    }

    public BigDecimal getArrecadado() {
        return arrecadado;
    }

    public void setArrecadado(BigDecimal arrecadado) {
        this.arrecadado = arrecadado;
    }

    public BigDecimal getNivel() {
        return nivel;
    }

    public void setNivel(BigDecimal nivel) {
        this.nivel = nivel;
    }
}

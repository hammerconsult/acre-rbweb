package br.com.webpublico.entidadesauxiliares;

import java.math.BigDecimal;

/**
 * Created by Mateus on 19/11/2014.
 */
public class Lei4320ExecucaoAnexo18 {

    private String descricao;
    private BigDecimal valorColuna1;
    private BigDecimal valorColuna2;
    private Integer ordem;

    public Lei4320ExecucaoAnexo18() {
        valorColuna1 = BigDecimal.ZERO;
        valorColuna2 = BigDecimal.ZERO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public BigDecimal getValorColuna1() {
        return valorColuna1;
    }

    public void setValorColuna1(BigDecimal valorColuna1) {
        this.valorColuna1 = valorColuna1;
    }

    public BigDecimal getValorColuna2() {
        return valorColuna2;
    }

    public void setValorColuna2(BigDecimal valorColuna2) {
        this.valorColuna2 = valorColuna2;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }
}

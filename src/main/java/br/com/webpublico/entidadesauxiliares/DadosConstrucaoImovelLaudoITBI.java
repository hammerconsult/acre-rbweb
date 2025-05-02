package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.math.BigDecimal;

public class DadosConstrucaoImovelLaudoITBI implements Serializable {
    private String descricao;
    private String atributo;

    private BigDecimal areaConstruida;

    public DadosConstrucaoImovelLaudoITBI() {
        this.areaConstruida = BigDecimal.ZERO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getAtributo() {
        return atributo;
    }

    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }

    public BigDecimal getAreaConstruida() {
        return areaConstruida;
    }

    public void setAreaConstruida(BigDecimal areaConstruida) {
        this.areaConstruida = areaConstruida;
    }
}

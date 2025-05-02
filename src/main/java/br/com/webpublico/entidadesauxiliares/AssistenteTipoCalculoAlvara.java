package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoCalculoAlvara;
import br.com.webpublico.enums.TipoIdentificaoCalculoAlvara;

import java.io.Serializable;
import java.math.BigDecimal;

public class AssistenteTipoCalculoAlvara implements Serializable {
    private TipoCalculoAlvara tipoCalculo;
    private TipoIdentificaoCalculoAlvara identificacao;
    private BigDecimal valorTotalAlvara;

    public AssistenteTipoCalculoAlvara() {
        this.valorTotalAlvara = BigDecimal.ZERO;
    }

    public TipoCalculoAlvara getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(TipoCalculoAlvara tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }

    public TipoIdentificaoCalculoAlvara getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(TipoIdentificaoCalculoAlvara identificacao) {
        this.identificacao = identificacao;
    }

    public BigDecimal getValorTotalAlvara() {
        return valorTotalAlvara;
    }

    public void setValorTotalAlvara(BigDecimal valorTotalAlvara) {
        this.valorTotalAlvara = valorTotalAlvara;
    }
}

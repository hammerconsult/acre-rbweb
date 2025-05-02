package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.SituacaoParcela;
import com.google.common.base.Objects;

import java.io.Serializable;
import java.math.BigDecimal;

public class VOSituacaoParcela implements Serializable {
    private Long idParcela;
    private String referencia;
    private SituacaoParcela situacaoParcela;
    private BigDecimal saldo;

    public VOSituacaoParcela() {
    }

    public VOSituacaoParcela(Long idParcela, String referencia, SituacaoParcela situacaoParcela, BigDecimal saldo) {
        this.idParcela = idParcela;
        this.referencia = referencia;
        this.situacaoParcela = situacaoParcela;
        this.saldo = saldo;
    }

    public Long getIdParcela() {
        return idParcela;
    }

    public void setIdParcela(Long idParcela) {
        this.idParcela = idParcela;
    }

    public String getReferencia() {
        return referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public SituacaoParcela getSituacaoParcela() {
        return situacaoParcela;
    }

    public void setSituacaoParcela(SituacaoParcela situacaoParcela) {
        this.situacaoParcela = situacaoParcela;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VOSituacaoParcela that = (VOSituacaoParcela) o;
        return Objects.equal(idParcela, that.idParcela);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idParcela);
    }
}

package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.util.DataUtil;

import java.util.Date;
import java.util.Objects;

public class AgrupadorValorConta {

    private Long idLoteBaixa;
    private Date data;
    private Long idConta;

    public AgrupadorValorConta(Long idLoteBaixa, Date data, Long idConta) {
        this.idLoteBaixa = idLoteBaixa;
        this.data = data;
        this.idConta = idConta;
    }

    public Long getIdLoteBaixa() {
        return idLoteBaixa;
    }

    public Date getData() {
        return data;
    }

    public Long getIdConta() {
        return idConta;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AgrupadorValorConta that = (AgrupadorValorConta) o;
        return Objects.equals(idLoteBaixa, that.idLoteBaixa) &&
            Objects.equals(data, that.data) &&
            Objects.equals(idConta, that.idConta);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idLoteBaixa, data, idConta);
    }

    @Override
    public String toString() {
        return idLoteBaixa + " " + DataUtil.getDataFormatada(data) + " Conta: " + idConta;
    }
}

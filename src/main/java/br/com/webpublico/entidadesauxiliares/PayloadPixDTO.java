package br.com.webpublico.entidadesauxiliares;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Objects;
import com.google.common.collect.Lists;

import java.io.Serializable;
import java.util.List;

public class PayloadPixDTO implements Serializable {
    private String txid;
    private String endToEndId;
    private String valor;
    private String horario;
    private String infoPagador;

    @JsonIgnore
    private List<Long> idsDam;

    public PayloadPixDTO() {
    }

    public String getTxid() {
        return txid;
    }

    public void setTxid(String txid) {
        this.txid = txid;
    }

    public String getEndToEndId() {
        return endToEndId;
    }

    public void setEndToEndId(String endToEndId) {
        this.endToEndId = endToEndId;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public String getInfoPagador() {
        return infoPagador;
    }

    public void setInfoPagador(String infoPagador) {
        this.infoPagador = infoPagador;
    }

    @JsonIgnore
    public void inicializarDansDependentes() {
        this.idsDam = Lists.newArrayList();
    }

    public List<Long> getIdsDam() {
        return idsDam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PayloadPixDTO that = (PayloadPixDTO) o;
        return Objects.equal(txid, that.txid);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(txid);
    }
}

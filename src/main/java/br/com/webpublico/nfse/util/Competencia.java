package br.com.webpublico.nfse.util;

import java.util.Objects;

public class Competencia {
    private Long prestadorId;
    private Integer mes;
    private Integer ano;

    public Competencia(Integer mes, Integer ano, Long prestadorId) {
        this.mes = mes;
        this.ano = ano;
        this.prestadorId = prestadorId;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Long getPrestadorId() {
        return prestadorId;
    }

    public void setPrestadorId(Long prestadorId) {
        this.prestadorId = prestadorId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Competencia that = (Competencia) o;
        return Objects.equals(prestadorId, that.prestadorId) &&
            Objects.equals(mes, that.mes) &&
            Objects.equals(ano, that.ano);
    }

    @Override
    public int hashCode() {
        return Objects.hash(prestadorId, mes, ano);
    }
}

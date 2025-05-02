package br.com.webpublico.entidadesauxiliares.rh.auditoria;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;

import java.io.Serializable;

public class ObjetoAuditoriaRH implements Serializable, Comparable<ObjetoAuditoriaRH> {
    private String chave;
    private Integer posicao;

    public ObjetoAuditoriaRH(String chave, Integer posicao) {
        this.chave = chave;
        this.posicao = posicao;
    }

    public ObjetoAuditoriaRH(Integer posicao) {
        this.posicao = posicao;
    }

    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public Integer getPosicao() {
        return posicao;
    }

    public void setPosicao(Integer posicao) {
        this.posicao = posicao;
    }

    @Override
    public String toString() {
        return chave != null ? chave : "";
    }

    @Override
    public int compareTo(ObjetoAuditoriaRH o) {
        return ComparisonChain.start().compare(this.getPosicao(), o.getPosicao()).result();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjetoAuditoriaRH that = (ObjetoAuditoriaRH) o;
        return Objects.equal(chave, that.chave);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(chave);
    }
}

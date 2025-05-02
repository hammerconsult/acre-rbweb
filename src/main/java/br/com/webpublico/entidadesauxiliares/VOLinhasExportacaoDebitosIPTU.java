package br.com.webpublico.entidadesauxiliares;

import com.google.common.base.Objects;

import java.io.Serializable;

public class VOLinhasExportacaoDebitosIPTU implements Serializable {
    private Long idExportacao;
    private Long indice;
    private String linha;

    public VOLinhasExportacaoDebitosIPTU(Long idExportacao, Long indice, String linha) {
        this.idExportacao = idExportacao;
        this.indice = indice;
        this.linha = linha;
    }

    public Long getIdExportacao() {
        return idExportacao;
    }

    public void setIdExportacao(Long idExportacao) {
        this.idExportacao = idExportacao;
    }

    public Long getIndice() {
        return indice;
    }

    public void setIndice(Long indice) {
        this.indice = indice;
    }

    public String getLinha() {
        return linha;
    }

    public void setLinha(String linha) {
        this.linha = linha;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VOLinhasExportacaoDebitosIPTU that = (VOLinhasExportacaoDebitosIPTU) o;
        return Objects.equal(idExportacao, that.idExportacao) && Objects.equal(indice, that.indice);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(idExportacao, indice);
    }
}

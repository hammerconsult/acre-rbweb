package br.com.webpublico.entidades.rh.avaliacao;

import java.util.Date;

public class AvaliacaoVinculoDTO {

    private Long id;
    private Long idAvaliacao;

    private String vinculoFP;

    private Date dataInicioExecucao;

    private Date dataTerminoExecucao;

    public Long getIdAvaliacao() {
        return idAvaliacao;
    }

    public void setIdAvaliacao(Long idAvaliacao) {
        this.idAvaliacao = idAvaliacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(String vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public Date getDataInicioExecucao() {
        return dataInicioExecucao;
    }

    public void setDataInicioExecucao(Date dataInicioExecucao) {
        this.dataInicioExecucao = dataInicioExecucao;
    }

    public Date getDataTerminoExecucao() {
        return dataTerminoExecucao;
    }

    public void setDataTerminoExecucao(Date dataTerminoExecucao) {
        this.dataTerminoExecucao = dataTerminoExecucao;
    }
}

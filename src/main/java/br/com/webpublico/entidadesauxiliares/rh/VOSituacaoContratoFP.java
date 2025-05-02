package br.com.webpublico.entidadesauxiliares.rh;

import com.google.common.base.Objects;

import java.io.Serializable;
import java.util.Date;

public class VOSituacaoContratoFP implements Serializable {
    private Long id;
    private String matricula;
    private String nome;
    private String situacao;
    private Date dataInicioContrato;
    private Date dataFinalContrato;
    private Date dataInicioSituacao;
    private Date dataFinalSituacao;

    public VOSituacaoContratoFP() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }

    public Date getDataInicioContrato() {
        return dataInicioContrato;
    }

    public void setDataInicioContrato(Date dataInicioContrato) {
        this.dataInicioContrato = dataInicioContrato;
    }

    public Date getDataFinalContrato() {
        return dataFinalContrato;
    }

    public void setDataFinalContrato(Date dataFinalContrato) {
        this.dataFinalContrato = dataFinalContrato;
    }

    public Date getDataInicioSituacao() {
        return dataInicioSituacao;
    }

    public void setDataInicioSituacao(Date dataInicioSituacao) {
        this.dataInicioSituacao = dataInicioSituacao;
    }

    public Date getDataFinalSituacao() {
        return dataFinalSituacao;
    }

    public void setDataFinalSituacao(Date dataFinalSituacao) {
        this.dataFinalSituacao = dataFinalSituacao;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        VOSituacaoContratoFP that = (VOSituacaoContratoFP) o;
        return Objects.equal(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

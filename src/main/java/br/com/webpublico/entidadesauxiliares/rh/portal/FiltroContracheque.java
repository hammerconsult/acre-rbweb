package br.com.webpublico.entidadesauxiliares.rh.portal;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Mailson on 18/02/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class FiltroContracheque implements Serializable {

    private String matricula;
    private String cpf;

    public FiltroContracheque() {
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }
}

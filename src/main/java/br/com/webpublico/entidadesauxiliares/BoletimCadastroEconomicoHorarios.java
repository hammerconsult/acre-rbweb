package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;

public class BoletimCadastroEconomicoHorarios implements Serializable {
    private String descricaoHorario;

    public BoletimCadastroEconomicoHorarios() {
    }

    public String getDescricaoHorario() {
        return descricaoHorario;
    }

    public void setDescricaoHorario(String descricaoHorario) {
        this.descricaoHorario = descricaoHorario;
    }
}

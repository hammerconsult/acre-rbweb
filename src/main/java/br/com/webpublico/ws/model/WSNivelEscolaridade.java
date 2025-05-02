package br.com.webpublico.ws.model;

import br.com.webpublico.entidades.GrauEscolaridadeSiprev;
import br.com.webpublico.enums.GrauEscolaridadeDependente;
import br.com.webpublico.enums.GrauInstrucaoCAGED;

import java.io.Serializable;

/**
 * Created by Buzatto on 06/03/2017.
 */
public class WSNivelEscolaridade implements Serializable {

    private String descricao;
    protected GrauEscolaridadeDependente grauEscolaridadeDependente;
    protected GrauEscolaridadeSiprev grauEscolaridadeSiprev;
    protected GrauInstrucaoCAGED grauInstrucaoCAGED;
    protected Long id;
    protected Long ordem;

    public WSNivelEscolaridade() {
    }

    public WSNivelEscolaridade(String descricao, Long id) {
        this.descricao = descricao;
        this.id = id;
    }

    public WSNivelEscolaridade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public GrauEscolaridadeDependente getGrauEscolaridadeDependente() {
        return grauEscolaridadeDependente;
    }

    public void setGrauEscolaridadeDependente(GrauEscolaridadeDependente grauEscolaridadeDependente) {
        this.grauEscolaridadeDependente = grauEscolaridadeDependente;
    }

    public GrauEscolaridadeSiprev getGrauEscolaridadeSiprev() {
        return grauEscolaridadeSiprev;
    }

    public void setGrauEscolaridadeSiprev(GrauEscolaridadeSiprev grauEscolaridadeSiprev) {
        this.grauEscolaridadeSiprev = grauEscolaridadeSiprev;
    }

    public GrauInstrucaoCAGED getGrauInstrucaoCAGED() {
        return grauInstrucaoCAGED;
    }

    public void setGrauInstrucaoCAGED(GrauInstrucaoCAGED grauInstrucaoCAGED) {
        this.grauInstrucaoCAGED = grauInstrucaoCAGED;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrdem() {
        return ordem;
    }

    public void setOrdem(Long ordem) {
        this.ordem = ordem;
    }

    @Override
    public String toString() {
        return "WSNivelEscolaridade{" +
            "descricao='" + descricao + '\'' +
            ", id=" + id +
            '}';
    }
}

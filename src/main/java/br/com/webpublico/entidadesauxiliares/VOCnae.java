package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.CNAE;

import java.io.Serializable;

public class VOCnae implements Serializable {
    private Long id;
    private String codigoCnae;
    private String descricaoDetalhada;
    private String grauDeRiscoCnae;
    private Boolean adicionado;

    public VOCnae() {
        this.adicionado = Boolean.FALSE;
    }

    public VOCnae(CNAE cnae, Boolean adicionado) {
        this.id = cnae.getId();
        this.codigoCnae = cnae.getCodigoCnae();
        this.descricaoDetalhada = cnae.getDescricaoDetalhada();
        this.grauDeRiscoCnae = cnae.getGrauDeRisco() != null ? cnae.getGrauDeRisco().getDescricao() : null;
        this.adicionado = adicionado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigoCnae() {
        return codigoCnae;
    }

    public void setCodigoCnae(String codigoCnae) {
        this.codigoCnae = codigoCnae;
    }

    public String getDescricaoDetalhada() {
        return descricaoDetalhada;
    }

    public void setDescricaoDetalhada(String descricaoDetalhada) {
        this.descricaoDetalhada = descricaoDetalhada;
    }

    public String getGrauDeRiscoCnae() {
        return grauDeRiscoCnae;
    }

    public void setGrauDeRiscoCnae(String grauDeRiscoCnae) {
        this.grauDeRiscoCnae = grauDeRiscoCnae;
    }

    public Boolean getAdicionado() {
        return adicionado;
    }

    public void setAdicionado(Boolean adicionado) {
        this.adicionado = adicionado;
    }
}

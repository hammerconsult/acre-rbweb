package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.entidades.EscritorioContabil;

import java.io.Serializable;

public class EscritorioContabilDTO implements Serializable {
    private Long id;
    private String nomeEscritorio;
    private String nomeContador;

    public EscritorioContabilDTO() {
    }

    public EscritorioContabilDTO(EscritorioContabil escritorioContabil) {
        this.id = escritorioContabil.getId();

        // Nome do escritório
        if (escritorioContabil.getNomeEscritorio() != null && escritorioContabil.getNomeEscritorio().trim().length() >= 0) {
            this.nomeEscritorio = escritorioContabil.getNomeEscritorio();
        } else if (escritorioContabil.getPessoa() != null) {
            this.nomeEscritorio = escritorioContabil.getPessoa().getNomeFantasia();
        }

        // Nome do contador
        if (escritorioContabil.getNomeContador() != null && escritorioContabil.getNomeContador().trim().length() >= 0) {
            this.nomeContador = escritorioContabil.getNomeContador();
        } else if (escritorioContabil.getResponsavel() != null) {
            this.nomeContador = escritorioContabil.getResponsavel().getNome();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomeEscritorio() {
        return nomeEscritorio;
    }

    public void setNomeEscritorio(String nomeEscritorio) {
        this.nomeEscritorio = nomeEscritorio;
    }

    public String getNomeContador() {
        return nomeContador;
    }

    public void setNomeContador(String nomeContador) {
        this.nomeContador = nomeContador;
    }
}

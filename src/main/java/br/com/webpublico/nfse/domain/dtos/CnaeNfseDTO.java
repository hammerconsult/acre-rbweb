package br.com.webpublico.nfse.domain.dtos;

import java.io.Serializable;

public class CnaeNfseDTO implements Serializable, NfseDTO {

    private Long id;
    private String codigo;
    private String descricao;
    private Boolean ativo;
    private Boolean exclusivoSimplesNacional;

    public CnaeNfseDTO() {
    }

    public CnaeNfseDTO(Long id, String codigo, String descricao, Boolean ativo, Boolean exclusivoSimplesNacional) {
        this.id = id;
        this.codigo = codigo;
        this.descricao = descricao;
        this.ativo = ativo;
        this.exclusivoSimplesNacional = exclusivoSimplesNacional;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public Boolean getExclusivoSimplesNacional() {
        return exclusivoSimplesNacional;
    }

    public void setExclusivoSimplesNacional(Boolean exclusivoSimplesNacional) {
        this.exclusivoSimplesNacional = exclusivoSimplesNacional;
    }

    @Override
    public String toString() {
        return "CnaeSearchDTO{" +
            "id=" + id +
            ", codigo='" + codigo + '\'' +
            ", descricao='" + descricao + '\'' +
            ", ativo=" + ativo +
            '}';
    }
}

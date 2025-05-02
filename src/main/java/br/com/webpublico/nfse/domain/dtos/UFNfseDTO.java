package br.com.webpublico.nfse.domain.dtos;

import java.util.Objects;

public class UFNfseDTO implements NfseDTO {

    private Long id;
    private String uf;
    private String nome;

    public UFNfseDTO() {
    }

    public UFNfseDTO(Long id, String uf, String nome) {
        this.id = id;
        this.uf = uf;
        this.nome = nome;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UFNfseDTO ufNfseDTO = (UFNfseDTO) o;
        return Objects.equals(id, ufNfseDTO.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}

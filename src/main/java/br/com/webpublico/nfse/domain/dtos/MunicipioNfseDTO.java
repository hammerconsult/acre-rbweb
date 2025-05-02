package br.com.webpublico.nfse.domain.dtos;

import java.util.Objects;

/**
 * Created by rodolfo on 09/10/17.
 */
public class MunicipioNfseDTO implements NfseDTO {

    private Long id;
    private String codigo;
    private String nome;
    private String estado;

    public MunicipioNfseDTO() {
    }

    public MunicipioNfseDTO(String nome, String estado) {
        this.nome = nome;
        this.estado = estado;
    }

    public MunicipioNfseDTO(Long id, String codigo, String nome, String estado) {
        this.id = id;
        this.codigo = codigo;
        this.nome = nome;
        this.estado = estado;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MunicipioNfseDTO that = (MunicipioNfseDTO) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}

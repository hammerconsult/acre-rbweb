package br.com.webpublico.nfse.domain.dtos;


public class PaisNfseDTO implements NfseDTO {

    private Long id;
    private String codigo;
    private String nome;
    private String sigla;

    public PaisNfseDTO() {
    }

    public PaisNfseDTO(String sigla) {
        this.sigla = sigla;
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

    public String getSigla() {
        return sigla;
    }

    public void setSigla(String sigla) {
        this.sigla = sigla;
    }


}

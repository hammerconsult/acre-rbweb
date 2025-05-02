package br.com.webpublico.nfse.domain.dtos;

public class TipoEventoSimplesNacionalNfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {

    private Long id;

    private Integer codigo;

    private String nome;

    private Integer codigoNatureza;

    private String descricao;

    private char tipo;

    private Boolean sansao;

    public TipoEventoSimplesNacionalNfseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getCodigoNatureza() {
        return codigoNatureza;
    }

    public void setCodigoNatureza(Integer codigoNatureza) {
        this.codigoNatureza = codigoNatureza;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public char getTipo() {
        return tipo;
    }

    public void setTipo(char tipo) {
        this.tipo = tipo;
    }

    public Boolean getSansao() {
        return sansao;
    }

    public void setSansao(Boolean sansao) {
        this.sansao = sansao;
    }
}

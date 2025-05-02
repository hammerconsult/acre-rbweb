package br.com.webpublico.nfse.domain.dtos;

public class ConstrucaoCivilLocalizacaoNfseDTO implements NfseDTO {

    private Long id;

    private MunicipioNfseDTO municipio;

    private String logradouro;

    private String numero;

    private String bairro;

    private String cep;

    private String nomeEmpreendimento;

    public ConstrucaoCivilLocalizacaoNfseDTO() {
    }

    public ConstrucaoCivilLocalizacaoNfseDTO(Long id,
                                             MunicipioNfseDTO municipio,
                                             String logradouro,
                                             String numero,
                                             String bairro,
                                             String cep,
                                             String nomeEmpreendimento) {
        this.id = id;
        this.municipio = municipio;
        this.logradouro = logradouro;
        this.numero = numero;
        this.bairro = bairro;
        this.cep = cep;
        this.nomeEmpreendimento = nomeEmpreendimento;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MunicipioNfseDTO getMunicipio() {
        return municipio;
    }

    public void setMunicipio(MunicipioNfseDTO municipio) {
        this.municipio = municipio;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getNomeEmpreendimento() {
        return nomeEmpreendimento;
    }

    public void setNomeEmpreendimento(String nomeEmpreendimento) {
        this.nomeEmpreendimento = nomeEmpreendimento;
    }
}

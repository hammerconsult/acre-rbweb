package br.com.webpublico.nfse.domain.dtos;

public class CadastroImobiliarioSearchNfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {

    private Long id;
    private String inscricaoCadastral;
    private String proprietarios;
    private String logradouro;
    private String bairro;
    private String numero;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getInscricaoCadastral() {
        return inscricaoCadastral;
    }

    public void setInscricaoCadastral(String inscricaoCadastral) {
        this.inscricaoCadastral = inscricaoCadastral;
    }

    public String getProprietarios() {
        return proprietarios;
    }

    public void setProprietarios(String proprietarios) {
        this.proprietarios = proprietarios;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }
}

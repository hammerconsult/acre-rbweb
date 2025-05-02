package br.com.webpublico.nfse.domain.dtos;

public class BancoNfseDTO implements br.com.webpublico.nfse.domain.dtos.NfseDTO {

    private Long id;
    private String descricao;
    private String numeroBanco;
    private String digitoVerificador;
    private String homePage;
    private String numeroContrato;

    public BancoNfseDTO() {
    }

    public BancoNfseDTO(Long id, String descricao, String numeroBanco, String digitoVerificador, String homePage, String numeroContrato) {
        this.id = id;
        this.descricao = descricao;
        this.numeroBanco = numeroBanco;
        this.digitoVerificador = digitoVerificador;
        this.homePage = homePage;
        this.numeroContrato = numeroContrato;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNumeroBanco() {
        return numeroBanco;
    }

    public void setNumeroBanco(String numeroBanco) {
        this.numeroBanco = numeroBanco;
    }

    public String getDigitoVerificador() {
        return digitoVerificador;
    }

    public void setDigitoVerificador(String digitoVerificador) {
        this.digitoVerificador = digitoVerificador;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }
}

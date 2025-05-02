package br.com.webpublico.entidadesauxiliares;

public class FichaCredorContaCBanc {

    private String numeroBanco;
    private String numeroAgencia;
    private String digitoVerificadorAgencia;
    private String nomeAgencia;
    private String numeroConta;
    private String digitoVerificadorContaCBanc;
    private String modalidadeConta;
    private Boolean principal;
    private String situacao;


    public FichaCredorContaCBanc() {
    }

    public String getNumeroBanco() {
        return numeroBanco;
    }

    public void setNumeroBanco(String numeroBanco) {
        this.numeroBanco = numeroBanco;
    }

    public String getNumeroAgencia() {
        return numeroAgencia;
    }

    public void setNumeroAgencia(String numeroAgencia) {
        this.numeroAgencia = numeroAgencia;
    }

    public String getDigitoVerificadorAgencia() {
        return digitoVerificadorAgencia;
    }

    public void setDigitoVerificadorAgencia(String digitoVerificadorAgencia) {
        this.digitoVerificadorAgencia = digitoVerificadorAgencia;
    }

    public String getNomeAgencia() {
        return nomeAgencia;
    }

    public void setNomeAgencia(String nomeAgencia) {
        this.nomeAgencia = nomeAgencia;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public String getDigitoVerificadorContaCBanc() {
        return digitoVerificadorContaCBanc;
    }

    public void setDigitoVerificadorContaCBanc(String digitoVerificadorContaCBanc) {
        this.digitoVerificadorContaCBanc = digitoVerificadorContaCBanc;
    }

    public String getModalidadeConta() {
        return modalidadeConta;
    }

    public void setModalidadeConta(String modalidadeConta) {
        this.modalidadeConta = modalidadeConta;
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public String getSituacao() {
        return situacao;
    }

    public void setSituacao(String situacao) {
        this.situacao = situacao;
    }
}

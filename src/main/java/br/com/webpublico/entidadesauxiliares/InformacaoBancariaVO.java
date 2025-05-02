package br.com.webpublico.entidadesauxiliares;

/**
 * Created by carlos on 05/04/17.
 */
public class InformacaoBancariaVO {
    private String banco;
    private String agencia;
    private String numeroConta;
    private String dvConta;
    private String numeroAgencia;
    private String dvAgencia;

    public InformacaoBancariaVO() {
    }

    public String getBanco() {
        return banco;
    }

    public void setBanco(String banco) {
        this.banco = banco;
    }

    public String getAgencia() {
        return agencia;
    }

    public void setAgencia(String agencia) {
        this.agencia = agencia;
    }

    public String getNumeroConta() {
        return numeroConta;
    }

    public void setNumeroConta(String numeroConta) {
        this.numeroConta = numeroConta;
    }

    public String getDvConta() {
        return dvConta;
    }

    public void setDvConta(String dvConta) {
        this.dvConta = dvConta;
    }

    public String getNumeroAgencia() {
        return numeroAgencia;
    }

    public void setNumeroAgencia(String numeroAgencia) {
        this.numeroAgencia = numeroAgencia;
    }

    public String getDvAgencia() {
        return dvAgencia;
    }

    public void setDvAgencia(String dvAgencia) {
        this.dvAgencia = dvAgencia;
    }
}

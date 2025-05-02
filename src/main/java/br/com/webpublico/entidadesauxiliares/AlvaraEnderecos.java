package br.com.webpublico.entidadesauxiliares;

public class AlvaraEnderecos {

    private Boolean principal;
    private Integer sequencial;
    private String bairro;
    private String logradouro;
    private String numero;
    private String complemento;
    private String cep;
    private Double areaUtilizacao;

    public AlvaraEnderecos() {
    }

    public Boolean getPrincipal() {
        return principal;
    }

    public void setPrincipal(Boolean principal) {
        this.principal = principal;
    }

    public Integer getSequencial() {
        return sequencial;
    }

    public void setSequencial(Integer sequencial) {
        this.sequencial = sequencial;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
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

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Double getAreaUtilizacao() {
        return areaUtilizacao;
    }

    public void setAreaUtilizacao(Double areaUtilizacao) {
        this.areaUtilizacao = areaUtilizacao;
    }
}

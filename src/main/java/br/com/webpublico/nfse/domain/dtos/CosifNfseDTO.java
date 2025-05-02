package br.com.webpublico.nfse.domain.dtos;

public class CosifNfseDTO implements NfseDTO {

    private Long id;
    private String conta;
    private String descricao;
    private String funcao;

    public CosifNfseDTO() {
    }

    public CosifNfseDTO(Long id, String conta, String descricao, String funcao) {
        this.id = id;
        this.conta = conta;
        this.descricao = descricao;
        this.funcao = funcao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }
}

package br.com.webpublico.nfse.domain.dtos;

public class RelatorioContaReceitaBancariaDTO {
    private String conta;
    private String descricaoConta;
    private String funcao;
    private String nomeServico;
    private String cadastroEconomico;

    public RelatorioContaReceitaBancariaDTO() {
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public String getDescricaoConta() {
        return descricaoConta;
    }

    public void setDescricaoConta(String descricaoConta) {
        this.descricaoConta = descricaoConta;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getNomeServico() {
        return nomeServico;
    }

    public void setNomeServico(String nomeServico) {
        this.nomeServico = nomeServico;
    }

    public String getCadastroEconomico() {
        return cadastroEconomico;
    }

    public void setCadastroEconomico(String cadastroEconomico) {
        this.cadastroEconomico = cadastroEconomico;
    }
}

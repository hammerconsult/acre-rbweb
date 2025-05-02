package br.com.webpublico.enums;

public enum TipoAtoPotencial {
    CONVENIO_DE_RECEITA("Convênio de Receita"),
    CONVENIO_DE_DESPESA("Convênio de Despesa"),
    CONTRATO_DE_SERVICO_DESPESA("Contrato de Serviço – Despesa"),
    CONTRATO_DE_ALUGUEL_DESPESA("Contrato de Aluguel – Despesa"),
    CONTRATO_DE_SEGURO("Contrato de Seguro – Despesa"),
    CONTRATO_DE_FORNECIMENTO_DE_BEM_DESPESA("Contrato de Fornecimento de Bem – Despesa");

    private String descricao;

    TipoAtoPotencial(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isConvenioReceita() {
        return CONVENIO_DE_RECEITA.equals(this);
    }

    public boolean isConvenioDespesa(){
        return CONVENIO_DE_DESPESA.equals(this);
    }

    public boolean isContrato() {
        return CONTRATO_DE_ALUGUEL_DESPESA.equals(this)
            || CONTRATO_DE_FORNECIMENTO_DE_BEM_DESPESA.equals(this)
            || CONTRATO_DE_SEGURO.equals(this)
            || CONTRATO_DE_SERVICO_DESPESA.equals(this);
    }
}

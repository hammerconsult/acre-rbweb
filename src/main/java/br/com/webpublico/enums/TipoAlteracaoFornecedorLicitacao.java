package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoAlteracaoFornecedorLicitacao implements EnumComDescricao {

    NOVA_PROPOSTA("Nova Proposta"),
    ALTERAR_PROPOSTA("Alterar Proposta"),
    NOVO_PARTICIPANTE("Novo Participante"),
    ALTERAR_PARTICIPANTE("Alterar Participante"),
    SUBSTITUIR_PARTICIPANTE("Substituir Participante"),
    SUBSTITUIR_FORNECEDOR_DISPENSA_LICITACAO("Substituir Fornecedor da Dispensa/Inexigibilidade");
    private String descricao;

    TipoAlteracaoFornecedorLicitacao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public boolean isNovoParticipante(){
        return NOVO_PARTICIPANTE.equals(this);
    }

    public boolean isNovaProposta(){
        return NOVA_PROPOSTA.equals(this);
    }

    public boolean isAlterarProposta(){
        return ALTERAR_PROPOSTA.equals(this);
    }

    public boolean isAlterarParticipante(){
        return ALTERAR_PARTICIPANTE.equals(this);
    }

    public boolean isSubstituirParticipante(){
        return SUBSTITUIR_PARTICIPANTE.equals(this);
    }
    public boolean isSubstituirFornecedorDispensa(){
        return SUBSTITUIR_FORNECEDOR_DISPENSA_LICITACAO.equals(this);
    }
}

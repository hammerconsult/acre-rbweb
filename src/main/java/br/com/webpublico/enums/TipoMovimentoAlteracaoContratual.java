package br.com.webpublico.enums;

import br.com.webpublico.webreportdto.dto.administrativo.TipoMovimentoAlteracaoContratualDTO;

public enum TipoMovimentoAlteracaoContratual {

    PRAZO_DE_VIGENCIA("Prazo de Vigência", 1, TipoMovimentoAlteracaoContratualDTO.PRAZO_DE_VIGENCIA),
    PRAZO_DE_EXECUCAO("Prazo de Execução", 2, TipoMovimentoAlteracaoContratualDTO.PRAZO_DE_EXECUCAO),
    VALOR_VARIACAO("Valor/Variação", 3, TipoMovimentoAlteracaoContratualDTO.VALOR_VARIACAO),
    VALOR_EXECUCAO("Valor/Execução", 4, TipoMovimentoAlteracaoContratualDTO.VALOR_EXECUCAO),
    NAO_SE_APLICA("Não se Aplica", 5, TipoMovimentoAlteracaoContratualDTO.NAO_SE_APLICA);
    private String descricao;
    private Integer ordem;
    private TipoMovimentoAlteracaoContratualDTO dto;

    TipoMovimentoAlteracaoContratual(String descricao, Integer ordem, TipoMovimentoAlteracaoContratualDTO dto) {
        this.descricao = descricao;
        this.ordem = ordem;
        this.dto = dto;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getOrdem() {
        return ordem;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

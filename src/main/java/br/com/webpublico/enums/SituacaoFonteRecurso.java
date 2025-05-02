/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Munif
 */
public enum SituacaoFonteRecurso {

    ATIVO("Ativo", "Ativação", SituacaoCadastralPessoa.ATIVO, 1),
    BAIXADO("Baixado", "Baixa", SituacaoCadastralPessoa.BAIXADO, 2),
    SUSPENSO("Suspenso", "Suspensão", SituacaoCadastralPessoa.SUSPENSO, 3),
    AGUARDANDO_AVALIACAO("Aguardando Avaliação", "Aguardando Avaliação", SituacaoCadastralPessoa.AGUARDANDO_CONFIRMACAO_CADASTRO, 4),
    INATIVO("Inativo", "Inativação", SituacaoCadastralPessoa.INATIVO, 5),
    REATIVO("Reativado", "Reativação", SituacaoCadastralPessoa.ATIVO, 6),
    NAO_IDENTIFICADO("Não Identificado", "Não Identificado", SituacaoCadastralPessoa.INATIVO, 7);
    private final String descricao;
    private final String descricaoMovimento;
    private final SituacaoCadastralPessoa situacaoCadastralPessoa;
    private final Integer codigo;

    SituacaoFonteRecurso(String descricao, String descricaoMovimento, SituacaoCadastralPessoa situacaoCadastralPessoa, Integer codigo) {
        this.descricao = descricao;
        this.descricaoMovimento = descricaoMovimento;
        this.situacaoCadastralPessoa = situacaoCadastralPessoa;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoMovimento() {
        return descricaoMovimento;
    }

    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

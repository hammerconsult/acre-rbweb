package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author octavio
 */
public enum TipoRevisaoAuditoria implements EnumComDescricao {

    INCLUSAO("Inclusão", 0L),
    ALTERACAO("Alteração", 1L),
    EXCLUSAO("Exclusão", 2L);

    private String descricao;
    private Long tipo;

    TipoRevisaoAuditoria(String descricao, Long tipo) {
        this.descricao = descricao;
        this.tipo = tipo;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public Long getTipo() {
        return tipo;
    }
}

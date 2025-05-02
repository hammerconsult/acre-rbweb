package br.com.webpublico.enums;

/**
 * Created by Fabio on 02/05/2018.
 */
public enum TipoVerificacaoDebitoAlvara {

    NAO_PERMITIR("Não permitir se houver débitos vencidos"),
    PERMITIR_AVISAR("Permitir, mas avisar se houver débitos vencidos"),
    PERMITIR("Permitir sem verificar"),
    NAO_PERMITIR_DEBITO_ALVARA("Não permitir se houver débitos de Alvará");

    private String descricao;

    TipoVerificacaoDebitoAlvara(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

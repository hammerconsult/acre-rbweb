/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author gustavo
 */
public enum ClassificacaoAtividade {

    INDUSTRIA("Indústria"),
    COMERCIO("Comércio"),
    COMERCIO_INDUSTRIA("Comércio/Indústria"),
    PRESTACAO_DE_SERVICO("Prestação de Serviço"),
    COMERCIO_PRESTACAO_DE_SERVICO("Comércio/Prestação de Serviço"),
    INDUSTRIA_PRESTACAO_DE_SERVICO("Indústria/Prestação de Serviço"),
    COMERCIO_INDUSTRIA_PRESTACAO_DE_SERVICO("Comércio/Indústria/Prestação de Serviço"),
    ORGAO_PUBLICO_FEDERAL("Orgão Público Federal"),
    ORGAO_PUBLICO_ESTADUAL("Orgão Público Estadual"),
    ORGAO_PUBLICO_MUNICIPAL("Orgão Público Municipal"),
    AGROPECUARIA("Agropecuária"),
    DIVERSAO_PUBLICA("Diversão Pública"),
    OUTROS("Outros"),
    SUPERMERCADO("Supermercado"),
    PROFISSIONAL_AUTONOMO("Profissional Autônomo"),
    FEIRANTE("Feirante"),
    ENTIDADE("Entidade"),
    ASSOCIACAO_CIVIL_DESPORTIVA_RELIGIOSA("Sociedade/Associação Civil/Desportiva/Religiosa"),
    HOTEL_MOTEL("Hotéis e Motéis"),
    DEPOSITO_FECHADO("Depósito Fechado");

    private String descricao;

    public boolean isPrestacaoServico() {
        return PRESTACAO_DE_SERVICO.equals(this) ||
            COMERCIO_INDUSTRIA_PRESTACAO_DE_SERVICO.equals(this) ||
            COMERCIO_PRESTACAO_DE_SERVICO.equals(this) ||
            INDUSTRIA_PRESTACAO_DE_SERVICO.equals(this);
    }

    public String getDescricao() {
        return descricao;
    }

    ClassificacaoAtividade(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

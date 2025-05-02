package br.com.webpublico.nfse.domain.dtos;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;

public enum ClassificacaoAtividadeNfseDTO implements NfseEnumDTO {

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

    ClassificacaoAtividadeNfseDTO(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}

package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;

public enum TipoEnquadramentoFiscalNfseDTO implements NfseEnumDTO {
    PADRAO("Padrão"),
    CONSTRUCAO_CIVIL("Construção Civil"),
    INSTITUICAO_FINANCEIRA("Instiruição Finanaceira"),
    OPERADORA_CREDITO("Operadora de Crédito"),
    ORGAO_PUBLICO("Orgão PÚblico"),
    CARTORIO("Cartorário, Notarial e Registros Públicos"),
    CONCESSIONARIA_RODOVIAS("Concessionária de Rodovias");

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    private TipoEnquadramentoFiscalNfseDTO(String descricao) {
        this.descricao = descricao;
    }

}

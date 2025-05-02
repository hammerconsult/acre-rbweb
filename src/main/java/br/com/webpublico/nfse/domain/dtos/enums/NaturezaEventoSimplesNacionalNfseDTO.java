package br.com.webpublico.nfse.domain.dtos.enums;

import br.com.webpublico.tributario.interfaces.NfseEnumDTO;

public enum NaturezaEventoSimplesNacionalNfseDTO implements NfseEnumDTO {
    MEDIDA_JUDICIAL("Medida Judicial", 1),
    ATO_ADMINISTRATIVO("Ato Administrativo", 2),
    OPCAO_DO_CONTRIBUINTE("Opção do Contribuinte", 3);

    private String descricao;
    private Integer codigo;

    NaturezaEventoSimplesNacionalNfseDTO(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    @Override
    public String toString() {
        return descricao;
    }

}

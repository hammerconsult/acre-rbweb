package br.com.webpublico.nfse.enums;

import br.com.webpublico.nfse.domain.dtos.enums.NaturezaEventoSimplesNacionalNfseDTO;

public enum NaturezaEventoSimplesNacional implements NfseEnum {
    MEDIDA_JUDICIAL("Medida Judicial", 1, NaturezaEventoSimplesNacionalNfseDTO.MEDIDA_JUDICIAL),
    ATO_ADMINISTRATIVO("Ato Administrativo", 2, NaturezaEventoSimplesNacionalNfseDTO.ATO_ADMINISTRATIVO),
    OPCAO_DO_CONTRIBUINTE("Opção do Contribuinte", 3, NaturezaEventoSimplesNacionalNfseDTO.OPCAO_DO_CONTRIBUINTE);

    private String descricao;
    private Integer codigo;
    private NaturezaEventoSimplesNacionalNfseDTO dto;

    NaturezaEventoSimplesNacional(String descricao, Integer codigo, NaturezaEventoSimplesNacionalNfseDTO dto) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.dto = dto;
    }

    public String getDescricao() {
        return descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public static NaturezaEventoSimplesNacional findByCodigo(Integer codigo) {
        for (NaturezaEventoSimplesNacional value : values()) {
            if (value.codigo.equals(codigo)) {
                return value;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return descricao;
    }

    @Override
    public NaturezaEventoSimplesNacionalNfseDTO toDto() {
        return dto;
    }
}

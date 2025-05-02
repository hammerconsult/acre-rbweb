package br.com.webpublico.enums;

import br.com.webpublico.ott.enums.VeiculoPoluenteDTO;

public enum VeiculoPoluente {
    MOVIDO_PROPULSAO_MATRIZ_ENERGETICA_POLUENTE("Movido por propulsão de matriz energética poluente", VeiculoPoluenteDTO.MOVIDO_PROPULSAO_MATRIZ_ENERGETICA_POLUENTE),
    HIBRIDO_OU_NAO_POLUENTE("Híbrido ou movido por propulsão de matriz energética não poluente", VeiculoPoluenteDTO.HIBRIDO_OU_NAO_POLUENTE);

    private String descricao;
    private VeiculoPoluenteDTO veiculoPoluenteDTO;

    private VeiculoPoluente(String descricao, VeiculoPoluenteDTO veiculoPoluenteDTO) {
        this.descricao = descricao;
        this.veiculoPoluenteDTO = veiculoPoluenteDTO;
    }

    public String getDescricao() {
        return descricao;
    }

    public VeiculoPoluenteDTO getVeiculoPoluenteDTO() {
        return veiculoPoluenteDTO;
    }

    public static VeiculoPoluente getEnumByDTO(VeiculoPoluenteDTO dto) {
        for (VeiculoPoluente value : values()) {
            if (value.veiculoPoluenteDTO.equals(dto)) {
                return value;
            }
        }
        return null;
    }
}

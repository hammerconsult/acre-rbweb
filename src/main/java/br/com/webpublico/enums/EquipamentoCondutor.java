package br.com.webpublico.enums;

import br.com.webpublico.ott.enums.EquipamentoCondutorDTO;

public enum EquipamentoCondutor {
    SMARTPHONE("Smartphone", EquipamentoCondutorDTO.SMARTPHONE),
    TABLET("Tablet", EquipamentoCondutorDTO.TABLET);

    private String descricao;
    private EquipamentoCondutorDTO equipamentoCondutorDTO;

    private EquipamentoCondutor(String descricao, EquipamentoCondutorDTO equipamentoCondutorDTO){
        this.descricao = descricao;
        this.equipamentoCondutorDTO = equipamentoCondutorDTO;
    }

    public String getDescricao() {
        return descricao;
    }

    public EquipamentoCondutorDTO getEquipamentoCondutorDTO() {
        return equipamentoCondutorDTO;
    }

    public static EquipamentoCondutor getEnumByDTO(EquipamentoCondutorDTO dto) {
        for (EquipamentoCondutor value : values()) {
            if (value.equipamentoCondutorDTO.equals(dto)) {
                return value;
            }
        }
        return null;
    }
}

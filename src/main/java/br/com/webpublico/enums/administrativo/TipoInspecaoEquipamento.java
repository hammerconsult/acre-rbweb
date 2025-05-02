package br.com.webpublico.enums.administrativo;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.webreportdto.dto.administrativo.TipoInspecaoEquipamentoDTO;

/**
 * Created by carlos on 24/05/17.
 */
public enum TipoInspecaoEquipamento implements EnumComDescricao {
    MECANICA_ELETRICA_HIDRAULICA("Mecânica - Elétrica e Hidráulica", TipoInspecaoEquipamentoDTO.MECANICA_ELETRICA_HIDRAULICA),
    DISPOSITIVO_SEGURANCA("Dispositivos de Segurança", TipoInspecaoEquipamentoDTO.DISPOSITIVO_SEGURANCA),
    MANUTENCAO_PREVENTIVA("Manutenção Preventiva", TipoInspecaoEquipamentoDTO.MANUTENCAO_PREVENTIVA);
    private String descricao;
    private TipoInspecaoEquipamentoDTO toDto;

    TipoInspecaoEquipamento(String descricao, TipoInspecaoEquipamentoDTO toDto) {
        this.descricao = descricao;
        this.toDto = toDto;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public TipoInspecaoEquipamentoDTO getToDto() {
        return toDto;
    }

    @Override
    public String toString() {
        return this.descricao;
    }
}

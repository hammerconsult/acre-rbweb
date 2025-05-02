package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import br.com.webpublico.nfse.enums.NfseEnum;
import br.com.webpublico.tributario.enumeration.RegimeTributarioNfseDTO;
import com.google.common.collect.Lists;

import java.util.List;

public enum RegimeTributario implements NfseEnum, EnumComDescricao {
    SIMPLES_NACIONAL("Simples Nacional", RegimeTributarioNfseDTO.SIMPLES_NACIONAL),
    LUCRO_PRESUMIDO("Lucro Presumido / Real", RegimeTributarioNfseDTO.LUCRO_PRESUMIDO);

    private String descricao;
    private RegimeTributarioNfseDTO dto;

    public String getDescricao() {
        return descricao;
    }

    private RegimeTributario(String descricao, RegimeTributarioNfseDTO dto) {
        this.descricao = descricao;
        this.dto = dto;
    }


    @Override
    public RegimeTributarioNfseDTO toDto() {
        return dto;
    }

    public static List<String> getDescricoes() {
        List<String> descricoes = Lists.newArrayList();
        for (RegimeTributario e : values()) {
            descricoes.add(e.getDescricao());
        }
        return descricoes;
    }

    public static List<String> getNames() {
        List<String> names = Lists.newArrayList();
        for (RegimeTributario e : values()) {
            names.add(e.name());
        }
        return names;
    }
}

package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoArquivoLogAlvaraRedeSim implements EnumComDescricao {
    DAM("DAM de Alvará"),
    ALVARA("Alvará"),
    BCM("BCM"),
    DISPENSA_LICENCIAMENTO("Dispensa de Licenciamento");

    private String descricao;

    TipoArquivoLogAlvaraRedeSim(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}

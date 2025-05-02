package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 08/09/14
 * Time: 17:58
 * To change this template use File | Settings | File Templates.
 */
public enum TipoObjetoFrota implements EnumComDescricao {
    VEICULO("Veículo"),
    EQUIPAMENTO("Equipamento/Máquina");

    private String descricao;

    TipoObjetoFrota(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isVeiculo() {
        return VEICULO.equals(this);
    }

    public boolean isEquipamento() {
        return EQUIPAMENTO.equals(this);
    }
}

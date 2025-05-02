package br.com.webpublico.enums;

/**
 * @author GhouL
 */
public enum TipoCalculoCaracteristicaFuncionamento {

    FIXO("Fixo"),
    FIXO_PROPORCIONAL("Fixo Proporcional");
    public String descricao;

    public String getDescricao() {
        return this.descricao;
    }

    private TipoCalculoCaracteristicaFuncionamento(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

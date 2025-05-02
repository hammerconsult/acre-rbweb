package br.com.webpublico.enums;

/**
 * Created by mga on 18/01/2018.
 */
public enum TipoSaidaMaterial {

    DEVOLUCAO("Devolução"),
    CONSUMO("Consumo"),
    TRANSFERENCIA("Transferência"),
    PRODUCAO("Produção"),
    DESINCORPORACAO("Desincorporação"),
    INVENTARIO("Inventário"),
    DIRETA("Direta");
    private String descricao;

    TipoSaidaMaterial(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

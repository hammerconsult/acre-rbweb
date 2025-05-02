/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author cheles
 */
@GrupoDiagrama(nome = "Materiais")
public enum TipoMarca {

    MOTOCICLETA("Marca de Motocicleta"),
    CARRO("Marca de Carro"),
    VAN("Marca de Van"),
    CAMINHAO("Marca de Caminhão"),
    CARRETA("Marca de Carreta"),
    MARCA_VEICULO("Outros Veículos (afins)"),
    MARCA_MATERIAL_PERMANENTE("Marca de Material Permanente"),
    MARCA_MATERIAL_CONSUMO("Marca de Material de Consumo");

    private String descricao;

    private TipoMarca(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

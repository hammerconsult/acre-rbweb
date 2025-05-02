/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author William
 */
public enum TipoPermissaoTaxi {

    RETENCAO_DO_VEICULO_ATE_REGULARIZACAO("RETENÇÃO DO VEÍCULO ATÉ A REGULARIZAÇÃO"),
    RETENCAO_DO_VEICULO("RETENÇÃO DO VEÍCULO"),
    APREENSAO_DO_VEICULO("APREENSÃO DO VEÍCULO"),
    REMOCAO_DO_VEICULO("REMOÇÃO DO VEÍCULO");

    private String descricao;

    private TipoPermissaoTaxi(String descricao) {
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

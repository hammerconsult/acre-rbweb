/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 *
 * @author William
 */
public enum TipoPermissaoMotoTaxi {

    RETENCAO_DO_VEICULO("RETENÇÃO DO VEÍCULO"),
    REMOCAO_DO_VEICULO("REMOÇÃO DO VEÍCULO"),
    RECOLHIMENTO_DA_CREDENCIAL_TRANSPORTE("RECOLHIMENTO DA CREDENCIAL DE TRANSPORTE"),
    RECOLHIMENTO_DA_CREDENCIAL_TRAFEGO("RECOLHIMENTO DA CREDENCIAL DE TRÁFEGO"),
    RECOLHIMENTO_QUALQUER_EQUIPAMENTO_OU_ACESSORIO_PROIBIDO_PELA_LEGISLACAO_DE_TRANSITO("RECOLHIMENTO DE QUALQUER EQUIPAMENTO OU ACESSÓRIO PROIBIDO PELA LEGISLAÇÃO DE TRÂNSITO E TRANSPORTES, CASO SEJA DE FÁCIL REMOÇÃO"),
    DESEMBARQUE_CARGA_INCOMPATIVEL_OU_EM_EXCESSO("DESEMBARQUE DA CARGA INCOMPATÍVEL OU EM EXCESSO");
    private String descricao;

    private TipoPermissaoMotoTaxi(String descricao) {
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

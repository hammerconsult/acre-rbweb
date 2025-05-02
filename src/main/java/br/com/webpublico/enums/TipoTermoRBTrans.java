/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import com.google.common.collect.Lists;

/**
 * @author AndréGustavo
 */
@GrupoDiagrama(nome = "RBTrans")
public enum TipoTermoRBTrans {
    /**
     * Gerado para: Táxi, Moto-Táxi .
     * Quando: Ao cadastrar uma nova permissão e no processo de transferência.
     */
    TERMO_DE_PERMISSAO("Termo de Permissão", TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI),
    /**
     * Gerado para: Táxi, Moto-Táxi e Frete.
     * Quando: Ao cadastrar um novo veículo.
     * Obs: No processo de transferência
     */
    TERMO_AUTORIZA_VEICULO("Termo de Autorização de Inserção de Veículo", TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI, TipoPermissaoRBTrans.FRETE),
    /**
     * Gerado para: Táxi, Moto-Táxi e Frete.
     * Quando: Ao Baixar um veículo
     */
    TERMO_AUTORIZA_BAIXA_VEICULO("Termo de Autorização de Baixa de Veículo", TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI, TipoPermissaoRBTrans.FRETE),
    /**
     * Gerado para: Táxi
     * Quando: Ao Transferir a permissão
     */
    TERMO_AUTORIZA_TRANSFERENCIA_PERMISSAO("Termo de Autorização de Transferência de Permissão", TipoPermissaoRBTrans.TAXI),
    /**
     * Gerado para: Moto-Táxi .
     * Quando: Ao Cadastrar um novo auxiliar.
     */
    TERMO_AUTORIZA_MOTORISTA("Termo de Permissão de Condutor Auxiliar", TipoPermissaoRBTrans.MOTO_TAXI),
    /**
     * Gerado para: Táxi, Moto-Táxi e Frete
     * Quando: Solicitado baixa e inserção ao mesmo tempo.
     */
    TERMO_AUTORIZA_BAIXA_INSERCAO_VEICULO("Termo de Baixa e Inserção de Veículo", TipoPermissaoRBTrans.TAXI, TipoPermissaoRBTrans.MOTO_TAXI, TipoPermissaoRBTrans.FRETE);

    private String descricao;
    private TipoPermissaoRBTrans[] tiposPermissao;

    private TipoTermoRBTrans(String descricao, TipoPermissaoRBTrans... tiposPermissao) {
        this.descricao = descricao;
        this.tiposPermissao = tiposPermissao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean contains(TipoPermissaoRBTrans tipoPermissao) {
        return Lists.newArrayList(this.tiposPermissao).contains(tipoPermissao);
    }
}

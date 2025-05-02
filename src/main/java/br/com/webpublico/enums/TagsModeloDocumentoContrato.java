/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

import java.io.Serializable;

/**
 * @author Felipe Marinzeck
 */
@GrupoDiagrama(nome = "Contrato")
public enum TagsModeloDocumentoContrato implements Serializable {

    NUMERO_CONTRATO("Número do contrato"),
    ANO_CONTRATO("Ano do contrato"),
    TIPO_DE_AQUISICAO("Tipo de aquisição"),
    NUMERO_DO_PROCESSO_DE_COMPRA("Número do processo de compra"),
    ANO_DO_PROCESSO_DE_COMPRA("Número do processo de compra"),
    ORGAO_ENTIDADE_FUNDO("Unidade Administrativa"),
    REGIME_DE_EXECUCAO("Regime de execução");

    private String descricao;

    private TagsModeloDocumentoContrato(String descricao) {
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

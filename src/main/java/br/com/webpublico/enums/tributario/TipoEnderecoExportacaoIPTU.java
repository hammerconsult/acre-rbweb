/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums.tributario;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author Pedro
 */
@GrupoDiagrama(nome = "CadastroUnico")
public enum TipoEnderecoExportacaoIPTU {
    ENDERECO_IMOVEL("Endereço do Imóvel"),
    PRINCIPAL("Endereço Principal"),
    DOMICILIO_FISCAL("Domicílio Fiscal");

    private final String descricao;

    TipoEnderecoExportacaoIPTU(String descricao) {
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

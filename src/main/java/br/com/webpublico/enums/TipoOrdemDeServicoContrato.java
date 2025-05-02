/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

/**
 * @author Felipe Marinzeck
 */
public enum TipoOrdemDeServicoContrato {
    SERVICO("Serviço"),
    PARALIZACAO("Paralização"),
    REINICIO("Reinício");

    private String descricao;

    private TipoOrdemDeServicoContrato(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

}

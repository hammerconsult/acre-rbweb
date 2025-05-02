/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author renato
 */
@GrupoDiagrama(nome = "Licitacao")
public enum TipoProcessoDeCompra {

    LICITACAO("Licitação"),
    CREDENCIAMENTO("Credenciamento"),
    DISPENSA_LICITACAO_INEXIGIBILIDADE("Dispensa Licitação/Inexigibilidade");

    private String descricao;

    private TipoProcessoDeCompra(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isLicitacao(){
        return LICITACAO.equals(this);
    }

    public boolean isDispensa(){
        return DISPENSA_LICITACAO_INEXIGIBILIDADE.equals(this);
    }

    public boolean isCredenciamento(){
        return CREDENCIAMENTO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}

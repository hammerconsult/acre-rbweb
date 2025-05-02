/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author renato
 */
@GrupoDiagrama(nome = "Licitacao")
public enum TipoSolicitacao {


    COMPRA_SERVICO("Compra e Serviço"),
    OBRA_SERVICO_DE_ENGENHARIA("Obra e Serviço de Engenharia");
    private String descricao;

    private TipoSolicitacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public boolean isCompraServico() {
        return this.equals(COMPRA_SERVICO);
    }

    public boolean isObraServicoEngenharia() {
        return this.equals(OBRA_SERVICO_DE_ENGENHARIA);
    }

    public List<TipoObjetoCompra> getTiposObjetoCompra() {
        List<TipoObjetoCompra> toReturn = Lists.newArrayList();
        if (TipoSolicitacao.OBRA_SERVICO_DE_ENGENHARIA.equals(this)) {
            toReturn.add(TipoObjetoCompra.PERMANENTE_MOVEL);
            toReturn.add(TipoObjetoCompra.SERVICO);
            toReturn.add(TipoObjetoCompra.CONSUMO);
        }else{
            toReturn.add(TipoObjetoCompra.PERMANENTE_IMOVEL);
        }
        return toReturn;
    }

    @Override
    public String toString() {
        return descricao;
    }

}

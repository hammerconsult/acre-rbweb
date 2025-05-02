package br.com.webpublico.enums.tributario.procuradoria;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * Created by tributario on 19/08/2015.
 */
@GrupoDiagrama(nome = "Procuradoria")
public enum PapelProcesso {
    ADVOGADO("Advogado"),
    AUTOR("Autor"),
    OFICIAL_JUSTICA("Oficial de Justiça"),
    PERITO("Perito"),
    TESTEMUNHA("Testemunha"),
    PROMOTOR("Promotor"),
    REU("Réu");

    PapelProcesso(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

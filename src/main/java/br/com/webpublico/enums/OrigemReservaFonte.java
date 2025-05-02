/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author venon
 */
@GrupoDiagrama(nome = "PPA")
public enum OrigemReservaFonte {

    ATO_LEGAL("Ato Legal"),
    ANULACAO("Anulação"),
    DIARIA_CIVIL("Diária Civil"),
    LICITACAO("Licitação");

    private String descricao;

    private OrigemReservaFonte(String descricao) {
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

    public List<TipoContaDespesa> getTiposContasDespesasEquivalentes() {
        switch (this) {
            case DIARIA_CIVIL:
                return Lists.newArrayList(
                    TipoContaDespesa.DIARIA_CIVIL,
                    TipoContaDespesa.DIARIA_CAMPO,
                    TipoContaDespesa.COLABORADOR_EVENTUAL,
                    TipoContaDespesa.SUPRIMENTO_FUNDO
                );

            case LICITACAO:
                return Lists.newArrayList(
                    TipoContaDespesa.BEM_MOVEL,
                    TipoContaDespesa.BEM_IMOVEL,
                    TipoContaDespesa.BEM_ESTOQUE,
                    TipoContaDespesa.BEM_INTANGIVEL,
                    TipoContaDespesa.SERVICO_DE_TERCEIRO,
                    TipoContaDespesa.VARIACAO_PATRIMONIAL_DIMINUTIVA
                );

            default:
                return Lists.newArrayList(TipoContaDespesa.values());
        }
    }
}

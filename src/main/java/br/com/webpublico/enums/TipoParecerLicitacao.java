/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author lucas
 */
@GrupoDiagrama(nome = "Licitacao")
public enum TipoParecerLicitacao implements EnumComDescricao {

    TECNICO("Técnico"),
    JURIDICO_EDITAL("Jurídico - Edital"),
    JURIDICO_JULGAMENTO("Jurídico - Julgamento"),
    JURIDICO_DISPENSA("Jurídico - Dispensa"),
    JURIDICO_INEXIGIBILIDADE("Jurídico - Inexigibilidade"),
    JURIDICO_CREDENCIAMENTO("Jurídico - Credenciamento"),
    JURIDICO_OUTRO("Jurídico - Outro");
    private String descricao;

    TipoParecerLicitacao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static List<TipoParecerLicitacao> getTiposCredenciamento(){
        List<TipoParecerLicitacao> tipos = Lists.newArrayList();
        tipos.add(TipoParecerLicitacao.JURIDICO_CREDENCIAMENTO);
        return tipos;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

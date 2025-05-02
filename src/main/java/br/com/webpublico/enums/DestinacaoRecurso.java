/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author major
 */
public enum DestinacaoRecurso implements EnumComDescricao {

    ASSISTENCIA_SOCIAL("Assistência Social"),
    TRANSFCONVENIOSEDUCACAO("Transferência de Convênios - Educação"),
    TRANSFCONVENIOSAUDE("Transferência de Convênios - Saúde"),
    TRANSFACONVENIOSOUTROS("Transferência de Convênios - Outros (Não relacionados a educação/saúde)");
    private String descricao;

    DestinacaoRecurso(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}

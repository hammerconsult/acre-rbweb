/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * @author renato
 */
@GrupoDiagrama(nome = "Licitacao")
public enum TipoComissao implements EnumComDescricao {
    CONCURSO_PUBLICO("Concurso Público"),
    ESPECIAL("Especial"),
    LEILOEIRO("Leiloeiro"),
    PERMANENTE("Permanente"),
    PREGOEIRO("Pregoeiro"),
    SERVIDOR_DESIGNADO("Servidor Designado");
    private String descricao;

    TipoComissao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

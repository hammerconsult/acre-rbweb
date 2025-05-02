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
public enum AtribuicaoComissao {

    PRESIDENTE_PREGOEIRO("Presidente/Pregoeiro") ,
    MEMBRO_PREGOEIRO("Membro/Pregoeiro") ,
    LEILOEIRO("Leiloeiro"),
    MEMBRO("Membro"),
    PRESIDENTE("Presidente"),
    SECRETARIO("Secretário"),
    SERVIDOR_DESIGNADO("Servidor Designado"),
    PREGOEIRO("Pregoeiro");
    private String descricao;

    private AtribuicaoComissao(String descricao) {
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
}

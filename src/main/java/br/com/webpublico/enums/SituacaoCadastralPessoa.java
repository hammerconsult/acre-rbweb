/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

/**
 * @author Jaime
 */
@GrupoDiagrama(nome = "CadastroEconomico")
public enum SituacaoCadastralPessoa {

    ATIVO("Ativo", "Ativos"),
    INATIVO("Inativo", "Inativos"),
    BAIXADO("Baixado", "Baixados"),
    SUSPENSO("Suspenso", "Suspensos"),
    REJEITADO("Rejeitado", "Rejeitados"),
    AGUARDANDO_CONFIRMACAO_CADASTRO("Aguardando Confirmação de Cadastro", "Aguardando Confirmações de Cadastro");
    private String descricao;
    private String descricaoPlural;

    SituacaoCadastralPessoa(String descricao, String descricaoPlural) {
        this.descricao = descricao;
        this.descricaoPlural = descricaoPlural;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getDescricaoPlural() {
        return descricaoPlural;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

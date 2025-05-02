package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public enum SituacaoContrato implements EnumComDescricao {

    EM_ELABORACAO("Em Elaboração"),
    APROVADO("Aprovado"),
    RESCINDIDO("Rescindido"),
    DEFERIDO("Deferido");
    private String descricao;

    SituacaoContrato(String descricao) {
        this.descricao = descricao;
    }

    public static List<String> getSituacoesAsString(List<SituacaoContrato> situacoes) {
        List<String> toReturn = Lists.newArrayList();
        for (SituacaoContrato tipo : situacoes) {
            toReturn.add(tipo.name());
        }
        return toReturn;
    }

    public static SituacaoContrato[] situacoesDiferenteEmElaboracao =  {
        SituacaoContrato.APROVADO,
        SituacaoContrato.DEFERIDO
    };

    public String getDescricao() {
        return descricao;
    }

    public boolean isDeferido() {
        return SituacaoContrato.DEFERIDO.equals(this);
    }

    public boolean isEmElaboracao() {
        return SituacaoContrato.EM_ELABORACAO.equals(this);
    }

    public boolean isAprovado() {
        return SituacaoContrato.APROVADO.equals(this);
    }

    public boolean isRescindido() {
        return SituacaoContrato.RESCINDIDO.equals(this);
    }

    @Override
    public String toString() {
        return descricao;
    }
}

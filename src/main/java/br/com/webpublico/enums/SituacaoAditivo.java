package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import com.google.common.collect.Lists;

import java.util.List;

public enum SituacaoAditivo implements EnumComDescricao {

    EM_ELABORACAO("Em Elaboração"),
    APROVADO("Aprovado"),
    DEFERIDO("Deferido");
    private String descricao;

    SituacaoAditivo(String descricao) {
        this.descricao = descricao;
    }

    public static List<String> getSituacoesAsString(List<SituacaoAditivo> situacoes) {
        List<String> toReturn = Lists.newArrayList();
        for (SituacaoAditivo tipo : situacoes) {
            toReturn.add(tipo.name());
        }
        return toReturn;
    }

    public static SituacaoAditivo[] situacoesDiferenteEmElaboracao =  {
        SituacaoAditivo.APROVADO,
        SituacaoAditivo.DEFERIDO
    };

    public String getDescricao() {
        return descricao;
    }

    public boolean isDeferido() {
        return SituacaoAditivo.DEFERIDO.equals(this);
    }

    public boolean isEmElaboracao() {
        return SituacaoAditivo.EM_ELABORACAO.equals(this);
    }

    public boolean isAprovado() {
        return SituacaoAditivo.APROVADO.equals(this);
    }
}

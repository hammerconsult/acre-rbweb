package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by mga on 02/05/2018.
 */
public enum SituacaoRequisicaoCompra implements EnumComDescricao {

    EM_ELABORACAO("Em Elaboração"),
    EFETIVADA("Efetivada"),
    AGUARDANDO_EFETIVACAO("Aguardando Efetivação"),
    RECUSADA("Recusada"),
    ESTORNADA("Estornada"),
    ESTORNADA_PARCIAL("Estornada Parcial");
    private String descricao;

    SituacaoRequisicaoCompra(String descricao) {
        this.descricao = descricao;
    }

    public static List<String> getSituacoesAsString(List<SituacaoRequisicaoCompra> situacoes) {
        List<String> list = Lists.newArrayList();
        for (SituacaoRequisicaoCompra situacao : situacoes) {
            list.add(situacao.name());
        }
        return list;
    }

    public static List<SituacaoRequisicaoCompra> getSituacoesPermiteMovimentacoes() {
        List<SituacaoRequisicaoCompra> list = Lists.newArrayList();
        list.add(SituacaoRequisicaoCompra.EM_ELABORACAO);
        list.add(SituacaoRequisicaoCompra.ESTORNADA_PARCIAL);
        list.add(SituacaoRequisicaoCompra.RECUSADA);
        return list;
    }

    public boolean isEstornada(){
        return ESTORNADA.equals(this);
    }

    public boolean isEfetivada(){
        return EFETIVADA.equals(this);
    }

    public String getDescricao() {
        return descricao;
    }
}

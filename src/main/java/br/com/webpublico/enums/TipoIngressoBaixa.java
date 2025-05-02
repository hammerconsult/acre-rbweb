package br.com.webpublico.enums;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: BRAINIAC-USER
 * Date: 05/05/14
 * Time: 14:01
 * To change this template use File | Settings | File Templates.
 */
public enum TipoIngressoBaixa {
    INCORPORACAO("Incorporação"),
    COMPRA("Compra"),
    CONSUMO("Consumo"),
    DESINCORPORACAO("Desincorporação");
    private final String descricao;

    private TipoIngressoBaixa(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public static List<TipoIngressoBaixa> buscarTiposIngresso() {
        List<TipoIngressoBaixa> retorno = Lists.newArrayList();
        retorno.add(INCORPORACAO);
        retorno.add(COMPRA);
        return retorno;
    }

    public static List<TipoIngressoBaixa> buscarTiposBaixa() {
        List<TipoIngressoBaixa> retorno = Lists.newArrayList();
        retorno.add(CONSUMO);
        retorno.add(DESINCORPORACAO);
        return retorno;
    }
}

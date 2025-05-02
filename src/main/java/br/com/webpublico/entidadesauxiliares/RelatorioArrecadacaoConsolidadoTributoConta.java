package br.com.webpublico.entidadesauxiliares;

import com.google.common.collect.Lists;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 27/03/15
 * Time: 16:34
 * To change this template use File | Settings | File Templates.
 */
public class RelatorioArrecadacaoConsolidadoTributoConta implements Serializable {

    private List<RelatorioArrecadacaoConsolidadoTributoContaAtributos> arrecadacoes;
    private List<RelatorioArrecadacaoConsolidadoTributoContaAtributos> acrescimos;
    private List<RelatorioArrecadacaoConsolidadoTributoContaAtributos> descontos;

    public RelatorioArrecadacaoConsolidadoTributoConta() {
        arrecadacoes = Lists.newArrayList();
        acrescimos = Lists.newArrayList();
        descontos = Lists.newArrayList();
    }

    public void addArrecadacao(AgrupamentoMapaConsolidadoTributoConta agrupamento, BigDecimal valor) {
        this.arrecadacoes.add(new RelatorioArrecadacaoConsolidadoTributoContaAtributos(agrupamento.getBanco(),
            agrupamento.getContaReceita(), agrupamento.getTributo(), agrupamento.getDataArrecadacao(), agrupamento.getExercicio(), valor));
    }


    public void addAcrescimo(AgrupamentoMapaConsolidadoTributoConta agrupamento, BigDecimal valor) {
        this.acrescimos.add(new RelatorioArrecadacaoConsolidadoTributoContaAtributos(agrupamento.getBanco(),
            agrupamento.getContaReceita(), agrupamento.getTributo(), agrupamento.getDataArrecadacao(), agrupamento.getExercicio(), valor));
    }


    public void addDesconto(AgrupamentoMapaConsolidadoTributoConta agrupamento, BigDecimal valor) {
        this.descontos.add(new RelatorioArrecadacaoConsolidadoTributoContaAtributos(agrupamento.getBanco(),
            agrupamento.getContaReceita(), agrupamento.getTributo(), agrupamento.getDataArrecadacao(), agrupamento.getExercicio(), valor));
    }

    public List<RelatorioArrecadacaoConsolidadoTributoContaAtributos> getArrecadacoes() {
        return arrecadacoes;
    }

    public void setArrecadacoes(List<RelatorioArrecadacaoConsolidadoTributoContaAtributos> arrecadacoes) {
        this.arrecadacoes = arrecadacoes;
    }

    public List<RelatorioArrecadacaoConsolidadoTributoContaAtributos> getAcrescimos() {
        return acrescimos;
    }

    public void setAcrescimos(List<RelatorioArrecadacaoConsolidadoTributoContaAtributos> acrescimos) {
        this.acrescimos = acrescimos;
    }

    public List<RelatorioArrecadacaoConsolidadoTributoContaAtributos> getDescontos() {
        return descontos;
    }

    public void setDescontos(List<RelatorioArrecadacaoConsolidadoTributoContaAtributos> descontos) {
        this.descontos = descontos;
    }

}

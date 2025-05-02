package br.com.webpublico.negocios.administrativo.reducaovalorbem;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoReducaoValorBem;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * User: JoãoPaulo
 * Date: 08/10/14
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
public class ProcessamentoReducaoValorBem {

    private List<ReducaoValorBem> reducoes;
    private List<MsgLogReducaoOuEstorno> msgs;
    private List<ReducaoValorBemNaoAplicavel> naoAplicaeis;
    private List<ReducaoValorBemResidual> bensResidual;
    private Map<GrupoBem, TipoReducao> tipoReducaoPorGrupoBem;
    private List<Bem> bensDepreciacao;
    private List<Bem> bensExaustao;
    private List<Bem> bensAmortizacao;

    public ProcessamentoReducaoValorBem() {
        this.reducoes = Lists.newArrayList();
        this.msgs = Lists.newArrayList();
        this.naoAplicaeis = Lists.newArrayList();
        this.bensResidual = Lists.newArrayList();
        this.tipoReducaoPorGrupoBem = Maps.newHashMap();
        this.bensDepreciacao = Lists.newArrayList();
        this.bensExaustao = Lists.newArrayList();
        this.bensAmortizacao = Lists.newArrayList();
    }

    public List<ReducaoValorBem> getReducoes() {
        return reducoes;
    }

    public void setReducoes(List<ReducaoValorBem> reducoes) {
        this.reducoes = reducoes;
    }

    public List<MsgLogReducaoOuEstorno> getMsgs() {
        return msgs;
    }

    public void setMsgs(List<MsgLogReducaoOuEstorno> msgs) {
        this.msgs = msgs;
    }

    public List<ReducaoValorBemNaoAplicavel> getNaoAplicaeis() {
        return naoAplicaeis;
    }

    public void setNaoAplicaeis(List<ReducaoValorBemNaoAplicavel> naoAplicaeis) {
        this.naoAplicaeis = naoAplicaeis;
    }

    public List<ReducaoValorBemResidual> getBensResidual() {
        return bensResidual;
    }

    public void setBensResidual(List<ReducaoValorBemResidual> bensResidual) {
        this.bensResidual = bensResidual;
    }

    public Map<GrupoBem, TipoReducao> getTipoReducaoPorGrupoBem() {
        return tipoReducaoPorGrupoBem;
    }

    public void setTipoReducaoPorGrupoBem(Map<GrupoBem, TipoReducao> tipoReducaoPorGrupoBem) {
        this.tipoReducaoPorGrupoBem = tipoReducaoPorGrupoBem;
    }

    public TipoReducao getTipoReducaoCache(GrupoBem grupoBem) {
        return tipoReducaoPorGrupoBem.get(grupoBem);
    }

    public void adicionarTipoReducaoCache(GrupoBem grupoBem, TipoReducao tipoReducao) {
        tipoReducaoPorGrupoBem.put(grupoBem, tipoReducao);
    }

    public List<Bem> getBensDepreciacao() {
        return bensDepreciacao;
    }

    public void setBensDepreciacao(List<Bem> bensDepreciacao) {
        this.bensDepreciacao = bensDepreciacao;
    }

    public List<Bem> getBensExaustao() {
        return bensExaustao;
    }

    public void setBensExaustao(List<Bem> bensExaustao) {
        this.bensExaustao = bensExaustao;
    }

    public List<Bem> getBensAmortizacao() {
        return bensAmortizacao;
    }

    public void setBensAmortizacao(List<Bem> bensAmortizacao) {
        this.bensAmortizacao = bensAmortizacao;
    }

    public BigDecimal getValorDepreciacao() {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (Bem bem : bensDepreciacao) {
            toReturn = toReturn.add(bem.getValorOriginal());
        }
        return toReturn;
    }

    public BigDecimal getValorExaustao() {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (Bem bem : bensExaustao) {
            toReturn = toReturn.add(bem.getValorOriginal());
        }
        return toReturn;
    }

    public BigDecimal getValorAmortizacao() {
        BigDecimal toReturn = BigDecimal.ZERO;
        for (Bem bem : bensAmortizacao) {
            toReturn = toReturn.add(bem.getValorOriginal());
        }
        return toReturn;
    }

    public void atribuirBemPorTipoReducao(TipoReducaoValorBem tipoReducaoValorBem, Bem bem) {
        switch (tipoReducaoValorBem) {
            case DEPRECIACAO: {
                getBensDepreciacao().add(bem);
                break;
            }
            case EXAUSTAO: {
                getBensExaustao().add(bem);
                break;
            }
            case AMORTIZACAO: {
                getBensAmortizacao().add(bem);
                break;
            }
        }
    }
}

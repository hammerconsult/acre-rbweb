package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.entidades.LotacaoFuncional;
import br.com.webpublico.entidades.RecursoDoVinculoFP;
import br.com.webpublico.entidades.VinculoFP;

/**
 * @Author peixe on 06/03/2017  09:02.
 */
public class ItemDirfCorrecao {
    private VinculoFP vinculoFP;

    private boolean naoDeveExcluirLotacao;
    private boolean naoDeveExcluirRecurso;

    private LotacaoFuncional atual;
    private LotacaoFuncional ultima;
    private LotacaoFuncional antePenultima;

    private RecursoDoVinculoFP recursoAtual;
    private RecursoDoVinculoFP recursoUltimo;
    private RecursoDoVinculoFP recursoAntePenultimo;

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public boolean isNaoDeveExcluirLotacao() {
        return naoDeveExcluirLotacao;
    }

    public void setNaoDeveExcluirLotacao(boolean naoDeveExcluirLotacao) {
        this.naoDeveExcluirLotacao = naoDeveExcluirLotacao;
    }

    public LotacaoFuncional getAtual() {
        return atual;
    }

    public void setAtual(LotacaoFuncional atual) {
        this.atual = atual;
    }

    public LotacaoFuncional getUltima() {
        return ultima;
    }

    public void setUltima(LotacaoFuncional ultima) {
        this.ultima = ultima;
    }

    public LotacaoFuncional getAntePenultima() {
        return antePenultima;
    }

    public void setAntePenultima(LotacaoFuncional antePenultima) {
        this.antePenultima = antePenultima;
    }

    public RecursoDoVinculoFP getRecursoAtual() {
        return recursoAtual;
    }

    public void setRecursoAtual(RecursoDoVinculoFP recursoAtual) {
        this.recursoAtual = recursoAtual;
    }

    public RecursoDoVinculoFP getRecursoUltimo() {
        return recursoUltimo;
    }

    public void setRecursoUltimo(RecursoDoVinculoFP recursoUltimo) {
        this.recursoUltimo = recursoUltimo;
    }

    public RecursoDoVinculoFP getRecursoAntePenultimo() {
        return recursoAntePenultimo;
    }

    public void setRecursoAntePenultimo(RecursoDoVinculoFP recursoAntePenultimo) {
        this.recursoAntePenultimo = recursoAntePenultimo;
    }

    public boolean isNaoDeveExcluirRecurso() {
        return naoDeveExcluirRecurso;
    }

    public void setNaoDeveExcluirRecurso(boolean naoDeveExcluirRecurso) {
        this.naoDeveExcluirRecurso = naoDeveExcluirRecurso;
    }
}

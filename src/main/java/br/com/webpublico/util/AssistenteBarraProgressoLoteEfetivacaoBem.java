package br.com.webpublico.util;

import br.com.webpublico.entidades.LoteEfetivacaoLevantamentoBem;

public class AssistenteBarraProgressoLoteEfetivacaoBem extends AssistenteBarraProgresso {
    private LoteEfetivacaoLevantamentoBem lote;
    private ProcessoEfetivacaoLevantamentoBens processoEfetivacaoLevantamentoBens;

    public ProcessoEfetivacaoLevantamentoBens getProcessoEfetivacaoLevantamentoBens() {
        return processoEfetivacaoLevantamentoBens;
    }

    public void setProcessoEfetivacaoLevantamentoBens(ProcessoEfetivacaoLevantamentoBens processoEfetivacaoLevantamentoBens) {
        this.processoEfetivacaoLevantamentoBens = processoEfetivacaoLevantamentoBens;
    }

    public LoteEfetivacaoLevantamentoBem getLote() {
        return lote;
    }

    public void setLote(LoteEfetivacaoLevantamentoBem lote) {
        this.lote = lote;
    }

    public void zerarContadores() {
        setCalculados(0);
        setTotal(0);
        setTempo(System.currentTimeMillis());
    }

    public enum ProcessoEfetivacaoLevantamentoBens{
        CRIANDO_EFETIVACAO,
        PERSISTINDO_EFETIVACOES,
        REGISTRANDO_DEPRESIACAO,
        PESQUISANDO_PARA_ESTORNO,
        EFETIVANDO_ESTORNO;

    }
}

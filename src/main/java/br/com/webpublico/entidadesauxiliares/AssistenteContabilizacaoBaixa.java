package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.TipoOperacaoBensImoveis;
import br.com.webpublico.enums.TipoOperacaoBensMoveis;
import com.google.common.collect.Lists;

import java.util.List;

public class AssistenteContabilizacaoBaixa {

    private List<Long> itensSolicitacaoBaixa;
    private List<Long> itensParecerBaixa;
    private List<EventoBem> eventosApuracaoValorLiquido;
    private List<GanhoPerdaPatrimonial> registroGanhosPatrimonial;
    private List<GanhoPerdaPatrimonial> registroPerdasPatrimonial;
    private List<LeilaoAlienacaoLoteBem> itensAlienacao;
    private List<ItemEfetivacaoBaixaPatrimonial> itensEfetivacaoBaixa;
    private List<VOItemBaixaPatrimonial> bensParaContabilizar;
    private List<BensMoveis> bensMoveis;
    private List<BensImoveis> bensImoveis;
    private Exercicio exercicio;
    private EventoBem eventoBem;
    private String historico;
    private TipoOperacaoBensMoveis tipoOperacaoBensMoveis;
    private TipoOperacaoBensImoveis tipoOperacaoBensImoveis;

    public AssistenteContabilizacaoBaixa() {
        bensParaContabilizar = Lists.newArrayList();
        eventosApuracaoValorLiquido = Lists.newArrayList();
        registroGanhosPatrimonial = Lists.newArrayList();
        registroPerdasPatrimonial = Lists.newArrayList();
        itensSolicitacaoBaixa = Lists.newArrayList();
        itensParecerBaixa = Lists.newArrayList();
        itensEfetivacaoBaixa = Lists.newArrayList();
        itensAlienacao = Lists.newArrayList();
        bensMoveis = Lists.newArrayList();
        bensImoveis = Lists.newArrayList();
        historico = "";
    }

    public TipoOperacaoBensImoveis getTipoOperacaoBensImoveis() {
        return tipoOperacaoBensImoveis;
    }

    public void setTipoOperacaoBensImoveis(TipoOperacaoBensImoveis tipoOperacaoBensImoveis) {
        this.tipoOperacaoBensImoveis = tipoOperacaoBensImoveis;
    }

    public TipoOperacaoBensMoveis getTipoOperacaoBensMoveis() {
        return tipoOperacaoBensMoveis;
    }

    public void setTipoOperacaoBensMoveis(TipoOperacaoBensMoveis tipoOperacaoBensMoveis) {
        this.tipoOperacaoBensMoveis = tipoOperacaoBensMoveis;
    }

    public List<BensImoveis> getBensImoveis() {
        return bensImoveis;
    }

    public void setBensImoveis(List<BensImoveis> bensImoveis) {
        this.bensImoveis = bensImoveis;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public EventoBem getEventoBem() {
        return eventoBem;
    }

    public void setEventoBem(EventoBem eventoBem) {
        this.eventoBem = eventoBem;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<BensMoveis> getBensMoveis() {
        return bensMoveis;
    }

    public void setBensMoveis(List<BensMoveis> bensMoveis) {
        this.bensMoveis = bensMoveis;
    }

    public List<GanhoPerdaPatrimonial> getRegistroGanhosPatrimonial() {
        return registroGanhosPatrimonial;
    }

    public void setRegistroGanhosPatrimonial(List<GanhoPerdaPatrimonial> registroGanhosPatrimonial) {
        this.registroGanhosPatrimonial = registroGanhosPatrimonial;
    }

    public List<GanhoPerdaPatrimonial> getRegistroPerdasPatrimonial() {
        return registroPerdasPatrimonial;
    }

    public void setRegistroPerdasPatrimonial(List<GanhoPerdaPatrimonial> registroPerdasPatrimonial) {
        this.registroPerdasPatrimonial = registroPerdasPatrimonial;
    }

    public List<VOItemBaixaPatrimonial> getBensParaContabilizar() {
        return bensParaContabilizar;
    }

    public void setBensParaContabilizar(List<VOItemBaixaPatrimonial> bensParaContabilizar) {
        this.bensParaContabilizar = bensParaContabilizar;
    }

    public List<EventoBem> getEventosApuracaoValorLiquido() {
        return eventosApuracaoValorLiquido;
    }

    public void setEventosApuracaoValorLiquido(List<EventoBem> eventosApuracaoValorLiquido) {
        this.eventosApuracaoValorLiquido = eventosApuracaoValorLiquido;
    }

    public List<ItemEfetivacaoBaixaPatrimonial> getItensEfetivacaoBaixa() {
        return itensEfetivacaoBaixa;
    }

    public void setItensEfetivacaoBaixa(List<ItemEfetivacaoBaixaPatrimonial> itensEfetivacaoBaixa) {
        this.itensEfetivacaoBaixa = itensEfetivacaoBaixa;
    }

    public List<LeilaoAlienacaoLoteBem> getItensAlienacao() {
        return itensAlienacao;
    }

    public void setItensAlienacao(List<LeilaoAlienacaoLoteBem> itensAlienacao) {
        this.itensAlienacao = itensAlienacao;
    }

    public List<Long> getItensSolicitacaoBaixa() {
        return itensSolicitacaoBaixa;
    }

    public void setItensSolicitacaoBaixa(List<Long> itensSolicitacaoBaixa) {
        this.itensSolicitacaoBaixa = itensSolicitacaoBaixa;
    }

    public List<Long> getItensParecerBaixa() {
        return itensParecerBaixa;
    }

    public void setItensParecerBaixa(List<Long> itensParecerBaixa) {
        this.itensParecerBaixa = itensParecerBaixa;
    }
}

package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.contabil.reinf.FiltroReinf;
import br.com.webpublico.entidades.rh.esocial.ConfiguracaoEmpregadorESocial;
import br.com.webpublico.reinf.eventos.TipoArquivoReinf;
import br.com.webpublico.reinf.eventos.domain.*;
import com.google.common.collect.Lists;

import java.util.List;

public class AssistenteSincronizacaoReinf {

    private BarraProgressoItens barraProgressoItens;
    private ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial;
    private String mensagemErro;
    private List<String> mensagens = Lists.newArrayList();
    private List<EventoR1000> eventoR1000s;
    private List<EventoR1070> eventoR1070s;
    private List<EventoR2010> eventoR2010s;
    private List<EventoR2020> eventoR2020s;
    private List<EventoR4020> eventoR4020s;
    private TipoArquivoReinf tipoArquivo;

    private FiltroReinf selecionado;

    public void iniciarBarraProgresso(Integer tamanho) {
        BarraProgressoItens barraProgressoItens = this.getBarraProgressoItens();
        barraProgressoItens.inicializa();
        barraProgressoItens.setTotal(tamanho);
    }

    public void finalizar() {
        getBarraProgressoItens().finaliza();
    }

    public ConfiguracaoEmpregadorESocial getConfiguracaoEmpregadorESocial() {
        return configuracaoEmpregadorESocial;
    }

    public void setConfiguracaoEmpregadorESocial(ConfiguracaoEmpregadorESocial configuracaoEmpregadorESocial) {
        this.configuracaoEmpregadorESocial = configuracaoEmpregadorESocial;
    }

    public void setBarraProgressoItens(BarraProgressoItens barraProgressoItens) {
        this.barraProgressoItens = barraProgressoItens;
    }

    public BarraProgressoItens getBarraProgressoItens() {
        return barraProgressoItens;
    }

    public List<EventoR1000> getEventoR1000s() {
        return eventoR1000s;
    }

    public void setEventoR1000s(List<EventoR1000> eventoR1000s) {
        this.eventoR1000s = eventoR1000s;
    }

    public List<EventoR1070> getEventoR1070s() {
        return eventoR1070s;
    }

    public void setEventoR1070s(List<EventoR1070> eventoR1070s) {
        this.eventoR1070s = eventoR1070s;
    }

    public List<EventoR2010> getEventoR2010s() {
        return eventoR2010s;
    }

    public void setEventoR2010s(List<EventoR2010> eventoR2010s) {
        this.eventoR2010s = eventoR2010s;
    }

    public List<EventoR2020> getEventoR2020s() {
        return eventoR2020s;
    }

    public void setEventoR2020s(List<EventoR2020> eventoR2020s) {
        this.eventoR2020s = eventoR2020s;
    }

    public List<EventoR4020> getEventoR4020s() {
        return eventoR4020s;
    }

    public void setEventoR4020s(List<EventoR4020> eventoR4020s) {
        this.eventoR4020s = eventoR4020s;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<String> mensagens) {
        this.mensagens = mensagens;
    }

    public String getMensagemErro() {
        return mensagemErro;
    }

    public void setMensagemErro(String mensagemErro) {
        this.mensagemErro = mensagemErro;
    }

    public FiltroReinf getSelecionado() {
        return selecionado;
    }

    public void setSelecionado(FiltroReinf selecionado) {
        this.selecionado = selecionado;
    }

    public TipoArquivoReinf getTipoArquivo() {
        return tipoArquivo;
    }

    public void setTipoArquivo(TipoArquivoReinf tipoArquivo) {
        this.tipoArquivo = tipoArquivo;
    }
}

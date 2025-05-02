package br.com.webpublico.entidadesauxiliares.contabil;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by renat on 08/11/2016.
 */
public class BarraProgressoAssistente {
    private static final String formatoDataHora = "%d:%2$TM:%2$TS%n";
    private Boolean calculando;
    private String mensagem;
    private Double quantoFalta;
    private Integer total;
    private Long decorrido;
    private Long tempo;
    private Integer processados;
    private List<String> mensagens;
    private List<String> mensagensComErro;


    public BarraProgressoAssistente() {
        mensagem = "";
        calculando = Boolean.FALSE;
        quantoFalta = 0D;
        total = 0;
        decorrido = 0L;
        tempo = 0L;
        processados = 0;
        getTempoDecorrido();
        mensagens = Lists.newArrayList();
        mensagensComErro = Lists.newArrayList();
    }

    public Boolean getCalculando() {
        return calculando;
    }

    public void setCalculando(Boolean calculando) {
        this.calculando = calculando;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public List<String> getMensagens() {
        return mensagens;
    }

    public void setMensagens(List<String> mensagens) {
        this.mensagens = mensagens;
    }

    public List<String> getMensagensComErro() {
        return mensagensComErro;
    }

    public void setMensagensComErro(List<String> mensagensComErro) {
        this.mensagensComErro = mensagensComErro;
    }

    public Double getQuantoFalta() {
        return quantoFalta;
    }

    public void setQuantoFalta(Double quantoFalta) {
        this.quantoFalta = quantoFalta;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Long getDecorrido() {
        return decorrido;
    }

    public void setDecorrido(Long decorrido) {
        this.decorrido = decorrido;
    }

    public Long getTempo() {
        return tempo;
    }

    public void setTempo(Long tempo) {
        this.tempo = tempo;
    }

    public Integer getProcessados() {
        return processados;
    }

    public void setProcessados(Integer processados) {
        this.processados = processados;
    }

    public Double getPorcentagemDoCalculo() {
        if (this.processados == null || this.total == null) {
            return 0d;
        }
        return (this.processados.doubleValue() / this.total.doubleValue()) * 100;
    }

    public String getTempoDecorrido() {
        try {
            long HOUR = TimeUnit.HOURS.toMillis(1);
            this.decorrido = System.currentTimeMillis() - this.tempo;

            return String.format(formatoDataHora,  this.decorrido / HOUR, this.decorrido % HOUR);
        } catch (Exception e) {
            return "0";
        }
    }

    public String getTempoEstimado() {
        try {
            long HOUR = TimeUnit.HOURS.toMillis(1);
            long unitario = (System.currentTimeMillis() - this.tempo) / (this.processados + 1);
            Double qntoFalta = (unitario * this.total - this.processados.doubleValue());

            return String.format(formatoDataHora, qntoFalta.longValue() / HOUR, qntoFalta.longValue() % HOUR);
        } catch (Exception e) {
            return "0";
        }
    }

    public void inicializa() {
        this.processados = 0;
        this.total = 0;
        this.calculando = Boolean.TRUE;
        this.tempo = System.currentTimeMillis();
    }

    public void addMensagem(String msg) {
        this.mensagens.add(msg);
    }

    public void addMensagemErro(String msg) {
        this.mensagensComErro.add(msg);
    }

    public void finaliza() {
        this.calculando = Boolean.FALSE;
    }
}

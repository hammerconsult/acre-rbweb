package br.com.webpublico.entidadesauxiliares;

import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 21/05/15
 * Time: 15:38
 * To change this template use File | Settings | File Templates.
 */
public class BarraProgressoItens {

    private static final String formatoDataHora = "%d:%2$TM:%2$TS%n";
    private Boolean calculando;
    private String mensagens;
    private Double quantoFalta;
    private Integer total;
    private Long decorrido;
    private Long tempo;
    private Integer processados;

    public BarraProgressoItens() {
        mensagens = "";
        calculando = Boolean.FALSE;
        quantoFalta = 0D;
        total = 0;
        decorrido = 0L;
        tempo = 0L;
        processados = 0;
        getTempoDecorrido();
    }

    public Boolean getCalculando() {
        return calculando;
    }

    public void setCalculando(Boolean calculando) {
        this.calculando = calculando;
    }

    public String getMensagens() {
        return mensagens;
    }

    public void setMensagens(String mensagens) {
        this.mensagens = mensagens;
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

    public String addMensagem(String msg) {
        this.mensagens = msg;
        return mensagens;
    }

    public String addMensagemNegrito(String msg) {
        this.mensagens = "<b> <font color='black'>"+msg+"</font> </b>";
        return mensagens;
    }

    public void finaliza() {
        this.calculando = Boolean.FALSE;
    }
}

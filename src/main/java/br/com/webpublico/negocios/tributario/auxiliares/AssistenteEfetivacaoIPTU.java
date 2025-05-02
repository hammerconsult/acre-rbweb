package br.com.webpublico.negocios.tributario.auxiliares;


import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.UsuarioSistema;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class AssistenteEfetivacaoIPTU implements Serializable {

    private Integer totalCalculos;
    private Integer calculosProcessados;
    private boolean processando;
    private TipoProcesso tipo;
    private Exercicio exercicio;
    private String log;
    private UsuarioSistema usuarioSistema;

    public AssistenteEfetivacaoIPTU() {
        zerarTudo();
        log = "";
    }

    public Integer getTotalCalculos() {
        return totalCalculos;
    }

    public Integer getCalculosProcessados() {
        return calculosProcessados;
    }

    public boolean getProcessando() {
        return processando;
    }

    public TipoProcesso getTipo() {
        return tipo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    private void zerarTudo() {
        calculosProcessados = 0;
        totalCalculos = 0;
        processando = false;
    }

    public void inicializar(Integer totalCalculos, TipoProcesso tipo) {
        zerarTudo();
        this.totalCalculos = totalCalculos;
        this.processando = true;
        this.tipo = tipo;
    }

    public void abortar(){
        processando = false;
    }

    public synchronized void finalizar() {
       if(calculosProcessados >= totalCalculos){
           abortar();
       }
    }

    public Double getPorcentagem() {
        try {
            if (totalCalculos > 0) {
                BigDecimal divisao = BigDecimal.valueOf(calculosProcessados).divide(BigDecimal.valueOf(totalCalculos), 4, RoundingMode.HALF_UP);
                return divisao.multiply(BigDecimal.valueOf(100)).doubleValue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0d;
    }

    public synchronized void  contarLinha() {
       calculosProcessados++;
    }

    public enum TipoProcesso{
        CANCELAMENTO, EFETIVACAO;
    }

    public String getLog() {
        return log != null ? log.toString() : "";
    }

    public void addLog(String log){
        this.log = log;
    }
}

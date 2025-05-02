package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TipoLancamentoFP;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by peixe on 15/09/2015.
 */
public class EventoFPRejeitado implements Serializable {


    private String verba;
    private BigDecimal valor;
    private String motivoRejeicao;
    private TipoLancamentoFP tipoLancamentoFP;
    private Date inicioVigencia;
    private Date finalVigencia;
    private String periodo;

    public TipoLancamentoFP getTipoLancamentoFP() {
        return tipoLancamentoFP;
    }

    public void setTipoLancamentoFP(TipoLancamentoFP tipoLancamentoFP) {
        this.tipoLancamentoFP = tipoLancamentoFP;
    }

    public String getVerba() {
        return verba;
    }

    public void setVerba(String verba) {
        this.verba = verba;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public String getMotivoRejeicao() {
        return motivoRejeicao;
    }

    public void setMotivoRejeicao(String motivoRejeicao) {
        this.motivoRejeicao = motivoRejeicao;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public static class Comparators {

        public static Comparator<EventoFPRejeitado> EVENTO_VERBA = new Comparator<EventoFPRejeitado>() {
            @Override
            public int compare(EventoFPRejeitado o1, EventoFPRejeitado o2) {
                Date inicio1 = o1.getInicioVigencia();
                Date inicio2 = o2.getInicioVigencia();
                int comparacao = inicio1.compareTo(inicio2);
                if (comparacao == 0) {
                    String verba1 = o1.getVerba();
                    String verba2 = o2.getVerba();
                    return verba1.compareTo(verba2);
                }
                return comparacao;
            }
        };
    }
}

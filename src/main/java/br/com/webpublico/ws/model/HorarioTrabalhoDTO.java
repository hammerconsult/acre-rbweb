package br.com.webpublico.ws.model;

import java.math.BigDecimal;

public class HorarioTrabalhoDTO {

    private BigDecimal idHorarioTrabalho;
    private String horarioEntrada;
    private String horarioSaidaIntervalo;
    private String horarioRetornoIntervalo;
    private String horarioSaida;


    public HorarioTrabalhoDTO() {
    }

    public BigDecimal getIdHorarioTrabalho() {
        return idHorarioTrabalho;
    }

    public void setIdHorarioTrabalho(BigDecimal idHorarioTrabalho) {
        this.idHorarioTrabalho = idHorarioTrabalho;
    }

    public String getHorarioEntrada() {
        return horarioEntrada;
    }

    public void setHorarioEntrada(String horarioEntrada) {
        this.horarioEntrada = horarioEntrada;
    }

    public String getHorarioSaidaIntervalo() {
        return horarioSaidaIntervalo;
    }

    public void setHorarioSaidaIntervalo(String horarioSaidaIntervalo) {
        this.horarioSaidaIntervalo = horarioSaidaIntervalo;
    }

    public String getHorarioRetornoIntervalo() {
        return horarioRetornoIntervalo;
    }

    public void setHorarioRetornoIntervalo(String horarioRetornoIntervalo) {
        this.horarioRetornoIntervalo = horarioRetornoIntervalo;
    }

    public String getHorarioSaida() {
        return horarioSaida;
    }

    public void setHorarioSaida(String horarioSaida) {
        this.horarioSaida = horarioSaida;
    }
}

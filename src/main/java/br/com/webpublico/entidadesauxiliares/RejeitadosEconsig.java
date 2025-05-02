package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.VinculoFP;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by peixe on 15/09/2015.
 */
public class RejeitadosEconsig implements Serializable {

    private Long id;
    private String matricula;
    private String numeroContrato;
    private String nome;
    private String mes;
    private Integer ano;
    private List<EventoFPRejeitado> eventosRejeitados;
    private VinculoFP vinculoFP;
    private Integer totalRejeitados;
    private BigDecimal valorTotal;

    public RejeitadosEconsig() {
        eventosRejeitados = new LinkedList<>();
        totalRejeitados = 0;
        valorTotal = BigDecimal.ZERO;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getMes() {
        return mes;
    }

    public void setMes(String mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public List<EventoFPRejeitado> getEventosRejeitados() {
        return eventosRejeitados;
    }

    public void setEventosRejeitados(List<EventoFPRejeitado> eventosRejeitados) {
        this.eventosRejeitados = eventosRejeitados;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroContrato() {
        return numeroContrato;
    }

    public void setNumeroContrato(String numeroContrato) {
        this.numeroContrato = numeroContrato;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getTotalRejeitados() {
        return totalRejeitados;
    }

    public void setTotalRejeitados(Integer totalRejeitados) {
        this.totalRejeitados = totalRejeitados;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RejeitadosEconsig)) return false;
        //if (!super.equals(o)) return false;

        RejeitadosEconsig that = (RejeitadosEconsig) o;

        return id.equals(that.vinculoFP.getId());
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (id != null ? id.hashCode() : 0);
        return hash;
    }
}

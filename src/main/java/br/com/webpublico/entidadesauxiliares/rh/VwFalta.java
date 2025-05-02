package br.com.webpublico.entidadesauxiliares.rh;

import br.com.webpublico.enums.TipoFalta;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Date;

/**
 * @Author peixe on 05/04/2017  11:01.
 */
@Entity
public class VwFalta implements Serializable {
    @Column(name = "CONTRATOFP_ID", insertable = false, updatable = false, unique = false)
    private BigDecimal contratoFP;
    @Id
    @Column(name = "FALTA_ID", insertable = false, updatable = false, unique = false)
    private BigDecimal falta;
    @Column(name = "JUSTIFICATIVA_ID", insertable = false, updatable = false, unique = false)
    private BigDecimal justificativa;
    private Date inicio;
    private Date fim;
    private Date inicioJustificativa;
    private Date fimJustificativa;
    private Integer totalFaltas;
    private Integer totalFaltasEfetivas;
    private Integer totalFaltasJustificadas;
    @Enumerated(EnumType.STRING)
    private TipoFalta tipoFalta;
    @Column(name = "CIDFALTA_ID", insertable = false, updatable = false, unique = false)
    private BigDecimal cidfalta;
    @Column(name = "MEDICOFALTA_ID", insertable = false, updatable = false, unique = false)
    private BigDecimal medicoFalta;
    private String observacaoFalta;
    @Column(name = "CIDJUSTIFICATIVA_ID", insertable = false, updatable = false, unique = false)
    private BigDecimal cidJustificativa;
    @Column(name = "MEDICOJUSTIFICATIVA_ID", insertable = false, updatable = false, unique = false)
    private BigDecimal medicojustificativa;
    private String observacaoJustificativa;
    private Date dataParaCalculo;
    private Date dataCadastro;

    public VwFalta() {
    }

    public BigDecimal getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(BigDecimal contratoFP) {
        this.contratoFP = contratoFP;
    }

    public BigDecimal getFalta() {
        return falta;
    }

    public void setFalta(BigDecimal falta) {
        this.falta = falta;
    }

    public BigDecimal getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(BigDecimal justificativa) {
        this.justificativa = justificativa;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public Date getInicioJustificativa() {
        return inicioJustificativa;
    }

    public void setInicioJustificativa(Date inicioJustificativa) {
        this.inicioJustificativa = inicioJustificativa;
    }

    public Date getFimJustificativa() {
        return fimJustificativa;
    }

    public void setFimJustificativa(Date fimJustificativa) {
        this.fimJustificativa = fimJustificativa;
    }

    public Integer getTotalFaltas() {
        return totalFaltas;
    }

    public void setTotalFaltas(Integer totalFaltas) {
        this.totalFaltas = totalFaltas;
    }

    public Integer getTotalFaltasEfetivas() {
        return totalFaltasEfetivas;
    }

    public void setTotalFaltasEfetivas(Integer totalFaltasEfetivas) {
        this.totalFaltasEfetivas = totalFaltasEfetivas;
    }

    public Integer getTotalFaltasJustificadas() {
        return totalFaltasJustificadas;
    }

    public void setTotalFaltasJustificadas(Integer totalFaltasJustificadas) {
        this.totalFaltasJustificadas = totalFaltasJustificadas;
    }

    public TipoFalta getTipoFalta() {
        return tipoFalta;
    }

    public void setTipoFalta(TipoFalta tipoFalta) {
        this.tipoFalta = tipoFalta;
    }

    public BigDecimal getCidfalta() {
        return cidfalta;
    }

    public void setCidfalta(BigDecimal cidfalta) {
        this.cidfalta = cidfalta;
    }

    public BigDecimal getMedicoFalta() {
        return medicoFalta;
    }

    public void setMedicoFalta(BigDecimal medicoFalta) {
        this.medicoFalta = medicoFalta;
    }

    public String getObservacaoFalta() {
        return observacaoFalta;
    }

    public void setObservacaoFalta(String observacaoFalta) {
        this.observacaoFalta = observacaoFalta;
    }

    public BigDecimal getCidJustificativa() {
        return cidJustificativa;
    }

    public void setCidJustificativa(BigDecimal cidJustificativa) {
        this.cidJustificativa = cidJustificativa;
    }

    public BigDecimal getMedicojustificativa() {
        return medicojustificativa;
    }

    public void setMedicojustificativa(BigDecimal medicojustificativa) {
        this.medicojustificativa = medicojustificativa;
    }

    public String getObservacaoJustificativa() {
        return observacaoJustificativa;
    }

    public void setObservacaoJustificativa(String observacaoJustificativa) {
        this.observacaoJustificativa = observacaoJustificativa;
    }

    public Date getDataParaCalculo() {
        return dataParaCalculo;
    }

    public void setDataParaCalculo(Date dataParaCalculo) {
        this.dataParaCalculo = dataParaCalculo;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}

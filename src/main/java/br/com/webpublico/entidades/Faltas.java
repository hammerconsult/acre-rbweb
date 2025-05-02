/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoFalta;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author peixe
 */
@Entity

@Audited
@Etiqueta("Faltas")
@GrupoDiagrama(nome = "RecursosHumanos")
public class Faltas extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Etiqueta("Servidor")
    @Obrigatorio
    @ColunaAuditoriaRH()
    private ContratoFP contratoFP;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo de Falta")
    @ColunaAuditoriaRH(posicao = 2)
    private TipoFalta tipoFalta;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @ColunaAuditoriaRH(posicao = 3)
    @Etiqueta("Início")
    private Date inicio;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    @ColunaAuditoriaRH(posicao = 4)
    @Etiqueta("Fim")
    private Date fim;
    @Etiqueta("Total de Dias")
    @Tabelavel
    @Pesquisavel
    @ColunaAuditoriaRH(posicao = 5)
    private Integer dias;
    @ManyToOne
    @Etiqueta("CID")
    private CID cid;
    @Etiqueta("Médico")
    @ManyToOne
    private Medico medico;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataCadastro;
    @Etiqueta("OBS")
    private String obs;
    @Transient
    private Double totalDias;
    @Transient
    private String tipoDaFalta;

    public Faltas() {
        super();
        dataCadastro = new Date();
        tipoFalta = TipoFalta.FALTA_INJUSTIFICADA;
        dias = 0;
    }

    public Faltas(ContratoFP contratoFP, Double totalDias) {
        this.contratoFP = contratoFP;
        this.totalDias = totalDias;
    }

    public int getDias() {
        if (inicio != null && fim != null) {
            DateTime ini = new DateTime(inicio);
            DateTime termino = new DateTime(fim);
            dias = Days.daysBetween(ini, termino).getDays() + 1;
            return dias;
        } else {
            return dias;
        }

    }

    public void setDias(Integer dias) {
        this.dias = dias;
    }

    public Double getTotalDias() {
        return totalDias;
    }

    public void setTotalDias(Double totalDias) {
        this.totalDias = totalDias;
    }

    public String getTipoDaFalta() {
        return tipoDaFalta;
    }

    public void setTipoDaFalta(String tipoDaFalta) {
        this.tipoDaFalta = tipoDaFalta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CID getCid() {
        return cid;
    }

    public void setCid(CID cid) {
        this.cid = cid;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Date getInicio() {
        return inicio;
    }

    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    public Medico getMedico() {
        return medico;
    }

    public void setMedico(Medico medico) {
        this.medico = medico;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    public TipoFalta getTipoFalta() {
        return tipoFalta;
    }

    public void setTipoFalta(TipoFalta tipoFalta) {
        this.tipoFalta = tipoFalta;
    }

    public Long calculaDatas(Date inicio, Date fim) {
        return ((fim.getTime() - inicio.getTime()) / (1000 * 60 * 60 * 24)) + 1;
    }

    @Override
    public String toString() {
        return contratoFP + "";
    }

    public String getDescricao() {
        String ini = getInicio() == null ? "" : Util.dateToString(inicio);
        String end = getInicio() == null ? "" : Util.dateToString(fim);
        return ini + " a " + end + " - " + tipoFalta;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoHoraExtra;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author andre
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
@Etiqueta("Hora Extra")
public class HoraExtra extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Servidor")
    @ManyToOne
    private ContratoFP contratoFP;
    @Etiqueta("Mês")
    @Tabelavel
    @Pesquisavel
    private Integer mes;
    @Etiqueta("Ano")
    @Tabelavel
    @Pesquisavel
    private Integer ano;
    @Etiqueta("Data de Lançamento")
    @Temporal(TemporalType.TIMESTAMP)
    @Pesquisavel
    private Date dataCadastro;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Inicial")
    @Obrigatorio
    private Date inicio;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data Final")
    @Obrigatorio
    private Date fim;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Tipo de Hora Extra")
    @Enumerated(EnumType.STRING)
    private TipoHoraExtra tipoHoraExtra;
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Total de Horas")
    private Integer totalHoras;
    private String observacao;


    public HoraExtra () {
        super();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public TipoHoraExtra getTipoHoraExtra() {
        return tipoHoraExtra;
    }

    public void setTipoHoraExtra(TipoHoraExtra tipoHoraExtra) {
        this.tipoHoraExtra = tipoHoraExtra;
    }

    public Integer getTotalHoras() {
        return totalHoras;
    }

    public void setTotalHoras(Integer totalHoras) {
        this.totalHoras = totalHoras;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
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

    public Date getFim() {
        return fim;
    }

    public void setFim(Date fim) {
        this.fim = fim;
    }

    public String getCompetencia() {
        String s = "0" + this.mes;
        s = s.substring(s.length() - 2, s.length()) + "/" + this.getAno();
        return s;
    }

    @Override
    public String toString() {
        return contratoFP + " - " + getCompetencia() + " - " + totalHoras;
    }
}

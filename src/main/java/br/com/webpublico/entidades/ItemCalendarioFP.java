/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.MesCalendarioPagamento;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * @author andre
 */
@Entity

@Audited
@GrupoDiagrama(nome = "RecursosHumanos")
public class ItemCalendarioFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    @Etiqueta("Mês")
    @Obrigatorio
    private MesCalendarioPagamento mesCalendarioPagamento;
    @Tabelavel
    @Etiqueta("Último dia para a entrega de documentos")
    private Integer diaEntregaDocumentos;
    @Tabelavel
    @Etiqueta("Dia do corte das consignações")
    private Integer diaCorteConsignacoes;
    @Tabelavel
    @Etiqueta("Último dia para processamento")
    private Integer ultimoDiaProcessamento;
    @Tabelavel
    @Etiqueta("Dia do pagamento")
    private Integer diaPagamento;
    @Obrigatorio
    @ManyToOne
    @Etiqueta("Calendário FP")
    private CalendarioFP calendarioFP;
    @Transient
    private Date dataDiaPagamento;
    @Transient
    private Date dataUltimoDiaProcessamento;
    @Transient
    private Date dataDiaCorteConsignacoes;
    @Transient
    private Date dataDiaEntregaDocumentos;

    public ItemCalendarioFP() {
    }

    public ItemCalendarioFP(MesCalendarioPagamento mesCalendarioPagamento, Integer diaEntregaDocumentos, Integer diaCorteConsignacoes, Integer ultimoDiaProcessamento, Integer diaPagamento, CalendarioFP calendarioFP) {
        this.mesCalendarioPagamento = mesCalendarioPagamento;
        this.diaEntregaDocumentos = diaEntregaDocumentos;
        this.diaCorteConsignacoes = diaCorteConsignacoes;
        this.ultimoDiaProcessamento = ultimoDiaProcessamento;
        this.diaPagamento = diaPagamento;
        this.calendarioFP = calendarioFP;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUltimoDiaProcessamento(Integer ultimoDiaProcessamento) {
        this.ultimoDiaProcessamento = ultimoDiaProcessamento;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    public CalendarioFP getCalendarioFP() {
        return calendarioFP;
    }

    public void setCalendarioFP(CalendarioFP calendarioFP) {
        this.calendarioFP = calendarioFP;
    }

    public Integer getDiaCorteConsignacoes() {
        return diaCorteConsignacoes;
    }

    public void setDiaCorteConsignacoes(Integer diaCorteConsignacoes) {
        this.diaCorteConsignacoes = diaCorteConsignacoes;
    }

    public Integer getDiaEntregaDocumentos() {
        return diaEntregaDocumentos;
    }

    public void setDiaEntregaDocumentos(Integer diaEntregaDocumentos) {
        this.diaEntregaDocumentos = diaEntregaDocumentos;
    }

    public Integer getDiaPagamento() {
        return diaPagamento;
    }

    public void setDiaPagamento(Integer diaPagamento) {
        this.diaPagamento = diaPagamento;
    }

    public Integer getUltimoDiaProcessamento() {
        return ultimoDiaProcessamento;
    }

    public void setDiaProcessamento(Integer UltimoDiaProcessamento) {
        this.ultimoDiaProcessamento = UltimoDiaProcessamento;
    }

    public MesCalendarioPagamento getMesCalendarioPagamento() {
        return mesCalendarioPagamento;
    }

    public void setMesCalendarioPagamento(MesCalendarioPagamento mesCalendarioPagamento) {
        this.mesCalendarioPagamento = mesCalendarioPagamento;
    }

    public Date getDataDiaPagamento() {
        try {
            Calendar c = Calendar.getInstance();
            c.set(getCalendarioFP().getAno(), mesCalendarioPagamento.ordinal(), diaPagamento);
            dataDiaPagamento = c.getTime();
        } catch (Exception e) {
            dataDiaPagamento = new Date();
        }
        return dataDiaPagamento;
    }

    public Date getDataDiaCorteConsignacoes() {
        try {
            Calendar c = Calendar.getInstance();
            c.set(getCalendarioFP().getAno(), mesCalendarioPagamento.ordinal(), diaCorteConsignacoes);
            dataDiaCorteConsignacoes = c.getTime();
        } catch (Exception e) {
//            //System.out.println("Parmetros do calendário folha nulos " + e.getMessage());
            dataDiaCorteConsignacoes = new Date();
        }
        return dataDiaCorteConsignacoes;
    }

    public Date getDataDiaEntregaDocumentos() {
        try {
            Calendar c = Calendar.getInstance();
            c.set(getCalendarioFP().getAno(), mesCalendarioPagamento.ordinal(), diaEntregaDocumentos);
            dataDiaEntregaDocumentos = c.getTime();
        } catch (Exception e) {
//            //System.out.println("Parmetros do calendário folha nulos " + e.getMessage());
            dataDiaEntregaDocumentos = new Date();
        }
        return dataDiaEntregaDocumentos;
    }

    public Date getDataUltimoDiaProcessamento() {
        try {
            Calendar c = Calendar.getInstance();
            c.set(getCalendarioFP().getAno(), mesCalendarioPagamento.ordinal(), ultimoDiaProcessamento);
            dataUltimoDiaProcessamento = c.getTime();
        } catch (Exception e) {
//            //System.out.println("Parmetros do calendário folha nulos " + e.getMessage());
            dataUltimoDiaProcessamento = new Date();
        }
        return dataUltimoDiaProcessamento;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof ItemCalendarioFP)) {
            return false;
        }
        ItemCalendarioFP other = (ItemCalendarioFP) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return mesCalendarioPagamento+"";
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;
import org.joda.time.DateTime;
import org.joda.time.Days;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author everton
 */
@Entity

@Audited
@Etiqueta("Penalidade")
@GrupoDiagrama(nome = "RecursosHumanos")
public class PenalidadeFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Etiqueta("Contrato do Servidor")
    @Obrigatorio
    @Tabelavel
    private ContratoFP contratoFP;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Início da Vigência")
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    private Date inicioVigencia;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Final da Vigência")
    @Pesquisavel
    @Obrigatorio
    @Tabelavel
    private Date finalVigencia;
    @ManyToOne
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Tipo de Penalidade")
    private TipoPenalidadeFP tipoPenalidadeFP;
    @ManyToOne
    @Etiqueta("Ato Legal")
    @Obrigatorio
    private AtoLegal atoLegal;
    @Etiqueta("Observação")
    private String observacao;
    @Transient
    private int dias;


    public int getDias() {
        if (inicioVigencia != null && finalVigencia != null) {
            DateTime ini = new DateTime(inicioVigencia);
            DateTime termino = new DateTime(finalVigencia);
            dias = Days.daysBetween(ini, termino).getDays() + 1;
            return dias;
        } else return dias;

    }

    public boolean ultrapassaLimiteDiasPenalidade() {
        return getDias() > tipoPenalidadeFP.getDiasDescontar();
    }

    public void setDias(int dias) {
        this.dias = dias;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getFinalVigencia() {
        return finalVigencia;
    }

    public void setFinalVigencia(Date finalVigencia) {
        this.finalVigencia = finalVigencia;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public TipoPenalidadeFP getTipoPenalidadeFP() {
        return tipoPenalidadeFP;
    }

    public void setTipoPenalidadeFP(TipoPenalidadeFP tipoPenalidadeFP) {
        this.tipoPenalidadeFP = tipoPenalidadeFP;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PenalidadeFP)) {
            return false;
        }
        PenalidadeFP other = (PenalidadeFP) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        String strToString = new String();

        strToString = strToString.concat("Início da Vigência: " + Util.dateToString(inicioVigencia) + " ");
        if (finalVigencia != null) {
            strToString = strToString.concat("Final da Vigência: " + Util.dateToString(finalVigencia) + " ");
        }

        strToString = strToString.concat(tipoPenalidadeFP.toString());
        return strToString;
    }
}

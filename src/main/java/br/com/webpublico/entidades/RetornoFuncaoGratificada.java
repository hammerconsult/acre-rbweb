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

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author peixe
 */
@Entity

@Audited
@Etiqueta("Encerramento Função Gratificada")
@GrupoDiagrama(nome = "RecursosHumanos")
public class RetornoFuncaoGratificada implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Servidor")
    @Tabelavel
    private ContratoFP contratoFP;
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Date de Retorno")
    @Tabelavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataRetorno;

    @Obrigatorio
    @Etiqueta("Ato Legal de Exoneração")
    @Tabelavel
    @ManyToOne
    private AtoLegal atoLegalExoneracao;
    @ManyToOne
    private ProvimentoFP provimentoFP;

    @Etiqueta("Observação")
    private String observacao;

    public RetornoFuncaoGratificada() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public ProvimentoFP getProvimentoFP() {
        return provimentoFP;
    }

    public void setProvimentoFP(ProvimentoFP provimentoFP) {
        this.provimentoFP = provimentoFP;
    }

    public AtoLegal getAtoLegalExoneracao() {
        return atoLegalExoneracao;
    }

    public void setAtoLegalExoneracao(AtoLegal atoLegalExoneracao) {
        this.atoLegalExoneracao = atoLegalExoneracao;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    public Date getDataRetorno() {
        return dataRetorno;
    }

    public void setDataRetorno(Date dataRetorno) {
        this.dataRetorno = dataRetorno;
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
        if (!(object instanceof RetornoFuncaoGratificada)) {
            return false;
        }
        RetornoFuncaoGratificada other = (RetornoFuncaoGratificada) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return contratoFP + " - " + Util.dateToString(dataRetorno);
    }
}

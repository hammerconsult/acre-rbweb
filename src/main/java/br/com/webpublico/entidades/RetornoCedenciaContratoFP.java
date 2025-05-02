/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
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
@Etiqueta("Retorno da Cedência")

public class RetornoCedenciaContratoFP implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @ColunaAuditoriaRH(posicao = 2)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Data do Retorno")
    private Date dataRetorno;
    @Tabelavel
    @Etiqueta("Ato Legal")
    @ManyToOne
    private AtoLegal atoLegal;
    @ColunaAuditoriaRH
    @ManyToOne
    @Tabelavel
    @Etiqueta("Servidor")
    @Obrigatorio
    private ContratoFP contratoFP;
    @ColunaAuditoriaRH(posicao = 3)
    @Obrigatorio
    @Etiqueta("Cedência do Servidor")
    @OneToOne
    private CedenciaContratoFP cedenciaContratoFP;
    @Etiqueta("Lotação Funcional")
    @ManyToOne
    private LotacaoFuncional lotacaoFuncional;
    @Tabelavel
    @Etiqueta("Ofício de Retorno")
    private Boolean oficioRetorno;

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

    public CedenciaContratoFP getCedenciaContratoFP() {
        return cedenciaContratoFP;
    }

    public void setCedenciaContratoFP(CedenciaContratoFP cedenciaContratoFP) {
        this.cedenciaContratoFP = cedenciaContratoFP;
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

    public LotacaoFuncional getLotacaoFuncional() {
        return lotacaoFuncional;
    }

    public void setLotacaoFuncional(LotacaoFuncional lotacaoFuncional) {
        this.lotacaoFuncional = lotacaoFuncional;
    }

    public Boolean getOficioRetorno() {
        return oficioRetorno;
    }

    public void setOficioRetorno(Boolean oficioRetorno) {
        this.oficioRetorno = oficioRetorno;
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
        if (!(object instanceof RetornoCedenciaContratoFP)) {
            return false;
        }
        RetornoCedenciaContratoFP other = (RetornoCedenciaContratoFP) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Servidor : " + contratoFP + " - Data do retorno : " + dataRetorno + " - Ato Legal : " + atoLegal;
    }
}

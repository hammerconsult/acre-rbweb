/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.StatusFiscalizacaoRBTrans;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author AndreGustavo
 */
@Entity
@Audited
@GrupoDiagrama(nome = "RBTrans")
@Table(name = "SITUACAOFISCRBTRANS")
public class SituacaoFiscalizacaoRBTrans implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataInicial;
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataFinal;
    @Enumerated(EnumType.STRING)
    @Tabelavel
    private StatusFiscalizacaoRBTrans statusFiscalizacao;
    @ManyToOne
    private FiscalizacaoRBTrans fiscalizacao;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date finalizadoEM;

    public Date getFinalizadoEM() {
        return finalizadoEM;
    }

    public void setFinalizadoEM(Date finalizadoEM) {
        this.finalizadoEM = finalizadoEM;
    }

    public FiscalizacaoRBTrans getFiscalizacao() {
        return fiscalizacao;
    }

    public void setFiscalizacao(FiscalizacaoRBTrans fiscalizacao) {
        this.fiscalizacao = fiscalizacao;
    }

    public boolean isVigente() {
//        boolean toReturn = false;
//        Date dataAtual = new Date();
//        if (dataInicial != null && dataFinal != null && finalizadoEM == null) {
//            //System.out.println("DATA INICIAL --- " + dataInicial.getTime());
//            //System.out.println("DATA FINAL   --- " + dataFinal.getTime());
//            if (dataInicial.getTime() <= dataAtual.getTime() && dataFinal.getTime() >= dataAtual.getTime()) {
//                toReturn = true;
//            }
//            //System.out.println("To Return ---- " + toReturn);
//            return toReturn;
//        }
//        else if (dataInicial != null && dataFinal == null && finalizadoEM == null) {
//            if (dataInicial.getTime() <= dataAtual.getTime()) {
//                return true;
//            }
//            return false;
//        }
//        return false;

        if (finalizadoEM == null) {
            return true;
        }
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public StatusFiscalizacaoRBTrans getStatusFiscalizacao() {
        return statusFiscalizacao;
    }

    public void setStatusFiscalizacao(StatusFiscalizacaoRBTrans statusFiscalizacao) {
        this.statusFiscalizacao = statusFiscalizacao;
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
        if (!(object instanceof SituacaoFiscalizacaoRBTrans)) {
            return false;
        }
        SituacaoFiscalizacaoRBTrans other = (SituacaoFiscalizacaoRBTrans) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    public String getCorSituacao() {
        if (!(statusFiscalizacao.equals(StatusFiscalizacaoRBTrans.ARQUIVADO) || statusFiscalizacao.equals(StatusFiscalizacaoRBTrans.FINALIZADO))) {
            Date hoje = new Date();

            if (getDataFinal().after(hoje)) {
                return "#228B22";
            } else {
                return "#F00";
            }
        }
        return "#000";
    }

    @Override
    public String toString() {
        return statusFiscalizacao.getDescricao();
    }
}

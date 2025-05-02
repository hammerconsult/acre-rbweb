/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author Leonardo
 */
@GrupoDiagrama(nome = "RecursosHumanos")
@Entity

@Audited
@Etiqueta("Promoção")
public class Promocao extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    @Tabelavel
    private ContratoFP contratoFP;
    @Etiqueta("Enquadramento Funcional Novo")
    @Pesquisavel
    @ManyToOne
    @Tabelavel
    private EnquadramentoFuncional enquadramentoNovo;
    @Etiqueta("Enquadramento Funcional Anterior")
    @Tabelavel
    @ManyToOne
    private EnquadramentoFuncional enquadramentoAnterior;
    @Etiqueta("Data da Promoção")
    @Tabelavel
    @Pesquisavel
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date dataPromocao;

    public Promocao() {
    }

    public Promocao(Long id, EnquadramentoFuncional enquadramentoNovo, EnquadramentoFuncional enquadramentoAnterior, Date dataPromocao, ContratoFP contratoFP) {
        this.id = id;
        this.enquadramentoNovo = enquadramentoNovo;
        this.enquadramentoAnterior = enquadramentoAnterior;
        this.dataPromocao = dataPromocao;
        this.contratoFP = contratoFP;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EnquadramentoFuncional getEnquadramentoNovo() {
        return enquadramentoNovo;
    }

    public void setEnquadramentoNovo(EnquadramentoFuncional enquadramentoNovo) {
        this.enquadramentoNovo = enquadramentoNovo;
    }

    public EnquadramentoFuncional getEnquadramentoAnterior() {
        return enquadramentoAnterior;
    }

    public void setEnquadramentoAnterior(EnquadramentoFuncional enquadramentoAnterior) {
        this.enquadramentoAnterior = enquadramentoAnterior;
    }

    public Date getDataPromocao() {
        return dataPromocao;
    }

    public void setDataPromocao(Date dataPromocao) {
        this.dataPromocao = dataPromocao;
    }

    public ContratoFP getContratoFP() {
        return contratoFP;
    }

    public void setContratoFP(ContratoFP contratoFP) {
        this.contratoFP = contratoFP;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.Promocao[ id=" + id + " ]";
    }

    public boolean temEnquadramentoNovo() {
        return enquadramentoNovo != null;
    }
}

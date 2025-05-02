/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SubTipoDespesa;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Edi
 */
@Entity

@Table(name = "DIVIDAPUBLICASOLICEMP")
@Audited
public class DividaPublicaSolicitacaoEmpenho extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private DividaPublica dividaPublica;
    @ManyToOne
    private DividaPublica operacaoDeCredito;
    @ManyToOne
    private ParcelaDividaPublica parcelaDividaPublica;
    @ManyToOne
    private SolicitacaoEmpenho solicitacaoEmpenho;
    @Enumerated(EnumType.STRING)
    @Etiqueta("SubTipo de Despesa")
    private SubTipoDespesa subTipoDespesa;


    public DividaPublicaSolicitacaoEmpenho() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public DividaPublica getDividaPublica() {
        return dividaPublica;
    }

    public void setDividaPublica(DividaPublica dividaPublica) {
        this.dividaPublica = dividaPublica;
    }

    public ParcelaDividaPublica getParcelaDividaPublica() {
        return parcelaDividaPublica;
    }

    public void setParcelaDividaPublica(ParcelaDividaPublica parcelaDividaPublica) {
        this.parcelaDividaPublica = parcelaDividaPublica;
    }

    public SolicitacaoEmpenho getSolicitacaoEmpenho() {
        return solicitacaoEmpenho;
    }

    public void setSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
    }

    public SubTipoDespesa getSubTipoDespesa() {
        return subTipoDespesa;
    }

    public void setSubTipoDespesa(SubTipoDespesa subTipoDespesa) {
        this.subTipoDespesa = subTipoDespesa;
    }

    public DividaPublica getOperacaoDeCredito() {
        return operacaoDeCredito;
    }

    public void setOperacaoDeCredito(DividaPublica operacaoDeCredito) {
        this.operacaoDeCredito = operacaoDeCredito;
    }

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.DividaPublicaSolicitacaoEmpenho[ id=" + id + " ]";
    }
}

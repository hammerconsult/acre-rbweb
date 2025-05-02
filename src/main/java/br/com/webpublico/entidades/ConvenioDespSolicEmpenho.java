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

@Audited
public class ConvenioDespSolicEmpenho extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ConvenioDespesa convenioDespesa;
    @ManyToOne
    private DespesaExercConvenio despesaExercConvenio;
    @ManyToOne
    private SolicitacaoEmpenho solicitacaoEmpenho;
    @Enumerated(EnumType.STRING)
    private SubTipoDespesa subTipoDespesa;

    public ConvenioDespSolicEmpenho() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ConvenioDespesa getConvenioDespesa() {
        return convenioDespesa;
    }

    public void setConvenioDespesa(ConvenioDespesa convenioDespesa) {
        this.convenioDespesa = convenioDespesa;
    }

    public DespesaExercConvenio getDespesaExercConvenio() {
        return despesaExercConvenio;
    }

    public void setDespesaExercConvenio(DespesaExercConvenio despesaExercConvenio) {
        this.despesaExercConvenio = despesaExercConvenio;
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

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ConvenioDespSolicEmpenho[ id=" + id + " ]";
    }
}

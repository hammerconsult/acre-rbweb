package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPrestacaoContas;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 02/01/14
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
@Audited
@Entity
@GrupoDiagrama(nome = "Contabil")
@Etiqueta("Prestação de Contas")
public class PrestacaoContas implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(javax.persistence.TemporalType.DATE)
    @Pesquisavel
    @Etiqueta("Data da Prestação de Contas")
    private Date dataLancamento;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta("Status da Prestação de Contas")
    private TipoPrestacaoContas tipoPrestacaoContas;
    @ManyToOne
    private ConvenioReceita convenioReceita;
    @ManyToOne
    private ConvenioDespesa convenioDespesa;

    public PrestacaoContas() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public TipoPrestacaoContas getTipoPrestacaoContas() {
        return tipoPrestacaoContas;
    }

    public void setTipoPrestacaoContas(TipoPrestacaoContas tipoPrestacaoContas) {
        this.tipoPrestacaoContas = tipoPrestacaoContas;
    }

    public ConvenioReceita getConvenioReceita() {
        return convenioReceita;
    }

    public void setConvenioReceita(ConvenioReceita convenioReceita) {
        this.convenioReceita = convenioReceita;
    }

    public ConvenioDespesa getConvenioDespesa() {
        return convenioDespesa;
    }

    public void setConvenioDespesa(ConvenioDespesa convenioDespesa) {
        this.convenioDespesa = convenioDespesa;
    }

}

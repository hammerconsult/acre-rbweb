package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Entity
@Audited
@Table(name = "PLANOANUALCONTOBJETOCOMPRA")
@Etiqueta("Objeto de Compra - PAC")
public class PlanoAnualContratacaoObjetoCompra extends SuperEntidade implements Comparable<PlanoAnualContratacaoObjetoCompra> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private PlanoAnualContratacaoGrupoObjetoCompra planoAnualContratacaoGrupo;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Objeto de Compra")
    private ObjetoCompra objetoCompra;

    @Obrigatorio
    @Temporal(value = TemporalType.DATE)
    @Etiqueta("Data Desejada")
    private Date dataDesejada;

    @Obrigatorio
    @Etiqueta("Quantidade")
    private BigDecimal quantidade;

    @Obrigatorio
    @Etiqueta("Valor Unitário")
    private BigDecimal valorUnitario;

    @Obrigatorio
    @Etiqueta("Valor Total")
    private BigDecimal valorTotal;
    
    @Etiqueta("Número")
    private Integer numero;

    public PlanoAnualContratacaoObjetoCompra() {
        quantidade = BigDecimal.ZERO;
        valorUnitario = BigDecimal.ZERO;
        valorTotal = BigDecimal.ZERO;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public PlanoAnualContratacaoGrupoObjetoCompra getPlanoAnualContratacaoGrupo() {
        return planoAnualContratacaoGrupo;
    }

    public void setPlanoAnualContratacaoGrupo(PlanoAnualContratacaoGrupoObjetoCompra planoAnualContratacaoGrupo) {
        this.planoAnualContratacaoGrupo = planoAnualContratacaoGrupo;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public Date getDataDesejada() {
        return dataDesejada;
    }

    public void setDataDesejada(Date dataDesejada) {
        this.dataDesejada = dataDesejada;
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public boolean hasQuantidade() {
        return quantidade != null && quantidade.compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValorUnitario() {
        return valorUnitario != null && valorUnitario.compareTo(BigDecimal.ZERO) > 0;
    }

    public void calcularValorTotal() {
        if (hasQuantidade() && hasValorUnitario()) {
            setValorTotal(getQuantidade().multiply(getValorUnitario()).setScale(2, RoundingMode.HALF_EVEN));
        }
    }

    @Override
    public int compareTo(PlanoAnualContratacaoObjetoCompra o) {
        if (getObjetoCompra() != null && o.getObjetoCompra() != null) {
            return ComparisonChain.start().compare(getObjetoCompra().getCodigo(), o.getObjetoCompra().getCodigo()).result();
        }
        return 0;
    }

    @Override
    public String toString() {
        return numero + "/" + getPlanoAnualContratacaoGrupo().getPlanoAnualContratacao().getExercicio().getAno() + " - " + objetoCompra.getDescricao();
    }
}

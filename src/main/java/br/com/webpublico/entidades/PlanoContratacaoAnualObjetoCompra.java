package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.ComparisonChain;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Entity
@Audited
@Table(name = "PLANOCONTRATANUALOBJCOMP")
@Etiqueta("Objeto de Compra - PCA")
public class PlanoContratacaoAnualObjetoCompra extends SuperEntidade implements Comparable<PlanoContratacaoAnualObjetoCompra> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private PlanoContratacaoAnualGrupoObjetoCompra planoContratacaoAnualGrupo;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Objeto de Compra")
    private ObjetoCompra objetoCompra;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade de Medida")
    private UnidadeMedida unidadeMedida;

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

    @Etiqueta("Especificação")
    private String especificacao;

    @Obrigatorio
    @Etiqueta("Valor Orçamentário do Exercício")
    private BigDecimal valorOrcamentoExercicio;

    @Etiqueta("Sequencial Criado pelo PNCP ao Realizar o Envio do PCA")
    private String sequencialIdPncp;

    public PlanoContratacaoAnualObjetoCompra() {
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

    public PlanoContratacaoAnualGrupoObjetoCompra getPlanoContratacaoAnualGrupo() {
        return planoContratacaoAnualGrupo;
    }

    public void setPlanoContratacaoAnualGrupo(PlanoContratacaoAnualGrupoObjetoCompra planoContratacaoAnualGrupo) {
        this.planoContratacaoAnualGrupo = planoContratacaoAnualGrupo;
    }

    public ObjetoCompra getObjetoCompra() {
        return objetoCompra;
    }

    public void setObjetoCompra(ObjetoCompra objetoCompra) {
        this.objetoCompra = objetoCompra;
    }

    public UnidadeMedida getUnidadeMedida() {
        return unidadeMedida;
    }

    public void setUnidadeMedida(UnidadeMedida unidadeMedida) {
        this.unidadeMedida = unidadeMedida;
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

    public String getEspecificacao() {
        return especificacao;
    }

    public void setEspecificacao(String especificacao) {
        this.especificacao = especificacao;
    }

    public BigDecimal getValorOrcamentoExercicio() {
        return valorOrcamentoExercicio;
    }

    public void setValorOrcamentoExercicio(BigDecimal valorOrcamentoExercicio) {
        this.valorOrcamentoExercicio = valorOrcamentoExercicio;
    }

    public String getSequencialIdPncp() {
        return sequencialIdPncp;
    }

    public void setSequencialIdPncp(String sequencialIdPncp) {
        this.sequencialIdPncp = sequencialIdPncp;
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
    public int compareTo(PlanoContratacaoAnualObjetoCompra o) {
        if (getObjetoCompra() != null && o.getObjetoCompra() != null) {
            return ComparisonChain.start().compare(getObjetoCompra().getCodigo(), o.getObjetoCompra().getCodigo()).result();
        }
        return 0;
    }

    @Override
    public String toString() {
        return numero + "/" + getPlanoContratacaoAnualGrupo().getPlanoContratacaoAnual().getExercicio().getAno() + " - " + objetoCompra.getDescricao();
    }
}

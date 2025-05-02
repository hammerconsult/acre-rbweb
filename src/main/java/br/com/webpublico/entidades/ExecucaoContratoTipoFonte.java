package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Etiqueta;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by Wellington on 01/10/2015.
 */
@Entity
@Audited
@Etiqueta("Fonte de Despesa")
public class ExecucaoContratoTipoFonte extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Etiqueta("Execução Contrato Tipo")
    private ExecucaoContratoTipo execucaoContratoTipo;

    @ManyToOne
    @Etiqueta("Fonte de Recurso")
    private FonteDespesaORC fonteDespesaORC;

    @Transient
    @Etiqueta("Despesa Orçamentária")
    private DespesaORC despesaORC;

    @Etiqueta("Valor (R$)")
    private BigDecimal valor;

    @Transient
    private BigDecimal saldoFonteDespesaORC;
    @Transient
    private BigDecimal valorReservado;
    @Transient
    private BigDecimal valorEstornoReservado;
    @Transient
    private BigDecimal valorExecutado;
    @Transient
    private BigDecimal valorEstornoExecutado;
    @Transient
    private BigDecimal valorReservaExecucao;

    @Etiqueta("Itens")
    @OneToMany(mappedBy = "execucaoContratoTipoFonte", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExecucaoContratoItemDotacao> itens;

    private Boolean gerarReserva;

    public ExecucaoContratoTipoFonte() {
        super();
        saldoFonteDespesaORC = BigDecimal.ZERO;
        valor = BigDecimal.ZERO;
        valorExecutado = BigDecimal.ZERO;
        valorReservado = BigDecimal.ZERO;
        valorReservaExecucao = BigDecimal.ZERO;
        gerarReserva = Boolean.FALSE;
        itens = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ExecucaoContratoTipo getExecucaoContratoTipo() {
        return execucaoContratoTipo;
    }

    public void setExecucaoContratoTipo(ExecucaoContratoTipo execucaoContratoTipo) {
        this.execucaoContratoTipo = execucaoContratoTipo;
    }

    public FonteDespesaORC getFonteDespesaORC() {
        return fonteDespesaORC;
    }

    public void setFonteDespesaORC(FonteDespesaORC fonteDespesaORC) {
        this.fonteDespesaORC = fonteDespesaORC;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSaldoFonteDespesaORC() {
        return saldoFonteDespesaORC;
    }

    public void setSaldoFonteDespesaORC(BigDecimal saldoFonteDespesaORC) {
        this.saldoFonteDespesaORC = saldoFonteDespesaORC;
    }

    public Boolean getGerarReserva() {
        return gerarReserva == null ? Boolean.FALSE : gerarReserva;
    }

    public void setGerarReserva(Boolean gerarReserva) {
        this.gerarReserva = gerarReserva;
    }

    public DespesaORC getDespesaORC() {
        return despesaORC;
    }

    public void setDespesaORC(DespesaORC despesaORC) {
        this.despesaORC = despesaORC;
    }

    public List<ExecucaoContratoItemDotacao> getItens() {
        return itens;
    }

    public void setItens(List<ExecucaoContratoItemDotacao> itens) {
        this.itens = itens;
    }

    public BigDecimal getValorReservado() {
        return valorReservado;
    }

    public void setValorReservado(BigDecimal valorReservado) {
        this.valorReservado = valorReservado;
    }

    public BigDecimal getValorExecutado() {
        return valorExecutado;
    }

    public void setValorExecutado(BigDecimal valorExecutado) {
        this.valorExecutado = valorExecutado;
    }

    public BigDecimal getValorReservaExecucao() {
        return valorReservaExecucao;
    }

    public void setValorReservaExecucao(BigDecimal valorReservaExecucao) {
        this.valorReservaExecucao = valorReservaExecucao;
    }

    public BigDecimal getValorEstornoReservado() {
        return valorEstornoReservado;
    }

    public void setValorEstornoReservado(BigDecimal valorEstornoReservado) {
        this.valorEstornoReservado = valorEstornoReservado;
    }

    public BigDecimal getValorEstornoExecutado() {
        return valorEstornoExecutado;
    }

    public void setValorEstornoExecutado(BigDecimal valorEstornoExecutado) {
        this.valorEstornoExecutado = valorEstornoExecutado;
    }

    public BigDecimal getSaldoDisponivel() {
        try {
            return getValorReservado().subtract(getValorExecutado()).subtract(getValorEstornoReservado()).add(getValorEstornoExecutado());
        } catch (NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }

    public BigDecimal getQuantidadeReservadaItemDotacao(ExecucaoContratoItem execucaoContratoItem) {
        if (hasItens()) {
            for (ExecucaoContratoItemDotacao item : itens) {
                if (execucaoContratoItem.equals(item.getExecucaoContratoItem())) {
                    return item.getQuantidade();
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorReservadoItemDotacao(ExecucaoContratoItem execucaoContratoItem) {
        if (hasItens()) {
            for (ExecucaoContratoItemDotacao item : itens) {
                if (execucaoContratoItem.equals(item.getExecucaoContratoItem())) {
                    return item.getValorTotal();
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal getValorTotalItemValor() {
        BigDecimal total = BigDecimal.ZERO;
        try {
            for (ExecucaoContratoItemDotacao item : getItens()) {
                total = total.add(item.getValorTotal());
            }
            return total;
        } catch (NullPointerException e) {
            return total;
        }
    }

    public boolean hasSaldoDisponivel() {
        return getSaldoDisponivel() != null && getSaldoDisponivel().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasValorReservaNaExecucao() {
        return getValorReservaExecucao() != null && getValorReservaExecucao().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean hasItens() {
        return itens != null && !itens.isEmpty();
    }

    @Override
    public String toString() {
        return "(" + execucaoContratoTipo.getTipoObjetoCompra().getDescricao() + ") " + fonteDespesaORC;
    }
}

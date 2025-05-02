package br.com.webpublico.entidades.rh.administracaodepagamento;

import br.com.webpublico.entidades.EventoFP;
import br.com.webpublico.enums.TipoCalculoFP;
import br.com.webpublico.enums.TipoEventoFP;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Monetario;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@Table(name = "ITEMFICHASIMULACAO")
public class ItemFichaFinanceiraFPSimulacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EventoFP eventoFP;
    @ManyToOne
    private FichaFinanceiraFPSimulacao fichaFinanceiraFPSimulacao;
    @Monetario
    private BigDecimal valor;
    /**
     * Valor de referência do evento calculado (30 horas extra, 25 dias
     * trabalhados, etc...). Este valor é obtido a partir do cálculo do
     * EventoFP.
     */
    @Monetario
    private BigDecimal valorReferencia;
    /**
     * Valor integral do evento calculado. Este valor é obtido a partir do
     * cálculo do EventoFP.
     */
    @Monetario
    private BigDecimal valorIntegral;
    private BigDecimal quantidade;
    private String unidadeReferencia;
    @Enumerated(EnumType.STRING)
    private TipoCalculoFP tipoCalculoFP;
    @Monetario
    private BigDecimal valorBaseDeCalculo;
    private Integer ano;
    private Integer mes;
    @Enumerated(EnumType.STRING)
    private TipoEventoFP tipoEventoFP;

    public ItemFichaFinanceiraFPSimulacao(Long id, EventoFP eventoFP, FichaFinanceiraFPSimulacao fichaFinanceiraFPSimulacao, BigDecimal valor, BigDecimal valorReferencia, BigDecimal valorBaseDeCalculo) {
        this.id = id;
        this.eventoFP = eventoFP;
        this.fichaFinanceiraFPSimulacao = fichaFinanceiraFPSimulacao;
        this.valor = valor;
        this.valorReferencia = valorReferencia;
        this.valorBaseDeCalculo = valorBaseDeCalculo;
    }

    public ItemFichaFinanceiraFPSimulacao(EventoFP eventoFP, TipoEventoFP tipoEventoFP, BigDecimal valor, BigDecimal valorReferencia, BigDecimal valorBaseDeCalculo) {
        this.eventoFP = eventoFP;
        this.tipoEventoFP = tipoEventoFP;
        this.valor = valor;
        this.valorReferencia = valorReferencia;
        this.valorBaseDeCalculo = valorBaseDeCalculo;
    }

    public ItemFichaFinanceiraFPSimulacao() {
    }

    public BigDecimal getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(BigDecimal quantidade) {
        this.quantidade = quantidade;
    }

    public TipoCalculoFP getTipoCalculoFP() {
        return tipoCalculoFP;
    }

    public void setTipoCalculoFP(TipoCalculoFP tipoCalculoFP) {
        this.tipoCalculoFP = tipoCalculoFP;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public String getUnidadeReferencia() {
        return unidadeReferencia;
    }

    public void setUnidadeReferencia(String unidadeReferencia) {
        this.unidadeReferencia = unidadeReferencia;
    }

    public EventoFP getEventoFP() {
        return eventoFP;
    }

    public void setEventoFP(EventoFP eventoFP) {
        this.eventoFP = eventoFP;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public void valorDouble(Double valor) {
        if (valor != null && !valor.isInfinite() && !valor.isNaN()) {
            this.valor = new BigDecimal(valor);
        }
    }

    public FichaFinanceiraFPSimulacao getFichaFinanceiraFPSimulacao() {
        return fichaFinanceiraFPSimulacao;
    }

    public void setFichaFinanceiraFPSimulacao(FichaFinanceiraFPSimulacao fichaFinanceiraFPSimulacao) {
        this.fichaFinanceiraFPSimulacao = fichaFinanceiraFPSimulacao;
    }

    public BigDecimal getValorIntegral() {
        return valorIntegral;
    }

    public void setValorIntegral(BigDecimal valorIntegral) {
        this.valorIntegral = valorIntegral;
    }

    public BigDecimal getValorReferencia() {
        return valorReferencia;
    }

    public BigDecimal getValorBaseDeCalculo() {
        return valorBaseDeCalculo;
    }

    public void setValorBaseDeCalculo(BigDecimal valorBaseDeCalculo) {
        this.valorBaseDeCalculo = valorBaseDeCalculo;
    }

    public void setValorReferencia(BigDecimal valorReferencia) {
        this.valorReferencia = valorReferencia;
    }

    public TipoEventoFP getTipoEventoFP() {
        return tipoEventoFP;
    }

    public void setTipoEventoFP(TipoEventoFP tipoEventoFP) {
        this.tipoEventoFP = tipoEventoFP;
    }

    @Override
    public String toString() {
        return eventoFP + ", " + " valor=" + valor + ", valorReferencia=" + valorReferencia + ", valorBase=" + valorBaseDeCalculo + ", mês: " + mes + ", ano: " + ano;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ItemFichaFinanceiraFPSimulacao other = (ItemFichaFinanceiraFPSimulacao) obj;
        if (this.id != other.id && (this.id == null || !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

    public boolean isTipoEventoVantagem() {
        return TipoEventoFP.VANTAGEM.equals(tipoEventoFP);
    }
}

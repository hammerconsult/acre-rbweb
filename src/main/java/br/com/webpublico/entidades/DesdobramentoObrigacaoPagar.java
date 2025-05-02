package br.com.webpublico.entidades;

import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;

/**
 * Created by mga on 22/06/2017.
 */
@Entity
@Audited
@Etiqueta("Desdobramento Obrigação a Pagar")
@Table(name = "DESDOBRAMENTOOBRIGACAOPAGA")
public class DesdobramentoObrigacaoPagar extends SuperEntidade implements EntidadeContabil {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Obrigação a Pagar")
    private ObrigacaoAPagar obrigacaoAPagar;

    @ManyToOne
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Conta de Despesa")
    private Conta conta;

    @ManyToOne
    @Tabelavel
    @Etiqueta("Evento Contábil")
    private EventoContabil eventoContabil;

    @Monetario
    @Etiqueta("Valor")
    @Obrigatorio
    @Tabelavel
    @Positivo(permiteZero = false)
    private BigDecimal valor;

    @Monetario
    @Etiqueta("Saldo")
    @Obrigatorio
    @Tabelavel
    @Positivo(permiteZero = false)
    private BigDecimal saldo;

    @Monetario
    @Etiqueta("Valor Empenhado")
    @Transient
    private BigDecimal valorEmpenhado;

    @OneToOne
    private DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar;
    @ManyToOne
    private DesdobramentoEmpenho desdobramentoEmpenho;

    public DesdobramentoObrigacaoPagar() {
        valor = BigDecimal.ZERO;
        saldo = BigDecimal.ZERO;
        valorEmpenhado = BigDecimal.ZERO;
    }

    public BigDecimal getValorEmpenhado() {
        return valorEmpenhado.add(valor.subtract(saldo));
    }

    public void setValorEmpenhado(BigDecimal valorEmpenhado) {
        this.valorEmpenhado = valorEmpenhado;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ObrigacaoAPagar getObrigacaoAPagar() {
        return obrigacaoAPagar;
    }

    public void setObrigacaoAPagar(ObrigacaoAPagar obrigacaoAPagar) {
        this.obrigacaoAPagar = obrigacaoAPagar;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public DesdobramentoObrigacaoPagar getDesdobramentoObrigacaoPagar() {
        return desdobramentoObrigacaoPagar;
    }

    public void setDesdobramentoObrigacaoPagar(DesdobramentoObrigacaoPagar desdobramentoObrigacaoPagar) {
        this.desdobramentoObrigacaoPagar = desdobramentoObrigacaoPagar;
    }

    public DesdobramentoEmpenho getDesdobramentoEmpenho() {
        return desdobramentoEmpenho;
    }

    public void setDesdobramentoEmpenho(DesdobramentoEmpenho desdobramentoEmpenho) {
        this.desdobramentoEmpenho = desdobramentoEmpenho;
    }

    @Override
    public String toString() {
        try {
            return this.obrigacaoAPagar + ", " + conta + Util.formataValor(valor);
        } catch (Exception ex) {
            return "";
        }
    }

    public String toStringAutoComplete() {
        try {
            return this.conta +
                ", " + Util.formataValor(this.valor) +
                ", Nº " + this.obrigacaoAPagar.getNumero() +
                ", " + DataUtil.getDataFormatada(this.obrigacaoAPagar.getDataLancamento()) +
                ", " + this.obrigacaoAPagar.getTipoReconhecimento().getDescricao();
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return ((EntidadeContabil) obrigacaoAPagar).getReferenciaArquivoPrestacaoDeContas();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }
}

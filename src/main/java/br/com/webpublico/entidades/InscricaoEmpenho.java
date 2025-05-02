package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoRestosProcessado;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 01/10/14
 * Time: 11:24
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Inscrição do Empenho")
public class InscricaoEmpenho extends SuperEntidade implements Serializable,EntidadeContabil {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Empenho")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Empenho empenho;
    @Etiqueta("Empenho")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    private Empenho empenhoInscrito;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;
    @Etiqueta("Evento Contábil")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    @Tabelavel
    private EventoContabil eventoContabil;
    @Etiqueta("Tipo Restos")
    @Obrigatorio
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoRestosProcessado tipoRestos;
    @Etiqueta("Valor")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Monetario
    private BigDecimal valor;
    @Transient
    private BigDecimal saldoObrigacaoPagarAntesEmp;
    @Transient
    private BigDecimal saldoObrigacaoPagarDepoisEmp;

    public InscricaoEmpenho() {

    }

    public InscricaoEmpenho(Empenho empenhosRestosProcessado, AberturaFechamentoExercicio selecionado, TipoRestosProcessado tipoRestosProcessado, BigDecimal valor, BigDecimal saldoObrigacaoPagarAntesEmp, BigDecimal saldoObrigacaoPagarDepoisEmp) {
        this.empenho = empenhosRestosProcessado;
        this.aberturaFechamentoExercicio = selecionado;
        this.tipoRestos = tipoRestosProcessado;
        this.valor = valor;
        this.saldoObrigacaoPagarAntesEmp = saldoObrigacaoPagarAntesEmp;
        this.saldoObrigacaoPagarDepoisEmp = saldoObrigacaoPagarDepoisEmp;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Empenho getEmpenho() {
        return empenho;
    }

    public void setEmpenho(Empenho empenho) {
        this.empenho = empenho;
    }

    public AberturaFechamentoExercicio getAberturaFechamentoExercicio() {
        return aberturaFechamentoExercicio;
    }

    public void setAberturaFechamentoExercicio(AberturaFechamentoExercicio aberturaFechamentoExercicio) {
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public TipoRestosProcessado getTipoRestos() {
        return tipoRestos;
    }

    public void setTipoRestos(TipoRestosProcessado tipoRestos) {
        this.tipoRestos = tipoRestos;
    }

    public Empenho getEmpenhoInscrito() {
        return empenhoInscrito;
    }

    public void setEmpenhoInscrito(Empenho empenhoInscrito) {
        this.empenhoInscrito = empenhoInscrito;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getSaldoObrigacaoPagarAntesEmp() {
        return saldoObrigacaoPagarAntesEmp;
    }

    public void setSaldoObrigacaoPagarAntesEmp(BigDecimal saldoObrigacaoPagarAntesEmp) {
        this.saldoObrigacaoPagarAntesEmp = saldoObrigacaoPagarAntesEmp;
    }

    public BigDecimal getSaldoObrigacaoPagarDepoisEmp() {
        return saldoObrigacaoPagarDepoisEmp;
    }

    public void setSaldoObrigacaoPagarDepoisEmp(BigDecimal saldoObrigacaoPagarDepoisEmp) {
        this.saldoObrigacaoPagarDepoisEmp = saldoObrigacaoPagarDepoisEmp;
    }

    @Override
    public String toString() {
        return empenhoInscrito.toString();
    }

    @Override
    public String getReferenciaArquivoPrestacaoDeContas() {
        return empenho.getNumero() + "/" + empenho.getExercicio().getAno();
    }

    @Override
    public String getComplementoHistoricoPrestacaoDeContas() {
        return toString();
    }
}

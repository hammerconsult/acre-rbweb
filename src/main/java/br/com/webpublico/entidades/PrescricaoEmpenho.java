package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoRestosProcessado;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 29/09/14
 * Time: 15:54
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Abertura e Fechamento de Exercicio")
public class PrescricaoEmpenho extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Etiqueta("Empenho")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Empenho empenho;
    @Etiqueta("Estorno de Empenho")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne(cascade = CascadeType.ALL)
    private EmpenhoEstorno empenhoEstorno;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;
    @Etiqueta("Evento Contábil")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
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

    public PrescricaoEmpenho() {

    }

    public PrescricaoEmpenho(Empenho empenhosRestosProcessado, AberturaFechamentoExercicio selecionado, TipoRestosProcessado tipoRestosProcessado, BigDecimal valor) {
        this.empenho = empenhosRestosProcessado;
        this.aberturaFechamentoExercicio = selecionado;
        this.tipoRestos = tipoRestosProcessado;
        this.valor = valor;
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

    public EmpenhoEstorno getEmpenhoEstorno() {
        return empenhoEstorno;
    }

    public void setEmpenhoEstorno(EmpenhoEstorno empenhoEstorno) {
        this.empenhoEstorno = empenhoEstorno;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Boolean isProcessado() {
        if (this.getTipoRestos().equals(TipoRestosProcessado.PROCESSADOS)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return empenhoEstorno.toString();
    }
}

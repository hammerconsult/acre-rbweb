package br.com.webpublico.entidades;

import br.com.webpublico.entidades.contabil.SuperEntidadeContabilGerarContaAuxiliar;
import br.com.webpublico.entidadesauxiliares.contabil.GeradorContaAuxiliarDTO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.IGeraContaAuxiliar;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import br.com.webpublico.util.anotacoes.*;
import com.google.common.base.Strings;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.TreeMap;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 03/11/14
 * Time: 08:49
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Receita de Fechamento do Exercício")
public class ReceitaFechamentoExercicio extends SuperEntidadeContabilGerarContaAuxiliar implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    @Tabelavel
    @Etiqueta("Unidade")
    private HierarquiaOrganizacional hierarquiaOrganizacional;
    @Etiqueta("Unidade Organizacional")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Conta")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private Conta conta;
    @Etiqueta("Fonte de Recursos")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @Etiqueta("Conta de Destinação de Recurso")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @ManyToOne
    private ContaDeDestinacao contaDeDestinacao;
    @Etiqueta("Valor")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Monetario
    private BigDecimal valor;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;
    @Etiqueta("Tipo da Receita")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoReceitaFechamentoExercicio tipoReceita;
    @Etiqueta("Evento Contábil")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private EventoContabil eventoContabil;


    public ReceitaFechamentoExercicio() {

    }

    public ReceitaFechamentoExercicio(Conta conta, HierarquiaOrganizacional ho, FonteDeRecursos fonteDeRecursos, ContaDeDestinacao contaDeDestinacao, BigDecimal valor, TipoReceitaFechamentoExercicio tipoReceita, AberturaFechamentoExercicio selecionado) {
        this.conta = conta;
        this.hierarquiaOrganizacional = ho;
        if (ho.getSubordinada() != null) {
            this.unidadeOrganizacional = ho.getSubordinada();
        }
        this.fonteDeRecursos = fonteDeRecursos;
        this.contaDeDestinacao = contaDeDestinacao;
        this.valor = valor;
        this.tipoReceita = tipoReceita;
        this.aberturaFechamentoExercicio = selecionado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public AberturaFechamentoExercicio getAberturaFechamentoExercicio() {
        return aberturaFechamentoExercicio;
    }

    public void setAberturaFechamentoExercicio(AberturaFechamentoExercicio aberturaFechamentoExercicio) {
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public TipoReceitaFechamentoExercicio getTipoReceita() {
        return tipoReceita;
    }

    public void setTipoReceita(TipoReceitaFechamentoExercicio tipoReceita) {
        this.tipoReceita = tipoReceita;
    }

    public EventoContabil getEventoContabil() {
        return eventoContabil;
    }

    public void setEventoContabil(EventoContabil eventoContabil) {
        this.eventoContabil = eventoContabil;
    }

    public ContaDeDestinacao getContaDeDestinacao() {
        return contaDeDestinacao;
    }

    public void setContaDeDestinacao(ContaDeDestinacao contaDeDestinacao) {
        this.contaDeDestinacao = contaDeDestinacao;
    }

    @Override
    public String toString() {
        return "Unidade: " + unidadeOrganizacional + "; Conta: " + conta.toString() + "; Conta de Destinação de Recurso: " + fonteDeRecursos + "; Valor: " + Util.formataValor(valor);
    }

    @Override
    public GeradorContaAuxiliarDTO gerarContaAuxiliarDTO(ParametroEvento.ComplementoId complementoId) {
        return new GeradorContaAuxiliarDTO(unidadeOrganizacional,
            contaDeDestinacao,
            conta,
            conta.getCodigoContaSiconf(),
            aberturaFechamentoExercicio.getExercicio());
    }
}

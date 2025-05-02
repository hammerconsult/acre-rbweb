package br.com.webpublico.entidades;

import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.TreeMap;

@GrupoDiagrama(nome = "Contábil")
@Entity
@Audited
@Etiqueta("Transporte de Saldo de Conta Auxiliar Detalhada")
@Table(name = "TRANSPSALDOCNTAUXDETALHADA")
public class TransporteSaldoContaAuxiliarDetalhada extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Transient
    @Tabelavel
    @Etiqueta("Unidade")
    private HierarquiaOrganizacional unidade;
    @Transient
    @Tabelavel
    @Etiqueta("Conta Contabil")
    private ContaContabil contaContabil;
    @Etiqueta("Unidade Organizacional")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private UnidadeOrganizacional unidadeOrganizacional;
    @Etiqueta("Data")
    @Temporal(TemporalType.DATE)
    private Date data;
    @Etiqueta("Conta Auxiliar Siconfi")
    @ManyToOne
    private ContaAuxiliar contaAuxiliar;
    @Etiqueta("Conta Auxiliar Detalhada")
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @ManyToOne
    private ContaAuxiliarDetalhada contaAuxiliarDetalhada;
    @Transient
    @Tabelavel
    @Etiqueta("Conta Auxiliar Siconfi - Próximo Exercício")
    private String descricaoContaAuxiliarSiconfi;
    @Transient
    private TreeMap contaAuxiliarSiconfi;
    @Transient
    @Tabelavel
    @Etiqueta("Conta Auxiliar Detalhada - Próximo Exercício")
    private String descricaoNovaContaAuxiliarDetalhada;
    @Transient
    private TreeMap novaContaAuxiliarDetalhada;
    @Etiqueta("Crédito")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Monetario
    private BigDecimal credito;
    @Etiqueta("Débito")
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Monetario
    private BigDecimal debito;
    @Etiqueta("Abertura e Fechamento de Exercicio")
    @Obrigatorio
    @Pesquisavel
    @ManyToOne
    private AberturaFechamentoExercicio aberturaFechamentoExercicio;

    public TransporteSaldoContaAuxiliarDetalhada() {
        super();
    }

    public TransporteSaldoContaAuxiliarDetalhada(ContaContabil contaContabil, HierarquiaOrganizacional unidade, UnidadeOrganizacional unidadeOrganizacional, ContaAuxiliarDetalhada contaAuxiliarDetalhada, TreeMap novaContaAuxiliarDetalhada, TreeMap contaAuxiliarSiconfi, BigDecimal credito, BigDecimal debito, AberturaFechamentoExercicio aberturaFechamentoExercicio) {
        super();
        this.contaContabil = contaContabil;
        this.unidade = unidade;
        this.unidadeOrganizacional = unidadeOrganizacional;
        this.contaAuxiliarDetalhada = contaAuxiliarDetalhada;
        this.novaContaAuxiliarDetalhada = novaContaAuxiliarDetalhada;
        this.contaAuxiliarSiconfi = contaAuxiliarSiconfi;
        this.credito = credito;
        this.debito = debito;
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
        this.descricaoNovaContaAuxiliarDetalhada = contaAuxiliarDetalhada.getTipoContaAuxiliar().getCodigo() + "." + (String) novaContaAuxiliarDetalhada.lastEntry().getKey();
        this.descricaoContaAuxiliarSiconfi = contaAuxiliarDetalhada.getTipoContaAuxiliar().getCodigo() + "." + (String) contaAuxiliarSiconfi.lastEntry().getKey();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ContaContabil getContaContabil() {
        return contaContabil;
    }

    public void setContaContabil(ContaContabil contaContabil) {
        this.contaContabil = contaContabil;
    }

    public HierarquiaOrganizacional getUnidade() {
        return unidade;
    }

    public void setUnidade(HierarquiaOrganizacional unidade) {
        this.unidade = unidade;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public ContaAuxiliar getContaAuxiliar() {
        return contaAuxiliar;
    }

    public void setContaAuxiliar(ContaAuxiliar contaAuxiliar) {
        this.contaAuxiliar = contaAuxiliar;
    }

    public ContaAuxiliarDetalhada getContaAuxiliarDetalhada() {
        return contaAuxiliarDetalhada;
    }

    public void setContaAuxiliarDetalhada(ContaAuxiliarDetalhada contaAuxiliarDetalhada) {
        this.contaAuxiliarDetalhada = contaAuxiliarDetalhada;
    }

    public TreeMap getNovaContaAuxiliarDetalhada() {
        return novaContaAuxiliarDetalhada;
    }

    public void setNovaContaAuxiliarDetalhada(TreeMap novaContaAuxiliarDetalhada) {
        this.novaContaAuxiliarDetalhada = novaContaAuxiliarDetalhada;
    }

    public TreeMap getContaAuxiliarSiconfi() {
        return contaAuxiliarSiconfi;
    }

    public void setContaAuxiliarSiconfi(TreeMap contaAuxiliarSiconfi) {
        this.contaAuxiliarSiconfi = contaAuxiliarSiconfi;
    }

    public BigDecimal getCredito() {
        return credito;
    }

    public void setCredito(BigDecimal credito) {
        this.credito = credito;
    }

    public BigDecimal getDebito() {
        return debito;
    }

    public void setDebito(BigDecimal debito) {
        this.debito = debito;
    }

    public AberturaFechamentoExercicio getAberturaFechamentoExercicio() {
        return aberturaFechamentoExercicio;
    }

    public void setAberturaFechamentoExercicio(AberturaFechamentoExercicio aberturaFechamentoExercicio) {
        this.aberturaFechamentoExercicio = aberturaFechamentoExercicio;
    }
}

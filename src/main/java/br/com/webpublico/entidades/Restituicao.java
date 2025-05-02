package br.com.webpublico.entidades;

import br.com.webpublico.enums.OpcaoCredor;
import br.com.webpublico.enums.SituacaoProcessoDebito;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Entity
@Audited
@GrupoDiagrama(nome = "Tributário")
@Etiqueta("Processo de Restituição de Débitos")
public class Restituicao extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private Long codigo;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @ManyToOne
    @Etiqueta("Usuário Responsável")
    private UsuarioSistema usuarioResponsavel;
    @ManyToOne
    @Etiqueta("Ato Legal")
    private AtoLegal atoLegal;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Contribuinte")
    private Pessoa contribuinte;
    @ManyToOne
    @Etiqueta("Procurador")
    private Pessoa procurador;

    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    @Etiqueta("Situação")
    private SituacaoProcessoDebito situacao;
    @Etiqueta("Motivo")
    private String motivo;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Número do Protocolo")
    private String numProtocolo;
    @Etiqueta("Motivo do Estorno")
    private String motivoEstorno;

    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.TIMESTAMP)
    @Etiqueta("Data de lançamento")
    private Date dataLancamento;
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data da Restituição")
    private Date dataRestituicao;
    @Temporal(TemporalType.DATE)
    @Etiqueta("Data do Estorno")
    private Date dataEstorno;

    @Etiqueta("Valor a Restituir")
    private BigDecimal valorRestituir;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "restituicao")
    private List<ItemRestituicao> itens;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Opção de Credor")
    @Enumerated(EnumType.STRING)
    private OpcaoCredor opcaoCredor;

    @ManyToOne
    private Pessoa pessoaEmpenho;
    @ManyToOne
    private UnidadeOrganizacional unidadeEmpenho;
    @ManyToOne
    private SolicitacaoEmpenho solicitacaoEmpenho;

    public Restituicao() {
        this.valorRestituir = BigDecimal.ZERO;
        this.itens = Lists.newArrayList();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCodigo() {
        return codigo;
    }

    public void setCodigo(Long codigo) {
        this.codigo = codigo;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public UsuarioSistema getUsuarioResponsavel() {
        return usuarioResponsavel;
    }

    public void setUsuarioResponsavel(UsuarioSistema usuarioResponsavel) {
        this.usuarioResponsavel = usuarioResponsavel;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }

    public Pessoa getContribuinte() {
        return contribuinte;
    }

    public void setContribuinte(Pessoa contribuinte) {
        this.contribuinte = contribuinte;
    }

    public Pessoa getProcurador() {
        return procurador;
    }

    public void setProcurador(Pessoa procurador) {
        this.procurador = procurador;
    }

    public SituacaoProcessoDebito getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoProcessoDebito situacao) {
        this.situacao = situacao;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getNumProtocolo() {
        return numProtocolo;
    }

    public void setNumProtocolo(String numProtocolo) {
        this.numProtocolo = numProtocolo;
    }

    public String getMotivoEstorno() {
        return motivoEstorno;
    }

    public void setMotivoEstorno(String motivoEstorno) {
        this.motivoEstorno = motivoEstorno;
    }

    public Date getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(Date dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    public Date getDataRestituicao() {
        return dataRestituicao;
    }

    public void setDataRestituicao(Date dataRestituicao) {
        this.dataRestituicao = dataRestituicao;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public BigDecimal getValorRestituir() {
        return valorRestituir;
    }

    public void setValorRestituir(BigDecimal valorRestituir) {
        this.valorRestituir = valorRestituir;
    }

    public List<ItemRestituicao> getItens() {
        return itens;
    }

    public void setItens(List<ItemRestituicao> itens) {
        this.itens = itens;
    }

    public OpcaoCredor getOpcaoCredor() {
        return opcaoCredor;
    }

    public void setOpcaoCredor(OpcaoCredor opcaoCredor) {
        this.opcaoCredor = opcaoCredor;
    }

    public Pessoa getPessoaEmpenho() {
        return pessoaEmpenho;
    }

    public void setPessoaEmpenho(Pessoa pessoaEmpenho) {
        this.pessoaEmpenho = pessoaEmpenho;
    }

    public UnidadeOrganizacional getUnidadeEmpenho() {
        return unidadeEmpenho;
    }

    public void setUnidadeEmpenho(UnidadeOrganizacional unidadeEmpenho) {
        this.unidadeEmpenho = unidadeEmpenho;
    }

    public SolicitacaoEmpenho getSolicitacaoEmpenho() {
        return solicitacaoEmpenho;
    }

    public void setSolicitacaoEmpenho(SolicitacaoEmpenho solicitacaoEmpenho) {
        this.solicitacaoEmpenho = solicitacaoEmpenho;
    }
}

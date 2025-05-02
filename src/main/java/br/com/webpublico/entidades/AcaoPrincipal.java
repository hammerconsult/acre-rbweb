package br.com.webpublico.entidades;

import br.com.webpublico.enums.AndamentoAcao;
import br.com.webpublico.enums.OrigemAcao;
import br.com.webpublico.enums.StatusProduto;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Romanini
 * Date: 19/09/13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
@GrupoDiagrama(nome = "PPA")
@Entity
@Etiqueta("Ação PPA")
@Audited
public class AcaoPrincipal implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * Código usado para identificação da Ação PPA nos relatórios e formulários
     * internos.
     */
    @Obrigatorio
    @Etiqueta("Código")
    @Pesquisavel
    @Tabelavel
    private String codigo;
    @ManyToOne
    @Etiqueta("Programa")
    @Pesquisavel
    @Tabelavel
    private ProgramaPPA programa;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Tipo de Ação")
    @ManyToOne
    @Pesquisavel
    private TipoAcaoPPA tipoAcaoPPA;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Nome da Ação")
    private String descricao;

    @Obrigatorio
    @Etiqueta("Status da Ação")
    @Enumerated(EnumType.STRING)
    private StatusProduto statusAcao;

    /**
     * Determina com que periodicidade deve ser feita a medição da execução do
     * produto da ação.
     */
    @Etiqueta("Periodicidade de Medição")
    @ManyToOne
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Periodicidade periodicidadeMedicao;
    @Tabelavel
    @Etiqueta("Descrição do Produto")
    @Pesquisavel
    private String descricaoProduto;
    @Obrigatorio
    @Etiqueta("Status do Produto")
    @Enumerated(EnumType.STRING)
    private StatusProduto statusProduto;
    @ManyToOne
    @Etiqueta("Unidade de Medida de Produto")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Obrigatorio
    private UnidadeMedida unidadeMedidaProduto;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Função")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Funcao funcao;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Sub Função")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private SubFuncao subFuncao;
    @ManyToOne
    @Obrigatorio
    @Etiqueta("Unidade Responsável")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private UnidadeOrganizacional responsavel;
    @Etiqueta("Participantes da Ação PPA")
    @OneToMany(mappedBy = "acaoPrincipal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<ParticipanteAcaoPrincipal> participanteAcaoPrincipals;
    /**
     * Total de produtos que serão elaborados nesta ação
     */
    @Obrigatorio
    @Etiqueta("Meta Física")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private BigDecimal totalFisico;
    @Obrigatorio
    @Etiqueta("Status da Meta Física")
    @Enumerated(EnumType.STRING)
    private StatusProduto statusMetaFisica;
    @Etiqueta("Meta Física Acumulativa?")
    private Boolean metaFisicaAcumulativa;

    /**
     * Valor total dos produtos que serão elaborados nesta ação
     */
    @Etiqueta("Total Financeiro")
    @Monetario
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private BigDecimal totalFinanceiro;
    /**
     * Determina se a ação é NOVA ou se ela está em ANDAMENTO (continuidade de
     * um PPA anterior).
     */
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Execução da Ação")
    private AndamentoAcao andamento;
    @Invisivel
    @Tabelavel(campoSelecionado = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @OneToMany(mappedBy = "acaoPrincipal", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Produtos PPA")
    @Tabelavel(campoSelecionado = false)
    private List<ProdutoPPA> produtoPPAs;
    @OneToOne
    @Tabelavel(campoSelecionado = false)
    private AcaoPrincipal origem;
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Somente Leitura")
    private Boolean somenteLeitura;
    @Invisivel
    @ManyToOne
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Exercicio exercicio;
    @Transient
    private Long criadoEm;
    @OneToMany(mappedBy = "acaoPrincipal", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AcaoPPA> projetosAtividades;
    @Etiqueta("Origem da Ação")
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    private OrigemAcao origemAcao;
    @Etiqueta("Modifica Produto")
    private Boolean modificaProduto;
    @Etiqueta("Modifica Meta Física")
    private Boolean modificaMetaFisica;

    public AcaoPrincipal() {
        somenteLeitura = false;
        totalFinanceiro = new BigDecimal(BigInteger.ZERO);
        dataRegistro = new Date();
        criadoEm = System.nanoTime();
    }

    public AcaoPrincipal(ProgramaPPA programa, TipoAcaoPPA tipoAcaoPPA, String descricao, String codigo, Periodicidade periodicidadeMedicao, String descricaoProduto, UnidadeMedida unidadeMedidaProduto, Funcao funcao, SubFuncao subFuncao, UnidadeOrganizacional responsavel, List<ParticipanteAcaoPrincipal> participanteAcaoPPA, BigDecimal totalFisico, BigDecimal totalFinanceiro, AndamentoAcao andamento, Date dataRegistro, List<ProdutoPPA> produtoPPAs, AcaoPrincipal origem, Boolean somenteLeitura, Exercicio exercicio) {
        this.programa = programa;
        this.tipoAcaoPPA = tipoAcaoPPA;
        this.descricao = descricao;
        this.codigo = codigo;
        this.periodicidadeMedicao = periodicidadeMedicao;
        this.descricaoProduto = descricaoProduto;
        this.unidadeMedidaProduto = unidadeMedidaProduto;
        this.funcao = funcao;
        this.subFuncao = subFuncao;
        this.responsavel = responsavel;
        this.participanteAcaoPrincipals = participanteAcaoPPA;
        this.totalFisico = totalFisico;
        this.totalFinanceiro = totalFinanceiro;
        this.andamento = andamento;
        this.dataRegistro = dataRegistro;
        this.produtoPPAs = produtoPPAs;
        this.origem = origem;
        this.somenteLeitura = somenteLeitura;
        this.exercicio = exercicio;
    }

    public Boolean getSomenteLeitura() {
        return somenteLeitura;
    }

    public void setSomenteLeitura(Boolean somenteLeitura) {
        this.somenteLeitura = somenteLeitura;
    }

    public AcaoPrincipal getOrigem() {
        return origem;
    }

    public void setOrigem(AcaoPrincipal origem) {
        this.origem = origem;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getDescricaoProduto() {
        return descricaoProduto;
    }

    public void setDescricaoProduto(String descricaoProduto) {
        this.descricaoProduto = descricaoProduto;
    }

    public TipoAcaoPPA getTipoAcaoPPA() {
        return tipoAcaoPPA;
    }

    public void setTipoAcaoPPA(TipoAcaoPPA tipoAcaoPPA) {
        this.tipoAcaoPPA = tipoAcaoPPA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public ProgramaPPA getPrograma() {
        return programa;
    }

    public void setPrograma(ProgramaPPA programa) {
        this.programa = programa;
    }

    public UnidadeOrganizacional getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(UnidadeOrganizacional responsavel) {
        this.responsavel = responsavel;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public AndamentoAcao getAndamento() {
        return andamento;
    }

    public void setAndamento(AndamentoAcao andamento) {
        this.andamento = andamento;
    }

    public UnidadeMedida getUnidadeMedidaProduto() {
        return unidadeMedidaProduto;
    }

    public void setUnidadeMedidaProduto(UnidadeMedida unidadeMedidaProduto) {
        this.unidadeMedidaProduto = unidadeMedidaProduto;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Periodicidade getPeriodicidadeMedicao() {
        return periodicidadeMedicao;
    }

    public void setPeriodicidadeMedicao(Periodicidade periodicidadeMedicao) {
        this.periodicidadeMedicao = periodicidadeMedicao;
    }

    public BigDecimal getTotalFinanceiro() {
        return totalFinanceiro;
    }

    public void setTotalFinanceiro(BigDecimal totalFinanceiro) {
        this.totalFinanceiro = totalFinanceiro;
    }

    public BigDecimal getTotalFisico() {
        return totalFisico;
    }

    public void setTotalFisico(BigDecimal totalFisico) {
        this.totalFisico = totalFisico;
    }

    public String getParticipanteAcaoPPAString() {
        StringBuilder sb = new StringBuilder();
        for (ParticipanteAcaoPrincipal p : getParticipanteAcaoPrincipals()) {
            sb.append(p.toString() + " / ");
        }
        sb.toString().substring(0, sb.toString().length() - 3);
        return sb.toString();
    }

    public List<ParticipanteAcaoPrincipal> getParticipanteAcaoPrincipals() {
        return participanteAcaoPrincipals;
    }

    public void setParticipanteAcaoPrincipals(List<ParticipanteAcaoPrincipal> participanteAcaoPrincipals) {
        this.participanteAcaoPrincipals = participanteAcaoPrincipals;
    }

    public List<ProdutoPPA> getProdutoPPAs() {
        return produtoPPAs;
    }

    public void setProdutoPPAs(List<ProdutoPPA> produtoPPAs) {
        this.produtoPPAs = produtoPPAs;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<AcaoPPA> getProjetosAtividades() {
        return projetosAtividades;
    }

    public void setProjetosAtividades(List<AcaoPPA> projetosAtividades) {
        this.projetosAtividades = projetosAtividades;
    }

    public StatusProduto getStatusAcao() {
        return statusAcao;
    }

    public void setStatusAcao(StatusProduto statusAcao) {
        this.statusAcao = statusAcao;
    }

    public StatusProduto getStatusProduto() {
        return statusProduto;
    }

    public void setStatusProduto(StatusProduto statusProduto) {
        this.statusProduto = statusProduto;
    }

    public StatusProduto getStatusMetaFisica() {
        return statusMetaFisica;
    }

    public void setStatusMetaFisica(StatusProduto statusMetaFisica) {
        this.statusMetaFisica = statusMetaFisica;
    }

    public Boolean getMetaFisicaAcumulativa() {
        return metaFisicaAcumulativa == null ? Boolean.FALSE : metaFisicaAcumulativa;
    }

    public void setMetaFisicaAcumulativa(Boolean metaFisicaAcumulativa) {
        this.metaFisicaAcumulativa = metaFisicaAcumulativa;
    }

    public void ordernarProdutoPPA() {
        Collections.sort(this.getProdutoPPAs(), new Comparator<ProdutoPPA>() {
            @Override
            public int compare(ProdutoPPA o1, ProdutoPPA o2) {
                String i1 = o1.getCodigo();
                String i2 = o2.getCodigo();
                return i1.compareTo(i2);
            }
        });
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public BigDecimal getSomaMetaFinanceiraProduto() {
        BigDecimal soma = BigDecimal.ZERO;
        if (this.produtoPPAs != null) {
            for (ProdutoPPA prod : this.produtoPPAs) {
                if (prod.getTotalFinanceiro() != null) {
                    soma = soma.add(prod.getTotalFinanceiro());
                }
            }
        }
        return soma;
    }

    public BigDecimal getSomaMetaFisicoProduto() {
        BigDecimal soma = BigDecimal.ZERO;
        if (this.produtoPPAs != null) {
            for (ProdutoPPA prod : this.produtoPPAs) {
                if (prod.getTotalFisico() != null) {
                    soma = soma.add(prod.getTotalFisico());
                }
            }
        }
        return soma;
    }

    public Boolean getModificaProduto() {
        modificaProduto = modificaProduto == null ? false : modificaProduto;
        return modificaProduto;
    }

    public void setModificaProduto(Boolean modificaProduto) {
        this.modificaProduto = modificaProduto;
    }

    public Boolean getModificaMetaFisica() {
        modificaMetaFisica = modificaMetaFisica == null ? false : modificaMetaFisica;
        return modificaMetaFisica;
    }

    public void setModificaMetaFisica(Boolean modificaMetaFisica) {
        this.modificaMetaFisica = modificaMetaFisica;
    }

    public OrigemAcao getOrigemAcao() {
        return origemAcao;
    }

    public void setOrigemAcao(OrigemAcao origemAcao) {
        this.origemAcao = origemAcao;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.AndamentoAcao;
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
 * @author Munif
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Etiqueta("Projeto/Atividade")
@Audited
public class AcaoPPA implements Serializable, Comparable {

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
    @Pesquisavel
    @Obrigatorio
    @Temporal(javax.persistence.TemporalType.DATE)
    @Etiqueta("Data de Cadastro")
    private Date dataCadastro;
    @ManyToOne
    @Etiqueta("Programa")
    @Pesquisavel
    @Tabelavel
    @Obrigatorio
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
    @Etiqueta("Descrição")
    private String descricao;

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
    @ManyToOne
    @Etiqueta("Unidade de Medida de Produto")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
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
    @Tabelavel
    private UnidadeOrganizacional responsavel;
    @Etiqueta("Participantes da Ação PPA")
    @OneToMany(mappedBy = "acaoPPA", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<ParticipanteAcaoPPA> participanteAcaoPPA;
    /**
     * Total de produtos que serão elaborados nesta ação
     */
    @Etiqueta("Total Físico")
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private BigDecimal totalFisico;
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
    private AndamentoAcao andamento;
    @Invisivel
    @Tabelavel(campoSelecionado = false)
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @OneToMany(mappedBy = "acaoPPA", cascade = CascadeType.ALL, orphanRemoval = true)
    @Etiqueta("Sub-Projeto/Atividade")
    @Tabelavel(campoSelecionado = false)
    private List<SubAcaoPPA> subAcaoPPAs;
    @OneToOne
    @Tabelavel(campoSelecionado = false)
    private AcaoPPA origem;
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Somente Leitura")
    private Boolean somenteLeitura;
    @Invisivel
    @ManyToOne
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    private Exercicio exercicio;
    @Transient
    public Long criadoEm;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Ação PPA")
    @Obrigatorio
    private AcaoPrincipal acaoPrincipal;

    public AcaoPPA() {
        somenteLeitura = false;
        totalFinanceiro = new BigDecimal(BigInteger.ZERO);
        dataRegistro = new Date();
        criadoEm = System.nanoTime();
    }

    public AcaoPPA(ProgramaPPA programa, TipoAcaoPPA tipoAcaoPPA, String descricao, String codigo, Periodicidade periodicidadeMedicao, String descricaoProduto, UnidadeMedida unidadeMedidaProduto, Funcao funcao, SubFuncao subFuncao, UnidadeOrganizacional responsavel, List<ParticipanteAcaoPPA> participanteAcaoPPA, BigDecimal totalFisico, BigDecimal totalFinanceiro, AndamentoAcao andamento, Date dataRegistro, List<SubAcaoPPA> subAcaoPPAs, AcaoPPA origem, Boolean somenteLeitura, Exercicio exercicio) {
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
        this.participanteAcaoPPA = participanteAcaoPPA;
        this.totalFisico = totalFisico;
        this.totalFinanceiro = totalFinanceiro;
        this.andamento = andamento;
        this.dataRegistro = dataRegistro;
        this.subAcaoPPAs = subAcaoPPAs;
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

    public AcaoPPA getOrigem() {
        return origem;
    }

    public void setOrigem(AcaoPPA origem) {
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

    public List<ParticipanteAcaoPPA> getParticipanteAcaoPPA() {
        return participanteAcaoPPA;
    }

    public String getParticipanteAcaoPPAString() {
        StringBuilder sb = new StringBuilder();
        for (ParticipanteAcaoPPA p : getParticipanteAcaoPPA()) {
            sb.append(p.toString() + " / ");
        }
        sb.toString().substring(0, sb.toString().length() - 3);
        return sb.toString();
    }

    public void setParticipanteAcaoPPA(List<ParticipanteAcaoPPA> participanteAcaoPPA) {
        this.participanteAcaoPPA = participanteAcaoPPA;
    }

    public List<SubAcaoPPA> getSubAcaoPPAs() {
        return subAcaoPPAs;
    }

    public void setSubAcaoPPAs(List<SubAcaoPPA> subAcaoPPAs) {
        this.subAcaoPPAs = subAcaoPPAs;
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

    public AcaoPrincipal getAcaoPrincipal() {
        return acaoPrincipal;
    }

    public void setAcaoPrincipal(AcaoPrincipal acaoPrincipal) {
        this.acaoPrincipal = acaoPrincipal;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
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
        if (codigo != null) {
            return codigo + " - " + descricao;
        } else {
            return descricao;
        }
    }

    public String toStringAutoCompleteRelatorio() {
        String toReturn = "";
        if (tipoAcaoPPA != null) {
            toReturn += tipoAcaoPPA.getCodigo();
        }
        if (codigo != null) {
            toReturn += codigo + " - ";
        }
        if (descricao != null) {
            toReturn += descricao;
        }
        return toReturn;
    }

    public String toStringAutoComplete() {
        String retorno = toStringAutoCompleteRelatorio();
        return retorno.length() > 68 ? retorno.substring(0, 65) + "..." : retorno;
    }

    @Override
    public int compareTo(Object o) {
        return criadoEm.compareTo(((AcaoPPA) o).getCriadoEm());
    }

    public void ordenarSubProjetoAtividade() {
        Collections.sort(this.getSubAcaoPPAs(), new Comparator<SubAcaoPPA>() {
            @Override
            public int compare(SubAcaoPPA o1, SubAcaoPPA o2) {
                String i1 = o1.getCodigo();
                String i2 = o2.getCodigo();
                return i1.compareTo(i2);
            }
        });
    }
}

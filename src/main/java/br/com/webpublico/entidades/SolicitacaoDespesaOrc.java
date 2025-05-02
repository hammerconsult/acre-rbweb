package br.com.webpublico.entidades;

import br.com.webpublico.enums.EsferaOrcamentaria;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 22/07/14
 * Time: 10:30
 * To change this template use File | Settings | File Templates.
 */
@Etiqueta("Solicitação de Despesa Orçamentária")
@GrupoDiagrama(nome = "Orcamentario")
@Audited
@Entity
public class SolicitacaoDespesaOrc extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private AlteracaoORC alteracaoORC;

    @Tabelavel
    @Etiqueta("Unidade Organizacional")
    @ManyToOne
    @Obrigatorio
    private UnidadeOrganizacional unidadeOrganizacional;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Projeto/Atividade")
    @Pesquisavel
    @Tabelavel
    private AcaoPPA acaoPPA;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Sub-Projeto/Atividade")
    @Pesquisavel
    @Tabelavel
    private SubAcaoPPA subAcaoPPA;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Conta de Despesa")
    @Pesquisavel
    @Tabelavel
    private Conta conta;

    @Tabelavel
    @Etiqueta("Fonte de Recurso")
    @ManyToOne
    @Obrigatorio
    private Conta destinacaoDeRecursos;

    @ManyToOne
    @Obrigatorio
    @Etiqueta("Extensão Fonte de Recurso")
    private ExtensaoFonteRecurso extensaoFonteRecurso;

    @Obrigatorio
    @Etiqueta("Esfera Orçamentária")
    @Pesquisavel
    @Tabelavel
    @Enumerated(EnumType.STRING)
    private EsferaOrcamentaria esferaOrcamentaria;

    @Etiqueta("Deferida Em")
    @Pesquisavel
    @Tabelavel
    @Temporal(TemporalType.DATE)
    private Date deferidaEm;

    @Obrigatorio
    @Etiqueta("Valor")
    @Pesquisavel
    @Tabelavel
    private BigDecimal valor;

    public SolicitacaoDespesaOrc() {
        valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public SubAcaoPPA getSubAcaoPPA() {
        return subAcaoPPA;
    }

    public void setSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
        this.subAcaoPPA = subAcaoPPA;
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public Conta getDestinacaoDeRecursos() {
        return destinacaoDeRecursos;
    }

    public void setDestinacaoDeRecursos(Conta destinacaoDeRecursos) {
        this.destinacaoDeRecursos = destinacaoDeRecursos;
    }

    public EsferaOrcamentaria getEsferaOrcamentaria() {
        return esferaOrcamentaria;
    }

    public void setEsferaOrcamentaria(EsferaOrcamentaria esferaOrcamentaria) {
        this.esferaOrcamentaria = esferaOrcamentaria;
    }

    public AlteracaoORC getAlteracaoORC() {
        return alteracaoORC;
    }

    public void setAlteracaoORC(AlteracaoORC alteracaoORC) {
        this.alteracaoORC = alteracaoORC;
    }

    public Date getDeferidaEm() {
        return deferidaEm;
    }

    public void setDeferidaEm(Date deferidaEm) {
        this.deferidaEm = deferidaEm;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public ExtensaoFonteRecurso getExtensaoFonteRecurso() {
        return extensaoFonteRecurso;
    }

    public void setExtensaoFonteRecurso(ExtensaoFonteRecurso extensaoFonteRecurso) {
        this.extensaoFonteRecurso = extensaoFonteRecurso;
    }

    @Override
    public String toString() {
        return conta.toString() + " - " + destinacaoDeRecursos.getCodigo() + "(" + Util.formataValor(valor) + ")";
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Claudio
 */
@Etiqueta("Estorno da Alteração Orçamentária")
@GrupoDiagrama(nome = "Orcamentario")
@Audited
@Entity
public class EstornoAlteracaoOrc extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Temporal(TemporalType.DATE)
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Data do Estorno")
    private Date dataEstorno;
    @Tabelavel
    @Pesquisavel
    @Etiqueta("Código")
    private String codigo;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta(value = "Alteração Orçamentária")
    private AlteracaoORC alteracaoORC;
    @ManyToOne
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Unidade Orçamentária")
    private UnidadeOrganizacional unidadeOrganizacionalOrc;
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Unidade Administrativa")
    private UnidadeOrganizacional unidadeOrganizacionalAdm;
    @Pesquisavel
    @Obrigatorio
    @Monetario
    @Tabelavel(ALINHAMENTO = Tabelavel.ALINHAMENTO.DIREITA)
    @Etiqueta("Valor (R$)")
    private BigDecimal valor;
    @ManyToOne
    @Etiqueta("Exercício")
    private Exercicio exercicio;
    @Pesquisavel
    @Tabelavel(campoSelecionado = false)
    @Etiqueta("Histórico")
    private String historico;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "estornoAlteracaoOrc", orphanRemoval = true)
    private List<EstornoSuplementacaoOrc> listaEstornoSuplementacaoOrc;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "estornoAlteracaoOrc", orphanRemoval = true)
    private List<EstornoAnulacaoOrc> listaEstornoAnulacaoOrc;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "estornoAlteracaoOrc", orphanRemoval = true)
    private List<EstornoReceitaAlteracaoOrc> listaEstornoReceitaAlteracaoOrc;

    public EstornoAlteracaoOrc() {
        listaEstornoSuplementacaoOrc = new ArrayList<EstornoSuplementacaoOrc>();
        listaEstornoAnulacaoOrc = new ArrayList<EstornoAnulacaoOrc>();
        listaEstornoReceitaAlteracaoOrc = new ArrayList<EstornoReceitaAlteracaoOrc>();
        this.valor = BigDecimal.ZERO;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AlteracaoORC getAlteracaoORC() {
        return alteracaoORC;
    }

    public void setAlteracaoORC(AlteracaoORC alteracaoORC) {
        this.alteracaoORC = alteracaoORC;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Date getDataEstorno() {
        return dataEstorno;
    }

    public void setDataEstorno(Date dataEstorno) {
        this.dataEstorno = dataEstorno;
    }

    public List<EstornoAnulacaoOrc> getListaEstornoAnulacaoOrc() {
        return listaEstornoAnulacaoOrc;
    }

    public List<EstornoReceitaAlteracaoOrc> getListaEstornoReceitaAlteracaoOrc() {
        return listaEstornoReceitaAlteracaoOrc;
    }

    public List<EstornoSuplementacaoOrc> getListaEstornoSuplementacaoOrc() {
        return listaEstornoSuplementacaoOrc;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalOrc() {
        return unidadeOrganizacionalOrc;
    }

    public void setUnidadeOrganizacionalOrc(UnidadeOrganizacional unidadeOrganizacionalOrc) {
        this.unidadeOrganizacionalOrc = unidadeOrganizacionalOrc;
    }

    public UnidadeOrganizacional getUnidadeOrganizacionalAdm() {
        return unidadeOrganizacionalAdm;
    }

    public void setUnidadeOrganizacionalAdm(UnidadeOrganizacional unidadeOrganizacionalAdm) {
        this.unidadeOrganizacionalAdm = unidadeOrganizacionalAdm;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public String getHistorico() {
        return historico;
    }

    public void setHistorico(String historico) {
        this.historico = historico;
    }

    public void setListaEstornoSuplementacaoOrc(List<EstornoSuplementacaoOrc> listaEstornoSuplementacaoOrc) {
        this.listaEstornoSuplementacaoOrc = listaEstornoSuplementacaoOrc;
    }

    public void setListaEstornoAnulacaoOrc(List<EstornoAnulacaoOrc> listaEstornoAnulacaoOrc) {
        this.listaEstornoAnulacaoOrc = listaEstornoAnulacaoOrc;
    }

    public void setListaEstornoReceitaAlteracaoOrc(List<EstornoReceitaAlteracaoOrc> listaEstornoReceitaAlteracaoOrc) {
        this.listaEstornoReceitaAlteracaoOrc = listaEstornoReceitaAlteracaoOrc;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    @Override
    public String toString() {
        return "Estorno da alteração: " + this.alteracaoORC.toString() + " (" + DataUtil.getDataFormatada(dataEstorno) + ")";
    }
}

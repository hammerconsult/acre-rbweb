/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author venon
 */
@Entity

@Audited
@GrupoDiagrama(nome = "PPA")
@Etiqueta("Grupo Orçamentário")
public class GrupoOrcamentario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Código")
    @Tabelavel
    @Pesquisavel
    private String codigo;
    @Obrigatorio
    @Etiqueta("Descrição")
    @Tabelavel
    @Pesquisavel
    private String descricao;
    @ManyToOne
    @Etiqueta("Entidade")
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private UnidadeOrganizacional entidade;
    @Etiqueta("Órgão")
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private UnidadeOrganizacional orgao;
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    @Etiqueta(value = "Unidade")
    private UnidadeOrganizacional unidade;
    @Etiqueta("Função")
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Funcao funcao;
    @Etiqueta("Subfunção")
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private SubFuncao subFuncao;
    @Etiqueta("Exercício")
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Exercicio exercicio;
    @Etiqueta("Programa PPA")
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private ProgramaPPA programaPPA;
    @Etiqueta("Ação PPA")
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private AcaoPPA acaoPPA;
    @Etiqueta("Subação PPA")
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private SubAcaoPPA subAcaoPPA;
    @Etiqueta("Fonte de Despesa Orçamentária")
    @OneToMany(mappedBy = "grupoOrcamentario")
    @Tabelavel(campoSelecionado = false)
    private List<FonteDespesaORC> fonteDespesaOrc;
    @ManyToOne
    @Etiqueta("Fonte de Recursos")
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private FonteDeRecursos fonteDeRecursos;
    @ManyToOne
    @Etiqueta("Natureza de Despesa")
    @Tabelavel(campoSelecionado = false)
    @Pesquisavel
    private Conta naturezaDespesa;

    public GrupoOrcamentario() {
        fonteDespesaOrc = new ArrayList<FonteDespesaORC>();
    }

    public GrupoOrcamentario(String codigo, String descricao, UnidadeOrganizacional entidade, UnidadeOrganizacional orgao, UnidadeOrganizacional unidade, Funcao funcao, SubFuncao subFuncao, Exercicio exercicio, ProgramaPPA programaPPA, AcaoPPA acaoPPA, SubAcaoPPA subAcaoPPA, List<FonteDespesaORC> fonteDespesaOrc, FonteDeRecursos fonteDeRecursos, Conta naturezaDespesa, List<CotaOrcamentaria> cotaOrcamentarias) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.entidade = entidade;
        this.orgao = orgao;
        this.unidade = unidade;
        this.funcao = funcao;
        this.subFuncao = subFuncao;
        this.exercicio = exercicio;
        this.programaPPA = programaPPA;
        this.acaoPPA = acaoPPA;
        this.subAcaoPPA = subAcaoPPA;
        this.fonteDespesaOrc = fonteDespesaOrc;
        this.fonteDeRecursos = fonteDeRecursos;
        this.naturezaDespesa = naturezaDespesa;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public UnidadeOrganizacional getEntidade() {
        return entidade;
    }

    public void setEntidade(UnidadeOrganizacional entidade) {
        this.entidade = entidade;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<FonteDespesaORC> getFonteDespesaOrc() {
        return fonteDespesaOrc;
    }

    public void setFonteDespesaOrc(List<FonteDespesaORC> fonteDespesaOrc) {
        this.fonteDespesaOrc = fonteDespesaOrc;
    }

    public Funcao getFuncao() {
        return funcao;
    }

    public void setFuncao(Funcao funcao) {
        this.funcao = funcao;
    }

    public UnidadeOrganizacional getOrgao() {
        return orgao;
    }

    public void setOrgao(UnidadeOrganizacional orgao) {
        this.orgao = orgao;
    }

    public SubFuncao getSubFuncao() {
        return subFuncao;
    }

    public void setSubFuncao(SubFuncao subFuncao) {
        this.subFuncao = subFuncao;
    }

    public UnidadeOrganizacional getUnidade() {
        return unidade;
    }

    public void setUnidade(UnidadeOrganizacional unidade) {
        this.unidade = unidade;
    }

    public AcaoPPA getAcaoPPA() {
        return acaoPPA;
    }

    public void setAcaoPPA(AcaoPPA acaoPPA) {
        this.acaoPPA = acaoPPA;
    }

    public ProgramaPPA getProgramaPPA() {
        return programaPPA;
    }

    public void setProgramaPPA(ProgramaPPA programaPPA) {
        this.programaPPA = programaPPA;
    }

    public SubAcaoPPA getSubAcaoPPA() {
        return subAcaoPPA;
    }

    public void setSubAcaoPPA(SubAcaoPPA subAcaoPPA) {
        this.subAcaoPPA = subAcaoPPA;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }

    public Conta getNaturezaDespesa() {
        return naturezaDespesa;
    }

    public void setNaturezaDespesa(Conta naturezaDespesa) {
        this.naturezaDespesa = naturezaDespesa;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GrupoOrcamentario)) {
            return false;
        }
        GrupoOrcamentario other = (GrupoOrcamentario) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}

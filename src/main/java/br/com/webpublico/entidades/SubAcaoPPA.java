/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Arthur
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Etiqueta("Sub-Projeto/Atividade")
@Audited
public class SubAcaoPPA implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Etiqueta("Código")
    @Tabelavel
    private String codigo;
    @Pesquisavel
    @Etiqueta("Descrição")
    @Tabelavel
    private String descricao;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Projeto/Atividade")
    @Tabelavel
    private AcaoPPA acaoPPA;
    @Etiqueta(value = "Valor Físico")
    @Tabelavel(campoSelecionado = false)
    private BigDecimal totalFisico;
    @Tabelavel(campoSelecionado = false)
    @Etiqueta(value = "Valor Financeiro")
    private BigDecimal totalFinanceiro;
    @Etiqueta("Provisão PPA")
    @ManyToOne
    @Tabelavel(campoSelecionado = false)
    private ProvisaoPPA provisaoPPA;
    @Etiqueta("Medições da Ação PPA")
    @OneToMany(mappedBy = "subAcaoPPA", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<MedicaoSubAcaoPPA> medicoesSubAcaoPPA;
    @Etiqueta("Provisões PPA Despesa")
    @OneToMany(mappedBy = "subAcaoPPA", cascade = CascadeType.ALL, orphanRemoval = true)
    @Tabelavel(campoSelecionado = false)
    private List<ProvisaoPPADespesa> provisaoPPADespesas;
    @Invisivel
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @OneToOne
    private SubAcaoPPA origem;
    @Etiqueta("Somente Leitura")
    private Boolean somenteLeitura;
    //criado para migração
    @Invisivel
    @ManyToOne
    private Exercicio exercicio;
    @Transient
    private Long criadoEm;

    public SubAcaoPPA() {
        somenteLeitura = false;
        criadoEm = System.nanoTime();
        medicoesSubAcaoPPA = Lists.newArrayList();
        provisaoPPADespesas = Lists.newArrayList();
    }

    public SubAcaoPPA(AcaoPPA acaoPPA, String descricao, String codigo, BigDecimal totalFisico, BigDecimal totalFinanceiro, ProvisaoPPA provisaoPPA, List<MedicaoSubAcaoPPA> medicoesSubAcaoPPA, Date dataRegistro, SubAcaoPPA origem, Boolean somenteLeitura, Exercicio exercicio) {
        this.acaoPPA = acaoPPA;
        this.descricao = descricao;
        this.codigo = codigo;
        this.totalFisico = totalFisico;
        this.totalFinanceiro = totalFinanceiro;
        this.provisaoPPA = provisaoPPA;
        this.medicoesSubAcaoPPA = medicoesSubAcaoPPA;
        this.dataRegistro = dataRegistro;
        this.origem = origem;
        this.somenteLeitura = somenteLeitura;
        this.exercicio = exercicio;
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

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<MedicaoSubAcaoPPA> getMedicoesSubAcaoPPA() {
        return medicoesSubAcaoPPA;
    }

    public void setMedicoesSubAcaoPPA(List<MedicaoSubAcaoPPA> medicoesSubAcaoPPA) {
        this.medicoesSubAcaoPPA = medicoesSubAcaoPPA;
    }

    public ProvisaoPPA getProvisaoPPA() {
        return provisaoPPA;
    }

    public void setProvisaoPPA(ProvisaoPPA provisaoPPA) {
        this.provisaoPPA = provisaoPPA;
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

    public SubAcaoPPA getOrigem() {
        return origem;
    }

    public void setOrigem(SubAcaoPPA origem) {
        this.origem = origem;
    }

    public Boolean getSomenteLeitura() {
        return somenteLeitura;
    }

    public void setSomenteLeitura(Boolean somenteLeitura) {
        this.somenteLeitura = somenteLeitura;
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

    public List<ProvisaoPPADespesa> getProvisaoPPADespesas() {
        return provisaoPPADespesas;
    }

    public void setProvisaoPPADespesas(List<ProvisaoPPADespesa> provisaoPPADespesas) {
        this.provisaoPPADespesas = provisaoPPADespesas;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

    public String toStringAutoCompleteRelatorio() {
        String toReturn = "";
        if (acaoPPA != null) {
            if (acaoPPA.getTipoAcaoPPA() != null) {
                toReturn += acaoPPA.getTipoAcaoPPA().getCodigo();
            }
            if (acaoPPA.getCodigo() != null) {
                toReturn += acaoPPA.getCodigo() + ".";
            }
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
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.interfaces.InterfaceComponenteFormula;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * Representa um item de uma fórmula de consolidação de lançamentos obtidos dos
 * movimentos, os quais serão escolhidos em função do tipo da fórmula
 * (PREVISTA/REALIZADA). É utilizada para somar ou subtrair valores que serão
 * agrupados em um ItemDemonstrativo.
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "Componente")
@Audited
@Entity

@Etiqueta("Componente Fórmula")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ComponenteFormula implements Serializable, InterfaceComponenteFormula {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Operação Fómula")
    @Enumerated(EnumType.STRING)
    private OperacaoFormula operacaoFormula;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Fórmula Item Demonstrativo")
    @ManyToOne
    private FormulaItemDemonstrativo formulaItemDemonstrativo;
    @Temporal(javax.persistence.TemporalType.TIMESTAMP)
    private Date dataRegistro;
    @Transient
    private Long criadoEm;
    @Transient
    private Integer coluna;

    public ComponenteFormula() {
        dataRegistro = new Date();
        this.criadoEm = System.nanoTime();
    }

    public ComponenteFormula(OperacaoFormula operacaoFormula, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        this.operacaoFormula = operacaoFormula;
        this.formulaItemDemonstrativo = formulaItemDemonstrativo;
        dataRegistro = new Date();
        this.criadoEm = System.nanoTime();
    }

    public String getContasItens() {
        String valor = "";
        if (this instanceof ComponenteFormulaConta) {
            if (((ComponenteFormulaConta) this).getConta() != null) {
                valor = ((ComponenteFormulaConta) this).getConta().toString();
            }
        } else if (this instanceof ComponenteFormulaItem) {
            valor = ((ComponenteFormulaItem) this).getItemDemonstrativo().toString();
        }

        return valor;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDataRegistro() {
        return dataRegistro;
    }

    public void setDataRegistro(Date dataRegistro) {
        this.dataRegistro = dataRegistro;
    }

    public OperacaoFormula getOperacaoFormula() {
        return operacaoFormula;
    }

    public void setOperacaoFormula(OperacaoFormula operacaoFormula) {
        this.operacaoFormula = operacaoFormula;
    }

    public FormulaItemDemonstrativo getFormulaItemDemonstrativo() {
        return formulaItemDemonstrativo;
    }

    public void setFormulaItemDemonstrativo(FormulaItemDemonstrativo formulaItemDemonstrativo) {
        this.formulaItemDemonstrativo = formulaItemDemonstrativo;
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
        if (this instanceof ComponenteFormulaConta) {
            if (((ComponenteFormulaConta) this).getConta() != null) {
                return ((ComponenteFormulaConta) this).getConta().toString();
            }
            return "";
        } else if (this instanceof ComponenteFormulaAcao) {
            return ((ComponenteFormulaAcao) this).getAcaoPPA().getDescricao() + " (\"Ação\")";
        } else if (this instanceof ComponenteFormulaFonteRecurso) {
            return ((ComponenteFormulaFonteRecurso) this).getFonteDeRecursos().getDescricao() + " (\"Fonte de Recursos\")";
        } else if (this instanceof ComponenteFormulaFuncao) {
            return ((ComponenteFormulaFuncao) this).getFuncao().getDescricao() + " (\"Função\")";
        } else if (this instanceof ComponenteFormulaSubFuncao) {
            return ((ComponenteFormulaSubFuncao) this).getSubFuncao().getDescricao() + " (\"Subfunção\")";
        } else if (this instanceof ComponenteFormulaPrograma) {
            return ((ComponenteFormulaPrograma) this).getProgramaPPA().getDenominacao() + " (\"Programa\")";
        } else if (this instanceof ComponenteFormulaUnidadeOrganizacional) {
            return ((ComponenteFormulaUnidadeOrganizacional) this).getUnidadeOrganizacional().getDescricao() + " (\"Unidade Organizacional\")";
        } else if (this instanceof ComponenteFormulaSubConta) {
            return ((ComponenteFormulaSubConta) this).getSubConta().toString() + " (\"Conta Financeira\")";
        } else {
            return ((ComponenteFormulaItem) this).getItemDemonstrativo().toString() + " (\"Item Demonstrativo\")";
        }
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Integer getColuna() {
        return coluna;
    }

    public void setColuna(Integer coluna) {
        this.coluna = coluna;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.geradores.GrupoDiagrama;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * @author juggernaut
 */
@GrupoDiagrama(nome = "Componente")
@Entity

public class ComponenteFormulaConta extends ComponenteFormula implements Serializable {

    /**
     * Se esta operação for de uma fórmula PREVISTA, somente contas de receita
     * ou despesa. Se esta operação for de uma fórmula REALIZADA, somente contas
     * contábeis.
     */
    @ManyToOne
    private Conta conta;
    @Transient
    private Long criadoEm;

    public ComponenteFormulaConta() {
        this.criadoEm = System.nanoTime();
    }

    public Conta getConta() {
        return conta;
    }

    public void setConta(Conta conta) {
        this.conta = conta;
    }

    public ComponenteFormulaConta(Long id, Conta conta, OperacaoFormula operacaoFormula, FormulaItemDemonstrativo formulaItemDemonstrativo) {
        super(operacaoFormula, formulaItemDemonstrativo);
        this.conta = conta;
        this.criadoEm = System.nanoTime();
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    @Override
    public ComponenteFormula criarComponenteFormula() {
        ComponenteFormulaConta novo = new ComponenteFormulaConta();
        novo.setConta(getConta());
        return novo;
    }
}

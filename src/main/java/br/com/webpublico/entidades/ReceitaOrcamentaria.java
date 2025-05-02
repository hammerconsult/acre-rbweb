/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.Cacheable;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

@GrupoDiagrama(nome = "Orcamentario")
@Audited
@Entity

public class ReceitaOrcamentaria extends LancamentoOrcamentario {

    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    /**
     * Conta corrente na qual será gerado o lançamento de integração com o
     * financeiro.
     */
    @ManyToOne
    private ContaCorrenteBancaria contaCorrente;
    /**
     * Validada com base na ReceitaExercicioPPA do PPA vigente para o exercício.
     */
    @ManyToOne
    private Conta contaDeReceita;
    /**
     * Lançamento gerado na conta corrente em decorrência da integração
     * financeiro/orçamento.
     */
    @ManyToOne
    private LancamentoContaBancaria lancamentoContaBancaria;
}

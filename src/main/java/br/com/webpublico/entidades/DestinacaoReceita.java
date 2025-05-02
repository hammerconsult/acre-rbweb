/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Determina os percentuais do valor de receita de determinada contaDeReceita
 * que serão vinculados como determinadas fontesDeRecursos.
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "PPA")
@Audited
@Entity

public class DestinacaoReceita implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Somente uma conta de receita pode ser selecionada! Verificar qual é o plano de contas
     * de receita vigente no exercício do PPA para filtrar as contas que poderão ser vinculadas
     * (ver PlanoDecontasExercicio)
     */
    @ManyToOne
    private Conta contaDeReceita;

    /**
     * Somente uma conta de destinação de recurso pode ser selecionada! Verificar qual é o plano de contas
     * de destinações de recursos vigente no exercício do PPA para filtrar as contas que poderão ser vinculadas
     * (ver PlanoDecontasExercicio)
     */
    @ManyToOne
    private Conta destinacaoDeRecursos;

    /**
     * Percentual do valor obtido na contaDeReceita que deverá ser destinado como determinada
     * fonteDeRecursos
     */
    private BigDecimal percentual;

    public Conta getContaDeReceita() {
        return contaDeReceita;
    }

    public void setContaDeReceita(Conta contaDeReceita) {
        this.contaDeReceita = contaDeReceita;
    }

    public Conta getDestinacaoDeRecursos() {
        return destinacaoDeRecursos;
    }

    public void setDestinacaoDeRecursos(Conta destinacaoDeRecursos) {
        this.destinacaoDeRecursos = destinacaoDeRecursos;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getPercentual() {
        return percentual;
    }

    public void setPercentual(BigDecimal percentual) {
        this.percentual = percentual;
    }

}

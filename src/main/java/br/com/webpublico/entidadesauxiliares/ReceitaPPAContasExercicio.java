/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.ReceitaPPAContas;
import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author wiplash
 */
public class ReceitaPPAContasExercicio {

    private Exercicio exercicio;
    private List<ReceitaPPAContas> receitasPPAContas;
    private BigDecimal totalValor;

    public ReceitaPPAContasExercicio() {
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<ReceitaPPAContas> getReceitasPPAContas() {
        return receitasPPAContas;
    }

    public void setReceitasPPAContas(List<ReceitaPPAContas> receitasPPAContas) {
        this.receitasPPAContas = receitasPPAContas;
    }

    public BigDecimal getTotalValor() {
        return totalValor;
    }

    public void setTotalValor(BigDecimal totalValor) {
        this.totalValor = totalValor;
    }
}

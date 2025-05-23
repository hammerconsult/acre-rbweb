/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades.rh.configuracao;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidadesauxiliares.ProcessoCalculoReajuste;
import br.com.webpublico.enums.Mes;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Etiqueta(value = "Reajuste Média Aposentadoria")
@Audited
public class ReajusteMediaAposentadoria extends SuperEntidade implements Serializable, Comparable<ReajusteMediaAposentadoria> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Exercício")
    @ManyToOne
    @Pesquisavel
    private Exercicio exercicio;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Exercício de Referência")
    @ManyToOne
    @Pesquisavel
    private Exercicio exercicioReferencia;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Mês")
    @Pesquisavel
    @Enumerated(value = EnumType.STRING)
    private Mes mes;
    @Tabelavel
    @Pesquisavel
    @Obrigatorio
    @Etiqueta("Valor do Reajuste")
    private BigDecimal valorReajuste;
    @Transient
    private List<ProcessoCalculoReajuste> processosCalculo;
    @Transient
    private Boolean reajusteTransiente;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public Mes getMes() {
        return mes;
    }

    public void setMes(Mes mes) {
        this.mes = mes;
    }

    public BigDecimal getValorReajuste() {
        return valorReajuste;
    }

    public void setValorReajuste(BigDecimal valorReajuste) {
        this.valorReajuste = valorReajuste;
    }

    public List<ProcessoCalculoReajuste> getProcessosCalculo() {
        return processosCalculo;
    }

    public void setProcessosCalculo(List<ProcessoCalculoReajuste> processosCalculo) {
        this.processosCalculo = processosCalculo;
    }

    @Override
    public String toString() {
        return getMes().getNumeroMes() + "/" + getExercicio() + " " + getValorReajuste();
    }

    public String toStringAlternativo() {
        if (!getReajusteTransiente()) {
            return mes.getDescricao() + " - " + exercicio + " - " + valorReajuste + "%";
        }
        return mes.getDescricao() + " - " + exercicio + " Não foi encontrado registro de percentual cadastrado. Os aposentados/pensionistas previdenciários abaixo não sofrerão reajuste:";
    }


    public void addProcessosCalculo(List<ProcessoCalculoReajuste> list) {
        if (processosCalculo == null) {
            processosCalculo = new LinkedList<>();
        }
        processosCalculo.addAll(list);
    }

    public void addProcessoCalculo(ProcessoCalculoReajuste processo) {
        if (processosCalculo == null) {
            processosCalculo = new LinkedList<>();
        }
        processosCalculo.add(processo);
    }

    public Exercicio getExercicioReferencia() {
        return exercicioReferencia;
    }

    public void setExercicioReferencia(Exercicio exercicioReferencia) {
        this.exercicioReferencia = exercicioReferencia;
    }

    public Boolean getReajusteTransiente() {
        return reajusteTransiente == null ? Boolean.FALSE : reajusteTransiente;
    }

    public void setReajusteTransiente(Boolean reajusteTransiente) {
        this.reajusteTransiente = reajusteTransiente;
    }

    @Override
    public int compareTo(ReajusteMediaAposentadoria o) {
        if (o.getMes() != null && this.getMes() != null)
            return this.getMes().getNumeroMes().compareTo(o.getMes().getNumeroMes());
        return 0;
    }

    public boolean todosItensMarcados() {
        try {
            for (ProcessoCalculoReajuste pcr : processosCalculo) {
                if (!pcr.getSelecionado()) {
                    return false;
                }
            }
            return true;
        } catch (NullPointerException npe) {
            return false;
        }
    }

    public List<ProcessoCalculoReajuste> getCalculosMarcados() {
        List<ProcessoCalculoReajuste> calculosMarcados = new ArrayList<>();
        if (processosCalculo != null && !processosCalculo.isEmpty()) {
            for (ProcessoCalculoReajuste calculo : processosCalculo) {
                if (calculo.getSelecionado() != null && calculo.getSelecionado()) {
                    calculosMarcados.add(calculo);
                }
            }
        }
        return calculosMarcados;
    }
}

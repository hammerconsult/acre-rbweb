/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.OperacaoFormula;
import br.com.webpublico.enums.TipoFormulaItemDemonstrativo;
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
 * Representa a fórmula de determinado ItemDemonstrativo, determinando o tipo
 * (PREVISTO/REALIZADO) e permitindo a associação com componentes da fórmula que
 * representam as contas que serão somadas ou subtraídas do total.
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "Componente")
@Audited
@Entity

@Etiqueta("Fórmula Item Demonstrativo")
public class FormulaItemDemonstrativo extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Item Demonstrativo")
    private ItemDemonstrativo itemDemonstrativo;
    @Tabelavel
    @Etiqueta("Fórmula Item Demonstrativo")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private TipoFormulaItemDemonstrativo tipoFormulaItemDemonstrativo;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Operação Fómula")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private OperacaoFormula operacaoFormula;
    @Tabelavel
    @Etiqueta("Coluna")
    @Pesquisavel
    private Integer coluna;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "formulaItemDemonstrativo")
    private List<ComponenteFormula> componenteFormula;
    @ManyToOne
    private PlanoDeContas planoDeContas;
    @ManyToOne
    private Exercicio exercicio;
    @ManyToOne
    @Etiqueta("Relatório do Item Demonstrativo")
    @Pesquisavel
    private RelatoriosItemDemonst relatoriosItemDemonst;

    public FormulaItemDemonstrativo() {
        super();
        this.componenteFormula = new ArrayList<ComponenteFormula>();
    }

    public FormulaItemDemonstrativo(ItemDemonstrativo itemDemonstrativo, TipoFormulaItemDemonstrativo tipoFormulaItemDemonstrativo, OperacaoFormula operacaoFormula, List<ComponenteFormula> componenteFormula, PlanoDeContas planoDeContas, Exercicio exercicio, RelatoriosItemDemonst relatoriosItemDemonst) {
        this.itemDemonstrativo = itemDemonstrativo;
        this.operacaoFormula = operacaoFormula;
        this.componenteFormula = componenteFormula;
        this.planoDeContas = planoDeContas;
        this.exercicio = exercicio;
        this.relatoriosItemDemonst = relatoriosItemDemonst;
        this.criadoEm = System.nanoTime();
    }

    public List<ComponenteFormula> getComponenteFormula() {
       return componenteFormula;
    }

    public void setComponenteFormula(List<ComponenteFormula> componenteFormula) {
        this.componenteFormula = componenteFormula;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ItemDemonstrativo getItemDemonstrativo() {
        return itemDemonstrativo;
    }

    public void setItemDemonstrativo(ItemDemonstrativo itemDemonstrativo) {
        this.itemDemonstrativo = itemDemonstrativo;
    }

    public TipoFormulaItemDemonstrativo getTipoFormulaItemDemonstrativo() {
        return tipoFormulaItemDemonstrativo;
    }

    public void setTipoFormulaItemDemonstrativo(TipoFormulaItemDemonstrativo tipoFormulaItemDemonstrativo) {
        this.tipoFormulaItemDemonstrativo = tipoFormulaItemDemonstrativo;
    }

    public PlanoDeContas getPlanoDeContas() {
        return planoDeContas;
    }

    public void setPlanoDeContas(PlanoDeContas planoDeContas) {
        this.planoDeContas = planoDeContas;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public OperacaoFormula getOperacaoFormula() {
        return operacaoFormula;
    }

    public void setOperacaoFormula(OperacaoFormula operacaoFormula) {
        this.operacaoFormula = operacaoFormula;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }

    public Integer getColuna() {
        return coluna;
    }

    public void setColuna(Integer coluna) {
        this.coluna = coluna;
    }
}

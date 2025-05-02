/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.contabil.relatorioitemdemonstrativo.ItemDemonstrativoDTO;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Representa um elemento consolidado nos relatórios contábeis/orçamentários.
 * Seu valor é resultado da soma ou subtração de valores de outras contas que
 * possuam movimentos, as quais serão selecionadas com base no tipo de suas
 * fórmulas (PREVISTAS/REALIZADAS)
 * <p/>
 * No ato da emissão dos relatórios que utilizem itens demonstrativos, a
 * aplicação consultará a fórmula e seus componentes para determinar o valor
 * total do item.
 *
 * @author arthur
 */
@GrupoDiagrama(nome = "Componente")
@Audited
@Entity
@Etiqueta("Item Demonstrativo")
public class ItemDemonstrativo  extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Descrição")
    @Pesquisavel
    private String descricao;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Nome")
    @Pesquisavel
    private String nome;
    @Etiqueta("Inverte Sinal?")
    private Boolean inverteSinal;
    @Etiqueta("Exibir no Relatório?")
    private Boolean exibirNoRelatorio;
    @OneToMany(mappedBy = "itemDemonstrativo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FormulaItemDemonstrativo> formulas;
    @ManyToOne
    private ItemDemonstrativo itemExercicioAnterior;
    private Integer numeroLinhaNoXLS;

    @Obrigatorio
    @Etiqueta("Ordem")
    private Integer ordem;
    @Obrigatorio
    @Etiqueta("Grupo")
    private Integer grupo;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Coluna")
    @Pesquisavel
    private Integer coluna;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Espaços")
    @Pesquisavel
    private Integer espaco;
    @ManyToOne
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Relatório do Item Demonstrativo")
    private RelatoriosItemDemonst relatoriosItemDemonst;

    public ItemDemonstrativo() {
        super();
        formulas = Lists.newArrayList();
        inverteSinal = false;
        exibirNoRelatorio = true;
    }

    public ItemDemonstrativoDTO toDto() {
        ItemDemonstrativoDTO itemDemonstrativoDTO = new ItemDemonstrativoDTO();
        itemDemonstrativoDTO.setInverterSinal(inverteSinal);
        itemDemonstrativoDTO.setId(id);
        itemDemonstrativoDTO.setDescricao(descricao);
        itemDemonstrativoDTO.setNome(nome);
        itemDemonstrativoDTO.setNumeroLinhaNoXLS(numeroLinhaNoXLS);
        itemDemonstrativoDTO.setOrdem(ordem);
        itemDemonstrativoDTO.setGrupo(grupo);
        itemDemonstrativoDTO.setColuna(coluna);
        itemDemonstrativoDTO.setEspaco(espaco);
        return itemDemonstrativoDTO;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public List<FormulaItemDemonstrativo> getFormulas() {
        return formulas;
    }

    public void setFormulas(List<FormulaItemDemonstrativo> formulas) {
        this.formulas = formulas;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Boolean getInverteSinal() {
        return inverteSinal;
    }

    public void setInverteSinal(Boolean inverteSinal) {
        this.inverteSinal = inverteSinal;
    }

    public ItemDemonstrativo getItemExercicioAnterior() {
        return itemExercicioAnterior;
    }

    public void setItemExercicioAnterior(ItemDemonstrativo itemExercicioAnterior) {
        this.itemExercicioAnterior = itemExercicioAnterior;
    }

    public Boolean getExibirNoRelatorio() {
        return exibirNoRelatorio  == null ? Boolean.FALSE : exibirNoRelatorio;
    }

    public void setExibirNoRelatorio(Boolean exibirNoRelatorio) {
        this.exibirNoRelatorio = exibirNoRelatorio;
    }

    public Integer getNumeroLinhaNoXLS() {
        return numeroLinhaNoXLS;
    }

    public void setNumeroLinhaNoXLS(Integer numeroLinhaNoXLS) {
        this.numeroLinhaNoXLS = numeroLinhaNoXLS;
    }

    public Integer getOrdem() {
        return ordem;
    }

    public void setOrdem(Integer ordem) {
        this.ordem = ordem;
    }

    public Integer getGrupo() {
        return grupo;
    }

    public void setGrupo(Integer grupo) {
        this.grupo = grupo;
    }

    public Integer getColuna() {
        return coluna;
    }

    public void setColuna(Integer coluna) {
        this.coluna = coluna;
    }

    public Integer getEspaco() {
        return espaco;
    }

    public void setEspaco(Integer espaco) {
        this.espaco = espaco;
    }

    public RelatoriosItemDemonst getRelatoriosItemDemonst() {
        return relatoriosItemDemonst;
    }

    public void setRelatoriosItemDemonst(RelatoriosItemDemonst relatoriosItemDemonst) {
        this.relatoriosItemDemonst = relatoriosItemDemonst;
    }

    public String toStringAutoComplete() {
        return descricao + " (" + nome + ") " + " - Ordem: " + ordem + " - Grupo: " + grupo;
    }
}

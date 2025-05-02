/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author Munif
 */
@GrupoDiagrama(nome = "PPA")
@Entity

@Audited
@Etiqueta("Planejamento Estratégico")
public class PlanejamentoEstrategico implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Descrição")
    @Pesquisavel
    private String descricao;
    @Etiqueta("Visão")
    @Tabelavel
    @Pesquisavel
    private String visao;
    @Etiqueta("Missão")
    @Pesquisavel
    @Tabelavel
    private String missao;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Valores")
    private String valores;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "planejamentoEstrategico", orphanRemoval = true)
    @Etiqueta("Exercício")
    @Obrigatorio
    private List<ExercicioPlanoEstrategico> exerciciosPlanoEstrategico;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "planejamentoEstrategico", orphanRemoval = true)
    @Etiqueta("Objetivos dos Eixos")
    private List<ItemPlanejamentoEstrategico> itens;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "planejamentoEstrategico", orphanRemoval = true)
    @Etiqueta("Eixos Estratégicos")
    private List<MacroObjetivoEstrategico> macros;
    @Transient
    private Long criadoEm;

    public PlanejamentoEstrategico() {
        criadoEm = System.nanoTime();
    }

    public PlanejamentoEstrategico(String visao, String missao, String valores, String descricao, List<ExercicioPlanoEstrategico> exerciciosPlanoEstrategico, List<ItemPlanejamentoEstrategico> itens, List<MacroObjetivoEstrategico> macros) {
        this.visao = visao;
        this.missao = missao;
        this.valores = valores;
        this.descricao = descricao;
        this.exerciciosPlanoEstrategico = exerciciosPlanoEstrategico;
        this.itens = itens;
        this.macros = macros;
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<ItemPlanejamentoEstrategico> getItens() {
        return itens;
    }

    public void setItens(List<ItemPlanejamentoEstrategico> itens) {
        this.itens = itens;
    }

    public String getMissao() {
        return missao;
    }

    public void setMissao(String missao) {
        this.missao = missao;
    }

    public String getValores() {
        return valores;
    }

    public void setValores(String valores) {
        this.valores = valores;
    }

    public String getVisao() {
        return visao;
    }

    public void setVisao(String visao) {
        this.visao = visao;
    }

    public List<ExercicioPlanoEstrategico> getExerciciosPlanoEstrategico() {
        return exerciciosPlanoEstrategico;
    }

    public void setExerciciosPlanoEstrategico(List<ExercicioPlanoEstrategico> exerciciosPlanoEstrategico) {
        this.exerciciosPlanoEstrategico = exerciciosPlanoEstrategico;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public List<MacroObjetivoEstrategico> getMacros() {
        return macros;
    }

    public void setMacros(List<MacroObjetivoEstrategico> macros) {
        this.macros = macros;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public String toString() {
        return descricao;
    }
}

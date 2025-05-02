/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoRecursoAquisicaoBem;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 *
 * @author Arthur Zavadski
 * @author Lucas Cheles
 */
@Entity
@Audited
@Etiqueta("Origem de Recurso do Bem")
public class OrigemRecursoBem extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Obrigatorio
    @Etiqueta("Tipo")
    @Enumerated(EnumType.STRING)
    private TipoRecursoAquisicaoBem tipoRecursoAquisicaoBem;

    @Obrigatorio
    @Etiqueta("Descrição")
    private String descricao;

    @Etiqueta("Fonte de Despesa")
    private String fonteDespesa;

    @Etiqueta("Despesa Orçamentária")
    private String despesaOrcamentaria;

    @ManyToOne
    private DetentorOrigemRecurso detentorOrigemRecurso;

    @ManyToOne
    private LevantamentoBemPatrimonial levantamentoBemPatrimonial;

    public OrigemRecursoBem() {
        super();
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

    public TipoRecursoAquisicaoBem getTipoRecursoAquisicaoBem() {
        return tipoRecursoAquisicaoBem;
    }

    public void setTipoRecursoAquisicaoBem(TipoRecursoAquisicaoBem tipoRecursoAquisicaoBem) {
        this.tipoRecursoAquisicaoBem = tipoRecursoAquisicaoBem;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public DetentorOrigemRecurso getDetentorOrigemRecurso() {
        return detentorOrigemRecurso;
    }

    public void setDetentorOrigemRecurso(DetentorOrigemRecurso detentorOrigemRecurso) {
        this.detentorOrigemRecurso = detentorOrigemRecurso;
    }

    public LevantamentoBemPatrimonial getLevantamentoBemPatrimonial() {
        return levantamentoBemPatrimonial;
    }

    public void setLevantamentoBemPatrimonial(LevantamentoBemPatrimonial levantamentoBemPatrimonial) {
        this.levantamentoBemPatrimonial = levantamentoBemPatrimonial;
    }

    public String getFonteDespesa() {
        return fonteDespesa;
    }

    public void setFonteDespesa(String fonteDespesa) {
        this.fonteDespesa = fonteDespesa;
    }

    public String getDespesaOrcamentaria() {
        return despesaOrcamentaria;
    }

    public void setDespesaOrcamentaria(String despesaOrcamentaria) {
        this.despesaOrcamentaria = despesaOrcamentaria;
    }
}

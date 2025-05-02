/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.enums.TargetListaRelatorio;
import br.com.webpublico.enums.TipoFuncaoAgrupador;
import br.com.webpublico.util.AtributoMetadata;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import br.com.webpublico.util.anotacoes.Tabelavel.ALINHAMENTO;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

/**
 * @author Romanini
 */
@Entity

@Audited
@Etiqueta("Atributo de Relatório Generico")
public class AtributoRelatorioGenerico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private RelatorioPesquisaGenerico relatorioPesquisaGenerico;
    private String condicao;
    private String label;
    @Enumerated(EnumType.STRING)
    private TargetListaRelatorio targetListaRelatorio;
    private Boolean atributoDeEntidade;
    private Boolean atributoDeLista;
    private Boolean podeImprimir;
    @Enumerated(EnumType.STRING)
    private Tabelavel.ALINHAMENTO alinhamento;
    private String classe;
    private Boolean agrupador;
    @Enumerated(EnumType.STRING)
    private TipoFuncaoAgrupador funcaoAgrupador;
    private Integer ordemExibicao;
    @Transient
    protected transient Field field;
    @Transient
    private Long criadoEm;

    public AtributoRelatorioGenerico() {
        criadoEm = System.nanoTime();
    }

    public AtributoRelatorioGenerico(String classe, String condicao, String label, Boolean atributoDeEntidade, Boolean atributoDeLista, Boolean podeImprimir, ALINHAMENTO alinhamento, Boolean agrupavel, TipoFuncaoAgrupador funcaoAgrupador, Field field) {
        this.classe = classe;
        this.condicao = condicao;
        this.label = label;
        this.atributoDeEntidade = atributoDeEntidade;
        this.atributoDeLista = atributoDeLista;
        this.podeImprimir = podeImprimir;
        this.agrupador = agrupavel;
        this.alinhamento = alinhamento;
        this.funcaoAgrupador = funcaoAgrupador;
        this.field = field;
    }

    public String getCondicao() {
        return condicao;
    }

    public void setCondicao(String condicao) {
        this.condicao = condicao;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RelatorioPesquisaGenerico getRelatorioPesquisaGenerico() {
        return relatorioPesquisaGenerico;
    }

    public void setRelatorioPesquisaGenerico(RelatorioPesquisaGenerico relatorioPesquisaGenerico) {
        this.relatorioPesquisaGenerico = relatorioPesquisaGenerico;
    }

    public TargetListaRelatorio getTargetListaRelatorio() {
        return targetListaRelatorio;
    }

    public void setTargetListaRelatorio(TargetListaRelatorio targetListaRelatorio) {
        this.targetListaRelatorio = targetListaRelatorio;
    }

    public Boolean getAtributoDeEntidade() {
        return atributoDeEntidade;
    }

    public void setAtributoDeEntidade(Boolean atributoDeEntidade) {
        this.atributoDeEntidade = atributoDeEntidade;
    }

    public ALINHAMENTO getAlinhamento() {
        return alinhamento;
    }

    public void setAlinhamento(ALINHAMENTO alinhamento) {
        this.alinhamento = alinhamento;
    }

    public Boolean getAtributoDeLista() {
        return atributoDeLista;
    }

    public void setAtributoDeLista(Boolean atributoDeLista) {
        this.atributoDeLista = atributoDeLista;
    }

    public Boolean getPodeImprimir() {
        return podeImprimir;
    }

    public void setPodeImprimir(Boolean podeImprimir) {
        this.podeImprimir = podeImprimir;
    }

    public String getClasse() {
        return classe;
    }

    public void setClasse(String classe) {
        this.classe = classe;
    }

    public Boolean getAgrupador() {
        return agrupador;
    }

    public void setAgrupador(Boolean agrupador) {
        this.agrupador = agrupador;
    }

    public TipoFuncaoAgrupador getFuncaoAgrupador() {
        return funcaoAgrupador;
    }

    public void setFuncaoAgrupador(TipoFuncaoAgrupador funcaoAgrupador) {
        this.funcaoAgrupador = funcaoAgrupador;
    }


    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Integer getOrdemExibicao() {
        return ordemExibicao;
    }

    public void setOrdemExibicao(Integer ordemExibicao) {
        this.ordemExibicao = ordemExibicao;
    }

    public boolean isTransiente() {
        try {
            return field.getAnnotation(Transient.class) != null;
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean renderIsNumber() {
        if (this.getClasse().equals(Integer.class.getName())
                || this.getClasse().equals(Long.class.getName())
                || this.getClasse().equals(BigDecimal.class.getName())
                || this.getClasse().equals(Double.class.getName())) {
            return true;
        }
        return false;
    }

    public AtributoRelatorioGenerico instanciaAtributoRelatorioGenerico(AtributoMetadata atributoMetadata) {
        String condicao = atributoMetadata.getAtributo().getName();
        String label = atributoMetadata.getEtiqueta();
        Boolean atributoDeLista = atributoMetadata.getAtributo().getType().equals(List.class) || atributoMetadata.getAtributo().getType().equals(Set.class) ? true : false;
        Boolean podeImprimir = false;
        Boolean agrupavel = false;
        Tabelavel.ALINHAMENTO alinhamento = null;
        if (atributoMetadata.getAtributo().isAnnotationPresent(Tabelavel.class)) {
            Tabelavel annotation = atributoMetadata.getAtributo().getAnnotation(Tabelavel.class);
            podeImprimir = annotation.podeImprimir();
            alinhamento = annotation.ALINHAMENTO();
            agrupavel = annotation.agrupavel();
        }
        TipoFuncaoAgrupador funcaoAgrupador = null;
        if (atributoMetadata.getAtributo().getType().equals(BigDecimal.class)) {
            funcaoAgrupador = TipoFuncaoAgrupador.SOMAR;
        }
        this.classe = atributoMetadata.getAtributo().getType().getName();
        this.condicao = condicao;
        this.label = label;
        this.atributoDeEntidade = Persistencia.isAtributoDeEntidade(atributoMetadata.getAtributo());
        this.atributoDeLista = atributoDeLista;
        this.podeImprimir = podeImprimir;
        this.agrupador = agrupavel;
        this.alinhamento = alinhamento;
        this.funcaoAgrupador = funcaoAgrupador;
        this.field = atributoMetadata.getAtributo();
        return this;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }
}

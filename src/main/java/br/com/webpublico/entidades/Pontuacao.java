/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoPontoIPTU;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@GrupoDiagrama(nome = "CadastroImobiliario")
@Entity
@Etiqueta("Fator de Correção e Pontuação Imobiliária")
@Audited
public class Pontuacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @Pesquisavel
    @Etiqueta("Exercício")
    @Tabelavel
    private Exercicio exercicio;
    @ManyToMany
    private List<Atributo> atributos;
    @OneToMany(mappedBy = "pontuacao", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPontuacao> itens;
    @Tabelavel
    @Obrigatorio
    @Etiqueta("Identificação")
    @Pesquisavel
    private String identificacao;
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta("Tipo de Pontuação")
    @Tabelavel
    private TipoPontoIPTU tipoPontoIPTU;
    private Boolean utilizaPontoPredial;
    @Transient
    private Long criadoEm;

    public Pontuacao() {
        this.criadoEm = System.nanoTime();
        this.utilizaPontoPredial = false;
        this.itens = Lists.newArrayList();
        this.atributos = Lists.newArrayList();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getUtilizaPontoPredial() {
        return utilizaPontoPredial;
    }

    public void setUtilizaPontoPredial(Boolean utilizaPontoPredial) {
        this.utilizaPontoPredial = utilizaPontoPredial;
    }

    public List<Atributo> getAtributos() {
        return atributos;
    }

    public void setAtributos(List<Atributo> atributos) {
        this.atributos = atributos;
    }

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public List<ItemPontuacao> getItens() {
        return itens;
    }

    public void setItens(List<ItemPontuacao> itens) {
        this.itens = itens;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    public void setIdentificacao(String identificacao) {
        this.identificacao = identificacao;
    }

    public TipoPontoIPTU getTipoPontoIPTU() {
        return tipoPontoIPTU;
    }

    public void setTipoPontoIPTU(TipoPontoIPTU tipoPontoIPTU) {
        this.tipoPontoIPTU = tipoPontoIPTU;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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
        String aRetornar = identificacao + "/" + exercicio;
        return aRetornar;
    }
}

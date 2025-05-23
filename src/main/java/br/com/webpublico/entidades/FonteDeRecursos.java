/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;


import br.com.webpublico.enums.ClassificacaoFonteRecursos;
import br.com.webpublico.enums.SituacaoFonteRecurso;
import br.com.webpublico.enums.TipoFonteRecurso;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.*;
import br.com.webpublico.webreportdto.dto.contabil.FonteDeRecursosDTO;
import com.google.common.collect.Lists;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;

@GrupoDiagrama(nome = "PPA")
@Audited
@Entity

@Etiqueta("Fonte de Recursos")
public class FonteDeRecursos implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Tabelavel
    @Etiqueta("Código")
    @Pesquisavel
    @NotNull
    @Obrigatorio
    @Size(min = 1, max = 4)
    private String codigo;
    @Tabelavel
    @Obrigatorio
    @Pesquisavel
    @Etiqueta("Descrição")
    @NotNull
    private String descricao;
    @Invisivel
    @ManyToOne
    private Exercicio exercicio;
    @Pesquisavel
    @Etiqueta(value = "Código TCE")
    @Tabelavel(campoSelecionado = false)
    private String codigoTCE;
    @Pesquisavel
    @Etiqueta(value = "Código SICONFI")
    @Tabelavel(campoSelecionado = false)
    private String codigoSiconfi;
    @Obrigatorio
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Etiqueta(value = "Classificação")
    @Tabelavel(campoSelecionado = false)
    private ClassificacaoFonteRecursos classificacaoFonteRecursos;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Pesquisavel
    @Etiqueta(value = "Tipo de Fonte de Recursos")
    @Tabelavel(campoSelecionado = false)
    private TipoFonteRecurso tipoFonteRecurso;
    @Enumerated(EnumType.STRING)
    @Obrigatorio
    @Tabelavel
    @Pesquisavel
    @Etiqueta(value = "Situação Cadastral")
    private SituacaoFonteRecurso situacaoCadastral;
    @Transient
    private Long criadoEm;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fonteDeRecursosDestino", orphanRemoval = true)
    private List<FonteDeRecursosEquivalente> fontesEquivalentes;
    private Boolean permitirSuprimentoDeFundo;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "fonteDeRecursos", orphanRemoval = true)
    private List<FonteCodigoCO> codigosCOs;

    public FonteDeRecursos() {
        classificacaoFonteRecursos = ClassificacaoFonteRecursos.ORDINARIA;
        situacaoCadastral = SituacaoFonteRecurso.ATIVO;
        criadoEm = System.nanoTime();
        fontesEquivalentes = Lists.newArrayList();
        codigosCOs = Lists.newArrayList();
        permitirSuprimentoDeFundo = Boolean.FALSE;
    }

    public FonteDeRecursos(String codigo, String descricao, Exercicio exercicio, String codigoTCE, ClassificacaoFonteRecursos classificacaoFonteRecursos, TipoFonteRecurso tipoFonteRecurso, SituacaoFonteRecurso situacaoCadastral) {
        this.codigo = codigo;
        this.descricao = descricao;
        this.exercicio = exercicio;
        this.codigoTCE = codigoTCE;
        this.classificacaoFonteRecursos = classificacaoFonteRecursos;
        this.tipoFonteRecurso = tipoFonteRecurso;
        this.situacaoCadastral = situacaoCadastral;
        criadoEm = System.nanoTime();
        fontesEquivalentes = Lists.newArrayList();
    }

    public static List<FonteDeRecursosDTO> fontesToDto(List<FonteDeRecursos> fontes) {
        List<FonteDeRecursosDTO> toReturn = Lists.newArrayList();
        for (FonteDeRecursos fonte : fontes) {
            toReturn.add(fonteToDto(fonte));
        }
        return toReturn;
    }

    public static FonteDeRecursosDTO fonteToDto(FonteDeRecursos fonte) {
        return new FonteDeRecursosDTO(fonte.getId(), fonte.getCodigo(), fonte.getDescricao());
    }

    public SituacaoFonteRecurso getSituacaoCadastral() {
        return situacaoCadastral;
    }

    public void setSituacaoCadastral(SituacaoFonteRecurso situacaoCadastral) {
        this.situacaoCadastral = situacaoCadastral;
    }

    public ClassificacaoFonteRecursos getClassificacaoFonteRecursos() {
        return classificacaoFonteRecursos;
    }

    public void setClassificacaoFonteRecursos(ClassificacaoFonteRecursos classificacaoFonteRecursos) {
        this.classificacaoFonteRecursos = classificacaoFonteRecursos;
    }

    public String getCodigoTCE() {
        return codigoTCE;
    }

    public void setCodigoTCE(String codigoTCE) {
        this.codigoTCE = codigoTCE;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
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

    public Exercicio getExercicio() {
        return exercicio;
    }

    public void setExercicio(Exercicio exercicio) {
        this.exercicio = exercicio;
    }

    public TipoFonteRecurso getTipoFonteRecurso() {
        return tipoFonteRecurso;
    }

    public void setTipoFonteRecurso(TipoFonteRecurso tipoFonteRecurso) {
        this.tipoFonteRecurso = tipoFonteRecurso;
    }

    public String getCodigoSiconfi() {
        return codigoSiconfi;
    }

    public void setCodigoSiconfi(String codigoSiconfi) {
        this.codigoSiconfi = codigoSiconfi;
    }

    public List<FonteDeRecursosEquivalente> getFontesEquivalentes() {
        return fontesEquivalentes;
    }

    public void setFontesEquivalentes(List<FonteDeRecursosEquivalente> fontesEquivalentes) {
        this.fontesEquivalentes = fontesEquivalentes;
    }

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        if (codigo != null) {
            return codigo + " - " + descricao.trim();
        } else {
            return descricao;
        }
    }

    public String toStringAutoComplete() {
        String retorno = codigo != null ? codigo + " - " + descricao.trim() : descricao;
        return retorno.length() > 68 ? retorno.substring(0, 65) + "..." : retorno;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public Boolean getPermitirSuprimentoDeFundo() {
        return permitirSuprimentoDeFundo != null ? permitirSuprimentoDeFundo : Boolean.FALSE;
    }

    public void setPermitirSuprimentoDeFundo(Boolean permitirSuprimentoDeFundo) {
        this.permitirSuprimentoDeFundo = permitirSuprimentoDeFundo;
    }

    public List<FonteCodigoCO> getCodigosCOs() {
        return codigosCOs;
    }

    public void setCodigosCOs(List<FonteCodigoCO> codigosCOs) {
        this.codigosCOs = codigosCOs;
    }
}

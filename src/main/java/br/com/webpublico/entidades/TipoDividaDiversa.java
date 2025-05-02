/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.SituacaoTipoDividaDiversa;
import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wellington
 */
@GrupoDiagrama(nome = "DividaDiversa")
@Entity
@Etiqueta("Tipo de Dívidas Diversas")

@Audited
public class TipoDividaDiversa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Invisivel
    private Long id;
    @Tabelavel
    @Etiqueta(value = "Código")
    @Pesquisavel
    private Integer codigo;
    @Tabelavel
    @Etiqueta(value = "Descrição")
    @Pesquisavel
    private String descricao;
    @Tabelavel
    @Etiqueta(value = "Descrição Curta")
    @Pesquisavel
    private String descricaoCurta;
    @Invisivel
    private String observacao;
    @Invisivel
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "tipoDividaDiversa")
    private List<TipoDivDiversaTributoTaxa> tributosTaxas;
    @Invisivel
    @Transient
    private Long criadoEm;
    @Tabelavel
    @Etiqueta(value = "Situação")
    @Pesquisavel
    @Enumerated(EnumType.STRING)
    private SituacaoTipoDividaDiversa situacao;
    @ManyToOne
    private Divida dividaCadastroImobiliario;
    @ManyToOne
    private Divida dividaCadastroEconomico;
    @ManyToOne
    private Divida dividaCadastroRural;
    @ManyToOne
    private Divida dividaContribuinteGeral;

    public TipoDividaDiversa() {
        criadoEm = System.nanoTime();
        tributosTaxas = new ArrayList<TipoDivDiversaTributoTaxa>();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao.toUpperCase();
    }

    public String getDescricaoCurta() {
        return descricaoCurta;
    }

    public void setDescricaoCurta(String descricaoCurta) {
        this.descricaoCurta = descricaoCurta.toUpperCase();
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public List<TipoDivDiversaTributoTaxa> getTributosTaxas() {
        return tributosTaxas;
    }

    public void setTributosTaxas(List<TipoDivDiversaTributoTaxa> tributosTaxas) {
        this.tributosTaxas = tributosTaxas;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public SituacaoTipoDividaDiversa getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoTipoDividaDiversa situacao) {
        this.situacao = situacao;
    }

    public Divida getDividaCadastroImobiliario() {
        return dividaCadastroImobiliario;
    }

    public void setDividaCadastroImobiliario(Divida dividaCadastroImobiliario) {
        this.dividaCadastroImobiliario = dividaCadastroImobiliario;
    }

    public Divida getDividaCadastroEconomico() {
        return dividaCadastroEconomico;
    }

    public void setDividaCadastroEconomico(Divida dividaCadastroEconomico) {
        this.dividaCadastroEconomico = dividaCadastroEconomico;
    }

    public Divida getDividaCadastroRural() {
        return dividaCadastroRural;
    }

    public void setDividaCadastroRural(Divida dividaCadastroRural) {
        this.dividaCadastroRural = dividaCadastroRural;
    }

    public Divida getDividaContribuinteGeral() {
        return dividaContribuinteGeral;
    }

    public void setDividaContribuinteGeral(Divida dividaContribuinteGeral) {
        this.dividaContribuinteGeral = dividaContribuinteGeral;
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.CategoriaConta;
import br.com.webpublico.enums.NaturezaDividaPublica;
import br.com.webpublico.enums.SituacaoCadastralCadastroEconomico;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import com.google.common.collect.Lists;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author major
 */
@Audited
@Entity
@Etiqueta("Natureza da Dívida Pública")

public class CategoriaDividaPublica implements Serializable, Comparable<Object> {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Etiqueta("Código")
    @Tabelavel
    @Pesquisavel
    private String codigo;
    @Obrigatorio
    @Etiqueta("Descrição")
    @Tabelavel
    @Pesquisavel
    private String descricao;
    @Etiqueta("Superior")
    @Tabelavel
    @ManyToOne
    private CategoriaDividaPublica superior;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "superior", fetch = FetchType.LAZY, orphanRemoval = true)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<CategoriaDividaPublica> filhos;
    @Obrigatorio
    @Etiqueta("Tipo de Natureza")
    @Enumerated(EnumType.STRING)
    @Pesquisavel
    @Tabelavel
    private NaturezaDividaPublica naturezaDividaPublica;
    @Transient
    private Long criadoEm;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Situação")
    @Enumerated(EnumType.STRING)
    private SituacaoCadastralCadastroEconomico ativoInativo;

    public CategoriaDividaPublica() {
        criadoEm = System.nanoTime();
        setFilhos(Lists.newArrayList());
        codigo = "";
        ativoInativo = SituacaoCadastralCadastroEconomico.ATIVA_REGULAR;
    }

    public CategoriaConta getCategoria() {
        if (getFilhos() == null) {
            return CategoriaConta.ANALITICA;
        }
        return CategoriaConta.SINTETICA;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public List<CategoriaDividaPublica> getFilhos() {
        return filhos;
    }

    public void setFilhos(List<CategoriaDividaPublica> filhos) {
        this.filhos = filhos;
    }

    public CategoriaDividaPublica getSuperior() {
        return superior;
    }

    public void setSuperior(CategoriaDividaPublica superior) {
        this.superior = superior;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public NaturezaDividaPublica getNaturezaDividaPublica() {
        return naturezaDividaPublica;
    }

    public void setNaturezaDividaPublica(NaturezaDividaPublica naturezaDividaPublica) {
        this.naturezaDividaPublica = naturezaDividaPublica;
    }

    public void setAtivoInativo(SituacaoCadastralCadastroEconomico ativoInativo) {
        this.ativoInativo = ativoInativo;
    }

    public SituacaoCadastralCadastroEconomico getAtivoInativo() {
        return ativoInativo;
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
    public int compareTo(Object o) {

        return this.codigo.compareTo(((CategoriaDividaPublica) o).codigo);
    }

    @Override
    public String toString() {
        String retorno = "";
        if (codigo != null && descricao != null) {
            return retorno + codigo + " - " + descricao;
        }
        if (!"".equals(retorno)) {
            return retorno;
        }
        return "";

    }
}

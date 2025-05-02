/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoOperacaoAtuarial;
import br.com.webpublico.enums.TipoPlano;
import br.com.webpublico.enums.TipoProvisao;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Pesquisavel;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.annotations.Cascade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author major
 */
@Entity
@Audited
@Etiqueta("Tipo Passivo Atuarial")

public class TipoPassivoAtuarial implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Código")
    private String codigo;
    @Obrigatorio
    @Pesquisavel
    @Tabelavel
    @Etiqueta("Descrição")
    private String descricao;
    @Transient
    private Long criadoEm;
    @ElementCollection(targetClass = TipoOperacaoAtuarial.class)
    @CollectionTable(name = "TIPOPASSIVO_TIPOOPERACAO", joinColumns =
    @JoinColumn(name = "TIPOPASSIVOATUARIAL_ID"))
    @Column(name = "TIPOOPERACAOATUARIAL", nullable = false)
    @Enumerated(EnumType.STRING)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<TipoOperacaoAtuarial> tipoOperacaoAtuarial = new ArrayList<TipoOperacaoAtuarial>();
    @ElementCollection(targetClass = TipoPlano.class)
    @CollectionTable(name = "TIPOPASSIVO_TIPOPLANO", joinColumns =
    @JoinColumn(name = "TIPOPASSIVOATUARIAL_ID"))
    @Column(name = "TIPOPLANO", nullable = false)
    @Enumerated(EnumType.STRING)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<TipoPlano> tipoPlano = new ArrayList<TipoPlano>();
    @ElementCollection(targetClass = TipoProvisao.class)
    @CollectionTable(name = "TIPOPASSIVO_TIPOPROVISAO", joinColumns =
    @JoinColumn(name = "TIPOPASSIVOATUARIAL_ID"))
    @Column(name = "TIPOPROVISAO", nullable = false)
    @Enumerated(EnumType.STRING)
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    private List<TipoProvisao> tipoProvisao = new ArrayList<TipoProvisao>();

    public TipoPassivoAtuarial() {
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

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public List<TipoOperacaoAtuarial> getTipoOperacaoAtuarial() {
        return tipoOperacaoAtuarial;
    }

    public void setTipoOperacaoAtuarial(List<TipoOperacaoAtuarial> tipoOperacaoAtuarial) {
        this.tipoOperacaoAtuarial = tipoOperacaoAtuarial;
    }

    public List<TipoPlano> getTipoPlano() {
        return tipoPlano;
    }

    public void setTipoPlano(List<TipoPlano> tipoPlano) {
        this.tipoPlano = tipoPlano;
    }

    public List<TipoProvisao> getTipoProvisao() {
        return tipoProvisao;
    }

    public void setTipoProvisao(List<TipoProvisao> tipoProvisao) {
        this.tipoProvisao = tipoProvisao;
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
        return codigo + " - " + descricao;
    }
}

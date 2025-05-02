/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Invisivel;
import br.com.webpublico.util.anotacoes.Obrigatorio;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * @author renato
 */
@Entity

@Audited
@GrupoDiagrama(nome = "Licitacao")
public class VersaoDoctoLicitacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Versão")
    private Integer versao;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Gerado em")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date geradoEm;
    @Tabelavel
    @Etiqueta("Modelo Documento")
    @ManyToOne(cascade= CascadeType.ALL)
    private ModeloDocto modeloDocto;
    @Obrigatorio
    @Tabelavel
    @Etiqueta("Usuário do Sistema")
    @ManyToOne
    private UsuarioSistema usuarioSistema;
    @ManyToOne
    private DoctoLicitacao doctoLicitacao;
    @Transient
    @Invisivel
    private Long criadoEm;

    public VersaoDoctoLicitacao() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getGeradoEm() {
        return geradoEm;
    }

    public void setGeradoEm(Date geradoEm) {
        this.geradoEm = geradoEm;
    }

    public ModeloDocto getModeloDocto() {
        return modeloDocto;
    }

    public void setModeloDocto(ModeloDocto modeloDocto) {
        this.modeloDocto = modeloDocto;
    }

    public Integer getVersao() {
        return versao;
    }

    public void setVersao(Integer versao) {
        this.versao = versao;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public DoctoLicitacao getDoctoLicitacao() {
        return doctoLicitacao;
    }

    public void setDoctoLicitacao(DoctoLicitacao doctoLicitacao) {
        this.doctoLicitacao = doctoLicitacao;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
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
        return "versao=" + versao;
    }
}

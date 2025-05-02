/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author claudio
 */
@Entity
@Audited
@GrupoDiagrama(nome = "Certidao")

public class ValorAtributoSolicitacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private SolicitacaoDoctoOficial solicitacaoDoctoOficial;
    @ManyToOne
    private AtributoDoctoOficial atributoDoctoOficial;
    private String valor;
    private Boolean obrigatorio;
    @Transient
    private Long criadoEm;

    public ValorAtributoSolicitacao() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AtributoDoctoOficial getAtributoDoctoOficial() {
        return atributoDoctoOficial;
    }

    public void setAtributoDoctoOficial(AtributoDoctoOficial atributoDoctoOficial) {
        this.atributoDoctoOficial = atributoDoctoOficial;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public SolicitacaoDoctoOficial getSolicitacaoDoctoOficial() {
        return solicitacaoDoctoOficial;
    }

    public void setSolicitacaoDoctoOficial(SolicitacaoDoctoOficial solicitacaoDoctoOficial) {
        this.solicitacaoDoctoOficial = solicitacaoDoctoOficial;
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
        return "br.com.webpublico.entidades.ValorAtributoSolicitacao[ id=" + id + " ]";
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public Boolean getObrigatorio() {
        return obrigatorio;
    }

    public void setObrigatorio(Boolean obrigatorio) {
        this.obrigatorio = obrigatorio;
    }
}

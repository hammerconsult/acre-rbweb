/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Renato
 */
@Entity
@Audited
@Etiqueta("Valor Atributo Fiscalização")
@GrupoDiagrama(nome = "fiscalizacaogeral")

public class ValorAtributoFiscalizacao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private AtributoDoctoOficial atributoDoctoOficial;
    @ManyToOne
    private TermoGeralFiscalizacao termoGeralFiscalizacao;
    private String valor;
    @Transient
    private Long criadoEm;

    public ValorAtributoFiscalizacao() {
        criadoEm = System.nanoTime();
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TermoGeralFiscalizacao getTermoGeralFiscalizacao() {
        return termoGeralFiscalizacao;
    }

    public void setTermoGeralFiscalizacao(TermoGeralFiscalizacao termoGeralFiscalizacao) {
        this.termoGeralFiscalizacao = termoGeralFiscalizacao;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author major
 */
@Entity

@Audited
@Table(name = "CONFIGRECREALIACONTAREC")
public class ConfigRecRealizadaContaRec implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Conta contaReceita;
    @ManyToOne
    private ConfigReceitaRealizada configReceitaRealizada;
    @Transient
    private Long criadoEm;

    public ConfigRecRealizadaContaRec() {
        criadoEm = System.nanoTime();
    }

    public ConfigRecRealizadaContaRec(Conta contaReceita, ConfigReceitaRealizada configReceitaRealizada, Long criadoEm) {
        this.contaReceita = contaReceita;
        this.configReceitaRealizada = configReceitaRealizada;
        this.criadoEm = criadoEm;
    }

    public ConfigReceitaRealizada getConfigReceitaRealizada() {
        return configReceitaRealizada;
    }

    public void setConfigReceitaRealizada(ConfigReceitaRealizada configReceitaRealizada) {
        this.configReceitaRealizada = configReceitaRealizada;
    }

    public Conta getContaReceita() {
        return contaReceita;
    }

    public void setContaReceita(Conta contaReceita) {
        this.contaReceita = contaReceita;
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
        if (contaReceita != null) {
            return contaReceita.getCodigo() + " - " + contaReceita.getDescricao();
        } else {
            return "";
        }
    }
}

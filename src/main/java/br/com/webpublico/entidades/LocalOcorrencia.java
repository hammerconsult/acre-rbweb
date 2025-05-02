package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Wellington
 * Date: 05/08/14
 * Time: 16:01
 * To change this template use File | Settings | File Templates.
 */

@Entity
@Audited
public class LocalOcorrencia implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private LogradouroBairro logradouroBairro;
    private String numero;
    private String pontoReferencia;
    @Transient
    private Long criadoEm;

    public LocalOcorrencia() {
        this.criadoEm = System.nanoTime();
        this.logradouroBairro = new LogradouroBairro();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LogradouroBairro getLogradouroBairro() {
        return logradouroBairro;
    }

    public void setLogradouroBairro(LogradouroBairro logradouroBairro) {
        this.logradouroBairro = logradouroBairro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getPontoReferencia() {
        return pontoReferencia;
    }

    public void setPontoReferencia(String pontoReferencia) {
        this.pontoReferencia = pontoReferencia;
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

}


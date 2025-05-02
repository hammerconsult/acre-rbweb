package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: André Gustavo
 * Date: 30/01/14
 * Time: 15:54
 */
@Entity
@Audited
public class LogradouroBairro implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Logradouro logradouro;
    @ManyToOne
    private Bairro bairro;
    private String cep;
    private Long numeroInicial;
    private Long numeroFinal;
    @Transient
    private Long criadoEm;

    public LogradouroBairro() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Logradouro getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(Logradouro logradouro) {
        this.logradouro = logradouro;
    }

    public Bairro getBairro() {
        return bairro;
    }

    public void setBairro(Bairro bairro) {
        this.bairro = bairro;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public Long getNumeroInicial() {
        return numeroInicial;
    }

    public void setNumeroInicial(Long numeroInicial) {
        this.numeroInicial = numeroInicial;
    }

    public Long getNumeroFinal() {
        return numeroFinal;
    }

    public void setNumeroFinal(Long numeroFinal) {
        this.numeroFinal = numeroFinal;
    }

    @Override
    public boolean equals(Object object) {
        return IdentidadeDaEntidade.calcularEquals(this, object);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    @Override
    public String toString() {
        if (logradouro != null && bairro != null) {
            if (logradouro.getTipoLogradouro() != null) {
                String log = logradouro.getTipoLogradouro().getDescricao() + " " + logradouro.getNome() + " - " + bairro.getDescricao() + " - " + cep;
                return log.replaceAll("null", " ");
            } else {
                String log = logradouro.getNome().trim() + " - " + bairro.getDescricao().trim() + " - " + cep.trim();
                return log.replaceAll("null", " ");
            }
        }
        return "";
    }
}

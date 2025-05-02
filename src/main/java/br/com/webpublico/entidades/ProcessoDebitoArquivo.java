package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Fabio
 */
@Entity
@Audited
public class ProcessoDebitoArquivo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ProcessoDebito processoDebito;
    @ManyToOne(cascade ={CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;
    @Transient
    private Long criadoEm;

    public ProcessoDebitoArquivo() {
        criadoEm = System.nanoTime();
    }

    public ProcessoDebito getProcessoDebito() {
        return processoDebito;
    }

    public void setProcessoDebito(ProcessoDebito processoDebito) {
        this.processoDebito = processoDebito;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "br.com.webpublico.entidades.ProcessoDebitoArquivo[ id=" + id + " ]";
    }
}

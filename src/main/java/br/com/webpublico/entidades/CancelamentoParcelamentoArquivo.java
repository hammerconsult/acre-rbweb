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
@Table(name = "CANCPARCELAMENTOARQUIVO")
public class CancelamentoParcelamentoArquivo implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CancelamentoParcelamento cancelamentoParcelamento;
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    private Arquivo arquivo;
    @Transient
    private Long criadoEm;

    public CancelamentoParcelamentoArquivo() {
        criadoEm = System.nanoTime();
    }

    public CancelamentoParcelamento getCancelamentoParcelamento() {
        return cancelamentoParcelamento;
    }

    public void setCancelamentoParcelamento(CancelamentoParcelamento cancelamentoParcelamento) {
        this.cancelamentoParcelamento = cancelamentoParcelamento;
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
        return "br.com.webpublico.entidades.CancelamentoParcelamentoArquivo[ id=" + id + " ]";
    }
}

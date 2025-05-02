package br.com.webpublico.entidades;

import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: William
 * Date: 02/09/13
 * Time: 09:03
 * To change this template use File | Settings | File Templates.
 */
@Entity
@Audited
public class CertidaoBCEArquivos implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CertidaoAtividadeBCE certidaoAtividadeBce;
    @OneToOne
    private Arquivo arquivo;
    @Transient
    private Object file;
    @Transient
    private Long criadoEm;

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public CertidaoBCEArquivos() {
        criadoEm = System.nanoTime();
    }

    public Object getFile() {
        return file;
    }

    public void setFile(Object file) {
        this.file = file;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    public CertidaoAtividadeBCE getCertidaoAtividadeBce() {
        return certidaoAtividadeBce;
    }

    public void setCertidaoAtividadeBce(CertidaoAtividadeBCE certidaoAtividadeBce) {
        this.certidaoAtividadeBce = certidaoAtividadeBce;
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
}

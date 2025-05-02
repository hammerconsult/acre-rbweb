package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: mga
 * Date: 12/09/14
 * Time: 10:47
 */
@Entity

@Audited
@Etiqueta("Invalidez CID")
@GrupoDiagrama(nome = "RecursosHumanos")
public class InvalidezCid implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private CID cid;
    @ManyToOne
    private InvalidezAposentado invalidezAposentado;
    @Transient
    private Long criadoEm = System.nanoTime();

    @Override
    public boolean equals(Object obj) {
        return IdentidadeDaEntidade.calcularEquals(this, obj);
    }

    public InvalidezCid() {
    }

    public InvalidezCid(CID cid) {
        this.cid = cid;
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CID getCid() {
        return cid;
    }

    public void setCid(CID cid) {
        this.cid = cid;
    }

    public InvalidezAposentado getInvalidezAposentado() {
        return invalidezAposentado;
    }

    public void setInvalidezAposentado(InvalidezAposentado invalidezAposentado) {
        this.invalidezAposentado = invalidezAposentado;
    }

    @Override
    public String toString() {
        return cid.toString();
    }
}

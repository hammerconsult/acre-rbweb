package br.com.webpublico.entidades;

import br.com.webpublico.geradores.GrupoDiagrama;
import br.com.webpublico.util.IdentidadeDaEntidade;
import br.com.webpublico.util.anotacoes.Etiqueta;
import br.com.webpublico.util.anotacoes.Tabelavel;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by zaca on 06/01/17.
 */
@GrupoDiagrama(nome = "RBTrans")
@Entity
@Audited
@Etiqueta("Chancela RBTrans")
public class ChancelaRBTrans extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @Tabelavel
    private Arquivo arquivo;

    public ChancelaRBTrans(Arquivo arquivo) {
        this.arquivo = new Arquivo();
        this.arquivo = arquivo;
    }

    public ChancelaRBTrans() {
        this.arquivo = new Arquivo();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Arquivo getArquivo() {
        return arquivo;
    }

    public void setArquivo(Arquivo arquivo) {
        this.arquivo = arquivo;
    }

    @Override
    public String toString() {
        return super.toString();
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

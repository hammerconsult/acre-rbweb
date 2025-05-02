package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class FonteDeRecursosEquivalente extends SuperEntidade implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursosOrigem;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursosDestino;

    public FonteDeRecursosEquivalente() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public FonteDeRecursos getFonteDeRecursosOrigem() {
        return fonteDeRecursosOrigem;
    }

    public void setFonteDeRecursosOrigem(FonteDeRecursos fonteDeRecursosOrigem) {
        this.fonteDeRecursosOrigem = fonteDeRecursosOrigem;
    }

    public FonteDeRecursos getFonteDeRecursosDestino() {
        return fonteDeRecursosDestino;
    }

    public void setFonteDeRecursosDestino(FonteDeRecursos fonteDeRecursosDestino) {
        this.fonteDeRecursosDestino = fonteDeRecursosDestino;
    }
}

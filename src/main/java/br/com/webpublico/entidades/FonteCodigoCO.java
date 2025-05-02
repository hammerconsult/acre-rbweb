package br.com.webpublico.entidades;

import br.com.webpublico.util.anotacoes.Obrigatorio;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class FonteCodigoCO extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private FonteDeRecursos fonteDeRecursos;
    @ManyToOne
    private CodigoCO codigoCO;

    @Obrigatorio
    public FonteCodigoCO() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CodigoCO getCodigoCO() {
        return codigoCO;
    }

    public void setCodigoCO(CodigoCO codigoCO) {
        this.codigoCO = codigoCO;
    }

    public FonteDeRecursos getFonteDeRecursos() {
        return fonteDeRecursos;
    }

    public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
        this.fonteDeRecursos = fonteDeRecursos;
    }
}

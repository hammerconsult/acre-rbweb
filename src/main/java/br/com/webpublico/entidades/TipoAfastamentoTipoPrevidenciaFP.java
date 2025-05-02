
package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity

@Audited
public class TipoAfastamentoTipoPrevidenciaFP extends SuperEntidade implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private TipoAfastamento tipoAfastamento;

    @ManyToOne
    private TipoPrevidenciaFP tipoPrevidenciaFP;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public TipoAfastamento getTipoAfastamento() {
        return tipoAfastamento;
    }

    public void setTipoAfastamento(TipoAfastamento tipoAfastamento) {
        this.tipoAfastamento = tipoAfastamento;
    }

    public TipoPrevidenciaFP getTipoPrevidenciaFP() {
        return tipoPrevidenciaFP;
    }

    public void setTipoPrevidenciaFP(TipoPrevidenciaFP tipoPrevidenciaFP) {
        this.tipoPrevidenciaFP = tipoPrevidenciaFP;
    }
}

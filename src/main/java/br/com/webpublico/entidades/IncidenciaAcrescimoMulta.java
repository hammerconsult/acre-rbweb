package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoIncidenciaAcrescimo;
import br.com.webpublico.util.IdentidadeDaEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class IncidenciaAcrescimoMulta implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Enumerated(EnumType.STRING)
    private TipoIncidenciaAcrescimo incidencia;
    @Transient
    private Long criadoEm;
    @ManyToOne
    private MultaConfiguracaoAcrescimo multa;

    public IncidenciaAcrescimoMulta() {
        criadoEm = System.nanoTime();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoIncidenciaAcrescimo getIncidencia() {
        return incidencia;
    }

    public void setIncidencia(TipoIncidenciaAcrescimo incidencia) {
        this.incidencia = incidencia;
    }

    public Long getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Long criadoEm) {
        this.criadoEm = criadoEm;
    }

    public MultaConfiguracaoAcrescimo getMulta() {
        return multa;
    }

    public void setMulta(MultaConfiguracaoAcrescimo multa) {
        this.multa = multa;
    }

    @Override
    public boolean equals(Object o) {
        return IdentidadeDaEntidade.calcularEquals(this, o);
    }

    @Override
    public int hashCode() {
        return IdentidadeDaEntidade.calcularHashCode(this);
    }

    public enum TipoAcrescimo {
        JUROS, MULTA, CORRECAO;
    }
}

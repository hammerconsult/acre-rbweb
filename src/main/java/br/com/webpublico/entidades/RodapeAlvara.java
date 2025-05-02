package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Audited
public class RodapeAlvara extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Boolean sanitario;
    private Boolean infraestrutura;
    private Boolean ambiental;
    private Boolean sanitarioEstadual;
    private String textoRodape;

    public RodapeAlvara() {
        this.sanitario = Boolean.FALSE;
        this.infraestrutura = Boolean.FALSE;
        this.ambiental = Boolean.FALSE;
        this.sanitarioEstadual = Boolean.FALSE;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Boolean getSanitario() {
        return sanitario != null ? sanitario : Boolean.FALSE;
    }

    public void setSanitario(Boolean sanitario) {
        this.sanitario = sanitario;
    }

    public Boolean getInfraestrutura() {
        return infraestrutura != null ? infraestrutura : Boolean.FALSE;
    }

    public void setInfraestrutura(Boolean infraestrutura) {
        this.infraestrutura = infraestrutura;
    }

    public Boolean getAmbiental() {
        return ambiental != null ? ambiental : Boolean.FALSE;
    }

    public void setAmbiental(Boolean ambiental) {
        this.ambiental = ambiental;
    }

    public Boolean getSanitarioEstadual() {
        return sanitarioEstadual != null ? sanitarioEstadual : Boolean.FALSE;
    }

    public void setSanitarioEstadual(Boolean sanitarioEstadual) {
        this.sanitarioEstadual = sanitarioEstadual;
    }

    public String getTextoRodape() {
        return textoRodape;
    }

    public void setTextoRodape(String textoRodape) {
        this.textoRodape = textoRodape;
    }
}

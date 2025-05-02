package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ParamSimplesNacionalDivida extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParametroSimplesNacional parametroSimplesNacional;
    @ManyToOne
    private Divida divida;

    public ParamSimplesNacionalDivida() {
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ParametroSimplesNacional getParametroSimplesNacional() {
        return parametroSimplesNacional;
    }

    public void setParametroSimplesNacional(ParametroSimplesNacional parametroSimplesNacional) {
        this.parametroSimplesNacional = parametroSimplesNacional;
    }

    public Divida getDivida() {
        return divida;
    }

    public void setDivida(Divida divida) {
        this.divida = divida;
    }
}

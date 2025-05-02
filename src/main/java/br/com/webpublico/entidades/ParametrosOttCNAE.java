package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ParametrosOttCNAE extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ParametrosOtt parametrosOtt;
    @ManyToOne
    private CNAE cnae;

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    public ParametrosOtt getParametrosOtt() {
        return parametrosOtt;
    }

    public void setParametrosOtt(ParametrosOtt parametrosOtt) {
        this.parametrosOtt = parametrosOtt;
    }

    public CNAE getCnae() {
        return cnae;
    }

    public void setCnae(CNAE cnae) {
        this.cnae = cnae;
    }
}

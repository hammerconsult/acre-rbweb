package br.com.webpublico.entidades.rh.saudeservidor;

import br.com.webpublico.entidades.ASO;
import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class RiscoOcupacionalASO extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private RiscoOcupacional riscoOcupacional;
    @ManyToOne
    private ASO aso;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public RiscoOcupacional getRiscoOcupacional() {
        return riscoOcupacional;
    }

    public void setRiscoOcupacional(RiscoOcupacional riscoOcupacional) {
        this.riscoOcupacional = riscoOcupacional;
    }

    public ASO getAso() {
        return aso;
    }

    public void setAso(ASO aso) {
        this.aso = aso;
    }
}

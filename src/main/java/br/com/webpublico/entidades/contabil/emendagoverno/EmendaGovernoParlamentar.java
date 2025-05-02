package br.com.webpublico.entidades.contabil.emendagoverno;

import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
public class EmendaGovernoParlamentar extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EmendaGoverno emendaGoverno;
    @ManyToOne
    private Parlamentar parlamentar;

    public EmendaGovernoParlamentar() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmendaGoverno getEmendaGoverno() {
        return emendaGoverno;
    }

    public void setEmendaGoverno(EmendaGoverno emendaGoverno) {
        this.emendaGoverno = emendaGoverno;
    }

    public Parlamentar getParlamentar() {
        return parlamentar;
    }

    public void setParlamentar(Parlamentar parlamentar) {
        this.parlamentar = parlamentar;
    }

    @Override
    public String toString() {
        return parlamentar.getNome();
    }
}

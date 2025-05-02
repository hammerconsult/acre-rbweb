package br.com.webpublico.entidades.rh;

import br.com.webpublico.entidades.AtoLegal;
import br.com.webpublico.entidades.Pensao;
import br.com.webpublico.entidades.SuperEntidade;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * Created by William on 21/08/2018.
 */
@Entity
@Audited
public class PensaoAtoLegal extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private Pensao pensao;
    @ManyToOne
    private AtoLegal atoLegal;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Pensao getPensao() {
        return pensao;
    }

    public void setPensao(Pensao pensao) {
        this.pensao = pensao;
    }

    public AtoLegal getAtoLegal() {
        return atoLegal;
    }

    public void setAtoLegal(AtoLegal atoLegal) {
        this.atoLegal = atoLegal;
    }
}

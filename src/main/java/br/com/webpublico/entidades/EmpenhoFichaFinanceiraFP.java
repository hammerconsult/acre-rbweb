package br.com.webpublico.entidades;

import org.hibernate.envers.Audited;

import javax.persistence.*;

@Audited
@Entity
public class EmpenhoFichaFinanceiraFP extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private EmpenhoVinculoFP empenhoVinculoFP;
    @ManyToOne
    private FichaFinanceiraFP fichaFinanceiraFP;

    public EmpenhoFichaFinanceiraFP() {
        super();
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmpenhoVinculoFP getEmpenhoVinculoFP() {
        return empenhoVinculoFP;
    }

    public void setEmpenhoVinculoFP(EmpenhoVinculoFP empenhoVinculoFP) {
        this.empenhoVinculoFP = empenhoVinculoFP;
    }

    public FichaFinanceiraFP getFichaFinanceiraFP() {
        return fichaFinanceiraFP;
    }

    public void setFichaFinanceiraFP(FichaFinanceiraFP fichaFinanceiraFP) {
        this.fichaFinanceiraFP = fichaFinanceiraFP;
    }
}

package br.com.webpublico.entidades.rh.cadastrofuncional;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.VinculoFP;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Audited
@Entity
@Table(name = "REPROSITUACAOFUNCIONALVINC")
public class ReprocessamentoSituacaoFuncionalVinculo extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    private ReprocessamentoSituacaoFuncional reprocessamento;
    @ManyToOne
    private VinculoFP vinculoFP;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReprocessamentoSituacaoFuncional getReprocessamento() {
        return reprocessamento;
    }

    public void setReprocessamento(ReprocessamentoSituacaoFuncional reprocessamento) {
        this.reprocessamento = reprocessamento;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }
}

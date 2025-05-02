package br.com.webpublico.entidades.rh.exoneracao;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.entidades.VinculoFP;
import org.hibernate.envers.Audited;

import javax.persistence.*;

@Entity
@Audited
public class ExoneracaoRescisaoContrato extends SuperEntidade implements Comparable<ExoneracaoRescisaoContrato> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VinculoFP vinculoFP;
    @ManyToOne
    private ExoneracaoRescisaoLote exoneracaoRescisaoLote;

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public ExoneracaoRescisaoLote getExoneracaoRescisaoLote() {
        return exoneracaoRescisaoLote;
    }

    public void setExoneracaoRescisaoLote(ExoneracaoRescisaoLote exoneracaoRescisaoLote) {
        this.exoneracaoRescisaoLote = exoneracaoRescisaoLote;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }


    @Override
    public int compareTo(ExoneracaoRescisaoContrato o) {
        return this.vinculoFP.getMatriculaFP().getPessoa().getNome().compareTo(o.getVinculoFP().getMatriculaFP().getPessoa().getNome());
    }
}

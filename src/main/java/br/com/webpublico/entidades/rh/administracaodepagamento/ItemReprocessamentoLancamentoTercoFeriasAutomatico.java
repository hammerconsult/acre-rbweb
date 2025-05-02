package br.com.webpublico.entidades.rh.administracaodepagamento;

import br.com.webpublico.entidades.SuperEntidade;
import br.com.webpublico.geradores.GrupoDiagrama;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
@Audited
@Table(name = "ITEMREPROCLANCTERCFERAUT")
public class ItemReprocessamentoLancamentoTercoFeriasAutomatico extends SuperEntidade implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private ReprocessamentoLancamentoTercoFeriasAutomatico reprocLancTerCoferiasAut;
    @ManyToOne(cascade = CascadeType.ALL)
    private LancamentoTercoFeriasAut lancamentoTercoFeriasAut;


    public ItemReprocessamentoLancamentoTercoFeriasAutomatico() {
    }

    public ItemReprocessamentoLancamentoTercoFeriasAutomatico(ReprocessamentoLancamentoTercoFeriasAutomatico reprocLancTerCoferiasAut, LancamentoTercoFeriasAut lancamentoTercoFeriasAut) {
        this.reprocLancTerCoferiasAut = reprocLancTerCoferiasAut;
        this.lancamentoTercoFeriasAut = lancamentoTercoFeriasAut;
    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ReprocessamentoLancamentoTercoFeriasAutomatico getReprocLancTerCoferiasAut() {
        return reprocLancTerCoferiasAut;
    }

    public void setReprocLancTerCoferiasAut(ReprocessamentoLancamentoTercoFeriasAutomatico reprocLancTerCoferiasAut) {
        this.reprocLancTerCoferiasAut = reprocLancTerCoferiasAut;
    }

    public LancamentoTercoFeriasAut getLancamentoTercoFeriasAut() {
        return lancamentoTercoFeriasAut;
    }

    public void setLancamentoTercoFeriasAut(LancamentoTercoFeriasAut lancamentoTercoFeriasAut) {
        this.lancamentoTercoFeriasAut = lancamentoTercoFeriasAut;
    }
}

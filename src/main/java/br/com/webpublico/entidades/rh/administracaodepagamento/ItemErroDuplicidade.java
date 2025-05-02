package br.com.webpublico.entidades.rh.administracaodepagamento;

import br.com.webpublico.entidades.FolhaDePagamento;
import br.com.webpublico.entidades.VinculoFP;
import br.com.webpublico.enums.rh.TipoCalculo;
import br.com.webpublico.geradores.GrupoDiagrama;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@GrupoDiagrama(nome = "RecursosHumanos")
@Entity
public class ItemErroDuplicidade implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    private VinculoFP vinculoFP;
    @ManyToOne
    private FolhaDePagamento folhaDePagamento;
    @Temporal(TemporalType.TIMESTAMP)
    private Date criadoEm;
    @Enumerated(EnumType.STRING)
    private TipoCalculo tipoCalculo;
    private String observacao;

    public ItemErroDuplicidade() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public VinculoFP getVinculoFP() {
        return vinculoFP;
    }

    public void setVinculoFP(VinculoFP vinculoFP) {
        this.vinculoFP = vinculoFP;
    }

    public FolhaDePagamento getFolhaDePagamento() {
        return folhaDePagamento;
    }

    public void setFolhaDePagamento(FolhaDePagamento folhaDePagamento) {
        this.folhaDePagamento = folhaDePagamento;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Date getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Date criadoEm) {
        this.criadoEm = criadoEm;
    }

    public TipoCalculo getTipoCalculo() {
        return tipoCalculo;
    }

    public void setTipoCalculo(TipoCalculo tipoCalculo) {
        this.tipoCalculo = tipoCalculo;
    }
}

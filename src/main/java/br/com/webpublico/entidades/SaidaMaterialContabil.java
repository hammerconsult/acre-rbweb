package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoSaidaMaterial;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.*;

@Entity
@Etiqueta("Saída Material Contábil")
public class SaidaMaterialContabil extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoSaidaMaterial tipoSaidaMaterial;

    @ManyToOne
    @Etiqueta("Saída de Material")
    private SaidaMaterial saidaMaterial;

    @ManyToOne
    @Etiqueta("Bens de Estoque")
    private BensEstoque bensEstoque;

    @ManyToOne
    @Etiqueta("Transferência de Bens de Estoque")
    private TransferenciaBensEstoque transferenciaBensEstoque;

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public TipoSaidaMaterial getTipoSaidaMaterial() {
        return tipoSaidaMaterial;
    }

    public void setTipoSaidaMaterial(TipoSaidaMaterial tipoSaidaMaterial) {
        this.tipoSaidaMaterial = tipoSaidaMaterial;
    }

    public SaidaMaterial getSaidaMaterial() {
        return saidaMaterial;
    }

    public void setSaidaMaterial(SaidaMaterial saidaMaterial) {
        this.saidaMaterial = saidaMaterial;
    }

    public BensEstoque getBensEstoque() {
        return bensEstoque;
    }

    public void setBensEstoque(BensEstoque bensEstoque) {
        this.bensEstoque = bensEstoque;
    }

    public TransferenciaBensEstoque getTransferenciaBensEstoque() {
        return transferenciaBensEstoque;
    }

    public void setTransferenciaBensEstoque(TransferenciaBensEstoque transferenciaBensEstoque) {
        this.transferenciaBensEstoque = transferenciaBensEstoque;
    }

    @Override
    public String toString() {
        try {
            if (bensEstoque != null) {
                return "Nrº " + bensEstoque.getNumero() + " - " + DataUtil.getDataFormatada(bensEstoque.getDataBem()) + " - " + bensEstoque.getHistorico();
            }
            return "Nrº " + transferenciaBensEstoque.getNumero() + " - " + DataUtil.getDataFormatada(transferenciaBensEstoque.getDataTransferencia()) + " - " + transferenciaBensEstoque.getHistorico();
        } catch (NullPointerException e) {
            return "";
        }
    }
}

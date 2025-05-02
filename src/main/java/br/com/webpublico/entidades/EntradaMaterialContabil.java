package br.com.webpublico.entidades;

import br.com.webpublico.enums.TipoEntradaMaterial;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.anotacoes.Etiqueta;

import javax.persistence.*;

@Entity
@Etiqueta("Entrada Material Contábil")
public class EntradaMaterialContabil extends SuperEntidade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TipoEntradaMaterial tipoEntradaMaterial;

    @ManyToOne
    @Etiqueta("Entrada Material")
    private EntradaMaterial entradaMaterial;

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

    public TipoEntradaMaterial getTipoEntradaMaterial() {
        return tipoEntradaMaterial;
    }

    public void setTipoEntradaMaterial(TipoEntradaMaterial tipoEntradaMaterial) {
        this.tipoEntradaMaterial = tipoEntradaMaterial;
    }

    public EntradaMaterial getEntradaMaterial() {
        return entradaMaterial;
    }

    public void setEntradaMaterial(EntradaMaterial entradaMaterial) {
        this.entradaMaterial = entradaMaterial;
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

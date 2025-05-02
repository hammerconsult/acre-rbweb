package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.util.Objects;

/**
 * @author Renato
 */
public class DataTablePesquisaGenerico implements Serializable {

    private String valuePesquisa;
    private String valeuPesquisaDataFim;
    private ItemPesquisaGenerica itemPesquisaGenerica;
    private String labelBooleanFalse;
    private String labelBooleanTrue;
    private Boolean podeRemover;

    public ItemPesquisaGenerica getItemPesquisaGenerica() {
        return itemPesquisaGenerica;
    }

    public void setItemPesquisaGenerica(ItemPesquisaGenerica itemPesquisaGenerica) {
        this.itemPesquisaGenerica = itemPesquisaGenerica;
    }

    public String getValuePesquisa() {
        return valuePesquisa != null ? valuePesquisa.trim() : "";
    }

    public void setValuePesquisa(String valuePesquisa) {
        this.valuePesquisa = valuePesquisa;
    }

    public String getValeuPesquisaDataFim() {
        return valeuPesquisaDataFim;
    }

    public void setValeuPesquisaDataFim(String valeuPesquisaDataFim) {
        this.valeuPesquisaDataFim = valeuPesquisaDataFim;
    }

    public Boolean getPodeRemover() {
        return podeRemover == null ? Boolean.TRUE : podeRemover;
    }

    public void setPodeRemover(Boolean podeRemover) {
        this.podeRemover = podeRemover;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.valuePesquisa);
        hash = 67 * hash + Objects.hashCode(this.valeuPesquisaDataFim);
        hash = 67 * hash + Objects.hashCode(this.itemPesquisaGenerica);
        hash = 67 * hash + Objects.hashCode(this.labelBooleanFalse);
        hash = 67 * hash + Objects.hashCode(this.labelBooleanTrue);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final DataTablePesquisaGenerico other = (DataTablePesquisaGenerico) obj;
        if (!Objects.equals(this.valuePesquisa, other.valuePesquisa)) {
            return false;
        }
        if (!Objects.equals(this.valeuPesquisaDataFim, other.valeuPesquisaDataFim)) {
            return false;
        }
        if (!Objects.equals(this.itemPesquisaGenerica, other.itemPesquisaGenerica)) {
            return false;
        }
        if (!Objects.equals(this.labelBooleanFalse, other.labelBooleanFalse)) {
            return false;
        }
        if (!Objects.equals(this.labelBooleanTrue, other.labelBooleanTrue)) {
            return false;
        }
        return true;
    }
}

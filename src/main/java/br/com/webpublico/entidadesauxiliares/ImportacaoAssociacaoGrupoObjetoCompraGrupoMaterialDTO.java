package br.com.webpublico.entidadesauxiliares;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Wellington Abdo on 30/01/2017.
 */
public class ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialDTO implements Serializable {

    private String grupoObjetoCompra;
    private String grupoMaterial;
    private Date inicioVigencia;

    public String getGrupoObjetoCompra() {
        return grupoObjetoCompra;
    }

    public void setGrupoObjetoCompra(String grupoObjetoCompra) {
        this.grupoObjetoCompra = grupoObjetoCompra;
    }

    public String getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(String grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public Date getInicioVigencia() {
        return inicioVigencia;
    }

    public void setInicioVigencia(Date inicioVigencia) {
        this.inicioVigencia = inicioVigencia;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialDTO that = (ImportacaoAssociacaoGrupoObjetoCompraGrupoMaterialDTO) o;

        if (grupoObjetoCompra != null ? !grupoObjetoCompra.equals(that.grupoObjetoCompra) : that.grupoObjetoCompra != null)
            return false;
        return !(grupoMaterial != null ? !grupoMaterial.equals(that.grupoMaterial) : that.grupoMaterial != null);

    }

    @Override
    public int hashCode() {
        int result = grupoObjetoCompra != null ? grupoObjetoCompra.hashCode() : 0;
        result = 31 * result + (grupoMaterial != null ? grupoMaterial.hashCode() : 0);
        return result;
    }
}

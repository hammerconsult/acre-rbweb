package br.com.webpublico.entidadesauxiliares;

import java.util.Date;

/**
 * Created by mateus on 19/03/18.
 */
public class FiltrosLevantamentoBem {
    private Boolean consolidado;
    private Date dataReferencia;
    private String clausulas;
    private String orderBy;
    private String codigoEDescricaoHierarquiaAdministrativa;

    public FiltrosLevantamentoBem() {
    }

    public Boolean getConsolidado() {
        return consolidado;
    }

    public void setConsolidado(Boolean consolidado) {
        this.consolidado = consolidado;
    }

    public Date getDataReferencia() {
        return dataReferencia;
    }

    public void setDataReferencia(Date dataReferencia) {
        this.dataReferencia = dataReferencia;
    }

    public String getClausulas() {
        return clausulas;
    }

    public void setClausulas(String clausulas) {
        this.clausulas = clausulas;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getCodigoEDescricaoHierarquiaAdministrativa() {
        return codigoEDescricaoHierarquiaAdministrativa;
    }

    public void setCodigoEDescricaoHierarquiaAdministrativa(String codigoEDescricaoHierarquiaAdministrativa) {
        this.codigoEDescricaoHierarquiaAdministrativa = codigoEDescricaoHierarquiaAdministrativa;
    }
}

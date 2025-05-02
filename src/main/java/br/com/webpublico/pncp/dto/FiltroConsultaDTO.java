package br.com.webpublico.pncp.dto;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.util.DataUtil;

import java.util.Date;

public class FiltroConsultaDTO {

    private Date dataInicial;
    private Date dataFinal;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public Date getDataInicial() {
        return dataInicial;
    }

    public void setDataInicial(Date dataInicial) {
        this.dataInicial = dataInicial;
    }

    public Date getDataFinal() {
        return dataFinal;
    }

    public void setDataFinal(Date dataFinal) {
        this.dataFinal = dataFinal;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public String getCodigoUnidade() {
        if (hierarquiaOrganizacional != null) {
            return hierarquiaOrganizacional.getCodigo().substring(0, 5);
        }
        return " ";
    }

    public String getDataInicialAsString(){
        return DataUtil.getDataFormatada(dataInicial, "dd-MM-yyyy");
    }

    public String getDataFinalAsString(){
        return DataUtil.getDataFormatada(dataFinal, "dd-MM-yyyy");
    }
}

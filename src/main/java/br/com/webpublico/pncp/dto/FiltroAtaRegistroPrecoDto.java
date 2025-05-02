package br.com.webpublico.pncp.dto;

import br.com.webpublico.entidades.HierarquiaOrganizacional;

import java.util.Date;

public class FiltroAtaRegistroPrecoDto {
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
}

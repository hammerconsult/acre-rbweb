package br.com.webpublico.entidadesauxiliares.comum;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoHierarquiaOrganizacional;

import java.util.Date;

public class AlterarDataUnidadeLogada {

    private Date dataOperacao;
    private HierarquiaOrganizacional administrativa;
    private HierarquiaOrganizacional orcamentaria;
    private TipoHierarquiaOrganizacional tipo;

    public AlterarDataUnidadeLogada() {
    }

    public Date getDataOperacao() {
        return dataOperacao;
    }

    public void setDataOperacao(Date dataOperacao) {
        this.dataOperacao = dataOperacao;
    }

    public HierarquiaOrganizacional getAdministrativa() {
        return administrativa;
    }

    public void setAdministrativa(HierarquiaOrganizacional administrativa) {
        this.administrativa = administrativa;
    }

    public HierarquiaOrganizacional getOrcamentaria() {
        return orcamentaria;
    }

    public void setOrcamentaria(HierarquiaOrganizacional orcamentaria) {
        this.orcamentaria = orcamentaria;
    }

    public TipoHierarquiaOrganizacional getTipo() {
        return tipo;
    }

    public void setTipo(TipoHierarquiaOrganizacional tipo) {
        this.tipo = tipo;
    }

    public Boolean isAdministrativa() {
        return TipoHierarquiaOrganizacional.ADMINISTRATIVA.equals(getTipo());
    }
}

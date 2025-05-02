package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.HierarquiaOrganizacional;
import br.com.webpublico.enums.TipoOrgaoAtoLegal;

public class OrgaoAtoLegalVO {
    private TipoOrgaoAtoLegal tipo;
    private HierarquiaOrganizacional hierarquiaOrganizacional;

    public OrgaoAtoLegalVO() {
    }

    public OrgaoAtoLegalVO(TipoOrgaoAtoLegal tipo, HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.tipo = tipo;
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }

    public TipoOrgaoAtoLegal getTipo() {
        return tipo;
    }

    public void setTipo(TipoOrgaoAtoLegal tipo) {
        this.tipo = tipo;
    }

    public HierarquiaOrganizacional getHierarquiaOrganizacional() {
        return hierarquiaOrganizacional;
    }

    public void setHierarquiaOrganizacional(HierarquiaOrganizacional hierarquiaOrganizacional) {
        this.hierarquiaOrganizacional = hierarquiaOrganizacional;
    }
}

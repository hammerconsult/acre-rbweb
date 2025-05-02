package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.StatusMaterial;

public class FiltroEfetivacaoMaterial {

    private GrupoMaterial grupoMaterial;
    private StatusMaterial statusMaterial;

    private UsuarioSistema usuarioSistema;

    private UnidadeOrganizacional unidadeOrganizacional;

    private HierarquiaOrganizacional hierarquiaAdministrativa;

    private String parteMaterial;

    private String observacao;

    public FiltroEfetivacaoMaterial() {
        inicializarFiltros();
    }

   public void inicializarFiltros() {
       statusMaterial = StatusMaterial.DEFERIDO;
       grupoMaterial = null;
       usuarioSistema = null;
       unidadeOrganizacional = null;
   }

    public void limparFiltros() {
        inicializarFiltros();
    }

    public GrupoMaterial getGrupoMaterial() {
        return grupoMaterial;
    }

    public void setGrupoMaterial(GrupoMaterial grupoMaterial) {
        this.grupoMaterial = grupoMaterial;
    }

    public StatusMaterial getStatusMaterial() {
        return statusMaterial;
    }

    public void setStatusMaterial(StatusMaterial statusMaterial) {
        this.statusMaterial = statusMaterial;
    }

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public UnidadeOrganizacional getUnidadeOrganizacional() {
        return unidadeOrganizacional;
    }

    public void setUnidadeOrganizacional(UnidadeOrganizacional unidadeOrganizacional) {
        this.unidadeOrganizacional = unidadeOrganizacional;
    }

    public HierarquiaOrganizacional getHierarquiaAdministrativa() {
        return hierarquiaAdministrativa;
    }

    public void setHierarquiaAdministrativa(HierarquiaOrganizacional hierarquiaAdministrativa) {
        if (hierarquiaAdministrativa != null) {
            setUnidadeOrganizacional(hierarquiaAdministrativa.getSubordinada());
        }
        this.hierarquiaAdministrativa = hierarquiaAdministrativa;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public String getParteMaterial() {
        return parteMaterial;
    }

    public void setParteMaterial(String parteMaterial) {
        this.parteMaterial = parteMaterial;
    }
}

package br.com.webpublico.entidadesauxiliares;

import br.com.webpublico.entidades.Material;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidades.UsuarioSistema;
import br.com.webpublico.enums.StatusMaterial;

public class EfetivacaoMaterialVO {

    private UsuarioSistema usuarioSistema;
    private String observacao;
    private Material material;
    private String siglaAdministrativa;
    private UnidadeOrganizacional unidadeAdministrativa;
    private StatusMaterial situacao;

    public UsuarioSistema getUsuarioSistema() {
        return usuarioSistema;
    }

    public void setUsuarioSistema(UsuarioSistema usuarioSistema) {
        this.usuarioSistema = usuarioSistema;
    }

    public String getObservacao() {
        return observacao;
    }

    public void setObservacao(String observacao) {
        this.observacao = observacao;
    }

    public Material getMaterial() {
        return material;
    }

    public void setMaterial(Material material) {
        this.material = material;
    }

    public String getSiglaAdministrativa() {
        return siglaAdministrativa;
    }

    public void setSiglaAdministrativa(String siglaAdministrativa) {
        this.siglaAdministrativa = siglaAdministrativa;
    }

    public UnidadeOrganizacional getUnidadeAdministrativa() {
        return unidadeAdministrativa;
    }

    public void setUnidadeAdministrativa(UnidadeOrganizacional unidadeAdministrativa) {
        this.unidadeAdministrativa = unidadeAdministrativa;
    }

    public StatusMaterial getSituacao() {
        return situacao;
    }

    public void setSituacao(StatusMaterial situacao) {
        this.situacao = situacao;
    }

    public boolean hasSituacao() {
        return situacao != null;
    }
}

package br.com.webpublico.enums;

import br.com.webpublico.entidades.Perfil;

public enum PerfilEnum {

    PERFIL_RH("RH", "br.com.webpublico.perfil.PerfilRH"),
    PERFIL_TRIBUTARIO("Tributário", "br.com.webpublico.perfil.PerfilTributario"),
    PERFIL_DEPENDENTE("Dependente", "br.com.webpublico.perfil.PerfilDependente"),
    PERFIL_ADMINISTRATIVO("Administrativo", "br.com.webpublico.perfil.PerfilAdministrativo"),
    PERFIL_PENSIONISTA("Pensionista", "br.com.webpublico.perfil.PerfilPensionista"),
    PERFIL_CREDOR("Credor", "br.com.webpublico.perfil.PerfilCredor"),
    PERFIL_ESPECIAL("Especial", "br.com.webpublico.perfil.PerfilEspecial"),
    PERFIL_PRESTADOR("Prestador", "br.com.webpublico.perfil.PerfilRH");
    private String nomeDaClasse = "";
    private String descricao;

    PerfilEnum(String descricao, String nomeDaClasse) {
        this.descricao = descricao;
        this.nomeDaClasse = nomeDaClasse;
    }

    PerfilEnum() {
    }

    public String getDescricao() {
        return descricao;
    }

    public Perfil getPerfil() throws Exception {
        return (Perfil) Class.forName(this.nomeDaClasse).newInstance();
    }

    @Override
    public String toString() {
        return descricao;
    }
}

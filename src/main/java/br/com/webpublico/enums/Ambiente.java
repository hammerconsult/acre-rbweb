package br.com.webpublico.enums;

import br.com.webpublico.negocios.SistemaFacade;

import java.util.Arrays;

public enum Ambiente {
    PRODUCAO("Produção", SistemaFacade.PerfilApp.PROD),
    HOMOLOGACAO("Homologação", SistemaFacade.PerfilApp.DEV);

    private String descricao;
    private SistemaFacade.PerfilApp perfilApp;

    Ambiente(String descricao, SistemaFacade.PerfilApp perfilApp) {
        this.descricao = descricao;
        this.perfilApp = perfilApp;
    }

    public String getDescricao() {
        return descricao;
    }

    public SistemaFacade.PerfilApp getPerfilApp() {
        return perfilApp;
    }

    public static Ambiente getPorPerfil(SistemaFacade.PerfilApp perfilApp){
       return Arrays.stream(Ambiente.values()).filter(ambiente -> ambiente.getPerfilApp().equals(perfilApp))
            .findFirst().orElse(null);
    }
}

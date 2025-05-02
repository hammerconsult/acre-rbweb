package br.com.webpublico.controle.portaltransparencia.entidades;

import br.com.webpublico.entidades.Arquivo;

/**
 * Created by renatoromanini on 26/07/16.
 */
public class LinkPortal {


    private String nomeLink;
    private String siteLink;
    private Arquivo logo;

    public LinkPortal() {
    }

    public String getNomeLink() {
        return nomeLink;
    }

    public void setNomeLink(String nomeLink) {
        this.nomeLink = nomeLink;
    }

    public String getSiteLink() {
        return siteLink;
    }

    public void setSiteLink(String siteLink) {
        this.siteLink = siteLink;
    }

    public Arquivo getLogo() {
        return logo;
    }

    public void setLogo(Arquivo logo) {
        this.logo = logo;
    }

    @Override
    public String toString() {
        return nomeLink;
    }
}

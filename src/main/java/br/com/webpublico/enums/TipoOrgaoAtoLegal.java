package br.com.webpublico.enums;

public enum TipoOrgaoAtoLegal {
    GABINETE_DO_PREFEITO, CASA_CIVIL, SMCCI, SMGA, SEMSA, EMURB, SAERB, SEME, PGM, SEMEIA;

    TipoOrgaoAtoLegal() {
    }

    @Override
    public String toString() {
        return name();
    }
}

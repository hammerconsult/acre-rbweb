package br.com.webpublico.enums.rh;

public enum TipoFuncaoGratificada {
    COORDENACAO("Coordenação"),
    NORMAL("Normal"),
    GESTOR_RECURSOS("Gestor de Recursos"),
    CONTROLE_INTERNO("Controle Interno");

    private String descricao;

    TipoFuncaoGratificada(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

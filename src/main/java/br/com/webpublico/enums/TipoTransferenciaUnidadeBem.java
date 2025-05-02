package br.com.webpublico.enums;

public enum TipoTransferenciaUnidadeBem {

    INTERNA("Interna"),
    EXTERNA("Externa");
    private String descricao;

    TipoTransferenciaUnidadeBem(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }

    public boolean isExterna(){
        return EXTERNA.equals(this);
    }

    public boolean isInterna(){
        return INTERNA.equals(this);
    }
}

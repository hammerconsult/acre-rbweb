package br.com.webpublico.enums;

/**
 * Created by William on 10/04/2017.
 */
public enum TipoArquivoBancarioTributario {
    DAF607("DAF607 - INFORMAÇÕES DO SERPRO RELATIVAS ÀS GUIAS DO SIMPLES NACIONAL"),
    RCB001("RCB001");

    private String descricao;

    TipoArquivoBancarioTributario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

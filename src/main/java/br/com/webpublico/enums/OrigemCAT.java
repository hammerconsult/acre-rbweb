package br.com.webpublico.enums;

/**
 * @Author peixe on 13/09/2016  14:39.
 */
public enum OrigemCAT {
    INICIATIVA_EMPREGADOR("Iniciativa do Empregador"),
    ORDEM_JUDICIAL("Ordem Judicial"),
    DETERMINACAO_ORGAO_FISCALIZADOR("Determinação do Órgão Fiscalizador");
    private String descricao;

    OrigemCAT(String descricao) {
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

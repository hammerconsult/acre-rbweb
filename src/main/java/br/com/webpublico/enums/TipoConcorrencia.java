package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: JULIO-MGA
 * Date: 24/03/14
 * Time: 12:02
 * To change this template use File | Settings | File Templates.
 */
public enum TipoConcorrencia {

    NORMAL("Normal"),
    REGISTRO_DE_PRECOS("Registro de Preços");


    private String descricao;

    private TipoConcorrencia(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return descricao;

    }
}

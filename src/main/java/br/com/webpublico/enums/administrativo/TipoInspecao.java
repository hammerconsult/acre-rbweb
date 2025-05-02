package br.com.webpublico.enums.administrativo;

/**
 * Created by Desenvolvimento on 05/04/2017.
 */
public enum TipoInspecao {

    INSPECAO_EXTERNA_CARRO_NO_CHAO("Inspeção Externa (Carro no Chão)"),
    INSPECAO_INTERNA("Inspeção Interna"),
    INSTRUMENTACAO("Instrumentação"),
    INSPECAO_EXTERNA_CARRO_NO_ELEVADOR("Inspeção Externa (Carro no Elevador)");

    private String descricao;

    TipoInspecao(String descricao) {
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
        return this.descricao;
    }
}

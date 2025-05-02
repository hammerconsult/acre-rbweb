package br.com.webpublico.entidades;

/**
 * Created with IntelliJ IDEA.
 * User: Roma
 * Date: 14/08/14
 * Time: 10:17
 * To change this template use File | Settings | File Templates.
 */
public enum NivelChamado {
    SUPORTE_PREFEITURA("Suporte da Prefeitura"),
    SUPORTE("Suporte"),
    RESPONSAVEL_MODULO("Responsável Módulo");

    private String descricao;

    private NivelChamado(String descricao) {
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

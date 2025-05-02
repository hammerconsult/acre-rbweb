package br.com.webpublico.enums.rh.esocial;

/**
 * Created by William on 30/05/2018.
 */
public enum TipoContagemEspecialEsocial {
    NAO("Não", 1),
    PROFESSOR_ENSINO_MEDIO("Professor (Infantil, Fundamental e Médio)", 2),
    PROFESSOR_ENSINO_SUPERIOR("Professor de Ensino Superior, Magistrado, Membro de Ministério Público, Membro do Tribunal de Contas (com ingresso anterior a 16/12/1998 EC nr. 20/98)", 3),
    ATIVIDADE_RISCO("Atividade de risco", 4);

    private String descricao;
    private Integer codigo;

    TipoContagemEspecialEsocial(String descricao, Integer codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return getCodigo() + " - " + getDescricao();
    }
}

package br.com.webpublico.enums.tributario.regularizacaoconstrucao;

public enum TipoEspecialidadeServico {

    ARQUITETO(1, "Arquiteto(a)"),
    ARQUITETO_E_URBANISTA(2, "Arquiteto(a) e Urbanista"),
    ENGENHEIRO_CIVIL(3, "Engenheiro(a) Civil"),
    TECNOLOGO(4, "Tecnólogo"),
    TECNICO_EM_EDIFICACOES(5, "Técnico em Edificações");

    private Integer codigo;
    private String descricao;

    TipoEspecialidadeServico(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
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
        return this.codigo + " - " + this.descricao;
    }
}

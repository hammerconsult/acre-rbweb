package br.com.webpublico.enums.rh.estudoatuarial;

public enum TipoEspecificacaoCargo {
    MAGISTRADO_MEMBRO_MIN_PUBLICO_OU_TRIBUNAL_CONTAS("Magistrados, Membros do Min. Público ou de Tribunal de Contas", "1"),
    PROFESSORES_EDUC_INFANTIL_ENSINO_FUN_MEDIO("Professores da Educ. Infantil e do Ensino Fund. e Médio", "2"),
    PROFESSORES_ENSINO_SUPERIOR("Professores do Ensino Superior", "3"),
    POLICIAS_CIVIS("Policiais Civis (Federais, Distritais ou Estaduais)", "4"),
    AGENTE_PENITENCIARIO("Agente Penitenciário", "5"),
    GUARDA_MUNICIPAL("Guarda Municipal", "6"),
    DEMAIS_SERVIDORES("Demais Servidores", "7"),
    MILITARES("Militares", "8");


    TipoEspecificacaoCargo(String descricao, String codigo) {
        this.descricao = descricao;
        this.codigo = codigo;
    }

    private String descricao;
    private String codigo;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}

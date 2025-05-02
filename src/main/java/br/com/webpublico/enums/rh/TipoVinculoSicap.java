package br.com.webpublico.enums.rh;

public enum TipoVinculoSicap {
    SERVIDOR_CARGO_EFETIVO("1", "Servidor de cargo efetivo"),
    SERVIDOR_CARGO_TEMPORARIO("2", " Servidor em cargo temporário"),
    SERVIDOR_CARGO_COMISSIONADO("3", " Servidor em cargo comissionado"),
    SERVIDOR_EFETIVO_CARGO_COMISSAO("4", " Servidor efetivo em cargo de comissão"),
    SERVIDOR_EXERCENDO_MANDATO_ELETIVO("5", " Servidor exercendo mandato eletivo"),
    EMPREGADO_PUBLICO_CLT("6", " Empregado público (CLT)"),
    SERVIDOR_ESTAVEL_NAO_EFETIVO_NA_FORMA_DO_ART_19_DO_ADCT("7", " Servidor estável não efetivo na forma do Art. 19 do ADCT (Ato das Disposições Constitucionais Transitórias)"),
    SERVIDOR_EFETIVO_CARGO_COMISSAO_CEDIDO_DE_OUTRO_ORGAO("8", " Servidor efetivo em cargo de comissão - Cedido de outro órgão"),
    EMPREGADO_CLT_CONTRATO_DE_GESTAO("9", " Empregado (CLT) - Contrato de gestão"),
    ESTAGIARIOS("10", " Estagiários"),
    CONTRIBUINTE_INDIVIDUAL_E_AUTONOMO("11", " Contribuinte Individual e Autônomo");

    private String codigo;
    private String descricao;

    TipoVinculoSicap(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}

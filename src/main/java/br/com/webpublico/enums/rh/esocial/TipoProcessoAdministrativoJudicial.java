package br.com.webpublico.enums.rh.esocial;

/**
 * Created by mateus on 18/06/18.
 */
public enum TipoProcessoAdministrativoJudicial {
    ADMINISTRATIVO(1, "Administrativo"),
    JUDICIAL(2, "Judicial"),
    NUMERO_DE_BENEFICIO(3, "Número de Benefício (NB) do INSS"),
    PROCESSO_FAP(4, "Processo FAP");

    private Integer codigo;
    private String descricao;

    TipoProcessoAdministrativoJudicial(Integer codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public Integer getCodigo() {
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

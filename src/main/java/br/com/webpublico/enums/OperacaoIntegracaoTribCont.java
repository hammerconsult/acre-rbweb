package br.com.webpublico.enums;

/**
 * Created by Fabio on 07/05/2018.
 */
public enum OperacaoIntegracaoTribCont {

    PRINCIPAL("Principal - PCP", "PCP"),
    MULTA_JUROS_CORRECAO("Multa, Juros de Mora e Correção Monetária - MJM", "MJM"),
    HONORARIOS("Honorários", "HOR");
    private String descricao;
    private String sigla;

    OperacaoIntegracaoTribCont(String descricao, String sigla) {
        this.descricao = descricao;
        this.sigla = sigla;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getSigla() {
        return sigla;
    }
}

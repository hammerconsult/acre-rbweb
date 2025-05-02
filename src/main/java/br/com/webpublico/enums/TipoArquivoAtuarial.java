package br.com.webpublico.enums;

/**
 * Created with IntelliJ IDEA.
 * User: Claudio
 * Date: 22/01/14
 * Time: 11:41
 * To change this template use File | Settings | File Templates.
 */
public enum TipoArquivoAtuarial {
    SERVIDORES_ATIVOS("Servidores Ativos"),
    APOSENTADOS("Aposentados"),
    PENSIONISTAS("Pensionistas"),
    ATIVOS_FALECIDOS_OU_EXONERADOS("Ativos Falecidos ou Exonerados"),
    APOSENTADOS_FALECIDOS("Aposentados Falecidos"),
    PENSIONISTAS_FALECIDOS("Pensionistas Falecidos"),
    DEPENDENTES("Dependentes");

    private TipoArquivoAtuarial(String descricao) {
        this.descricao = descricao;
    }

    private String descricao;

    public String getDescricao() {
        return descricao;
    }
}

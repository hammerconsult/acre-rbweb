package br.com.webpublico.enums.rh.esocial;

public enum ClassificacaoTributariaESocial {

    EMPRESA_TRIBUTACAO_SIMPLES_COM_PREVIDENCIA_SUBSTITUIDA("Empresa enquadrada no regime de tributação Simples Nacional com tributação previdenciária\n" +
        "substituída", "01", 1, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    EMPRESA_TRIBUTACAO_SIMPLES_COM_PREVIDENCIA_NAO_SUBSTITUIDA("Empresa enquadrada no regime de tributação Simples Nacional com tributação previdenciária não\n" +
        "substituída", "02", 1, TipoDesoneracaoFolhaESocial.EMPRESA_ENQUADRADA),
    EMPRESA_TRIBUTACAO_SIMPLES_COM_PREVIDENCIA_SUBSTITUIDA_NAO_SUBSTITUIDA("Empresa enquadrada no regime de tributação Simples Nacional com tributação previdenciária\n" +
        "substituída e não substituída", "03", 1, TipoDesoneracaoFolhaESocial.EMPRESA_ENQUADRADA),
    MEI("MEI - Micro Empreendedor Individual", "04", 1, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    AGROINDUSTRIA("Agroindústria", "06", 1, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    PRODUTOR_RUAL_PESSOA_JURIDICA("Produtor Rural Pessoa Jurídica", "07", 1, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    CONSORCIO_SIMPLIFICADO_PRODUTOR_RURAL("Consórcio Simplificado de Produtores Rurais", "08", 1, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    ORGAO_GESTOR_MAO_DE_OBRA("Órgão Gestor de Mão de Obra", "09", 1, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    ENTIDADE_SINDICAL_LEI_112023_2009("Entidade Sindical a que se refere a Lei 12.023/2009", "10", 1, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    ASSOCIACAO_DESPORTIVA_CLUBE_PROFISSIONAL("Associação Desportiva que mantém Clube de Futebol Profissional", "11", 1, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),

    BANCO_CAIXA_ECONOMICA_DEMAIS_EMPRESAS_PARAGRAFO_1_ART_22_8212_91("Banco, caixa econômica, sociedade de crédito, financiamento e investimento e demais empresas\n" +
        "relacionadas no parágrafo 1o do art. 22 da Lei 8.212./91", "13", 1, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    SINDICATOS_EM_GERAL_EXETO_CODIGO_10("Sindicatos em geral, exceto aquele classificado no código [10]", "14", 1, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    PESSOA_FISICA_EXETO_SEGURADO_ESPECIAL("Pessoa Física, exceto Segurado Especial", "21", 2, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    SEGURADO_ESPECIAL("Segurado Especial", "22", 2, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    MISSAO_DIPLOMATICA_REPARTICAO_CONSULAR("Missão Diplomática ou Repartição Consular de carreira estrangeira", "60", 1, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    EMPRESA_DECRETO_5436_2005("Empresa de que trata o Decreto 5.436/2005", "70", 1, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    ENTIDADE_BENEFICENTE_ASSISTENCIA_SOCIAL_ISENTA_CONTRIBUICAO_SOCIAL("Entidade Beneficente de Assistência Social isenta de contribuições sociais", "80", 1, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    ADMINISTRACAO_DIRETA_UNIAO_ESTADOS_MUNICIPIOS_AUTARQUIAS_FUNDACOES_PUBLICAS("Administração Direta da União, Estados, Distrito Federal e Municípíos; Autarquias e Fundações\n" +
        "Públicas", "85", 1, TipoDesoneracaoFolhaESocial.NAO_APLICAVEL),
    PESSOA_JURIDICA_GERAL("Pessoas Jurídicas em Geral", "99", 1, TipoDesoneracaoFolhaESocial.EMPRESA_ENQUADRADA);


    private String descricao;
    private String codigo;
    private Integer tpInsc; //tipoInscricao
    private TipoDesoneracaoFolhaESocial tipoDesoneracaoFolhaESocial;

    ClassificacaoTributariaESocial(String descricao, String codigo, Integer tpInsc, TipoDesoneracaoFolhaESocial tipoDesoneracaoFolhaESocial) {
        this.descricao = descricao;
        this.codigo = codigo;
        this.tpInsc = tpInsc;
        this.tipoDesoneracaoFolhaESocial = tipoDesoneracaoFolhaESocial;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public Integer getTpInsc() {
        return tpInsc;
    }

    public TipoDesoneracaoFolhaESocial getTipoDesoneracaoFolhaESocial() {
        return tipoDesoneracaoFolhaESocial;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}

package br.com.webpublico.nfse.enums;

public enum GrupoProdutoServicoBancario {
    AGENCIAMENTO_INTERMEDIACAO_EM_OPERACOES_CREDITO("Agenciamento / intermediação em operações de crédito"),
    AGENCIAMENTO_INTERMEDIACAO_EM_OPERACOES_CAMBIO("Agenciamento / intermediação em operações de câmbio"),
    AGENCIAMENTO_INTERMEDIACAO_VENDA_SEGUROS("Agenciamento / intermediação na venda de seguros"),
    AGENCIAMENTO_INTERMEDIACAO_VENDA_SERVICOS_TURISMO("Agenciamento / intermediação na venda de serviços de turismo"),
    AGENCIAMENTO_INTERMEDIACAO_VENDA_CARTOES_CREDITO("Agenciamento / intermediação na venda de cartões de crédito"),
    AGENCIAMENTO_INTERMEDIACAO_VENDA_TITULOS_CAPITALIZACAO("Agenciamento / intermediação na venda de títulos de capitalização"),
    AGENCIAMENTO_INTERMEDIACAO_VENDA_PLANOS_SAUDE("Agenciamento / intermediação na venda de planos de saúde"),
    AGENCIAMENTO_INTERMEDIACAO_VENDA_ARRENDAMENTO_MERCANTIL("Agenciamento / intermediação na venda de arrendamento mercantil -\"leasing\""),
    AGENCIAMENTO_INTERMEDIACAO_VENDA_PREVIDENCIA_PRIVADA("Agenciamento / intermediação na venda de planos de previdência privada"),
    AGENCIAMENTO_INTERMEDIACAO_PROGRAMAS("Agenciamento / intermediação de programas"),
    AGENCIAMENTO_INTERMEDIACAO_GERAL("Agenciamento / intermediação em geral"),
    INTERMEDIACAO_CONTRATOS_BMF("Intermediação de contratos na BM&F"),
    INTERMEDIACAO_TITULOS("Intermediação de títulos"),
    INTERMEDIACAO_VENDA_CONTAS_FUNDOS_CLUBES_INVESTIMENTO("Intermediação na venda de cotas de fundos e clubes de investimento"),
    INTERMEDIACAO_COMPRA_VENDA_ACOES("Intermediação na compra e venda de ações"),
    INTERMEDIACAO_BENS_IMOVEIS("Intermediação de bens imóveis"),
    INTERMEDIACAO_BENS("Intermediação de bens"),
    COLOCACAO_TITULOS_VALORES_MOBILIARIOS("Colocação de títulos e valores mobiliários"),
    COLOCACAO_COTAS_FUNDOS_CLUBE_INVESTIMENTO("Colocação de cotas de fundos e clubes de investimento"),
    DISTRIBUICAO_TITULOS_VALORES_MOBILIARIOS("Distribuição de títulos e valores mobiliários"),
    CORRETAGEM_CAMBIO("Corretagem de câmbio"),
    CORRETAGEM_OPERACOES_BOLSA("Corretagem de operações em Bolsas"),
    CORRETAGEM_BENS("Corretagem de bens"),
    ADMINISTRACAO_PROGRAMAS_OFICIAIS("Administração de programas oficiais (PROAGRO, p.ex.)"),
    ADMINISTRACAO_LOTERIAS("Administração de loterias"),
    ADMINISTRACAO_SOCIEDADE_INVESTIMENTO("Administração de Sociedades de Investimento"),
    ADMINISTRACAO_CONSORCIOS("Administração de consórcios"),
    ADMINISTRACAO_DIVIDAS_SETOR_PUBLICO("Administração de dívidas do setor público"),
    ADMINISTRACAO_FUNDOS_PROGRAMAS_SETOR_PUBLICO("Administração de fundos e programas – Setor Público"),
    ADMINISTRACAO_FUNDOS_PROGRAMAS_SETOR_PRIVADO("Administração de fundos e programas – Setor Privado"),
    ADMINISTRACAO_SISTEMAS_NEGOCIACAO_ATIVOS("Administração de sistemas de negociação de ativos"),
    ADMINISTRACAO_CUSTODIA_TITULOS("Administração da custódia de títulos"),
    ADMINISTRACAO_CARTEIRA_CLIENTES("Administração de carteiras de clientes"),
    ADMINISTRACAO_FUNDOS_DESENVOLVIMENTO("Administração de fundos de desenvolvimento"),
    ADMINISTRACAO_REPASSES("Administração de repasses"),
    REPRESENTACAO_INVESTIDOR_NAO_RESIDENTE("Representação de investidor não residente"),
    SERVICO_TESOURARIA_TERCEIRO("Serviços de tesouraria para terceiros"),
    ADMINISTRACAO_BENS_NEGOCIOS_TERCEIROS("Administração de bens e negócios de terceiros"),
    SERVICO_ACESSORIA_CONSULTORIA_COMERCIO_EXTERIOR("Serviços de assessoria e consultoria em Comércio Exterior"),
    SERVICO_ACESSORIA_CONSULTORIA_CARTEIRAS_INVESTIMENTO("Serviços de assessoria e consultoria em carteiras de investimento"),
    SERVICO_CONSULTORIA_UTILIZACAO_SOFTWARE("Serviços de consultoria na utilização de softwares"),
    SERVICO_ACESSORIA_FINANCEIRA("Serviços de assessoria financeira"),
    SERVICO_ACESSORIA_CONSULTORIA("Serviços de assessoria e consultoria"),
    PROCESSAMENTO_DADOS("Processamento de dados"),
    COMISSOES_COMPRAS_CARTAO_EXTERIOR("Comissões de compras com cartões no exterior"),
    CAPTACAO_REPASSE_RECURSOS("Captação / repasse de recursos"),
    COMISSAO_LEILOES_ELETRONICOS("Comissões em leilões eletrônicos"),
    SERVICOS_RELACIONADOS_TRANSFERENCIAS("Serviços relacionados a transferências"),
    OUTROS("Outros");

    private String descricao;

    GrupoProdutoServicoBancario(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

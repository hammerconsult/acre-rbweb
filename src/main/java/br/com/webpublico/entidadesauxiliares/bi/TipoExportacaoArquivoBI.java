package br.com.webpublico.entidadesauxiliares.bi;

import br.com.webpublico.enums.ModuloSistema;
import com.google.common.collect.Lists;

import java.util.List;

public enum TipoExportacaoArquivoBI {

    PLANO_CONTAS("Plano de contas", "IMPITENS", ModuloSistema.CONTABIL),
    ACAO("Ação", "IMPACOES", ModuloSistema.CONTABIL),
    PROGRAMA("Programa", "IMPPROGRAMAS", ModuloSistema.CONTABIL),
    ORGAO("Órgão", "IMPORGAOS", ModuloSistema.CONTABIL),
    SUB_FONTE("Sub-Fonte", "IMPSUBFONTE", ModuloSistema.CONTABIL),
    FONTE("Fonte", "IMPFONTE", ModuloSistema.CONTABIL),
    ORCAMENTO_RECEITA("Orçamento Receita", "IMPORCAMENTORECEITA", ModuloSistema.CONTABIL),
    ORCAMENTO_INICIAL("Orçamento Inicial", "IMPORCAMENTOINICIAL", ModuloSistema.CONTABIL),
    ORCAMENTO_ALTERADO("Orçamento Alterado", "IMPORCAMENTOALTERADO", ModuloSistema.CONTABIL),
    BLOQUEADOS("Bloqueados", "IMPBLOQUEADOS", ModuloSistema.CONTABIL),
    CREDITOS("Créditos", "IMPCREDITOS", ModuloSistema.CONTABIL),
    LOGRADOURO("Logradouro", "IMPLOGRADOUROS", ModuloSistema.TRIBUTARIO),
    BAIRRO("Bairro", "IMPBAIRROS", ModuloSistema.TRIBUTARIO),
    PESSOA("Contribuinte", "IMPCONTRIBUINTES", ModuloSistema.TRIBUTARIO),
    EVENTO("Evento", "IMPEVENTOS", ModuloSistema.TRIBUTARIO),
    RECEITA("Receita", "IMPRECEITA", ModuloSistema.CONTABIL),
    CONTRATO("Contrato", "IMPCONTRATOS", ModuloSistema.CONTABIL),
    FORNECEDOR("Fornecedor", "IMPFORNECEDOR", ModuloSistema.CONTABIL),
    LIMITE_PESSOAL("Limite Pessoal", "IMPLIMITEPESSOAL", ModuloSistema.CONTABIL),
    EMPENHADO("Empenhado", "IMPEMPENHADO", ModuloSistema.CONTABIL),
    LIQUIDADO("Liquidado", "IMPLIQUIDADO", ModuloSistema.CONTABIL),
    PAGO("Pago", "IMPPAGO", ModuloSistema.CONTABIL),
    RESTOS_A_PAGAR("Restos a Pagar", "IMPRAP", ModuloSistema.CONTABIL),
    GUIAS_TAXAS("Taxas", "IMPGUIASTAXAS", ModuloSistema.TRIBUTARIO),
    IMOVEIS("Imóveis", "IMPIMOVEIS", ModuloSistema.TRIBUTARIO),
    TIPO_USO_IMOVEL_TERRENO("Tipos de Uso do Imóvel/Terreno", "IMPTIPOSUSO", ModuloSistema.TRIBUTARIO),
    VALORES_LANCAMENTO_IPTU("Valores de Lançamento do IPTU", "IMPVALORESIPTU", ModuloSistema.TRIBUTARIO),
    VALORES_IPTU_PAGO("Valores de IPTU Pago", "IMPVALORESIPTUPAGO", ModuloSistema.TRIBUTARIO),
    TIPOS_IPTU("Tipos de IPTU", "IMPTIPOSIPTU", ModuloSistema.TRIBUTARIO),
    CMC("Cadastro Econômico", "IMPINSCRICAOMOBILIARIA", ModuloSistema.TRIBUTARIO),
    TIPOISS("Tipo ISS", "IMPTIPOSISS", ModuloSistema.TRIBUTARIO),
    ATIVIDADE("Atividade", "IMPATIVIDADES", ModuloSistema.TRIBUTARIO),
    GUIA_ISS("Guia ISS", "IMPGUIASISS", ModuloSistema.TRIBUTARIO),
    ORIGEM_DEBITOS("Origem do Débito", "IMPORIGEM", ModuloSistema.TRIBUTARIO),
    SITUACAO_DEBITO("Situação do Débito", "IMPSITUACAODEBITO", ModuloSistema.TRIBUTARIO),
    TRIBUTO("Tributo", "IMPTRIBUTO", ModuloSistema.TRIBUTARIO),
    PARCELAMENTO("Parcelamento", "IMPPARCELAMENTO", ModuloSistema.TRIBUTARIO),
    DEBITOS_GERAL("Débitos Geral", "IMPDEBITOSGERAL", ModuloSistema.TRIBUTARIO),
    CONTAS_BANCARIAS("Contas Bancárias", "IMPCONTASBANCARIOS", ModuloSistema.CONTABIL),
    CONTAS_FINANCEIRAS("Contas Financeiras", "IMPDADOSCONTAFINANCEIRA", ModuloSistema.CONTABIL),
    MOVIMENTO_CONTA_BANCARIA("Movimento Conta Bancária", "IMPMOVIMENTOCONTABANCARIA", ModuloSistema.CONTABIL),
    MOVIMENTO_CONTA_FINANCEIRA("Movimento Conta Financeira", "IMPMOVIMENTOCONTAFINANCEIRA", ModuloSistema.CONTABIL),
    SALDO_CONTA_BANCARIA("Saldo Conta Bancária", "IMPSALDOCONTABANCARIA", ModuloSistema.CONTABIL),
    SALDO_CONTA_FINANCEIRA("Saldo Conta Financeira", "IMPSALDOCONTAFINANCEIRA", ModuloSistema.CONTABIL),
    NOME_BANCO("Nome Banco", "IMPORGAOSBANCARIOS", ModuloSistema.CONTABIL);

    private String descricao;
    private String nomeArquivo;
    private ModuloSistema moduloSistema;

    TipoExportacaoArquivoBI(String descricao, String nomeArquivo, ModuloSistema moduloSistema) {
        this.descricao = descricao;
        this.nomeArquivo = nomeArquivo;
        this.moduloSistema = moduloSistema;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public ModuloSistema getModuloSistema() {
        return moduloSistema;
    }

    public static List<TipoExportacaoArquivoBI> getTiposArquivosBiPorModulo(ModuloSistema moduloSistema) {
        List<TipoExportacaoArquivoBI> retorno = Lists.newArrayList();
        for (TipoExportacaoArquivoBI tipo : TipoExportacaoArquivoBI.values()) {
            if (moduloSistema.equals(tipo.moduloSistema)) {
                retorno.add(tipo);
            }
        }
        return retorno;
    }
}

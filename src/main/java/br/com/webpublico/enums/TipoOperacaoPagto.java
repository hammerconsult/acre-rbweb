package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * Created with IntelliJ IDEA.
 * User: Edi
 * Date: 22/10/13
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public enum TipoOperacaoPagto implements EnumComDescricao {

    NAO_APLICAVEL("99 - Não Aplicável","99"),
    BAIXA_PARA_REGULARIZACAO("00 - Baixa para Regularização","00"),
    CREDOR_OUTRO_BANCO("11 - Credor Outro Banco","11"),
    CREDOR_MESMO_BANCO("12 - Credor Mesmo Banco","12"),
    CREDOR_OUTRO_BANCO_NAO_CONTA_UNICA("31 - Credor Outro Banco/Não Conta Única","31"),
    CREDOR_MESMO_BANCO_NAO_CONTA_UNICA("32 - Credor Mesmo Banco/Não Conta Única","32"),
    LIQUIDACAO_NO_CAIXA("16 - Liquidação no Caixa","16"),
    LIQUIDACAO_NO_CAIXA_NAO_CONTA_UNICA("36 - Liquidação no Caixa não Conta Única","36"),
    PAGAMENTO_COM_AUTENTICACAO("13 - Pagamento com Autenticação","13"),
    PAGAMENTO_NAO_AUTENTICADO_NAO_CONTA_UNICA("33 - Pagamento com Autenticação não Conta Única","33"),
    PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA("18 - Pagamento de guia com Código de Barras","18"),
    PAGAMENTO_DE_GUIA_COM_CODIGO_DE_BARRA_NAO_CONTA_UNICA("38 - Pagamento de guia com Código de Barras/Não Conta Única","38"),
    PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA("19 - Pagamento de guia sem Código de Barras","19"),
    PAGAMENTO_DE_GUIA_SEM_CODIGO_DE_BARRA_NAO_CONTA_UNICA("39 - Pagamento de guia sem Código de Barras/Não Conta Única","39"),
    TRANSFERENCIA_DE_RECURSO("21 - Transferência de Recurso","21"),
    TRANSFERENCIA_DE_RECURSOS_OB_LISTA("17 - Transferência de recursos OB lista", "17"),
    TRANSFERENCIA_DE_RECURSOS_OB_LISTA_NAO_CONTA_UNICA("17 - Transferência de recursos OB lista/Não Conta Única", "17");

    private String descricao;
    private String numero;

    TipoOperacaoPagto(String descricao,String numero) {
        this.descricao = descricao;
        this.numero = numero;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }

    public String getNumero() {
        return numero;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

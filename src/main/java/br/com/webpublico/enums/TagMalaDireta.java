package br.com.webpublico.enums;

/**
 * Created by Rodolfo on 07/06/2017.
 */
public enum TagMalaDireta {

    CONTRIBUINTE_NOME("Nome do Contribuinte"),
    CONTRIBUINTE_CPF_CNPJ("CPF/CNPJ do Contribuinte"),
    CONTRIBUINTE_LOGRADOURO("Logradouro do Contribuinte"),
    CONTRIBUINTE_BAIRRO("Bairro do Contribuinte"),
    CONTRIBUINTE_NUMERO_ENDERECO("Número do endereço do Contribuinte"),
    CONTRIBUINTE_COMPLEMENTO_ENDERECO("Complemento do endereço do Contribuinte"),
    CONTRIBUINTE_CEP("CEP do Contribuinte"),
    CONTRIBUINTE_CIDADE("Cidade do Contribuinte"),
    CONTRIBUINTE_UF("UF do Contribuinte"),
    CONTRIBUINTE_TIPO_ENDERECO("Tipo de endereço do Contribuinte"),
    CADASTRO_NUMERO("Inscrição Cadastral"),
    CADASTRO_LOGRADOURO("Logradouro do Cadastro"),
    CADASTRO_BAIRRO("Bairro do Cadastro"),
    CADASTRO_NUMERO_ENDERECO("Número do endereço do Cadastro"),
    CADASTRO_COMPLEMENTO_ENDERECO("Complemento do endereço do Cadastro"),
    CADASTRO_CEP("CEP do Cadastro"),
    CADASTRO_IMOBILIARIO_LOTE("Lote do Cadastro Imobiliário"),
    CADASTRO_IMOBILIARIO_SETOR("Setor do Cadastro Imobiliário"),
    CADASTRO_IMOBILIARIO_QUADRA("Quadra do Cadastro Imobiliário"),
    CADASTRO_ECONOMICO_SITUACAO_CADASTRAL("Situação Cadastral do Cadastro Econômico"),
    CADASTRO_ECONOMICO_DATA_ABERTURA("Data de Abertura do Cadastro Econômico"),
    CADASTRO_ECONOMICO_RAZAO_SOCIAL("Razão Social do Cadastro Econômico"),
    CADASTRO_ECONOMICO_NOME_FANTASIA("Nome Fantasia do Cadastro Econômico"),
    DAM_VENCIMENTO("Vencimento do DAM"),
    DAM_NUMERO("Número do DAM"),
    DAM_VALOR_ORIGINAL("Valor Original do DAM"),
    DAM_JUROS("Valor de Juros do DAM"),
    DAM_MULTA("Valor de Multa do DAM"),
    DAM_CORRECAO_MONETARIA("Valor de Correção Monetário do DAM"),
    DAM_HONORARIOS("Valor de Honorários do DAM"),
    DAM_CODIGO_BARRAS_DIGITOS("Código de Barras do DAM (Com dígitos)"),
    DAM_CODIGO_BARRAS("Código de Barras do DAM"),
    DAM_DIVIDAS("Dívidas do DAM");

    private String descricao;

    TagMalaDireta(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}

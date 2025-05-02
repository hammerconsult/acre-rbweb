package br.com.webpublico.enums;

import java.util.List;

/**
 * @author Alex
 * @since 07/06/2016 15:08
 */
public enum CodigoOcorrenciaRetornoArquivoCaixa {

    CREDITO_DEBITO_EFETIVADO("Crédito ou Débito Efetivado", true),
    INSUFICIENCIA_FUNDOS("Insuficiência de Fundos", false),
    CREDITO_DEBITO_CANCELADO_PAGADOR_CREDOR("Crédito ou Débito Cancelado pelo Pagador/Credor", false),
    DEBITO_AUTORIZADO_PELA_AGENCIA("Débito Autorizado pela Agência - Efetuado", true),
    AA("Controle Inválido", false),
    AB("Tipo de operação inválido", false),
    AC("Tipo de serviço Inválido", false),
    AD("Forma de Lançamento Inválida", false),
    AE("Tipo/Número de Inscrição Inválido", false),
    AF("Código de convênio Inválido", false),
    AG("Agência/Conta Corrente/DV inválido", false),
    AH("Nº Sequencial do Registro de Lote Inválido", false),
    AI("Código de Segmento de Detalhe Inválido", false),
    AJ("Tipo de movimento inválido", false),
    AK("Código da câmara de compensação do banco favorecido/depositário Inválido", false),
    AL("Código do banco favorecido ou depositário Inválido", false),
    AM("Agência mantenedora da conta corrente do favorecido inválida", false),
    AN("Conta Corrente/DV do Favorecido Inválido", false),
    AO("Nome do favorecido não informado", false),
    AP("Data de Lançamento Inválida", false),
    AQ("Tipo/quantidade da moeda Inválida", false),
    AR("Valor do lançamento inválido", false),
    AS("Aviso ao favorecido - identificação inválida", false),
    AT("Tipo/Número da inscrição do Favorecido Inválido", false),
    AU("Logradouro do favorecido não informado", false),
    AV("Número do local do favorecido não informado", false),
    AW("Cidade do favorecido não informada ", false),
    AX("CEP/Complemento do Favorecido Inválido", false),
    AY("Sigla do Estado do Favorecido Inválida", false),
    AZ("Código/nome do banco depositário inválido", false),
    BA("Código/nome da agência depositária não informado", false),
    BB("Seu número inválido", false),
    BC("Nosso número inválido", false),
    BD("Inclusão efetuada com sucesso", true),
    BE("Alteração efetuada com sucesso", true),
    BF("Exclusão efetuada com sucesso", true),
    BG("Agência/conta impedida legalmente", false),
    CA("Código de barras - código do banco inválido", false),
    CB("Código de barras - código da moeda inválida", false),
    CC("Código de barras - dígito verificador geral inválido", false),
    CD("Código de barras - valor do titulo Inválido", false),
    CE("Código de barras - campo livre Inválido", false),
    CF("Valor do Documento Inválido", false),
    CG("Valor do abatimento inválido", false),
    CH("Valor do desconto inválido", false),
    CI("Valor de mora inválido", false),
    CJ("Valor da multa inválido", false),
    CK("Valor do IR inválido", false),
    CL("Valor do ISS inválido", false),
    CM("Valor do IOF inválido", false),
    CN("Valor de outras deduções inválido", false),
    CO("Valor de outros acréscimos inválido", false),
    CP("Valor do INSS inválido", false),
    CQ("Código de barras inválido", false),
    TA("Lote não aceito - totais de lote com diferença", false),
    TB("Lote sem trailler", false),
    TC("Lote de Arquivo sem trailler", false),
    HA("Lote não aceito", false),
    HB("Inscrição da empresa inválida para o contrato", false),
    HC("Convênio com a empresa inexistente/inválido para o contrato", false),
    HD("Agência/Conta Corrente da Empresa Inexistente/Inválido para o Contrato", false),
    HE("Tipo de Serviço Inválido para o Contrato", false),
    HF("Conta Corrente da Empresa com Saldo Insuficiente", false),
    HG("Lote de Serviço fora de Seqüência", false),
    HH("Lote de serviço inválido", false),
    HI("Número da remessa inválido", false),
    HJ("Arquivo sem “HEADER”", false),
    HK("Código Remessa/Retorno Inválido", false),
    HL("Versão de leiaute Inválida", false),
    HM("Versão do arquivo inválido", false),
    HV("Quantida de parcela Inválida", false),
    YA("Título não encontrado", false),
    YB("Identificador registro opcional inválido", false),
    YC("Código padrão inválido", false),
    YD("Código de ocorrência inválido", false),
    YE("Complemento de ocorrência inválido", false),
    YF("Alegação já informada", false),
    ZA("Agência/conta do favorecido substituída", false);

    private String descricao;

    private boolean sucesso;

    CodigoOcorrenciaRetornoArquivoCaixa(String descricao, boolean sucesso) {
        this.descricao = descricao;
        this.sucesso = sucesso;
    }


    public String getDescricao() {
        return descricao;
    }

    public boolean isSucesso() {
        return sucesso;
    }

    public static Boolean isTodasOcorrenciasSucesso(List<CodigoOcorrenciaRetornoArquivoCaixa> ocorrencias) {
        if (ocorrencias.isEmpty()) {
            return null;
        }
        return ocorrencias.stream()
            .map(CodigoOcorrenciaRetornoArquivoCaixa::isSucesso)
            .reduce(true, (sucesso, ocorrencia) -> sucesso && ocorrencia);
    }
}

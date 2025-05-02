package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

/**
 * Created by Filipe
 * On 17, Abril, 2019,
 * At 15:26
 */
public enum InconsistenciaImportacaoDebitoIPTU implements EnumComDescricao {


    REGISTRO_FOI_ACEITO_E_PROCESSADO("Registro foi aceito e processado com sucesso!", " ", "00"),
    CODIGO_DE_BARRAS_NAO_PERTENCE_AO_CONVENIO("Código de barras não pentence ao Convênio", "A", "01"),
    ARQUIVO_COM_MAIS_DE_UM_HEADER("Arquivo com mais de um Header", "A", "02"),
    ARQUIVO_SEM_REGISTRO_DETALHE("Arquivo sem registro detalhe", "A", "03"),
    ARQUIVO_COM_MAIS_DE_UM_TRAILLER("Arquivo com mais de um trailler", "A", "04"),
    ARQUIVO_SEM_TRAILLER("Arquivo sem trailler", "A", "05"),
    QUANTIDADE_DE_REGISTROS_DO_ARQUIVO_DIFERENTE_DO_ESPERADO("Quantidade de registros do arquivo diferente do esperado", "A", "06"),
    CONVENIO_INVALIDO_OU_NAO_INFORMADO("Convênio Inválido ou não informado", "A", "07"),
    DATA_DE_GERACAO_DO_ARQUIVO_NAO_INFORMADA_OU_NAO_NUMERICA("Data  de geração do arquivo não informada ou não numérica", "A", "08"),
    IDENTIFICACAO_DO_ARQUIVO_INVALIDA("Identificação do arquivo Inválida", "A", "09"),
    TIPO_DE_ARQUIVO_INVALIDO_OU_NAO_INFORMADO("Tipo de arquivo inválido ou não informado", "A", "10"),
    CODIGO_DA_AGENCIA_CENTRALIZADORA_DO_CONVENIO_NAO_INFORMADO_OU_INVALIDO("Código da agência Centralizadora do convênio não informado ou inválido", "A", "11"),
    ANO_DA_REMESSA_INVALIDO_OU_NAO_INFORMADO("Ano da remessa inválido ou não informado", "A", "12"),
    NUMERO_SEQUENCIAL_DA_REMESSA_INVALIDO_OU_NAO_INFORMADO("Número sequencial da remessa inválido ou não informado", "A", "13"),
    DATA_DE_INICO_DE_VIGENCIA_NAO_NUMERICA_OU_NAO_INFORMADA("Data de início de vigência não numérica ou não informada", "A", "14"),
    DATA_DE_FIM_DE_VIGENCIA_NAO_NUMERICA_OU_NAO_INFORMADA("Data de fim de vigência não numérica ou não informada", "A", "15"),
    SEQUENCIAL_DO_HEADER_INVALIDO_OU_NAO_INFORMADO("Sequencial do header inválido ou não informado", "A", "16"),
    DATA_DE_GERACAO_DO_ARQUIVO_NAO_E_UMA_DATA_VALIDA("Data de geração do arquivo não é uma data válida", "A", "17"),
    DATA_INFORMADA_INVALIDA("Data informada inválida", "A", "18"),
    DATA_DE_FIM_DE_VIGENCIA_NAO_E_UMA_DATA_VALIDA("Data de fim de vigência não é uma data válida", "A", "19"),
    DATA_DE_GERACAO_DO_ARQUIVO_MAIOR_QUE_A_DATA_DE_PROCESSAMENTO("Data de geraçã do arquivo, maior, que a data de processamento", "A", "20"),
    DATA_DE_INICIO_DE_VIGENCIA_NAO_MAIOR_QUE_A_DATA_DE_PROCESSAMENTO_DO_ARQUIVO("Data de início de vigência NÃO MAIOR que a data de processamento do arqiuvo", "A", "21"),
    DATA_DE_FIM_DE_VIGENCIA_MENOR_QUE_A_DATA_DE_INICIO_DE_VIGENCIA("Data de fim de vigência menor que a data de início de vigência", "A", "22"),
    CONTROLE_DE_SEQUENCIAL_DA_REMESSA_NAO_CORRESPONDE_AO_ESPERADO("Controle de sequencial da remessa não corresponde ao esperado", "A", "23"),
    CONVENIO_DE_REEBIMENTO_NAO_EXISTE("Convênio de recebimento não existe", "A", "24"),
    RESPONSAVEL_PELO_DEBITO_NAO_INFORMADO("Responsável pelo débito não informado", "R", "25"),
    TIPO_DE_ATUALIZACAO_INVALIDO_OU_NAO_INFORMADO("tipo de atualização inválido ou não informado", "R", "26"),
    CPF_CNPJ_NAO_INFORMADO_ZERADO_OU_FORMATO_INVALIDO("Cpf/Cnpj Não informado (zerado) ou formato inválido", "R", "27"),
    IDENTIFICADOR_DO_DEBITO_NAO_INFORMADO("Identificador do débito não informado", "R", "28"),
    REFERENCIA_NAO_INFORMADA("Refência não informada", "R", "29"),
    DETALHAMENTO_DO_DEBITO_NAO_INFORMADA("Detalhamento do débito não informada", "R", "30"),
    DATA_DE_VENCIMENTO_NAO_INFORMADA_OU_NAO_NUMERICA("Data de vencimento, não informada, ou não numérica", "R", "31"),
    CODIGO_DE_BARRAS_NAO_INFORMADO("Código de barras não informado", "R", "32"),
    VALOR_DO_DEBITO_NAO_NUMERICO_OU_NAO_INFORMADO("Valor do débito não numérico ou não informado", "R", "33"),
    TIPO_DE_REGISTRO_INVALIDO("Tipo de registro inválido", "R", "34"),
    TIPO_DE_DEBITO_INVALIDO("Tipo de débito inválido", "R", "35"),
    TIPO_DE_DEBITO_NAO_NUMERICO_OU_NAO_INFORMADO("Tipo de débito não numérico ou não informado", "R", "36"),
    NUMERO_DA_PARCELA_NAO_NUMERICO("Número da parcela não numérico", "R", "37"),
    SEQUENCIAL_DO_REGISTRO_DETALHE_INVALIDO_OU_NAO_INFORMADO("Sequencial do registro detalhe inválido ou não informado", "R", "38"),
    CPFCNPJ_INVALIDO("Cpf/Cnpj inválido", "R", "39"),
    CODIGO_DE_BARRAS_INVALIDO("Código de barras inválido", "R", "40"),
    NAO_CLIENTE_DO_BANCO("Não cliente do banco", "R", "41"),
    CLIENTE_SEM_CONTA_ATIVA_NO_BANCO("Cliente sem conta ativa no banco", "R", "42"),
    SEQUENCIAL_DO_REGISTRO_TRAILER_INVALIDO_OU_NAO_INFORMADO("Sequencial do registro trailer inválido ou não informado", "A", "43"),
    ANO_DA_REMESSA_DEVE_SER_O_MESMO_DA_DATA_DE_PROCESSAMENTO_DO_ARQUIVO("Ano da remessa deve ser o mesmo da data de processamento do arquivo", "A", "44"),
    CODIGO_DA_AGENCIA_CENTRALIZADORA_INEXISTENTE("Código da agência centralizadora inexistente", "A", "45"),
    DATA_DE_VENCIMENTO_INFORMADA_NAO_E_UMA_DATA_VALIDA("Data de vencimento informada não é uma data válida", "R", "46"),
    QUANTIDADE_DE_REGISTROS_DO_ARQUIVO_INVALIDO_OU_NAO_INFORMADO("Quantidade de registros do arquivo inválido ou não informado", "A", "47"),
    INCLUSAO_INVALIDA_BARRA_JA_EXISTENTE("Inclusão inválida. Barra já Existente", "R", "48"),
    ALTERACAO_INVALIDA_BARRA_INEXISTENTE("Alteração inválida. Barra Inexistente", "R", "49"),
    EXCLUSAO_INVALIDA_BARRA_INEXISTENTE("Exclusão inválida. Barra Inexistente", "R", "50"),
    ARQUIVO_RECUSADO_POR_ERRO_NO_TRAILER("Arquivo recusado por erro no trailer", "A", "51"),
    CONVENIO_NAO_ESTA_ATIVO("Convênio não está ativo", "A", "52"),
    CONVENIO_NAO_POSSUI_CONTRTO_DE_OFERTA_ATIVA("Convênio não possui contrato de oferta ativa", "A", "53"),
    BARRA_VINCULADORA_INVALIDA("Barra vinculadora Inválida", "R", "54"),
    BARRA_VINCULADORA_INEXISTENTE("Barra vinculadora inexistente", "R", "55"),
    REGISTRO_NAO_VALIDO_POR_RECUSA_TOTAL_DO_ARQUIVO("Registro não válido por recusa total do arquivo (Erro no header ou trailer)", "R", "56"),
    CODIGO_DO_TIPO_DE_PESSOA_PROPRIETARIO_INVALIDO("Código do tipo de pessoa proprietário inválido", "R", "57"),
    CODIGO_DE_BARRAS_DA_BARRA_VINCULADORA_NAO_PERTENCE_CONVENIO("Código de barras da barra vinculadora não pertence ao Convênio", "R", "58"),
    SOLICITACAO_DE_INCLUSAO_ALTERACAO_EXCLUSAO_DE_BARRA_LIQUIDADA_AGENDADA("Solicitação de inclusão/alteração/exclusão de barra liquidada/agendada", "R", "59"),
    SOLICITACAO_DE_ALTERACAO_EXCLUSAO_DE_BARRA_LIQUIDADA_AGENDADA("Solicitação de alteração/exclusão de barra liquidada/agendada", "R", "60"),
    PEDIDO_DE_ALTERACAO_OU_EXCLUSAO_DE_BARRA_DE_OUTRO_CPF_CNPJ("Pedido de alteração ou exclusão de barra de outro cpf/cnpj", "R", "61");


    private String tipoDeRecusa;
    private String descricao;
    private String codigo;


    private InconsistenciaImportacaoDebitoIPTU(String descricao, String tipoDeRecusa, String codigo) {
        this.descricao = descricao;
        this.tipoDeRecusa = tipoDeRecusa;
        this.codigo = codigo;
    }

    public static InconsistenciaImportacaoDebitoIPTU getInconcistenciaPorCodigo(String codRetorno) {
        for (InconsistenciaImportacaoDebitoIPTU inconsistencia : InconsistenciaImportacaoDebitoIPTU.values()) {
            if (inconsistencia.getCodigo().equals(codRetorno)) {
                return inconsistencia;
            }
        }
        return null;
    }

    public String getTipoDeRecusa() {
        return tipoDeRecusa;
    }

    public void setTipoDeRecusa(String tipoDeRecusa) {
        this.tipoDeRecusa = tipoDeRecusa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }}

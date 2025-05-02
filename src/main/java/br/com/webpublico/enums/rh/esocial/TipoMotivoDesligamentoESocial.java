package br.com.webpublico.enums.rh.esocial;

/**
 * Created by William on 22/10/2018.
 */

//Tabela 19 - Motivos de Desligamento

public enum TipoMotivoDesligamentoESocial {
    RESCISAO_COM_JUSTA_CAUSA_INICIATIVA_EMPREGADOR("01", "Rescisão com justa causa, por iniciativa do empregador"),
    RESCISAO_SEM_JUSTA_CAUSA_INICIATIVA_EMPREGADOR("02", "Rescisão sem justa causa, por iniciativa do empregador"),
    RESCISAO_ANTECIPADA_CONTRATO_INICIATIVA_EMPREGADOR("03", "Rescisão antecipada do contrato a termo por iniciativa do empregador"),
    RESCISAO_ANTECIPADA_CONTRATO_INICIATIVA_EMPREGADO("04", "Rescisão antecipada do contrato a termo por iniciativa do empregado"),
    RESCISAO_CULPA_RECIPROCA("05", "Rescisão por culpa recíproca"),
    RESCISAO_TERMINO_CONTRATO("06", "Rescisão por término do contrato a termo"),
    RESCISAO_CONTRATO_INICIATIVA_EMPREGADO("07", "Rescisão do contrato de trabalho por iniciativa do empregado"),
    RESCISAO_CONTRATO_INTERESSE_EMPREGADO("08", "Rescisão do contrato de trabalho por interesse do(a) empregado(a), nas hipóteses previstas nos arts. 394 e 483, § 1º, da CLT"),
    RESCISAO_OPCAO_EMPREGADO_VIRTUDE_FALECIMENTO_EMPREGADOR_INDIVIDUAL_OU_DOCUMESTICO("09", "Rescisão por opção do empregado em virtude de falecimento do empregador individual ou empregador doméstico"),
    RESCISAO_FALECIMENTO_EMPREGADO("10", "Rescisão por falecimento do empregado"),
    TRANSFERENCIA_EMPREGADO_MESMO_GRUPO_EMPRESARIAL("11", "Transferência de empregado para empresa do mesmo grupo empresarial que tenha assumido os encargos trabalhistas, sem que tenha havido rescisão do contrato de trabalho"),
    TRANSFERENCIA_EMPREGADO_EMPRESA_CONSORCIADA_PARA_CONSORCIO("12", "Transferência de empregado da empresa consorciada para o consórcio que tenha assumido os encargos trabalhistas, e vice-versa, sem que tenha havido rescisão do contrato de trabalho"),
    TRANSFERENCIA_EMPREGADO_EMPRESA_OU_CONSORCIO("13", "Transferência de empregado de empresa ou consórcio, para outra empresa ou consórcio que tenha assumido os encargos trabalhistas por motivo de sucessão (fusão, cisão ou incorporação)..."),
    RESCISAO_CONTRATO_ENCERRAMENTO_EMPRESA("14", "Rescisão do contrato de trabalho por encerramento da empresa, de seus estabelecimentos ou supressão de parte de suas atividades ou falecimento do empregador individual ou empregador doméstico ..."),
    DEMISSAO_APRENDIZ_POR_DESEMPENHO_OU_INADAPTACAO("15", "Rescisão do contrato de aprendizagem por desempenho insuficiente, inadaptação ou ausência injustificada do aprendiz à escola que implique perda do ano letivo"),
    DECLARACAO_NULIDADE_CONTRATO_POR_INFRINGENCIA("16", "Declaração de nulidade do contrato de trabalho por infringência ao inciso II do art. 37 da Constituição Federal, quando mantido o direito ao salário"),
    RESCISAO_INDIRETA_CONTRATO_TRABALHO("17", "Rescisão indireta do contrato de trabalho"),
    APOSENTADORIA_COMPULSORIA("18", "Aposentadoria Compulsória (Para todas as categorias de trabalhadores exceto 104)"),
    APOSENTADORIA_POR_IDADE("19", "Aposentadoria por idade (somente para categorias de trabalhadores 301, 302, 303, 306, 307 e 309)"),
    APOSENTADORIA_POR_IDADE_E_TEMPO_CONTRIBUICAO("20", "Aposentadoria por idade e tempo de contribuição (somente categorias 301, 302, 303, 306, 307 e 309)"),
    REFORMA_MILITAR("21", "Reforma Militar (somente para categorias de trabalhadores 307 e 314)"),
    RESERVA_MILITAR("22", "Reserva Militar (somente para categorias de trabalhadores 307 e 314)"),
    EXONERACAO("23", "Exoneração (somente para categorias de trabalhadores 301, 302, 303, 306, 307, 309, 310, 312 e 314)"),
    DEMISSAO("24", "Demissão (somente para categorias de trabalhadores 301, 302, 303, 306, 307, 309, 310, 312 e 314)"),
    VACANCIA("25", "Vacância de cargo efetivo (somente para categorias de trabalhadores 301, 307 e 314)"),
    RESCISAO_CONTRATO_PARALISACAO_TEMPORARIA_OU_DEFINITIVA_EMPRESA("26", "Rescisão do contrato de trabalho por paralisação temporária ou definitiva da empresa, estabelecimento ou parte das atividades motivada por atos de autoridade municipal, estadual ou federal"),
    RESCISAO_MOTIVO_FORCA_MAIOR("27", "Rescisão por motivo de força maior"),
    TERMINO_CESSAO_REQUISICAO("28", "Término da cessão/requisição"),
    REDISTRIBUICAO("29", "Redistribuição ou Reforma Administrativa (somente para categorias de trabalhadores 301, 303, 306,307, 309 e 314)"),
    MUDANCA_REGIME_TRABALHISTA("30", "Mudança de Regime Trabalhista"),
    REVERSAO_REINTEGRACAO("31", "Reversão de Reintegração"),
    EXTRAVIO_MILITAR("32", "Extravio de Militar (somente para categorias de trabalhadores 307 e 314)"),
    RESCISAO_ACORDO_ENTRE_PARTES("33", "Rescisão por acordo entre as partes (art. 484-A da CLT)"),
    TRANSFERENCIA_TITULARIDADE_EMPREGADO_DOMESTICO("34", "Transferência de titularidade do empregado doméstico para outro representante da mesma unidade  (somente para categorias de trabalhadores 104)"),
    EXTINCAO_CONTRATO_TRABALAHO_INTERMITENTE("35", "Extinção do contrato de trabalho intermitente"),
    MUDANCA_DE_CPF("36", "Mudança de CPF"),
    REMOCAO_EM_CASO_ALTERACAO_ORGAO_DECLARANTE("37", "Remoção, em caso de alteração do órgão declarante (somente para categorias de trabalhadores 301, 306, 307, 309 e 314)"),
    APOSENTADORIA_EXCETO_INVALIDEZ("38", "Aposentadoria, exceto por invalidez (somente para categorias de trabalhadores 101, 301, 302, 303, 306, 307, 309, 310, 312 e 314)"),
    APOSENTADORIA_SERVIDOR_ESTATUTARIO_INVALIDEZ("39", "Aposentadoria de servidor estatutário, por invalidez (somente para categorias de trabalhadores 301, 306 e 309)"),
    TERMINO_EXERCICIO_MANDATO("40", "Término de exercício de mandato (somente para categorias de trabalhadores 301, 302, 303, 306, 307, 309, 310, 312 e 314)"),
    RESCISAO_APRENDIZ_DESEMPENHO_INSUFICIENTE_INADAPTACAO("41", "Rescisão do contrato de aprendizagem por desempenho insuficiente ou inadaptação do aprendiz (somente para categorias de trabalhadores 103)"),
    RESCISAO_APRENDIZ_AUSENCIA_INJUSTIFICADA_PERDA_ANO_LETIVO("42", "Rescisão do contrato de aprendizagem por ausência injustificada do aprendiz à escola que implique perda do ano letivo (somente para categorias de trabalhadores 103)"),
    TRANSFERENCIA_EMPREGADO_EMPRESA_INAPTA_INEXISTENCIA_FATO("43", "Transferência de empregado de empresa considerada inapta por inexistência de fato"),
    AGRUPAMENTO_CONTRATUAL("44", "Agrupamento contratual"),
    EXCLUSAO_MILITAR_SERVICO_ATIVO_COM_EFEITOS_FINANCEIROS("45", "Exclusão de militar das Forças Armadas do serviço ativo, com efeitos financeiros (somente para categorias de trabalhadores 314)"),
    EXCLUSAO_MILITAR_SERVICO_ATIVO_SEM_EFEITOS_FINANCEIROS("46", "Exclusão de militar das Forças Armadas do serviço ativo, sem efeitos financeiros (somente para categorias de trabalhadores 314)");

    private String codigo;
    private String descricao;

    TipoMotivoDesligamentoESocial(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
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
    }

    @Override
    public String toString() {
        return this.codigo + " - " + this.descricao;
    }
}

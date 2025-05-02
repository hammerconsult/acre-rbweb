package br.com.webpublico.enums.rh.esocial;

/**
 * Created by William on 05/10/2018.
 */
public enum TipoAfastamentoESocial {
    ACIDENTE_DOENCA_TRABALHO("01", "Acidente/Doença do trabalho"),
    ACIDENTE_DOENCA_NAO_RELACIONADA_TRABALHO("03", "Acidente/Doença não relacionada ao trabalho"),
    AFASTAMENTO_LICENCA_SEM_REMUNERACAO("05", "Afastamento/licença prevista em regime próprio (estatuto), sem remuneração"),
    APOSENTADORIA_INVALIDEZ("06", "Aposentadoria por invalidez"),
    ACOMPANHAMENTO("07", "Acompanhamento - Licença para acompanhamento de membro da família enfermo"),
    AFASTAMENTO_CONSELHO_CURADOR_FGTS("08", "Afastamento do empregado para participar de atividade do Conselho Curador do FGTS - art. 65, §6º, Dec. 99.684/90 (Regulamento do FGTS)"),
    AFASTAMENTO_LICENCA_REGIME_PROPRIO_COM_REMUNERACAO("10", "Afastamento/licença prevista em regime próprio (estatuto), com remuneração"),
    CARCERE("11", "Cárcere"),
    CARGO_ELETIVO_COLESTISTAS("12", "Cargo Eletivo - Candidato a cargo eletivo - Lei 7.664/1988. art. 25°, parágrafo único - Celetistas em geral"),
    CARGO_ELETIVO("13", "Cargo Eletivo - Candidato a cargo eletivo - Lei Complementar 64/1990. art. 1°, inciso II, alínea “l” Servidor público, estatutário ou não, dos órgãos ou entidades da Administração..."),
    CESSAO_REQUISICAO("14", "Cessão / Requisição"),
    GOZO_FERIAS_OU_RECESSO("15", "Gozo de férias ou recesso - Afastamento temporário para o gozo de férias ou recesso"),
    LICENCA_REMUNERADA("16", "Licença remunerada - Lei, liberalidade da empresa ou Acordo/Convenção Coletiva de Trabalho"),
    LICENCA_MATERNIDADE_120("17", "Licença Maternidade - 120 dias e suas prorrogações/antecipações, inclusive para o cônjuge sobrevivente"),
    LICENCA_MATERNIDADE_121_180("18", "Licença Maternidade - 121 dias a 180 dias, Lei 11.770/2008 (Empresa Cidadã), inclusive para o cônjuge sobrevivente"),
    LICENCA_MATERNIDADE_ABORTO_NAO_CRIMINOSO("19", "Licença Maternidade - Afastamento temporário por motivo de aborto não criminoso"),
    LICENCA_MATERNIDADE("20", "Licença Maternidade - Afastamento temporário por motivo de licença-maternidade decorrente de adoção ou guarda judicial de criança, inclusive para o cônjuge sobrevivente"),
    LICENCA_NAO_REMUNERADA("21", "Licença não remunerada ou Sem Vencimento"),
    MANDATO_ELEITORAL_SEM_REMUNERACAO("22", "Mandato Eleitoral - Afastamento temporário para o exercício de mandato eleitoral, sem remuneração"),
    MANDATO_ELEITORAL_COM_REMUNERACAO("23", "Mandato Eleitoral - Afastamento temporário para o exercício de mandato eleitoral, com remuneração"),
    MANDATO_SINDICAL("24", "Mandato Sindical - Afastamento temporário para exercício de mandato sindical"),
    MULHER_VITIMA_VIOLENCIA("25", "Mulher vítima de violência - Lei 11.340/2006 - art. 9º §2o, II - Lei Maria da Penha"),
    PARTICIPACAO_CNPS("26", "Participação de empregado no Conselho Nacional de Previdência Social-CNPS (art. 3º, Lei 8.213/1991)"),
    QUALIFICACAO("27", "Qualificação - Afastamento por suspensão do contrato de acordo com o art 476-A da CLT"),
    REPRESENTANTE_SINDICAL("28", "Representante Sindical - Afastamento pelo tempo que se fizer necessário, quando, na qualidade de representante de entidade sindical, estiver participando de reunião oficial..."),
    SERVICO_MILITAR("29", "Serviço Militar - Afastamento temporário para prestar serviço militar obrigatório"),
    SUSPENSAO_DISCIPLINAR("30", "Suspensão disciplinar - CLT, art. 474"),
    SERVICO_PUBLICO("31", "Servidor Público em Disponibilidade"),
    LICENCA_MATERNINADADE_180("33", "Licença Maternidade - de 180 dias, Lei 13.301/2016"),
    INATIVIDADE_TRABALHADOR_AVULSO("34", "Inatividade do trabalhador avulso (portuário ou não portuário) por período superior a 90 dias"),
    LICENCA_MATERNIDADE_ANTECIPACAO_PRORROGACAO("35", "Licença maternidade - Antecipação e/ou prorrogação mediante atestado médico "),
    AFASTAMENTO_TEMPORARIO_MANDATO_ELETIVO("36", "Afastamento temporário de exercente de mandato eletivo para cargo em comissão"),
    SUSPENSAO_TEMPORARIA_LEI("37", "Suspensão temporária do contrato de trabalho nos termos da Lei 14.020/2020 (conversão da MP 936/2020)"),
    SUSPENSAO_TEMPORARIA_PROGRAMA("37", "Suspensão temporária do contrato de trabalho nos termos do Programa Emergencial de Manutenção do Emprego e da Renda"),
    IMPEDIMENTO_CONCORRENCIA("38", "Impedimento de concorrência à escala para trabalho avulso"),
    SUSPENSAO_PAGAMENTO_POR_NAO_RECADASTRAMENTO("39", "Suspensão de pagamento de servidor público por não recadastramento"),
    EXERCICIO_OUTRO_ORGAO_SERVIDOR_CEDIDO("40", "Exercício em outro órgão de servidor ou empregado público cedido"),
    QUALIFICACAO_AFASTAMENTO_SUSPENSAO_ART_15("41", "Qualificação - Afastamento por suspensão do contrato de acordo com o art. 15 da Lei 14.457/2022"),
    QUALIFICACAO_AFASTAMENTO_SUSPENSAO_ART_17("42", "Qualificação - Afastamento por suspensão do contrato de acordo com o art. 17 da Lei 14.457/2022");

    private String codigo;
    private String descricao;

    TipoAfastamentoESocial(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    @Override
    public String toString() {
        return this.codigo + " - " + this.descricao;
    }
}

package br.com.webpublico.enums.rh.esocial;

/**
 * Created by mateus on 18/07/17.
 */
public enum InconsistenciaRetornoQualificacaoCadastral {
    COD_CNIS_NIS("NIS inconsistente", ""),
    COD_CNIS_DN("Data de Nascimento informada diverge da existente no CNIS", ""),
    COD_CNIS_OBITO("NIS com óbito no CNIS", ""),
    COD_CNIS_CPF("CPF informado diverge do existente no CNIS", ""),
    COD_CNIS_CPF_NAO_INF("CPF não preenchido no CNIS", ""),
    COD_CPF_NAO_CONSTA("CPF informado não consta o Cadastro CPF", ""),
    COD_CPF_NULO("CPF informado NULO no Cadastro CPF", ""),
    COD_CPF_CANCELADO("CPF informado CANCELADO no Cadastro CPF", ""),
    COD_CPF_SUSPENSO("CPF informado SUSPENSO no Cadastro CPF", ""),
    COD_CPF_DN("Data de Nascimento informada diverge da existente no Cadastro CPF", ""),
    COD_CPF_NOME("NOME informado diverge do existente no Cadastro CPF", ""),
    COD_ORIENTACAO_CPF("Procurar Conveniadas da RFB: Correios, Banco do Brasil", ""),
    COD_ORIENTACAO_NIS_1("Atualizar NIS no INSS – Ligar 135 para agendar atendimento;", ""),
    COD_ORIENTACAO_NIS_2("Atualizar o Cadastro NIS da CAIXA – Utilizar Cadastro NIS Empresa pelo Conectividade Social ou uma agência da CAIXA", ""),
    COD_ORIENTACAO_NIS_3("Atualizar o Cadastro NIS em uma agência do Banco do Brasil", ""),
    COD_CPF_INV("CPF inválido", "CPF inconsistente"),
    COD_NIS_INV("NIS inválido", "NIS inconsistente"),
    COD_NOME_INV("NOME inválido", "NOME inconsistente"),
    COD_DN_INV("DN inválida", "DN inconsistente"),
    SEPARADOR("SEPARADOR INVÁLIDO", "SEPARADOR INVÁLIDO"),
    REG_DESFORMATADO("FORMATAÇÃO inválida", "FORMATAÇÃO inválida");

    private String descricaoProcessado;
    private String descricaoRejeitado;

    InconsistenciaRetornoQualificacaoCadastral(String descricaoProcessado, String descricaoRejeitado) {
        this.descricaoProcessado = descricaoProcessado;
        this.descricaoRejeitado = descricaoRejeitado;
    }

    public String getDescricaoProcessado() {
        return descricaoProcessado;
    }

    public String getDescricaoRejeitado() {
        return descricaoRejeitado;
    }
}

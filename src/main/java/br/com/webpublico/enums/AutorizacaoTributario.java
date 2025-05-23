/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.enums;

import br.com.webpublico.geradores.GrupoDiagrama;

@GrupoDiagrama(nome = "Segurança")
public enum AutorizacaoTributario {

    LANCAR_ITBI_COM_DEBITO("Permitir lançar ITBI para Cadastro Imobiliário com Débitos", false),
    IMPRIMIR_LAUDO_ITBI_NAO_PAGO("Permitir imprimir Laudo de Avaliação de ITBI não quitado", false),
    SOLICITAR_CERTIDAO_NEGATIVA_COM_DEBITO("Permitir solicitar Certidão Negativa de Débitos para cadastro ou contribuinte com Débitos", false),
    IMPRIMIR_ALVARA_LOCALIZACAO_NAO_PAGO("Permitir imprimir Alvará de Localização sem o pagamento do DAM", false),
    IMPRIMIR_ALVARA_FUNCIONAMENTO_NAO_PAGO("Permitir imprimir Alvará de Funcionamento Definitivo sem o pagamento do DAM", false),
    IMPRIMIR_ALVARA_FUNCIONAMENTO_PROVISORIO_NAO_PAGO("Permitir imprimir Alvará de Funcionamento Provisório sem o pagamento do DAM", false),
    IMPRIMIR_ALVARA_SANITARIO_NAO_PAGO("Permitir imprimir Alvará Sanitário Definitivo sem o pagamento do DAM", false),
    IMPRIMIR_ALVARA_SANITARIO_PROVISORIO_NAO_PAGO("Permitir imprimir Alvará Sanitário Provisório sem o pagamento do DAM", false),
    INFORMAR_VALOR_ISSQN_MENSAL_MENOR("Permitir informar valor da Base de Cálculo de ISSQN Mensal menor que a calculada", false),
    EMITIR_NOTA_FISCAL_AVULSA_CMC_NAO_AUTONOMO("Permitir emitir Nota Fiscal Avulsa para C.M.C. que não é autônomo", false),
    PERMITIR_CADASTRAR_NOVOS_CONTRATOS_DE_RENDAS_PATRIMONIAIS("Permitir cadastrar novos contratos de Rendas Patrimoniais", true),
    PERMITIR_CADASTRAR_NOVOS_CONTRATOS_DE_CEASA("Permitir cadastrar novos contratos de Ceasa", true),
    PERMITIR_REATIVAR_VEICULO_ESPECIAL("Permitir Reativar Veículo Especial", true),
    PERMITIR_INSERIR_VEICULO_ESPECIAL("Permitir Inserir Veículo Especial", true),
    PERMITIR_BAIXAR_VEICULO_ESPECIAL("Permitir Baixar Veículo Especial", false),
    PERMITIR_REATIVAR_MOTORISTA_ESPECIAL("Permitir Reativar Motorista  Especial", true),
    PERMITIR_INSERIR_MOTORISTA_ESPECIAL("Permitir Inserir Motorista  Especial", true),
    PERMITIR_INFORMAR_DATA_VENCIMENTO_CREDENCIAL_RBTRANS("Permitir Informar a Data de Vencimento da Credencial", true),
    PERMITIR_BAIXAR_MOTORISTA_ESPECIAL("Permitir Baixar Motorista  Especial", false),
    PERMITIR_LANCAR_VALOR_ENTRADA_MENOR_QUE_PARAMETRO("Permitir lançar o Valor de Entrada do Parcelamento menor que parâmetro", true),
    GERAR_QUALQUER_CALCULO_PERMISSAO_TRANSITO_TRANSPORTE("Permitir Gerar qualquer Cálculo para a Permissão de Trânsito Transporte", true),
    PERMITIR_CANCELAR_PARCELAMENTO("Permitir cancelar Processos de Parcelamento", true),
    PERMITIR_REATIVAR_PARCELAMENTO("Permitir reativar Processos de Parcelamento cancelados", true),
    EMISSAO_LAUDO_ITBI("Emissão de Laudo de ITBI", true),
    PERMITIR_CANCELAR_INSCRICAO_DIVIDA_ATIVA_AJUIZADA("Cancelamento de Inscrição de Débitos em Dívida Ativa Ajuizada", true),
    PERMITIR_ALTERAR_ENQUADRAMENTO_FISCAL("Permitir alterar Enquandramento Fiscal", true),
    PERMITIR_EFETIVAR_PROCESSO_BLOQUEIO_JUDICIAL("Permitir efetivar Processo de Bloqueio Judicial", true),
    PERMITIR_LANCAMENTO_DIVIDA_DIVERSA_EXERCICIOS_ANTERIORES("Permitir Lançamento de Dívidas Diversas em Exercícios Anteriores", true),
    PERMITIR_LANCAMENTO_IPTU_PAGO("Permitir Lançamento de IPTU mesmo para débitos já PAGOS", true),
    PERMITIR_EDITAR_CADASTRO_PESSOA_JURIDICA_PERFIL_TRIBUTARIO("Permitir editar cadastro de Pessoa Jurídica (perfil tributário)", true),
    PERMITIR_APROVAR_REJEITAR_CADASTRO_PESSOA_PERFIL_TRIBUTARIO("Permitir aprovar/rejeitar cadastro de Pessoa (perfil tributário)", true),
    PERMITIR_ATRIBUIR_MAIS_TECNICOS_AO_PROCESSO_LICENCIAMENTO_AMBIENTAL("Permitir atribuir mais técnicos ao processo de licenciamento ambiental", true),
    PERMITIR_CONSULTAR_BCI_APENAS_INFORMANDO_CPF_CNPJ_PELO_PORTAL("Permitir consultar BCI apenas informando CPF/CNPJ pelo portal", true),
    PERMITIR_LANCAR_REVISAO_PARECER_LICENCIAMENTO_AMBIENTAL("Permitir lançar revisão dos pareceres do licenciamento ambiental", true),
    GERENCIAR_CONTRATO_RENDAS_PATRIMONIAIS("Permitir gerenciar contrato de rendas patrimoniais", true);
    private final String descricao;
    private final Boolean liberado;

    private AutorizacaoTributario(String descricao, Boolean liberado) {
        this.descricao = descricao;
        this.liberado = liberado;
    }

    public String getDescricao() {
        return descricao;
    }

    public Boolean getLiberado() {
        return liberado;
    }

    @Override
    public String toString() {
        return descricao;
    }
}

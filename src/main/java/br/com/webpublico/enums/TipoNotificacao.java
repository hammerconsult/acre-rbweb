package br.com.webpublico.enums;

import br.com.webpublico.interfaces.EnumComDescricao;

public enum TipoNotificacao implements EnumComDescricao {
    APROVACAO_SOLICITAO_FINANCEIA("Aprovação de Solicitação Financeira", 1),
    ARRECADACAO("Arrecadação", 2),
    COMUNICACAO_SOFTPLAN("Comunicação SoftPlan", 3),
    COMUNICACAO_NFS_ELETRONICA("Comunicação Nota Fiscal Eletrônica", 4),
    CANCELAMENTO_NFS_ELETRONICA("Cancelamento de Nota Fiscal Eletrônica", 4),
    CONFIGURACAO_TRIBUTO_CONTA_RECEITA("Configuração de Conta de Receita nos Tributos", 5),
    CONFIGURACAO_CONTABIL("Configuração Contábil", 6),
    CRIACAO_CONTA_FINANCEIRA_CONVENIO_RECEITA("Criação de Conta Financeira para o Convênio de Receita", 6),
    LIBERACAO_FINANCEIA("Liberação Financeira", 7),
    LIBERACAO_ALTERACAO_ORCAMENTARIA("Liberação de Alteração Orçamentária", 8),
    LIBERAR_PERIODO_FASE("Liberar Período Fase", 9),
    IMPORTACAO_ARQUIVO_OBN600("Importação de Arquivo Bancário OB600", 10),
    RETORNO_LIBERACAO_FINANCEIA("Retorno de Liberação Financeira", 11),
    RETORNO_ALTERACAO_ORCAMENTARIA("Retorno de Alteração Orçamentária", 12),
    REPROCESSAMENTO_CONTABIL("Reprocessamento Contábil", 13),
    SOLICITACAO_ABERTURA_CONTA_FINANCEIRA_CONVENIO_RECEITA("Solicitação de Abertura de Conta Financeira para Convênio de Receita", 14),
    VENCIMENTO_CONTRATO("Aviso de Vencimento de Contratos", 15),
    PROTOCOLO("Protocolo / Processo", 16),
    COTA_ORCAMENTARIA("Cota Orçamentária", 17),
    AVISO_VEICULOS_COM_REVISAO_VENCENDO("Aviso de Veículos com Revisões Vencendo", 18),
    AVISO_EQUIPAMENTOS_COM_REVISAO_VENCENDO("Aviso de Equipamentos com Revisões Vencendo", 19),
    INTENCAO_REGISTRO_DE_PRECO("Intenção de Registro de Preço - IRP", 20),
    PROGRAMACAO_FERIAS_PORTAL("Solicitação de Programação de Férias - Portal", 21),
    FALE_CONOSCO_PORTAL_WEB("Fale Conosco", 22),
    FALE_CONOSCO_PORTAL_NFSE("Fale Conosco (Nfs-e)", 22),
    CESSAO_BENS("Cessão de Bens", 23),
    MALA_DIRETA_GERAL("Mala Direta Geral", 24),
    AVISO_NOVA_SOLICITACAO_RESERVA("Aviso de Nova Solicitação de Reserva", 25),
    ENVIO_EVENTO_ESOCIAL("Solicitação de Envio de Evento para o E-Social", 26),
    AVISO_NOVA_AVALIACAO_SOLICITACAO_VEICULO_EQUIPAMENTO("Aviso de Nova Avaliação de Solicitação de Veículo/Equipamento", 27),
    AVISO_RESERVA_VEICULO_EQUIPAMENTO_PROXIMA_PARA_GESTOR_FROTAS("Aviso de Reserva de Veículo/Equipamento Próxima para Gestor Frotas", 28),
    INTEGRACAO_REDESIM("Integração com a Rede SIM", 28),
    AVISO_RESERVA_CANCELADA_VEICULO_EQUIPAMENTO("Aviso de Reserva Cancelada", 30),
    AVISO_RESERVA_VEICULO_EQUIPAMENTO_PROXIMA_PARA_SOLICITANTE("Aviso de Reseva de Veículo/Equipamento Próxima para Solicitante", 29),
    AVISO_AGUARDANDO_APROVACAO_NOVA_OPERADORA_TECNOLOGIA_TRANSPORTE("Nova Operadora de Tecnologia de Transporte aguardando aprovação", 30),
    AVISO_AGUARDANDO_APROVACAO_NOVO_VEICULO_OPERADORA_TRANSPORTE("Novo veículo de Operadora de Transporte aguardando aprovação", 31),
    AVISO_AGUARDANDO_APROVACAO_NOVO_CONDUTOR_OPERADORA_TRANSPORTE("Novo Condutor de Operadora de Transporte aguardando aprovação", 32),
    AVISO_PRAZO_MONITORAMENTO_FISCAL_MAIOR("Prazo de Monitoramento Fiscal Maior Que o Prazo Informado no Parâmetro de Monitoramento Fiscal", 33),
    AVISO_PRAZO_RESPOSTA_MALA_DIRETA_MONITORAMENTO_FISCAL("Prazo Para Resposta da Mala Direta do Monitoramento Fiscal", 34),
    AVISO_PRAZO_REGULARIZACAO_MALA_DIRETA_MONITORAMENTO_FISCAL("Prazo Para Regularização da Mala Direta do Monitoramento Fiscal", 35),
    AVISO_ALTERACAO_CADASTRAL_PESSOA_AGUARDANDO_APROVACAO("Alteração cadastral de Pessoa aguardando aprovacao", 36),
    AVISO_DIARIA_PENDENTE_PRESTACAO_CONTAS_PRIMEIRA_NOTIFICACAO("Diária pendente de prestação de contas - Primeira Notificação", 37),
    AVISO_DIARIA_PENDENTE_PRESTACAO_CONTAS_SEGUNDA_NOTIFICACAO("Diária pendente de prestação de contas - Segunda Notificação", 38),
    AVISO_ALVARA_CONSTRUCAO_PARA_VENCER("Alvará de Construção prestes a vencer", 39),
    AVISO_ALVARA_CONSTRUCAO_VENCIDO("Alvará de Construção vencido", 40),
    AVISO_DE_DEBITOS_A_PRESCREVER("Aviso de Débitos à Prescrever", 39),
    AVISO_NOTIFICAO_COBRANCA_ADMINISTRATIVA_VENCIDA("Aviso/Notificação de Cobrança Administrativa com prazo vencido", 40),
    FALE_CONOSCO_WEB("Fale Conosco", 41),
    RECLAMACAO_NOTA_PREMIADA("Reclamações Portal da Nota Premiada", 42),
    AVISO_NOTIFICACAO_SOLICITACOES_ISENCAO_IPTU("Aviso de Solicitações de Isenção de IPTU", 44),
    INTEGRACAO_SISOBRA("Integração SisObra Pref", 41),
    RECONHECIMENTO_DIVIDA("Novo Reconhecimento de Dívida do Exercício", 46),
    CALCULO_ALVARA("Cálculo de Alvará", 47),
    AVALIACAO_SOLICITACAO_COMPRA_AGUARDANDO("Nova Avaliação de Solicitação de Compra Aguardando Avaliação", 47),
    AVALIACAO_SOLICITACAO_COMPRA_AVALIADA("Solicitação de Compra Avaliada", 48),
    RESPOSTA_QUESTIONAMENTO_NFSE("Resposta de questionamento da nota fiscal eletrônica de serviços", 78),
    SOLICITACAO_AFASTAMENTO("Solicitação de Afastamento", 49),
    DOCUMENTO_OFICIAL("Documento Oficial", 50),
    INCONSISTENCIA_CADASTRO_CALCULO_RH("Inconsistências de Cadastros ou Cálculos do RH", 51),
    SOLICITACAO_FALTAS_PONTO("Solicitação de Faltas - Ponto", 51),
    INCONSISTENCIA_PARAMETRIZACAO_LOTACOES("Inconsistências na Parametrização de Lotações", 52),
    OCORRENCIA_CADASTRO_MATERIAL("Ocorrência no Cadastro de Material", 53),
    REQUISICAO_MATERIAL("Requisição de Material", 54);


    private String descricao;

    private Integer ordem;

    public String getDescricao() {
        return descricao;
    }

    private TipoNotificacao(String descricao, Integer ordem) {
        this.descricao = descricao;
        this.ordem = ordem;
    }
}

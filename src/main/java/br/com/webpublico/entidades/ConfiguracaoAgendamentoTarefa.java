package br.com.webpublico.entidades;

import br.com.webpublico.agendamentotarefas.job.*;
import br.com.webpublico.util.anotacoes.Etiqueta;
import org.hibernate.envers.Audited;
import org.quartz.Job;

import javax.persistence.*;

@Audited
@Entity
@Table(name = "CONFIGAGENDAMENTOTAREFA")
@Etiqueta("Configuração De Agendamento de Tarefas")
public class ConfiguracaoAgendamentoTarefa extends SuperEntidade {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer hora;
    private Integer minuto;
    @Enumerated(EnumType.STRING)
    private TipoTarefaAgendada tipoTarefaAgendada;
    private String cron;

    public ConfiguracaoAgendamentoTarefa() {

    }

    @Override
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getHora() {
        return hora;
    }

    public void setHora(Integer hora) {
        this.hora = hora;
    }

    public Integer getMinuto() {
        return minuto;
    }

    public void setMinuto(Integer minuto) {
        this.minuto = minuto;
    }

    public TipoTarefaAgendada getTipoTarefaAgendada() {
        return tipoTarefaAgendada;
    }

    public void setTipoTarefaAgendada(TipoTarefaAgendada tipoTarefaAgendada) {
        this.tipoTarefaAgendada = tipoTarefaAgendada;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public static enum TipoTarefaAgendada {
        CANCELAMENTO_PROCESSO_DEBITO("Cancelamento de Processo de Débitos Vencidos", CancelamentoProcessosDebitosVencidos.class),
        CANCELAMENTO_PARCELAMENTO("Cancelamento de Parcelamento", CancelamentoParcelamentoJob.class),
        INTEGRACAO_SIT("Integração com o Sistema de Georreferenciamento", Integracao24HorasSitJob.class),
        INTEGRACAO_SIT_GERAL("Integração com o Sistema de Georreferenciamento (Geral)", IntegracaoGeralSitJob.class),
        AVISO_VENCIMENTO_CONTRATOS("Aviso de Vencimento de Contratos", AvisoVencimentoContrato.class),
        INTEGRACAO_SOFTPLAN("Integração com o Sistema de Procuradoria", IntegracaoSoftPlanJob.class),
        ENCERRAMENTO_RPS_ATRASADO("Encerramento mensal de RPS atrasado", EncerramentoMensalRPSAtrasadoJob.class),
        ARQUIVO_CDA_ATUALIAZADA_SOFTPLAN("Envio do Arquivo de CDAs atualizadas ao Sistema de Procuradoria", ArquivoCDAAtualizadaSoftPlanJob.class),
        RETIFICACAO_CDA_SOFTPLAN("Envio de Retificação de CDAs ao Sistema de Procuradoria", RetificacaoCDASoftPlanJob.class),
        TRAMITES_DE_PROCESSOS("Lançar Notificação dos Prazo dos Trâmites dos Processos", TramitesDosProcessosJob.class),
        CANCELAR_DEBITOS_ISS_FORA_MUNICIPIO("Cancelar Débitos de ISS de Fora do Município", IssForaMunicipioJob.class),
        NOTIFICACOES_DE_EVENTOS_DA_AGENDA("Notificar Usuário de eventos futuros", NotificacaoEventoAgendaJob.class),
        NOTIFICACAO_DO_FROTAS("Notificações do Frotas", NotificacaoFrotasJob.class),
        MALA_DIRETA("Notificacao Mala Direta", NotificacaoMalaDireta.class),
        CESSAO_DE_BEM("Lançar Notificação das Cessões que Expirou o Prazo", AvisoCessaoBemContrato.class),
        PORTAL_TRANSPARENCIA("Portal da Transparência",IntegracaoPortalTransparenciaJob.class),
        DESCONTO_PARCELAMENTO("Carregar desconto dos Parcelamentos", DescontoParcelamentoJob.class),
        ROTINAS_INTERNAS_CONTABIL("Rotinas Internas do Contábil", ReprocessamentoJob.class),
        ATUALIZAR_CACHE_VALORES_PARCELAS("Atualizar Cache de Valores das Parcelas", AtualizarCacheValoresParcelasJob.class),
        BLOQUEIO_DESBLOQUEIO_USUARIO("Bloqueio/Desbloqueio de Usuários", BloqueioDesbloqueioUsuarioJob.class),
        GERAR_DECLARACAO_MENSAL_SERVICO("Geração de Declaração Mensal de Serviço", GeracaoDeclaracaoMensalServicoJob.class),
        BUSCAR_EVENTO_REDESIM("Buscar novos eventos no serviço da Redesim", BuscarEventoRedeSimJob.class),
        PROCESSAR_EVENTO_REDESIM("Processar eventos da Redesim", ProcessarEventoRedeSimJob.class),
        NOTIFICACAO_DE_DIARIAS_SEM_PRESTACAO_CONTAS("Notificacao de Diárias sem Prestação de Contas", NotificacaoDiariaJob.class),
        NOTIFICACAO_DE_DEBITOS_A_PRESCREVER("Notificação de Débitos À Prescrever", NotificacaoDebitosAPrescreverJob.class),
        NOTIFICACAO_COBRANCA_ADMINISTRATIVA_PRAZO_VENCIDO("Notificação de cobrança administrativa vencida", NotificacaoAvisoCobrancaAdministrativaJob.class),
        NOTIFICACAO_ALVARA_CONSTRUCAO("Notificação do Cartaz de Alvará de Construção", NotificacaoAlvaraConstrucaoJob.class),
        MONITORAMENTO_FISCAL_IRREGULARIDADE("Lançar Irregularidade para Empresas do Monitoramento Fiscal", MonitoramentoFiscalJob.class),
        WEB_REPORT("Webreport", WebreportJob.class),
        INTEGRACAO_SISOBRA("Integração SisObra Pref", IntegracaoSisobraPref.class),
        ATUALIZAR_SITUACAO_SOLICITACAO_PROCESSO_ISENCAO_IPTU("Atualizar Situação da Solicitação de Processo de Isenção de Cálculo de IPTU", SolicitacaoProcessoIsencaoIPTUJob.class),
        ARQUIVO_BI_TRIBUTARIO("Envio dos Arquivos Tributários para o BI", ArquivoBITributarioJob.class),
        ARQUIVO_BI_DEBITOS_GERAIS_TRIBUTARIO("Envio do Arquivo Débitos Gerais Tributários para o BI", ArquivoBIDebitoGeralTributarioJob.class),
        ARQUIVO_BI_CONTABIL("Envio dos Arquivos Contábeis para o BI", ArquivoBIContabilJob.class),
        INTEGRACAO_ESOCIAL("Integrar eventos do E-Social", IntegracaoEsocialJob.class),
        ATUALIZAR_DESCRICAO_ESOCIAL("Atualizar descrições e-social", AtualizarDescricoesEsocialJob.class),
        NOTIFICAO_EMAIL_EVENTOS_PRIMEIRAFASE("Notificação por E-mail Primeira Fase E-social", NotificacaoEmailPrimeiraFaseEsocialJob.class),
        NOTIFICAO_EMAIL_EVENTOS_SEGUNDAFASE("Notificação por E-mail Segunda Fase E-social", NotificacaoEmailSegundaFaseEsocialJob.class),
        GERACAO_CUPOM_NOTA_PREMIADA("Geração de Cupom da Nota Premiada", GerarCupomNotaPremiada.class),
        NOTIFICACAO_DE_SOLICITACOES_ISENCAO_IPTU("Notificação de Solicitações de Isenção de IPTU", NotificacaoSolicitacaoIsencoesIPTU.class),
        ENVIAR_REMESSA_PROTESTO("Envio de Remessa de Protestos", RemessaProtestoJob.class),
        ATUALIZAR_SITUACAO_PARCELAS_PROTESTO("Atualizar Situações das Parcelas de Protesto", SituacaoParcelaProtestoJob.class),
        EXPORTACAO_DEBITOS_IPTU("Exportação de Débitos de IPTU", ExportacaoDebitosIPTUJob.class),
        NOTIFICACAO_DEPRECIACAO_EM_ELABORACAO("Notificação de Depreciação em Elaboração", NotificacaoDepreciacaoEmElaboracao.class),
        ATUALIZAR_VWNOTASFISCAIS("Atualizar view materialized vwnotasfiscais", AtualizarVwNotasFiscaisJob.class),
        NOTIFICACOES_DE_INCONSISTENCIAS_CADASTROS_RH("Notificar Usuário de inconsistências nos cadastros", NotificacaoInconsistenciaRHJob.class),
        INTEGRACAO_REINF("Integrar eventos do Efd-Reinf", IntegracaoReinfJob.class),
        CANCELAMENTO_NOTA_AVULSA_NAO_PAGA("Cancelamento de Notas Avulsas Não Pagas", CancelamentoNotasAvulsaNaoPagasJob.class),
        PAGAR_TERCO_DE_FERIAS_AUTOMATICAMENTE("Pagar 1/3 de Férias Automaticamente", LancamentoTercoFeriasAutJob.class),
        RECARREGAR_NOTIFICACOES("Recarregar notificações de todos usuários", RecarregarNotificacaoJob.class),
        ENVIAR_ALVARA_IMEDIATO_POR_EMAIL("Enviar alvará(s) imediato por e-mail", EnviarAlvaraImediatoPorEmailJob.class),
        NOTIFICACOES_DE_INCONSISTENCIAS_PARAMETRIZACAO_LOTACOES("Notificar Usuário de Inconsistências na Parametrização de Lotações", NotificacaoInconsistenciasParametrizacaoLotacoesJob.class),
        GERAR_ATUALIZAR_PERIODOS_AQUISITIVOS_AUTOMATICAMENTE("Gerar/Atualizar períodos aquisitivos automaticamente", PeriodoAquisitivoFLJob.class),
        REPROCESSAMENTO_ATUALIZACAO_DADOS_RB_PONTO("Reprocessamento das Atualizações de Dados do RB Ponto que falharam", ReprocessamentoAtualizacaoDadosRBPontoJob .class),
        REQUISICAO_MATERIAL_NAO_ATENDIDA("Notificação de Requisição de Material Não Atendida", RequisicaoMaterialNaoAtendidaJob.class);

        private String descricao;
        private Class<? extends Job> job;

        TipoTarefaAgendada(String descricao, Class<? extends Job> job) {
            this.descricao = descricao;
            this.job = job;
        }

        public String getDescricao() {
            return descricao;
        }

        public Class<? extends Job> getJob() {
            return job;
        }

        @Override
        public String toString() {
            return descricao;
        }
    }
}

package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.ContratoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteExclusaoContrato;
import br.com.webpublico.entidadesauxiliares.BarraProgressoItens;
import br.com.webpublico.entidadesauxiliares.ExclusaoContratoVO;
import br.com.webpublico.entidadesauxiliares.RelacionamentoContrato;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Stateless
public class ExclusaoContratoFacade extends AbstractFacade<ExclusaoContrato> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContratoFacade contratoFacade;
    @EJB
    private ExecucaoContratoFacade execucaoContratoFacade;
    @EJB
    private ExecucaoContratoEstornoFacade execucaoContratoEstornoFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;
    @EJB
    private ReprocessamentoSaldoContratoFacade reprocessamentoSaldoContratoFacade;
    @EJB
    private AlteracaoContratualFacade alteracaoContratualFacade;
    @EJB
    private EmpenhoFacade empenhoFacade;

    public ExclusaoContratoFacade() {
        super(ExclusaoContrato.class);
    }

    public ExclusaoContrato salvarRetornando(AssistenteExclusaoContrato assistente) {
        ExclusaoContrato entity = assistente.getEntity();
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(ExclusaoContrato.class, "numero"));
        }
        if (entity.isTipoExclusaoExecucaoContrato()) {
            salvarExclusaoExecucaoContrato(assistente.getExecucaoContrato(), assistente.getRelacionamentos());
            entity.setSituacao(SituacaoMovimentoAdministrativo.FINALIZADO);
        }
        if (entity.isTipoExclusaoAditivo() || entity.isTipoExclusaoApostilamento()) {
            salvarExclusaoAlteracaoContratual(assistente.getAlteracaoContratual(), assistente);
            entity.setSituacao(SituacaoMovimentoAdministrativo.FINALIZADO);
        }
        return super.salvarRetornando(entity);
    }

    private void salvarExclusaoAlteracaoContratual(AlteracaoContratual alteracaoContratual, AssistenteExclusaoContrato assistente) {
        for (RelacionamentoContrato rel : assistente.getRelacionamentos()) {
            if (rel.isExecucao()) {
                ExecucaoContrato execucaoContrato = em.find(ExecucaoContrato.class, rel.getId());
                salvarExclusaoExecucaoContrato(execucaoContrato, assistente.getRelacionamentos());
            }
        }
        alteracaoContratualFacade.remover(alteracaoContratual);
    }

    private void salvarExclusaoExecucaoContrato(ExecucaoContrato execucaoContrato, List<RelacionamentoContrato> relacionamentos) {
        for (RelacionamentoContrato rel : relacionamentos) {
            if (rel.isEstornoExecucao()) {
                ExecucaoContratoEstorno execucaoContratoEstorno = execucaoContratoEstornoFacade.recuperar(rel.getId());
                execucaoContratoEstornoFacade.remover(execucaoContratoEstorno);
            }
            if (rel.isRequisicaoCompra()) {
                RequisicaoDeCompra requisicaoCompra = em.find(RequisicaoDeCompra.class, rel.getId());
                em.remove(requisicaoCompra);
            }
            if (rel.isSolicitacaoEmpenho()) {
                SolicitacaoEmpenho solicitacaoEmpenho = em.find(SolicitacaoEmpenho.class, rel.getId());
                solicitacaoEmpenho.setEmpenho(null);
                em.remove(solicitacaoEmpenho);
            }
        }
        execucaoContrato = execucaoContratoFacade.recuperarComDependenciasReservaEmpenho(execucaoContrato.getId());
        for (ExecucaoContratoEmpenho emp : execucaoContrato.getEmpenhos()) {
            if (emp.getEmpenho() != null) {
                emp.getEmpenho().setContrato(null);
                em.merge(emp.getEmpenho());
                emp.setEmpenho(null);
            }
        }
        execucaoContratoFacade.remover(execucaoContrato);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void removerContratoDoPortalPortal(AssistenteExclusaoContrato assistente) {
        portalTransparenciaNovoFacade.removerContrato(assistente.getContrato(), assistente.getHierarquiaOrganizacional());
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 1)
    public Future<AssistenteExclusaoContrato> removerContrato(AssistenteExclusaoContrato assistente) {
        try {
            ExclusaoContrato entity = assistente.getEntity();
            Contrato contrato = assistente.getContrato();
            List<RelacionamentoContrato> relacionamentos = assistente.getRelacionamentos();

            assistente.setBarraProgressoItens(new BarraProgressoItens());
            assistente.getBarraProgressoItens().setMensagens("Removendo " + relacionamentos.size() + " relacionamentos.");
            assistente.iniciarBarraProgresso(relacionamentos.size());

            if (!entity.getSomenteRemoverPortal()) {
                executarScript(relacionamentos, assistente);
                em.remove(em.find(Contrato.class, contrato.getId()));
            }
            entity.setSituacao(SituacaoMovimentoAdministrativo.FINALIZADO);
            assistente.setEntity(salvarRetornando(entity));

            assistente.getBarraProgressoItens().setMensagens("Finalizando.");
            finalizar(assistente);
        } catch (Exception e) {
            finalizar(assistente);
            throw e;
        }
        return new AsyncResult<>(assistente);
    }

    private void finalizar(AssistenteExclusaoContrato assistente) {
        assistente.getBarraProgressoItens().finaliza();
    }


    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void executarScript(List<RelacionamentoContrato> relacionamentos, AssistenteExclusaoContrato assistente) {
        for (RelacionamentoContrato rel : relacionamentos) {
            if (rel.isPortal()) {
                ContratoPortal contratoPortal = em.find(ContratoPortal.class, rel.getId());
                em.remove(contratoPortal);

            } else if (rel.isAditivo() || rel.isApostilamento()) {
                AlteracaoContratual alteracaoContratual = em.find(AlteracaoContratual.class, rel.getId());
                em.remove(alteracaoContratual);

            } else if (rel.isEstornoExecucao()) {
                ExecucaoContratoEstorno execucaoContratoEstorno = em.find(ExecucaoContratoEstorno.class, rel.getId());
                em.remove(execucaoContratoEstorno);

            } else if (rel.isExecucao()) {
                ExecucaoContrato execucaoContrato = execucaoContratoFacade.recuperarComDependenciasReservaEmpenho(rel.getExecucaoContrato().getId());
                for (ExecucaoContratoEmpenho emp : execucaoContrato.getEmpenhos()) {
                    if (emp.getEmpenho() != null) {
                        emp.getEmpenho().setContrato(null);
                        emp.setEmpenho(null);
                    }
                }
                execucaoContratoFacade.remover(execucaoContrato);
            }
            else if (rel.isEmpenho()) {
                Empenho empenho = em.find(Empenho.class, rel.getId());
                empenho.setContrato(null);
                em.merge(empenho);
            }
            assistente.getBarraProgressoItens().setProcessados(assistente.getBarraProgressoItens().getProcessados() + 1);
        }
    }

    public void gerarSaldoReservadoPorLicitacao(RelacionamentoContrato rel, ExecucaoContrato execucaoContrato) {
        for (RelacionamentoContrato fonte : rel.getFontes()) {
            if (fonte.getGerarSaldoOrcamentario() && fonte.getFonteDespesaORC().getDespesaORC().getExercicio().equals(sistemaFacade.getExercicioCorrente())) {
                SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                    fonte.getFonteDespesaORC(),
                    OperacaoORC.RESERVADO_POR_LICITACAO,
                    TipoOperacaoORC.ESTORNO,
                    fonte.getValorMovimento(),
                    sistemaFacade.getDataOperacao(),
                    fonte.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional(),
                    fonte.getIdMovimento(),
                    "ExecucaoContratoEmpenho",
                    execucaoContrato.getContrato().getNumeroAnoTermo(),
                    execucaoContratoFacade.getHistoricoExecucaoContrato(fonte.getFonteDespesaORC(), execucaoContrato, null)
                );
                saldoFonteDespesaORCFacade.gerarSaldoOrcamentarioSemRealizarValidacao(vo);
            }
        }
    }

    public List<RelacionamentoContrato> buscarRelacionamentosContrato(ExclusaoContrato entity) {
        String sql = " " +
            " select id, " +
            "       tipo_movimento," +
            "       data_movimento," +
            "       movimento," +
            "       cor " +
            "   from (" +
            "   select pc.id as id, " +
            "      'PUBLICACAO' as tipo_movimento," +
            "       pc.datapublicacao as data_movimento," +
            "       pc.numero || '/' || exerc.ano || ' - ' || vp.nome as movimento," +
            "       'green' as cor " +
            "   from publicacaocontrato pc" +
            "   inner join contrato c on c.id = pc.contrato_id" +
            "   inner join veiculodepublicacao vp on vp.id = pc.veiculodepublicacao_id" +
            "   inner join exercicio exerc on exerc.id = c.exerciciocontrato_id" +
            "   where c.id = :idMovimento" +
            " union all" +
            "   select cp.id as id, " +
            "      'PORTAL' as tipo_movimento," +
            "       c.dataaprovacao as data_movimento," +
            "       'Id da entidade portal ' || cp.id as movimento," +
            "       'green' as cor " +
            "   from contratoportal cp" +
            "   inner join contrato c on c.id = cp.contrato_id" +
            "   where c.id = :idMovimento" +
            " union all" +
            "   select ex.id as id, " +
            "      'EXECUCAO' as tipo_movimento," +
            "       ex.datalancamento as data_movimento," +
            "       ex.numero || '/' || exerc.ano as movimento," +
            "       'green' as cor " +
            "   from  execucaocontrato ex" +
            "   inner join contrato c on c.id = ex.contrato_id" +
            "   inner join exercicio exerc on exerc.id = c.exerciciocontrato_id" +
            "   where ex.contrato_id = :idMovimento" +
            " union all" +
            "   select exest.id as id, " +
            "      'ESTORNO_EXECUCAO' as tipo_movimento," +
            "       exest.datalancamento as data_movimento," +
            "       exest.numero || '/' || exerc.ano as movimento," +
            "       'green' as cor " +
            "   from  execucaocontratoestorno exest " +
            "   inner join execucaocontrato ex on ex.id = exest.execucaocontrato_id " +
            "   inner join contrato c on c.id = ex.contrato_id" +
            "   inner join exercicio exerc on exerc.id = c.exerciciocontrato_id" +
            "   where ex.contrato_id = :idMovimento" +
            " union all " +
            "   select ad.id as id, " +
            "       'ADITIVO' as tipo_movimento," +
            "       ad.datalancamento as data_movimento," +
            "       ad.numero || '/' || extract(year from ad.datalancamento) " +
            "       || ' ' || ad.numerotermo|| '/' || ex.ano || ' - ' || ad.justificativa as movimento," +
            "       'green' as cor " +
            "   from alteracaocontratual ad " +
            "     inner join alteracaocontratualcont acc on acc.alteracaocontratual_id = ad.id   " +
            "     inner join contrato c on c.id = acc.contrato_id " +
            "   inner join exercicio ex on ex.id = ad.exercicio_id" +
            "   where c.id = :idMovimento " +
            "    and ad.tipoalteracaocontratual = :tipoAditivo " +
            " union all " +
            "   select ad.id as id, " +
            "       'APOSTILAMENTO' as tipo_movimento," +
            "       ad.datalancamento as data_movimento," +
            "       ad.numero || '/' || extract(year from ad.datalancamento) " +
            "       || ' ' || ad.numerotermo|| '/' || ex.ano || ' - ' || ad.justificativa as movimento," +
            "       'green' as cor " +
            "   from alteracaocontratual ad " +
            "     inner join alteracaocontratualcont acc on acc.alteracaocontratual_id = ad.id   " +
            "     inner join contrato c on c.id = acc.contrato_id " +
            "   inner join exercicio ex on ex.id = ad.exercicio_id" +
            "   where c.id = :idMovimento " +
            "       and ad.tipoalteracaocontratual = :tipoApostilamento " +
            " union all " +
            "   select sol.id as id, " +
            "      'SOLICITACAO_EMPENHO' as tipo_movimento," +
            "       sol.datasolicitacao as data_movimento," +
            "       sol.complementohistorico as movimento," +
            "       case when sol.empenho_id is null then 'green' else 'blue' end as cor  " +
            "   from solicitacaoempenho sol" +
            "   inner join execucaocontratoempenho exemp on exemp.solicitacaoempenho_id = sol.id " +
            "   inner join execucaocontrato ex on ex.id = exemp.execucaocontrato_id " +
            "   where ex.contrato_id = :idMovimento " +
            " union all" +
            "   select emp.id as id, " +
            "      case when emp.categoriaorcamentaria = 'NORMAL' then 'EMPENHO_NORMAL' else 'EMPENHO_RESTO' end as tipo_movimento," +
            "       emp.dataempenho as data_movimento," +
            "       emp.numero || '/' || exc.ano || ' - ' || emp.complementohistorico as movimento," +
            "       'blue' as cor " +
            "   from empenho emp" +
            "   inner join exercicio exc on exc.id = emp.exercicio_id" +
            "   where emp.contrato_id = :idMovimento" +
            " union all" +
            "   select req.id as id, " +
            "      'REQUISICAO_COMPRA' as tipo_movimento," +
            "       req.datarequisicao as data_movimento," +
            "       req.numero || '/' || exerc.ano || ' - referente a execução nº ' || ex.numero as movimento," +
            "       case when req.situacaorequisicaocompra = 'EM_ELABORACAO' then 'green' else 'red' end as cor " +
            "   from requisicaodecompra req" +
            "   inner join requisicaocompraexecucao reqex on reqex.requisicaocompra_id = req.id " +
            "   inner join execucaocontrato ex on ex.id = reqex.execucaocontrato_id" +
            "   inner join contrato c on c.id = req.contrato_id" +
            "   inner join exercicio exerc on exerc.id = c.exerciciocontrato_id" +
            "   where req.contrato_id = :idMovimento" +
            " union all" +
            "   select sol.id as id, " +
            "      'SOLICITACAO_AQUISICAO' as tipo_movimento," +
            "       sol.datasolicitacao as data_movimento," +
            "       sol.numero  || '/' || exerc.ano || ' - referente a requisição de compra nº ' || req.numero as movimento," +
            "       'red' as cor " +
            "   from solicitacaoaquisicao sol" +
            "   inner join requisicaodecompra req on req.id = sol.requisicaodecompra_id" +
            "   inner join contrato c on c.id = req.contrato_id" +
            "   inner join exercicio exerc on exerc.id = c.exerciciocontrato_id" +
            "   where c.id = :idMovimento" +
            " union all" +
            "   select aq.id as id, " +
            "      'AQUISICAO' as tipo_movimento," +
            "       aq.datadeaquisicao as data_movimento," +
            "       aq.numero  || '/' || exerc.ano || ' referente a solicitação de aquisição nº ' || sol.numero as movimento," +
            "       'red' as cor " +
            "   from aquisicao aq" +
            "   inner join solicitacaoaquisicao sol on sol.id = aq.solicitacaoaquisicao_id" +
            "   inner join requisicaodecompra req on req.id = sol.requisicaodecompra_id" +
            "   inner join contrato c on c.id = req.contrato_id" +
            "   inner join exercicio exerc on exerc.id = c.exerciciocontrato_id" +
            "   where c.id = :idMovimento" +
            " union all" +
            "   select em.id as id, " +
            "      'ENTRADA_COMPRA' as tipo_movimento," +
            "       em.dataentrada as data_movimento," +
            "       em.numero  || '/' || exerc.ano || ' referente a requisição de compra nº ' || req.numero as movimento," +
            "       'red' as cor " +
            "   from entradamaterial em " +
            "   inner join entradacompramaterial ecm on ecm.id = em.id " +
            "   inner join requisicaodecompra req on req.id = ecm.requisicaodecompra_id" +
            "   inner join contrato c on c.id = req.contrato_id" +
            "   inner join exercicio exerc on exerc.id = c.exerciciocontrato_id" +
            "   where c.id = :idMovimento" +
            " union all" +
            "   select liq.id as id, " +
            "      case when liq.categoriaorcamentaria = 'NORMAL' then 'LIQUIDACAO_NORMAL' else 'LIQUIDACAO_RESTO' end as tipo_movimento," +
            "       liq.dataliquidacao as data_movimento," +
            "       liq.numero || '/' || exc.ano || ' - ' || ' referente ao empenho nº ' || emp.numero as movimento," +
            "       'red' as cor " +
            "   from empenho emp" +
            "   inner join liquidacao liq on liq.empenho_id = emp.id" +
            "   inner join exercicio exc on exc.id = emp.exercicio_id" +
            "   inner join solicitacaoempenho sol on sol.empenho_id = emp.id" +
            "   inner join execucaocontratoempenho exemp on exemp.solicitacaoempenho_id = sol.id" +
            "   inner join execucaocontrato ex on ex.id = exemp.execucaocontrato_id" +
            "   where ex.contrato_id = :idMovimento" +
            " union all" +
            "   select pag.id as id, " +
            "      case when pag.categoriaorcamentaria = 'NORMAL' then 'PAGAMENTO_NORMAL' else 'PAGAMENTO_RESTO' end as tipo_movimento," +
            "       coalesce(pag.dataregistro, pag.datapagamento) as data_movimento," +
            "       pag.numeropagamento || '/' || exc.ano || ' - ' || ' referente a liquidação nº ' || liq.numero as movimento," +
            "       'red' as cor " +
            "   from empenho emp" +
            "   inner join liquidacao liq on liq.empenho_id = emp.id" +
            "   inner join pagamento pag on pag.liquidacao_id = liq.id" +
            "   inner join exercicio exc on exc.id = emp.exercicio_id" +
            "   inner join solicitacaoempenho sol on sol.empenho_id = emp.id" +
            "   inner join execucaocontratoempenho exemp on exemp.solicitacaoempenho_id = sol.id" +
            "   inner join execucaocontrato ex on ex.id = exemp.execucaocontrato_id" +
            "   where ex.contrato_id = :idMovimento " +
            " ) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idMovimento", entity.getIdMovimento());
        if (entity.isTipoExclusaoContrato()) {
            q.setParameter("tipoAditivo", TipoAlteracaoContratual.ADITIVO.name());
            q.setParameter("tipoApostilamento", TipoAlteracaoContratual.APOSTILAMENTO.name());
        }
        List<RelacionamentoContrato> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            RelacionamentoContrato rel = new RelacionamentoContrato();
            rel.setId(((BigDecimal) obj[0]).longValue());
            rel.setTipo(RelacionamentoContrato.TipoRelacionamento.valueOf((String) obj[1]));
            rel.setData((Date) obj[2]);
            rel.setMovimento((String) obj[3]);
            rel.setCor((String) obj[4]);
            toReturn.add(rel);
        }
        preencherFontesExecucao(toReturn);
        return toReturn;
    }

    public List<RelacionamentoContrato> buscarRelacionamentosExecucaoContrato(ExclusaoContrato entity) {
        String sql = " " +
            " select id, " +
            "       tipo_movimento," +
            "       data_movimento," +
            "       movimento," +
            "       cor " +
            "   from ( " +
            "   select exest.id as id, " +
            "      'ESTORNO_EXECUCAO' as tipo_movimento," +
            "       exest.datalancamento as data_movimento," +
            "       exest.numero || '/' || exerc.ano as movimento," +
            "       'green' as cor " +
            "   from  execucaocontratoestorno exest " +
            "   inner join execucaocontrato ex on ex.id = exest.execucaocontrato_id " +
            "   inner join contrato c on c.id = ex.contrato_id " +
            "   inner join exercicio exerc on exerc.id = c.exerciciocontrato_id " +
            "   where ex.id = :idMovimento " +
            " union all " +
            "   select ad.id as id, " +
            "       'ADITIVO' as tipo_movimento," +
            "       ad.datalancamento as data_movimento," +
            "       ad.numero || '/' || extract(year from ad.datalancamento) " +
            "       || ' ' || ad.numerotermo|| '/' || ex.ano || ' - ' || ad.justificativa as movimento," +
            "       'red' as cor " +
            "   from alteracaocontratual ad " +
            "   inner join alteracaocontratualcont acc on acc.alteracaocontratual_id = ad.id   " +
            "   inner join contrato c on c.id = acc.contrato_id " +
            "   inner join exercicio ex on ex.id = ad.exercicio_id " +
            "   left join execucaocontrato excont on excont.idorigem = ad.id " +
            "   where excont.id = :idMovimento " +
            "     and ad.tipoalteracaocontratual = :tipoAditivo " +
            " union all " +
            "   select ad.id as id, " +
            "       'APOSTILAMENTO' as tipo_movimento," +
            "       ad.datalancamento as data_movimento," +
            "       ad.numero || '/' || extract(year from ad.datalancamento) " +
            "       || ' ' || ad.numerotermo|| '/' || ex.ano || ' - ' || ad.justificativa as movimento, " +
            "       'red' as cor " +
            "   from alteracaocontratual ad " +
            "     inner join alteracaocontratualcont acc on acc.alteracaocontratual_id = ad.id   " +
            "     inner join contrato c on c.id = acc.contrato_id " +
            "   inner join exercicio ex on ex.id = ad.exercicio_id" +
            "   left join execucaocontrato excont on excont.idorigem = ad.id " +
            "   where c.id = :idMovimento " +
            "     and ad.tipoalteracaocontratual = :tipoApostilamento " +
            " union all " +
            "   select sol.id as id, " +
            "      'SOLICITACAO_EMPENHO' as tipo_movimento," +
            "       sol.datasolicitacao as data_movimento," +
            "       sol.complementohistorico as movimento," +
            "       case when sol.empenho_id is null then 'green' else 'blue' end as cor  " +
            "   from solicitacaoempenho sol " +
            "   inner join execucaocontratoempenho exemp on exemp.solicitacaoempenho_id = sol.id " +
            "   where exemp.execucaocontrato_id = :idMovimento " +
            " union all" +
            "   select emp.id as id, " +
            "      case when emp.categoriaorcamentaria = 'NORMAL' then 'EMPENHO_NORMAL' else 'EMPENHO_RESTO' end as tipo_movimento," +
            "       emp.dataempenho as data_movimento," +
            "       emp.numero || '/' || exc.ano || ' - ' || emp.complementohistorico as movimento," +
            "       'blue' as cor " +
            "   from empenho emp " +
            "   inner join exercicio exc on exc.id = emp.exercicio_id " +
            "   inner join execucaocontratoempenho exemp on exemp.empenho_id = emp.id " +
            "   where exemp.execucaocontrato_id = :idMovimento " +
            " union all " +
            "   select distinct req.id as id, " +
            "      'REQUISICAO_COMPRA' as tipo_movimento," +
            "       req.datarequisicao as data_movimento," +
            "       req.numero || '/' || exerc.ano || ' - referente a execução nº ' || ex.numero as movimento," +
            "       case when req.situacaorequisicaocompra = 'EM_ELABORACAO' then 'green' else 'red' end as cor " +
            "   from requisicaodecompra req " +
            "   inner join requisicaocompraexecucao reqex on reqex.requisicaocompra_id = req.id " +
            "   inner join execucaocontrato ex on ex.id = reqex.execucaocontrato_id " +
            "   inner join contrato c on c.id = req.contrato_id " +
            "   inner join exercicio exerc on exerc.id = c.exerciciocontrato_id" +
            "   where reqex.execucaocontrato_id = :idMovimento " +
            " union all " +
            "   select distinct aq.id as id, " +
            "      'AQUISICAO' as tipo_movimento," +
            "       aq.datadeaquisicao as data_movimento," +
            "       aq.numero  || '/' || exerc.ano || ' referente a solicitação de aquisição nº ' || sol.numero as movimento," +
            "       'red' as cor " +
            "   from aquisicao aq " +
            "   inner join solicitacaoaquisicao sol on sol.id = aq.solicitacaoaquisicao_id " +
            "   inner join requisicaodecompra req on req.id = sol.requisicaodecompra_id " +
            "   inner join requisicaocompraexecucao reqex on reqex.requisicaocompra_id = req.id " +
            "   inner join contrato c on c.id = req.contrato_id " +
            "   inner join exercicio exerc on exerc.id = c.exerciciocontrato_id " +
            "   where reqex.execucaocontrato_id = :idMovimento " +
            " union all" +
            "   select em.id as id, " +
            "      'ENTRADA_COMPRA' as tipo_movimento," +
            "       em.dataentrada as data_movimento," +
            "       em.numero  || '/' || exerc.ano || ' referente a requisição de compra nº ' || req.numero as movimento," +
            "       'red' as cor " +
            "   from entradamaterial em " +
            "   inner join entradacompramaterial ecm on ecm.id = em.id " +
            "   inner join requisicaodecompra req on req.id = ecm.requisicaodecompra_id" +
            "   inner join requisicaocompraexecucao reqex on reqex.requisicaocompra_id = req.id " +
            "   inner join contrato c on c.id = req.contrato_id" +
            "   inner join exercicio exerc on exerc.id = c.exerciciocontrato_id" +
            "   where reqex.execucaocontrato_id = :idMovimento " +
            " union all " +
            "   select liq.id as id, " +
            "      case when liq.categoriaorcamentaria = 'NORMAL' then 'LIQUIDACAO_NORMAL' else 'LIQUIDACAO_RESTO' end as tipo_movimento," +
            "       liq.dataliquidacao as data_movimento," +
            "       liq.numero || '/' || exc.ano || ' - ' || ' referente ao empenho nº ' || emp.numero as movimento," +
            "       'red' as cor " +
            "   from empenho emp" +
            "   inner join liquidacao liq on liq.empenho_id = emp.id " +
            "   inner join exercicio exc on exc.id = emp.exercicio_id " +
            "   inner join execucaocontratoempenho exemp on exemp.empenho_id = emp.id " +
            "   where exemp.execucaocontrato_id = :idMovimento" +
            " union all" +
            "   select pag.id as id, " +
            "      case when pag.categoriaorcamentaria = 'NORMAL' then 'PAGAMENTO_NORMAL' else 'PAGAMENTO_RESTO' end as tipo_movimento," +
            "       coalesce(pag.dataregistro, pag.datapagamento) as data_movimento," +
            "       pag.numeropagamento || '/' || exc.ano || ' - ' || ' referente a liquidação nº ' || liq.numero as movimento," +
            "       'red' as cor " +
            "   from empenho emp " +
            "   inner join liquidacao liq on liq.empenho_id = emp.id " +
            "   inner join pagamento pag on pag.liquidacao_id = liq.id " +
            "   inner join exercicio exc on exc.id = emp.exercicio_id " +
            "   inner join execucaocontratoempenho exemp on exemp.empenho_id = emp.id " +
            "   where exemp.execucaocontrato_id = :idMovimento " +
            " ) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idMovimento", entity.getIdMovimento());
        q.setParameter("tipoAditivo", TipoAlteracaoContratual.ADITIVO.name());
        q.setParameter("tipoApostilamento", TipoAlteracaoContratual.APOSTILAMENTO.name());
        List<RelacionamentoContrato> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            RelacionamentoContrato rel = new RelacionamentoContrato();
            rel.setId(((BigDecimal) obj[0]).longValue());
            rel.setTipo(RelacionamentoContrato.TipoRelacionamento.valueOf((String) obj[1]));
            rel.setData((Date) obj[2]);
            rel.setMovimento((String) obj[3]);
            rel.setCor((String) obj[4]);
            toReturn.add(rel);
        }
        preencherFontesExecucao(toReturn);
        return toReturn;
    }

    public List<RelacionamentoContrato> buscarRelacionamentosAlteracaoContratual(ExclusaoContrato entity) {
        String sql = " " +
            " select id, " +
            "       tipo_movimento," +
            "       data_movimento," +
            "       movimento," +
            "       cor " +
            "   from ( " +
            "   select pc.id as id, " +
            "      'PUBLICACAO' as tipo_movimento," +
            "       pc.datapublicacao as data_movimento," +
            "       pc.numero || '/' || exerc.ano || ' - ' || vp.nome as movimento," +
            "       'green' as cor " +
            "   from publicacaoalteracaocont pc " +
            "   inner join alteracaocontratual ac on ac.id = pc.alteracaocontratual_id " +
            "   inner join veiculodepublicacao vp on vp.id = pc.veiculodepublicacao_id " +
            "   inner join exercicio exerc on exerc.id = ac.exercicio_id " +
            "   where ac.id = :idMovimento " +
            " union all " +
            "   select ex.id as id, " +
            "      'EXECUCAO' as tipo_movimento," +
            "       ex.datalancamento as data_movimento," +
            "       ex.numero || '/' || exerc.ano as movimento," +
            "       'green' as cor " +
            "   from execucaocontrato ex" +
            "   inner join contrato c on c.id = ex.contrato_id" +
            "   inner join exercicio exerc on exerc.id = c.exerciciocontrato_id" +
            "   where ex.idorigem = :idMovimento" +
            " union all " +
            "   select exest.id as id, " +
            "      'ESTORNO_EXECUCAO' as tipo_movimento," +
            "       exest.datalancamento as data_movimento," +
            "       exest.numero || '/' || exerc.ano as movimento," +
            "       'green' as cor " +
            "   from execucaocontratoestorno exest " +
            "   inner join execucaocontrato ex on ex.id = exest.execucaocontrato_id " +
            "   inner join contrato c on c.id = ex.contrato_id" +
            "   inner join exercicio exerc on exerc.id = c.exerciciocontrato_id" +
            "   where ex.idorigem = :idMovimento" +
            " union all " +
            "   select sol.id as id, " +
            "      'SOLICITACAO_EMPENHO' as tipo_movimento," +
            "       sol.datasolicitacao as data_movimento," +
            "       sol.complementohistorico as movimento," +
            "       case when sol.empenho_id is null then 'green' else 'blue' end as cor  " +
            "   from solicitacaoempenho sol" +
            "   inner join execucaocontratoempenho exemp on exemp.solicitacaoempenho_id = sol.id " +
            "   inner join execucaocontrato ex on ex.id = exemp.execucaocontrato_id " +
            "   where ex.idorigem = :idMovimento " +
            " union all" +
            "   select emp.id as id, " +
            "      case when emp.categoriaorcamentaria = 'NORMAL' then 'EMPENHO_NORMAL' else 'EMPENHO_RESTO' end as tipo_movimento," +
            "       emp.dataempenho as data_movimento," +
            "       emp.numero || '/' || exc.ano || ' - ' || emp.complementohistorico as movimento," +
            "       'blue' as cor " +
            "   from empenho emp " +
            "   inner join exercicio exc on exc.id = emp.exercicio_id " +
            "   inner join execucaocontratoempenho exemp on exemp.empenho_id = emp.id " +
            "   inner join execucaocontrato ex on ex.id = exemp.execucaocontrato_id " +
            "   where ex.idorigem = :idMovimento" +
            " union all" +
            "   select req.id as id, " +
            "      'REQUISICAO_COMPRA' as tipo_movimento," +
            "       req.datarequisicao as data_movimento," +
            "       req.numero || '/' || exerc.ano || ' - referente a execução nº ' || ex.numero as movimento," +
            "       case when req.situacaorequisicaocompra = 'EM_ELABORACAO' then 'green' else 'red' end as cor " +
            "   from requisicaodecompra req " +
            "   inner join requisicaocompraexecucao reqex on reqex.requisicaocompra_id = req.id " +
            "   inner join execucaocontrato ex on ex.id = reqex.execucaocontrato_id " +
            "   inner join contrato c on c.id = req.contrato_id " +
            "   inner join exercicio exerc on exerc.id = c.exerciciocontrato_id " +
            "   where ex.idorigem = :idMovimento" +
            " ) ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idMovimento", entity.getIdMovimento());
        if (entity.isTipoExclusaoContrato()) {
            q.setParameter("tipoAditivo", TipoAlteracaoContratual.ADITIVO.name());
            q.setParameter("tipoApostilamento", TipoAlteracaoContratual.APOSTILAMENTO.name());
        }
        List<RelacionamentoContrato> toReturn = Lists.newArrayList();
        for (Object[] obj : (List<Object[]>) q.getResultList()) {
            RelacionamentoContrato rel = new RelacionamentoContrato();
            rel.setId(((BigDecimal) obj[0]).longValue());
            rel.setTipo(RelacionamentoContrato.TipoRelacionamento.valueOf((String) obj[1]));
            rel.setData((Date) obj[2]);
            rel.setMovimento((String) obj[3]);
            rel.setCor((String) obj[4]);
            toReturn.add(rel);
        }
        preencherFontesExecucao(toReturn);
        return toReturn;
    }

    public void preencherFontesExecucao(List<RelacionamentoContrato> toReturn) {
        for (RelacionamentoContrato rel : toReturn) {
            if (rel.isExecucao()) {
                ExecucaoContrato execucaoContrato = execucaoContratoFacade.recuperarComDependencias(rel.getId());
                rel.setExecucaoContrato(execucaoContrato);

                for (ExecucaoContratoEmpenho exEmp : execucaoContrato.getEmpenhos()) {
                    if (exEmp.getEmpenho() == null) {
                        RelacionamentoContrato relFonte = new RelacionamentoContrato();
                        relFonte.setFonteDespesaORC(exEmp.getSolicitacaoEmpenho().getFonteDespesaORC());
                        relFonte.setValorMovimento(exEmp.getSolicitacaoEmpenho().getValor());

                        SaldoFonteDespesaORC saldoFonteDespesaORC = saldoFonteDespesaORCFacade.recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(
                            relFonte.getFonteDespesaORC(),
                            sistemaFacade.getDataOperacao(),
                            relFonte.getFonteDespesaORC().getDespesaORC().getProvisaoPPADespesa().getUnidadeOrganizacional());

                        relFonte.setValorColunaReservado(saldoFonteDespesaORC.getReservadoPorLicitacao());
                        relFonte.setSaldoColunaReservado(relFonte.getValorColunaReservado().subtract(relFonte.getValorMovimento()));

                        relFonte.setGerarSaldoOrcamentario(true);
                        relFonte.setIdMovimento(exEmp.getId().toString());
                        rel.getFontes().add(relFonte);
                    }
                }
            }
        }
    }

    public List<ExclusaoContratoVO> buscarExclusaoContrato(Contrato contrato) {
        String sql = "" +
            "  select distinct exc.id, " +
            "              exc.numero, " +
            "              exc.dataexclusao, " +
            "              exc.tipoexclusao, " +
            "              pf.nome, " +
            "              DBMS_LOB.SUBSTR(exc.historico, 4000, 1) as historico " +
            " from exclusaocontrato exc " +
            "   inner join usuariosistema usu on usu.id = exc.usuariosistema_id " +
            "   inner join pessoafisica pf on pf.id = usu.pessoafisica_id " +
            "   left join alteracaocontratual_aud ac on ac.id = exc.idmovimento " +
            "   left join alteracaocontratualcont_aud acc on ac.id = acc.alteracaocontratual_id " +
            "   left join execucaocontrato_aud ex on ex.id = exc.idmovimento " +
            "   inner join contrato c on c.id = coalesce(acc.contrato_id, ex.contrato_id) " +
            " where c.id = :idContrato " +
            "  and exc.tipoexclusao <> :tipoContrato " +
            " order by exc.numero ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idContrato", contrato.getId());
        q.setParameter("tipoContrato", TipoExclusaoContrato.CONTRATO.name());
        List<Object[]> resultList = q.getResultList();
        List<ExclusaoContratoVO> list = Lists.newArrayList();
        resultList.forEach(obj -> {
            ExclusaoContratoVO ex = new ExclusaoContratoVO();
            ex.setId(((BigDecimal) obj[0]).longValue());
            ex.setNumero(((BigDecimal) obj[1]).longValue());
            ex.setDataExclusao((Date) obj[2]);
            ex.setTipoExclusao(TipoExclusaoContrato.valueOf((String) obj[3]));
            ex.setUsuario((String) obj[4]);
            ex.setHistorico((String) obj[5]);
            list.add(ex);
        });
        return list;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ContratoFacade getContratoFacade() {
        return contratoFacade;
    }

    public AlteracaoContratualFacade getAlteracaoContratualFacade() {
        return alteracaoContratualFacade;
    }

    public ExecucaoContratoFacade getExecucaoContratoFacade() {
        return execucaoContratoFacade;
    }

    public EmpenhoFacade getEmpenhoFacade() {
        return empenhoFacade;
    }

    public ReprocessamentoSaldoContratoFacade getReprocessamentoSaldoContratoFacade() {
        return reprocessamentoSaldoContratoFacade;
    }
}

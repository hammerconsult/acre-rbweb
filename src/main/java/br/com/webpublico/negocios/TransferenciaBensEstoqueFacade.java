/*
 * Codigo gerado automaticamente em Wed Sep 12 14:06:38 GMT-03:00 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.financeiro.SuperEntidadeContabilFinanceira;
import br.com.webpublico.enums.TagValor;
import br.com.webpublico.enums.TipoEstoque;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class TransferenciaBensEstoqueFacade extends SuperFacadeContabil<TransferenciaBensEstoque> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private GrupoMaterialFacade grupoMaterialFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ConfigTransfEstoqueFacade configTransfEstoqueFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SaldoGrupoMaterialFacade saldoGrupoMaterialFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TransferenciaBensEstoqueFacade() {
        super(TransferenciaBensEstoque.class);
    }


    public String getUltimoNumero() {
        String sql = "SELECT MAX(TO_NUMBER(TRANSF.NUMERO)) +1 AS ULTIMONUMERO FROM TRANSFBENSESTOQUE TRANSF";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    @Override
    public void salvar(TransferenciaBensEstoque entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    @Override
    public void salvarNovo(TransferenciaBensEstoque entity) {
        salvarTransferencia(entity);
    }

    public TransferenciaBensEstoque salvarTransferencia(TransferenciaBensEstoque entity) {
        try {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                if (entity.getId() == null) {
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroTransferenciaBensEstoque(entity.getExercicio()));
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                gerarSaldoTransferenciaOrigem(entity);
                gerarSaldoTransferenciaDestino(entity);

                contabilizarTransferenciaOrigem(entity);
                contabilizarTransferenciaDestino(entity);
            }
            return entity;
        } catch (ExcecaoNegocioGenerica ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void buscarEventoDestino(TransferenciaBensEstoque entity) {
        try {
            if (entity.getOperacaoDestino() != null
                && entity.getTipoLancamento() != null) {
                ConfigTransfEstoque configuracao = configTransfEstoqueFacade.recuperarConfiguracaoEvento(
                    entity.getTipoLancamento(),
                    entity.getOperacaoDestino(),
                    entity.getDataTransferencia());
                entity.setEventoContabilDestino(configuracao.getEventoContabil());
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void buscarEventoOrigem(TransferenciaBensEstoque entity) {
        try {
            if (entity.getOperacaoOrigem() != null
                && entity.getTipoLancamento() != null) {
                ConfigTransfEstoque configuracao = configTransfEstoqueFacade.recuperarConfiguracaoEvento(
                    entity.getTipoLancamento(),
                    entity.getOperacaoOrigem(),
                    entity.getDataTransferencia());
                entity.setEventoContabilOrigem(configuracao.getEventoContabil());
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void gerarSaldoTransferenciaOrigem(TransferenciaBensEstoque entity) {

        saldoGrupoMaterialFacade.geraSaldoGrupoMaterial(
            entity.getUnidadeOrgOrigem(),
            entity.getGrupoMaterial(),
            entity.getValor(),
            entity.getTipoEstoqueOrigem(),
            entity.getDataTransferencia(),
            entity.getOperacaoOrigem(),
            entity.getTipoLancamento(),
            TipoOperacao.CREDITO,
            entity.getId(),
            true);

        saldoGrupoMaterialFacade.geraSaldoGrupoMaterial(
            entity.getUnidadeOrgOrigem(),
            entity.getGrupoMaterial(),
            entity.getValor(),
            entity.getTipoEstoqueOrigem(),
            entity.getDataTransferencia(),
            entity.getOperacaoOrigem(),
            entity.getTipoLancamento(),
            TipoOperacao.DEBITO,
            entity.getId(),
            true);
    }

    private void gerarSaldoTransferenciaDestino(TransferenciaBensEstoque entity) {

        saldoGrupoMaterialFacade.geraSaldoGrupoMaterial(
            entity.getUnidadeOrgDestino(),
            entity.getGrupoMaterialDestino(),
            entity.getValor(),
            entity.getTipoEstoqueDestino(),
            entity.getDataTransferencia(),
            entity.getOperacaoDestino(),
            entity.getTipoLancamento(),
            TipoOperacao.CREDITO,
            entity.getId(),
            true);

        saldoGrupoMaterialFacade.geraSaldoGrupoMaterial(
            entity.getUnidadeOrgDestino(),
            entity.getGrupoMaterialDestino(),
            entity.getValor(),
            entity.getTipoEstoqueDestino(),
            entity.getDataTransferencia(),
            entity.getOperacaoDestino(),
            entity.getTipoLancamento(),
            TipoOperacao.DEBITO,
            entity.getId(),
            true);
    }

    public void contabilizarTransferenciaOrigem(TransferenciaBensEstoque entity) throws ExcecaoNegocioGenerica {
        ConfigTransfEstoque configuracao = configTransfEstoqueFacade.recuperarConfiguracaoEvento(entity.getTipoLancamento(), entity.getOperacaoOrigem(), entity.getDataTransferencia());

        if (configuracao != null && configuracao.getEventoContabil() != null) {
            entity.setEventoContabilOrigem(configuracao.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Transferência de Bens de Estoque. ");
        }

        if (configuracao != null && configuracao.getId() != null) {
            entity.gerarHistoricos();

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataTransferencia());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrgOrigem());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.CONCEDIDO);
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setEventoContabil(configuracao.getEventoContabil());
            parametroEvento.setExercicio(entity.getExercicio());

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getId().toString(), TransferenciaBensEstoque.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getGrupoMaterial().getId().toString(), GrupoMaterial.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getTipoEstoqueOrigem().toString(), TipoEstoque.class.getSimpleName(), item));

            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não existe configuração para a Transferência de Bens de Estoque.");
        }
    }

    public void contabilizarTransferenciaDestino(TransferenciaBensEstoque entity) throws ExcecaoNegocioGenerica {
        ConfigTransfEstoque configuracao = configTransfEstoqueFacade.recuperarConfiguracaoEvento(entity.getTipoLancamento(), entity.getOperacaoDestino(), entity.getDataTransferencia());

        if (configuracao != null && configuracao.getEventoContabil() != null) {
            entity.setEventoContabilDestino(configuracao.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Transferência de Bens de Estoque. ");
        }

        if (configuracao != null && configuracao.getId() != null) {
            entity.gerarHistoricos();

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setDataEvento(entity.getDataTransferencia());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrgDestino());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.RECEBIDO);
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setEventoContabil(configuracao.getEventoContabil());
            parametroEvento.setExercicio(entity.getExercicio());

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getId().toString(), TransferenciaBensEstoque.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getGrupoMaterialDestino().getId().toString(), GrupoMaterial.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getTipoEstoqueDestino().toString(), TipoEstoque.class.getSimpleName(), item));

            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não existe configuração para a Transferência de Bens de Estoque.");
        }
    }

    public GrupoMaterialFacade getGrupoMaterialFacade() {
        return grupoMaterialFacade;
    }

    public ContabilizacaoFacade getContabilizacaoFacade() {
        return contabilizacaoFacade;
    }

    public ConfigTransfEstoqueFacade getConfigTransfEstoqueFacade() {
        return configTransfEstoqueFacade;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        ParametroEvento.ComplementoId complementoId = ((SuperEntidadeContabilFinanceira) entidadeContabil).getComplementoId();
        if (ParametroEvento.ComplementoId.NORMAL.equals(complementoId)
            || ParametroEvento.ComplementoId.CONCEDIDO.equals(complementoId)) {
            contabilizarTransferenciaOrigem((TransferenciaBensEstoque) entidadeContabil);
        }
        if (ParametroEvento.ComplementoId.NORMAL.equals(complementoId)
            || ParametroEvento.ComplementoId.RECEBIDO.equals(complementoId)) {
            contabilizarTransferenciaDestino((TransferenciaBensEstoque) entidadeContabil);
        }
    }


    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(TransferenciaBensEstoque.class, filtros);
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataTransferencia)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataTransferencia)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrgOrigem_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrgOrigem_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabilOrigem_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.TIPO_ESTOQUE, "obj.tipoEstoqueOrigem");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.TIPO_OPERACAO_ESTOQUE, "obj.operacaoOrigem");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.GRUPO_MATERIAL, "obj.grupoMaterial_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicorazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        return Arrays.asList(consulta);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public List<TransferenciaBensEstoque> recuperarObjetosReprocessamento(EventosReprocessar er, Boolean isReprocessamentoInicial, ParametroEvento.ComplementoId complementoId) {

        String sql = "select trans.* from TRANSFBENSESTOQUE trans" +
            " where trans.DATATRANSFERENCIA between to_date(:di,'dd/mm/yyyy') and to_date(:df,'dd/mm/yyyy')";
        if (er.hasUnidades() && complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
            sql = sql + " and trans.UNIDADEORGORIGEM_ID in (:unidades) ";
        }
        if (er.hasUnidades() && complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
            sql = sql + " and trans.UNIDADEORGDESTINO_ID in (:unidades) ";
        }
        if (!isReprocessamentoInicial && complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
            sql += " and trans.eventoContabilOrigem_ID = :evento";
        }
        if (!isReprocessamentoInicial && complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
            sql += " and trans.eventoContabilDestino_ID = :evento";
        }

        sql += " order by trans.DATATRANSFERENCIA asc";

        Query q = em.createNativeQuery(sql, TransferenciaBensEstoque.class);
        try {
            q.setParameter("di", DataUtil.getDataFormatada(er.getDataInicial()));
            q.setParameter("df", DataUtil.getDataFormatada(er.getDataFinal()));
            if (er.hasUnidades()) {
                q.setParameter("unidades", er.getIdUnidades());
            }
            if (!isReprocessamentoInicial) {
                q.setParameter("evento", er.getEventoContabil());
            }
            List<TransferenciaBensEstoque> retorno = q.getResultList();
            for (TransferenciaBensEstoque entidade : retorno) {
                entidade.setComplementoId(complementoId);
            }
            return retorno;
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }
}

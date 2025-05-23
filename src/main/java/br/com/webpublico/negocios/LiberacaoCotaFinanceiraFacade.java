/*
 * Codigo gerado automaticamente em Fri Mar 09 08:07:27 BRT 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.PortalTransparenciaNovoFacade;
import br.com.webpublico.controle.portaltransparencia.entidades.LiberacaoCotaFinanceiraPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.financeiro.SuperEntidadeContabilFinanceira;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.seguranca.NotificacaoService;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class LiberacaoCotaFinanceiraFacade extends SuperFacadeContabil<LiberacaoCotaFinanceira> {

    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private SolicitacaoCotaFinanceiraFacade solicitacaoCotaFinanceiraFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private PagamentoExtraFacade pagamentoExtraFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ConfigLiberacaoFinanceiraFacade configLiberacaoFinanceiraFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private FinalidadePagamentoFacade finalidadePagamentoFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private PortalTransparenciaNovoFacade portalTransparenciaNovoFacade;

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public LiberacaoCotaFinanceiraFacade() {
        super(LiberacaoCotaFinanceira.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public void salvarNovo(LiberacaoCotaFinanceira entity) {
        try {
            if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
                throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataLiberacao());
                validarBloqueio(entity);
                validarSolicitacao(entity);
                singletonConcorrenciaContabil.bloquear(entity.getSolicitacaoCotaFinanceira());
                movimentarSolicitacaoFinanceira(entity);
                movimentarLiberacaoFinanceira(entity);
                if (entity.getId() == null) {
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroLiberacaoFinanciera(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataLiberacao()));
                    entity = salvarGerandoHistorico(entity);
                } else {
                    entity = salvarGerandoHistorico(entity);
                }
                lancarSaldoContaFinanceira(entity);
                contabilizar(entity);
                singletonConcorrenciaContabil.desbloquear(entity.getSolicitacaoCotaFinanceira());
                portalTransparenciaNovoFacade.salvarPortal(new LiberacaoCotaFinanceiraPortal(entity));
            }
        } catch (ValidacaoException ve) {
            throw ve;
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    public void movimentarNotificacoes(LiberacaoCotaFinanceira entity, Notificacao notificacaoRecuperada) {
        solicitacaoCotaFinanceiraFacade.criarNotificacaoSolicitacaoLiberada(entity.getSolicitacaoCotaFinanceira());
        marcarNotificacoesComoLidas(entity, notificacaoRecuperada);
    }

    private void validarSolicitacao(LiberacaoCotaFinanceira entity) {
        ValidacaoException ve = new ValidacaoException();
        if (!solicitacaoCotaFinanceiraFacade.isVersaoAtual(entity.getSolicitacaoCotaFinanceira())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Solicitação Financeira selecionada foi alterada por outro usuário, atualize a tela e tente novamente.");
        }
        ve.lancarException();
    }


    private LiberacaoCotaFinanceira salvarGerandoHistorico(LiberacaoCotaFinanceira entity) {
        entity.gerarHistoricos();
        return em.merge(entity);
    }

    @Override
    public void salvar(LiberacaoCotaFinanceira entity) {
        salvarGerandoHistorico(entity);
    }

    private void validarBloqueio(LiberacaoCotaFinanceira entity) {
        ValidacaoException ve = new ValidacaoException();
        if (!singletonConcorrenciaContabil.isDisponivel(entity.getSolicitacaoCotaFinanceira())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A solicitação financeira: " + entity.getSolicitacaoCotaFinanceira() + " está sendo utilizado por outro usuário. Caso o problema persistir, selecione novamente a Solicitação Financeira.");
        }
        ve.lancarException();
    }


    private void lancarSaldoContaFinanceira(LiberacaoCotaFinanceira entity) {
        lancarSaldoContaFinanceiraConcedida(entity);
        lancarSaldoContaFinanceiraRecebida(entity);
    }

    private void contabilizar(LiberacaoCotaFinanceira entidadeContabil) {
        ParametroEvento.ComplementoId complementoId = ((SuperEntidadeContabilFinanceira) entidadeContabil).getComplementoId();
        if (complementoId.equals(ParametroEvento.ComplementoId.NORMAL)
            || complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
            contabilizarLiberacaoConcedida((LiberacaoCotaFinanceira) entidadeContabil);
        }
        if (complementoId.equals(ParametroEvento.ComplementoId.NORMAL)
            || complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
            contabilizarLiberacaoRecebido((LiberacaoCotaFinanceira) entidadeContabil);
        }
    }

    private void lancarSaldoContaFinanceiraRecebida(LiberacaoCotaFinanceira entity) {
        saldoSubContaFacade.gerarSaldoFinanceiro(entity.getSolicitacaoCotaFinanceira().getUnidadeOrganizacional(),
            entity.getSolicitacaoCotaFinanceira().getContaFinanceira(),
            entity.getSolicitacaoCotaFinanceira().getContaDeDestinacao(),
            entity.getValor(),
            TipoOperacao.CREDITO,
            entity.getDataLiberacao(),
            entity.getEventoContabilDeposito(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.LIBERACAO_DE_COTA_FINANCEIRA,
            entity.getUuid(),
            true);
    }


    private void lancarSaldoContaFinanceiraConcedida(LiberacaoCotaFinanceira entity) {
        saldoSubContaFacade.gerarSaldoFinanceiro(recuperaUnidadeDaSubContaRetirada(entity, entity.getExercicio()),
            entity.getSolicitacaoCotaFinanceira().getContaFinanceira().getContaVinculada(),
            entity.getContaDeDestinacao(),
            entity.getValor(),
            TipoOperacao.DEBITO,
            entity.getDataLiberacao(),
            entity.getEventoContabilRetirada(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.LIBERACAO_DE_COTA_FINANCEIRA,
            entity.getUuid(),
            true);
    }

    private void marcarNotificacoesComoLidas(LiberacaoCotaFinanceira entity, Notificacao notificacaoRecuperada) {
        String link = "/liberacao-financeira/nova-solicitacao/" + entity.getSolicitacaoCotaFinanceira().getId() + "/notificacao/";
        if (notificacaoRecuperada != null) {
            NotificacaoService.getService().marcarComoLida(notificacaoRecuperada);
        } else {
//            NotificacaoService.getService().marcarComoLida(NotificacaoService.getService().buscarNotificacoesPorLink(link));
        }
    }

    private void movimentarLiberacaoFinanceira(LiberacaoCotaFinanceira entity) {
        if (isMesmaContaBancaria(entity.getContaFinanceiraRecebida().getContaBancariaEntidade(), entity.getContaFinanceiraRetirada().getContaBancariaEntidade())) {
            entity.setStatusPagamento(StatusPagamento.PAGO);
            entity.setSaldo(BigDecimal.ZERO);
        } else {
            entity.setStatusPagamento(StatusPagamento.DEFERIDO);
            entity.setSaldo(entity.getValor());
        }
    }

    private void movimentarSolicitacaoFinanceira(LiberacaoCotaFinanceira entity) {
        SolicitacaoCotaFinanceira solicitacao = solicitacaoCotaFinanceiraFacade.recuperar(entity.getSolicitacaoCotaFinanceira().getId());
        solicitacao.setValorLiberado(solicitacao.getValorLiberado().add(entity.getValor()));
        solicitacao.setHistoricoLiberacao(entity.getObservacoes());
        solicitacao.setSaldo(solicitacao.getSaldo().subtract(entity.getValor()));
        if (solicitacao.getSaldoAprovar().compareTo(BigDecimal.ZERO) == 0) {
            solicitacao.setStatus(StatusSolicitacaoCotaFinanceira.CONCLUIDO);
        } else {
            solicitacao.setStatus(StatusSolicitacaoCotaFinanceira.LIBERADO_PARCIALMENTE);
        }
        em.merge(solicitacao);
    }

    public Boolean isMesmaContaBancaria(ContaBancariaEntidade c1, ContaBancariaEntidade c2) {
        if (c1.equals(c2)) {
            return true;
        }
        return false;
    }

    public UnidadeOrganizacional recuperaUnidadeDaSubContaRetirada(LiberacaoCotaFinanceira selecionado, Exercicio exercicio) {
        try {
            if (selecionado.getFonteDeRecursos() != null) {
                selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira().setContaVinculada(getSubContaFacade().recuperar(selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira().getContaVinculada().getId()));
                for (SubContaUniOrg subContaUniOrg : selecionado.getSolicitacaoCotaFinanceira().getContaFinanceira().getContaVinculada().getUnidadesOrganizacionais()) {
                    if (subContaUniOrg.getExercicio().equals(exercicio)) {
                        return subContaUniOrg.getUnidadeOrganizacional();
                    }
                }
            }
        } catch (Exception ex) {
        }
        return null;

    }


    public void contabilizarLiberacaoRecebido(LiberacaoCotaFinanceira entity) throws ExcecaoNegocioGenerica {
        ConfigLiberacaoFinanceira configLiberacaoFinanceira = configLiberacaoFinanceiraFacade.recuperaEvento(TipoLancamento.NORMAL, entity.getTipoLiberacaoFinanceira(), OrigemTipoTransferencia.RECEBIDO, entity.getDataLiberacao(), entity.getSolicitacaoCotaFinanceira().getResultanteIndependente());
        if (configLiberacaoFinanceira != null && configLiberacaoFinanceira.getEventoContabil() != null) {
            entity.setEventoContabilDeposito(configLiberacaoFinanceira.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Liberação Financeira (Recebida).");
        }

        if (entity.getEventoContabilDeposito() != null) {
            entity.gerarHistoricos();

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setDataEvento(entity.getDataLiberacao());
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeRecebida());
            parametroEvento.setExercicio(entity.getExercicio());
            parametroEvento.setEventoContabil(entity.getEventoContabilDeposito());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.RECEBIDO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getSolicitacaoCotaFinanceira().getId().toString(), SolicitacaoCotaFinanceira.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getContaFinanceiraRecebida().getId().toString(), SubConta.class.getSimpleName(), item));
            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada.");
        }
    }

    public void contabilizarLiberacaoConcedida(LiberacaoCotaFinanceira entity) throws ExcecaoNegocioGenerica {
        ConfigLiberacaoFinanceira configLiberacaoFinanceira = configLiberacaoFinanceiraFacade.recuperaEvento(TipoLancamento.NORMAL, entity.getTipoLiberacaoFinanceira(), OrigemTipoTransferencia.CONCEDIDO, entity.getDataLiberacao(), entity.getSolicitacaoCotaFinanceira().getResultanteIndependente());
        if (configLiberacaoFinanceira != null && configLiberacaoFinanceira.getEventoContabil() != null) {
            entity.setEventoContabilRetirada(configLiberacaoFinanceira.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Liberação Financeira (Concedida).");
        }

        if (entity.getEventoContabilRetirada() != null) {
            entity.gerarHistoricos();

            ParametroEvento parametroEvento = new ParametroEvento();
            parametroEvento.setDataEvento(entity.getDataLiberacao());
            parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
            parametroEvento.setUnidadeOrganizacional(entity.getUnidadeRetirada());
            parametroEvento.setExercicio(entity.getExercicio());
            parametroEvento.setEventoContabil(entity.getEventoContabilRetirada());
            parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
            parametroEvento.setIdOrigem(entity.getId().toString());
            parametroEvento.setComplementoId(ParametroEvento.ComplementoId.CONCEDIDO);

            ItemParametroEvento item = new ItemParametroEvento();
            item.setValor(entity.getValor());
            item.setParametroEvento(parametroEvento);
            item.setTagValor(TagValor.LANCAMENTO);

            List<ObjetoParametro> objetos = Lists.newArrayList();
            objetos.add(new ObjetoParametro(entity.getId().toString(), LiberacaoCotaFinanceira.class.getSimpleName(), item));
            objetos.add(new ObjetoParametro(entity.getContaFinanceiraRetirada().getId().toString(), SubConta.class.getSimpleName(), item));

            item.setObjetoParametros(objetos);

            parametroEvento.getItensParametrosEvento().add(item);

            contabilizacaoFacade.contabilizar(parametroEvento);

        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada.");
        }
    }

    @Override
    public LiberacaoCotaFinanceira recarregar(LiberacaoCotaFinanceira entity) {
        if (entity == null) {
            return null;
        }
        Object chave = Persistencia.getId(entity);
        if (chave == null) {
            return entity;
        }
        Query q;
        q = getEntityManager().createQuery("From LiberacaoCotaFinanceira l left join fetch l.pagamentoLiberacaoCotas plc left join fetch plc.pagamento where l=:param");
        q = getEntityManager().createQuery("From LiberacaoCotaFinanceira l left join fetch l.pagtoExtraLibCotas pelc left join fetch pelc.pagamentoExtra where l=:param");
        q.setParameter("param", entity);
        return (LiberacaoCotaFinanceira) q.getSingleResult();
    }

    public List<LiberacaoCotaFinanceira> listaFiltrandoPorBancoUnidade(Banco banco, UnidadeOrganizacional unidadeOrganizacional, SubConta subConta, Exercicio exercicio) {

        String sql = " SELECT LCF.* FROM LIBERACAOCOTAFINANCEIRA LCF "
            + " INNER JOIN solicitacaocotafinanceira sol on lcf.solicitacaocotafinanceira_id = sol.id "
            + " INNER JOIN SUBCONTA SC ON SC.ID = LCF.SUBCONTA_ID "
            + " INNER JOIN CONTABANCARIAENTIDADE CBE ON SC.CONTABANCARIAENTIDADE_ID = CBE.ID "
            + " INNER JOIN AGENCIA AG ON CBE.AGENCIA_ID = AG.ID "
            + " INNER JOIN BANCO BC ON AG.BANCO_ID = BC.ID "
            + " WHERE LCF.UNIDADEORGANIZACIONAL_ID = :uo "
            + " AND LCF.TIPOOPERACAOPAGTO <> '" + TipoOperacaoPagto.BAIXA_PARA_REGULARIZACAO + "' "
            + " AND (LCF.STATUSPAGAMENTO = '" + StatusPagamento.DEFERIDO.name() + "' or  LCF.STATUSPAGAMENTO = '" + StatusPagamento.INDEFERIDO.name() + "')"
            + " AND BC.ID = :idBanco "
            + " AND LCF.EXERCICIO_ID = :idExericio"
            + " AND SC.ID = :idSubconta ";
        Query q = em.createNativeQuery(sql, LiberacaoCotaFinanceira.class);
        q.setParameter("idBanco", banco.getId());
        q.setParameter("idExericio", exercicio.getId());
        q.setParameter("idSubconta", subConta.getId());
        q.setParameter("uo", unidadeOrganizacional.getId());
        return q.getResultList();
    }

    public List<LiberacaoCotaFinanceira> buscarFiltrandoPorUnidadeESaldo(String parte, UnidadeOrganizacional unidadeOrganizacional, Exercicio exercicio) {
        String sql = " SELECT LCF.* "
            + " FROM LIBERACAOCOTAFINANCEIRA LCF "
            + " INNER JOIN SOLICITACAOCOTAFINANCEIRA SCF ON LCF.SOLICITACAOCOTAFINANCEIRA_ID = SCF.ID "
            + " WHERE LCF.UNIDADEORGANIZACIONAL_ID = :uo "
            + " AND lcf.exercicio_id = :exercicio "
            + " AND LOWER(LCF.NUMERO) LIKE :parte "
            + " AND (LCF.STATUSPAGAMENTO = '" + StatusPagamento.DEFERIDO.name() + "' "
            + "   or LCF.STATUSPAGAMENTO = '" + StatusPagamento.INDEFERIDO.name() + "')"
            + " AND LCF.SALDO > 0 " +
            " order by lcf.dataLiberacao desc";
        Query q = em.createNativeQuery(sql, LiberacaoCotaFinanceira.class);
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setParameter("uo", unidadeOrganizacional.getId());
        q.setParameter("exercicio", exercicio.getId());
        q.setMaxResults(10);
        return q.getResultList();
    }

    public List<LiberacaoCotaFinanceira> listaPorUnidadeESaldo(UnidadeOrganizacional unidadeOrganizacional) {
        String sql = " SELECT LCF.* "
            + " FROM LIBERACAOCOTAFINANCEIRA LCF "
            + " INNER JOIN SOLICITACAOCOTAFINANCEIRA SCF ON LCF.SOLICITACAOCOTAFINANCEIRA_ID = SCF.ID "
            + " INNER JOIN USUARIOSISTEMA US ON SCF.USUARIOSOLICITANTE_ID = US.ID "
            + " INNER JOIN PESSOAFISICA PF ON US.PESSOAFISICA_ID = PF.ID "
            + " WHERE LCF.UNIDADEORGANIZACIONAL_ID = :uo AND LCF.SALDO > 0 ";
        Query q = em.createNativeQuery(sql, LiberacaoCotaFinanceira.class);
        q.setParameter("uo", unidadeOrganizacional.getId());
        return q.getResultList();
    }

    public String getUltimoNumero() {
        String sql = "SELECT MAX(TO_NUMBER(CR.NUMERO))+1 AS ULTIMONUMERO FROM LIBERACAOCOTAFINANCEIRA CR";
        Query q = em.createNativeQuery(sql);
        if (q.getSingleResult() == null) {
            return "1";
        }
        return q.getSingleResult().toString();
    }

    @Override
    public LiberacaoCotaFinanceira recuperar(Object id) {
        LiberacaoCotaFinanceira lcf = em.find(LiberacaoCotaFinanceira.class, id);
        lcf.getSolicitacaoCotaFinanceira().getContaFinanceira().getContaVinculada().getUnidadesOrganizacionais().size();
        lcf.getSolicitacaoCotaFinanceira().getContaFinanceira().getUnidadesOrganizacionais().size();
        return lcf;
    }

    public PagamentoFacade getPagamentoFacade() {
        return pagamentoFacade;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    public SolicitacaoCotaFinanceiraFacade getSolicitacaoCotaFinanceiraFacade() {
        return solicitacaoCotaFinanceiraFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    public PagamentoExtraFacade getPagamentoExtraFacade() {
        return pagamentoExtraFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ConfigLiberacaoFinanceiraFacade getConfigLiberacaoFinanceiraFacade() {
        return configLiberacaoFinanceiraFacade;
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    public SingletonGeradorCodigoContabil getSingletonGeradorCodigoContabil() {
        return singletonGeradorCodigoContabil;
    }

    public FinalidadePagamentoFacade getFinalidadePagamentoFacade() {
        return finalidadePagamentoFacade;
    }


    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizar((LiberacaoCotaFinanceira) entidadeContabil);
    }

    public void estornarConciliacao(LiberacaoCotaFinanceira liberacaoCotaFinanceira) {
        try {
            liberacaoCotaFinanceira.setDataConciliacao(null);
            liberacaoCotaFinanceira.setRecebida(null);
            getEntityManager().merge(liberacaoCotaFinanceira);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a conciliação. Consulte o suporte!");
        }
    }

    public void baixar(LiberacaoCotaFinanceira liberacaoCotaFinanceira) throws ExcecaoNegocioGenerica {
        try {
            liberacaoCotaFinanceira.setSaldo(liberacaoCotaFinanceira.getSaldo().subtract(liberacaoCotaFinanceira.getSaldo()));
            liberacaoCotaFinanceira.setStatusPagamento(StatusPagamento.PAGO);
            getEntityManager().merge(liberacaoCotaFinanceira);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao baixar. Consulte o suporte!");
        }
    }

    public void estornarBaixa(LiberacaoCotaFinanceira liberacaoCotaFinanceira) {
        try {
            if (listaEstornoLiberacaoFinanceira(liberacaoCotaFinanceira).isEmpty()) {
                liberacaoCotaFinanceira.setSaldo(liberacaoCotaFinanceira.getSaldo().add(liberacaoCotaFinanceira.getValor()));
                liberacaoCotaFinanceira.setStatusPagamento(StatusPagamento.DEFERIDO);
            } else {
                BigDecimal valor;
                valor = liberacaoCotaFinanceira.getValor().subtract(getSomaEstornosLiberacaoFinanceira(liberacaoCotaFinanceira));
                liberacaoCotaFinanceira.setSaldo(liberacaoCotaFinanceira.getSaldo().add(valor));
                liberacaoCotaFinanceira.setStatusPagamento(StatusPagamento.DEFERIDO);
            }
            getEntityManager().merge(liberacaoCotaFinanceira);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a baixa. Consulte o suporte! " + e.getMessage());
        }
    }

    public BigDecimal getSomaEstornosLiberacaoFinanceira(LiberacaoCotaFinanceira liberacaoCotaFinanceira) {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (EstornoLibCotaFinanceira est : listaEstornoLiberacaoFinanceira(liberacaoCotaFinanceira)) {
            estornos = estornos.add(est.getValor());
        }
        return estornos;
    }

    public List<EstornoLibCotaFinanceira> listaEstornoLiberacaoFinanceira(LiberacaoCotaFinanceira lib) {
        String sql = " select est.* from estornolibcotafinanceira est " +
            " inner join liberacaocotafinanceira lib on lib.id = est.liberacao_id " +
            " where lib.id = :idLiberacao ";
        Query q = em.createNativeQuery(sql, EstornoLibCotaFinanceira.class);
        q.setParameter("idLiberacao", lib.getId());
        if (!q.getResultList().isEmpty()) {
            return q.getResultList();
        }
        return new ArrayList<>();
    }


    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    public List<LiberacaoCotaFinanceira> listaLiberacaoPorUnidadesExercicio(Exercicio ano, List<HierarquiaOrganizacional> listaUnidades) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada());
        }

        String hql = " select lib from LiberacaoCotaFinanceira lib " +
            " where lib.solicitacaoCotaFinanceira.unidadeOrganizacional in (:unidades)" +
            " and lib.exercicio.id = :ano";
        Query consulta = em.createQuery(hql, LiberacaoCotaFinanceira.class);
        consulta.setParameter("unidades", unidades);
        consulta.setParameter("ano", ano.getId());
        List resultList = consulta.getResultList();
        if (resultList.isEmpty()) {
            return new ArrayList<>();
        } else {
            return resultList;
        }
    }

    public List<LiberacaoCotaFinanceira> buscarLiberacaoPorSolicitacao(SolicitacaoCotaFinanceira solicitacaoCotaFinanceira) {

        String hql = " select lib.* from LiberacaoCotaFinanceira lib " +
            " where lib.solicitacaoCotaFinanceira_id = :solicitacao";
        Query consulta = em.createNativeQuery(hql, LiberacaoCotaFinanceira.class);
        consulta.setParameter("solicitacao", solicitacaoCotaFinanceira.getId());
        List resultList = consulta.getResultList();
        if (resultList.isEmpty()) {
            return new ArrayList<>();
        } else {
            return resultList;
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @TransactionTimeout(value = 4, unit = TimeUnit.HOURS)
    public List<LiberacaoCotaFinanceira> recuperarObjetosReprocessamento(EventosReprocessar er, Boolean isReprocessamentoInicial, ParametroEvento.ComplementoId complementoId) {

        String sql = "select lib.* from LiberacaoCotaFinanceira lib " +
            " INNER JOIN SOLICITACAOCOTAFINANCEIRA SCF ON lib.SOLICITACAOCOTAFINANCEIRA_ID = SCF.ID " +
            " where trunc(lib.dataLiberacao) between to_date(:di,'dd/mm/yyyy') and to_date(:df,'dd/mm/yyyy')";
        if (er.hasUnidades() && complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
            sql = sql + " and lib.unidadeOrganizacional_id in (:unidades) ";
        }
        if (er.hasUnidades() && complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
            sql = sql + " and SCF.unidadeOrganizacional_id in (:unidades) ";
        }
        if (!isReprocessamentoInicial && complementoId.equals(ParametroEvento.ComplementoId.CONCEDIDO)) {
            sql += " and lib.eventoContabilRetirada_id = :evento";
        }
        if (!isReprocessamentoInicial && complementoId.equals(ParametroEvento.ComplementoId.RECEBIDO)) {
            sql += " and lib.eventoContabilDeposito_id = :evento";
        }

        sql += " order by lib.dataLiberacao asc";

        Query q = em.createNativeQuery(sql, LiberacaoCotaFinanceira.class);
        try {
            q.setParameter("di", DataUtil.getDataFormatada(er.getDataInicial()));
            q.setParameter("df", DataUtil.getDataFormatada(er.getDataFinal()));
            if (er.hasUnidades()) {
                q.setParameter("unidades", er.getIdUnidades());
            }
            if (!isReprocessamentoInicial) {
                q.setParameter("evento", er.getEventoContabil());
            }
            List<LiberacaoCotaFinanceira> retorno = q.getResultList();
            for (LiberacaoCotaFinanceira entidade : retorno) {
                entidade.setComplementoId(complementoId);
            }
            return retorno;
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(LiberacaoCotaFinanceira.class, filtros);
        consulta.incluirJoinsComplementar(" inner join solicitacaoCotaFinanceira sol on obj.solicitacaoCotaFinanceira_id = sol.id ");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataLiberacao)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataLiberacao)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "sol.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabilRetirada_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventoContabildeposito_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "sol.fonteDeRecursos_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "obj.fonteDeRecursos_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "obj.subConta_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "sol.contafinanceira_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        return Arrays.asList(consulta);
    }
}

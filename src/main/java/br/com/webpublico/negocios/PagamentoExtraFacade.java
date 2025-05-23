/*
 * Codigo gerado automaticamente em Tue Jan 03 10:09:55 BRST 2012
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.*;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.daos.JdbcPagamentoExtra;
import br.com.webpublico.negocios.contabil.reprocessamento.daos.JdbcRevisaoAuditoria;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.web.context.ContextLoader;

import javax.annotation.PostConstruct;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Stateless
public class PagamentoExtraFacade extends SuperFacadeContabil<PagamentoExtra> {

    private static final String NAO_AUTENTICADA = "Não Autenticada";
    private static final String AUTENTICACAO_MANUAL = "Autenticação Manual";
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoExtraorcamentarioFacade saldoExtraorcamentarioFacade;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private HistoricoContabilFacade historicoContabilFacade;
    @EJB
    private PessoaFacade fornecedorFacade;
    @EJB
    private ContaFacade contaExtraorcamentariaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private ReceitaExtraFacade receitaExtraFacade;
    @EJB
    private ClasseCredorFacade classeCredorFacade;
    @EJB
    private ConfigDespesaExtraFacade configDespesaExtraFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private FinalidadePagamentoFacade finalidadePagamentoFacade;
    @EJB
    private ContaCorrenteBancariaFacade contaCorrenteBancariaFacade;
    @EJB
    private ContaCorrenteBancPessoaFacade contaCorrenteBancPessoaFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private ConfigContaBancariaPessoaFacade configContaBancariaPessoaFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private PagamentoExtraEstornoFacade pagamentoExtraEstornoFacade;
    @EJB
    private PessoaFacade pessoaFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private BancoFacade bancoFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private NotaOrcamentariaFacade notaOrcamentariaFacade;
    private JdbcPagamentoExtra jdbcPagamentoExtra;
    private JdbcRevisaoAuditoria jdbcRevisaoAuditoria;

    @PostConstruct
    private void init() {
        jdbcPagamentoExtra = (JdbcPagamentoExtra) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcPagamentoExtra");
        jdbcRevisaoAuditoria = (JdbcRevisaoAuditoria) ContextLoader.getCurrentWebApplicationContext().getBean("jdbcRevisaoAuditoria");
    }

    public PagamentoExtraFacade() {
        super(PagamentoExtra.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    private void movimentarReceitaExtra(PagamentoExtra entity) {
        for (PagamentoReceitaExtra pre : entity.getPagamentoReceitaExtras()) {
            ReceitaExtra re = receitaExtraFacade.recuperar(pre.getReceitaExtra().getId());
            re.setSituacaoReceitaExtra(SituacaoReceitaExtra.EFETUADO);
            re.setSaldo(BigDecimal.ZERO);
            em.merge(re);
        }
    }

    @Override
    public void salvarNovo(PagamentoExtra entity) {
        try {
            if (taLiberado(entity)) {
                bloquearReceitaExtra(entity);
                if (entity.getId() == null) {
                    entity.setNumero(singletonGeradorCodigoContabil.getNumeroPagamentoExtra(entity.getExercicio(), entity.getUnidadeOrganizacional(), entity.getPrevistoPara()));
                    entity.setSaldo(entity.getValor());
                    entity = recuperarObjetosParaHistorico(entity);
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity = recuperarObjetosParaHistorico(entity);
                    if (StatusPagamento.ABERTO.equals(entity.getStatus())) {
                        entity.setSaldo(entity.getValor());
                    }
                    entity.gerarHistoricos();
                    entity = em.merge(entity);
                }
                desbloquearReceitaExtra(entity);
            }
        } catch (Exception ex) {
            desbloquearReceitaExtra(entity);
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    @Override
    public void salvar(PagamentoExtra entity) {
        entity = recuperarObjetosParaHistorico(entity);
        if (StatusPagamento.ABERTO.equals(entity.getStatus())) {
            entity.setSaldo(entity.getValor());
        }
        entity.gerarHistoricos();
        em.merge(entity);
    }

    public PagamentoExtra salvarRetornando(PagamentoExtra entity) {
        return em.merge(entity);
    }

    public PagamentoExtra salvarRetornandoEmJdbc(PagamentoExtra entity) {
        RevisaoAuditoria rev = jdbcRevisaoAuditoria.gerarRevisaoAuditoria(sistemaFacade.getIp(), sistemaFacade.getUsuarioCorrente());
        return jdbcPagamentoExtra.salvar(entity, rev.getId());
    }

    public PagamentoExtra atualizarRetornandoEmJdbc(PagamentoExtra entity) {
        RevisaoAuditoria rev = jdbcRevisaoAuditoria.gerarRevisaoAuditoria(sistemaFacade.getIp(), sistemaFacade.getUsuarioCorrente());
        return jdbcPagamentoExtra.atualizar(entity, rev.getId());
    }

    private void gerarSaldoExtraorcamentario(PagamentoExtra entity) throws ExcecaoNegocioGenerica {
        saldoExtraorcamentarioFacade.gerarSaldoExtraorcamentario(entity.getDataPagto(),
            TipoOperacao.DEBITO,
            entity.getValor(),
            entity.getContaExtraorcamentaria(),
            entity.getContaDeDestinacao(),
            entity.getUnidadeOrganizacional());
    }

    private void gerarSaldoContaFinanceira(PagamentoExtra entity) {
        saldoSubContaFacade.gerarSaldoFinanceiro(entity.getUnidadeOrganizacional(),
            entity.getSubConta(),
            entity.getContaDeDestinacao(),
            entity.getValor(),
            TipoOperacao.DEBITO,
            entity.getDataPagto(),
            entity.getEventoContabil(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.DESPESA_EXTRA,
            entity.getUuid(),
            true);
    }

    @Override
    public PagamentoExtra recuperar(Object id) {
        PagamentoExtra pe = em.find(PagamentoExtra.class, id);
        pe.getPagamentoExtraEstornos().size();
        pe.getPagamentoReceitaExtras().size();
        pe.getGuiaPagamentoExtras().size();
        return pe;
    }

    public boolean taLiberado(PagamentoExtra pagamentoExtra) {
        Boolean controle = true;
        for (PagamentoReceitaExtra pagamentoReceitaExtra : pagamentoExtra.getPagamentoReceitaExtras()) {
            if (!singletonConcorrenciaContabil.isDisponivel(pagamentoReceitaExtra.getReceitaExtra())) {
                controle = false;
            }
        }
        if (!controle) {
            if (pagamentoExtra.getId() == null) {
                throw new ExcecaoNegocioGenerica("A Receita Extraorçamentária está sendo utilizada por outro usuário. Caso o problema persistir, selecione novamente a receita.");
            } else {
                throw new ExcecaoNegocioGenerica("A Despesa Extraorçamentária está sendo deferida por outro usuário. Caso o problema persistir, tente deferir a despesa novamente.");
            }
        }
        return true;
    }

    public boolean isDisponivelDespesaExtra(PagamentoExtra pag) {
        return singletonConcorrenciaContabil.isDisponivel(pag);
    }

    public boolean isDisponivelUnidadeDespesaExtra(PagamentoExtra pag) {
        return singletonConcorrenciaContabil.isDisponivelUnidadePagamento(pag.getUnidadeOrganizacional());
    }

    public boolean bloquearReceitaExtra(PagamentoExtra pagamentoExtra) {
        for (PagamentoReceitaExtra pagamentoReceitaExtra : pagamentoExtra.getPagamentoReceitaExtras()) {
            singletonConcorrenciaContabil.bloquear(pagamentoReceitaExtra.getReceitaExtra());
        }
        return true;
    }

    public boolean desbloquearReceitaExtra(PagamentoExtra pagamentoExtra) {
        for (PagamentoReceitaExtra pagamentoReceitaExtra : pagamentoExtra.getPagamentoReceitaExtras()) {
            singletonConcorrenciaContabil.desbloquear(pagamentoReceitaExtra.getReceitaExtra());
        }
        return true;
    }

    public void indeferirPagamento(PagamentoExtra entity) {
        try {
            entity.setStatus(StatusPagamento.INDEFERIDO);
            em.merge(entity);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao indeferir: " + ex.getMessage());
        }
    }

    @TransactionTimeout(unit = TimeUnit.MINUTES, value = 30)
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public PagamentoExtra deferirPagamento(PagamentoExtra pag) {
        if (reprocessamentoLancamentoContabilSingleton.isCalculando()) {
            throw new ExcecaoNegocioGenerica(getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
        } else {
            validarConcorrencia(pag);
            bloquearDespesaExtra(pag);
            bloquearUnidade(pag);
            bloquearReceitaExtra(pag);
            hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(pag.getUnidadeOrganizacionalAdm(), pag.getUnidadeOrganizacional(), pag.getDataPagto());
            pag.setStatus(StatusPagamento.DEFERIDO);
            movimentarReceitaExtra(pag);
            gerarSaldoExtraorcamentario(pag);
            gerarSaldoContaFinanceira(pag);
            em.merge(pag);
            contabilizarDespesaExtra(pag);
            posValidarReceitas(pag);
        }
        return pag;
    }

    private void posValidarReceitas(PagamentoExtra selecionado) {
        List<ReceitaExtra> receitasDoPagamento = receitaExtraFacade.listaPorPagamentoExtra(selecionado);
        for (ReceitaExtra receitaExtra : receitasDoPagamento) {
            if (receitaExtra.getSaldo().compareTo(BigDecimal.ZERO) > 0) {
                throw new ValidacaoException("A Receita Extraorçamentária " + receitaExtra.getNumero() + " ficou com saldo maior que zero!");
            }
        }
    }

    private void desbloquearDespesaExtra(PagamentoExtra pag) {
        singletonConcorrenciaContabil.desbloquear(pag);
    }

    private void bloquearDespesaExtra(PagamentoExtra pag) {
        singletonConcorrenciaContabil.bloquear(pag);
    }

    private void bloquearUnidade(PagamentoExtra pag) {
        singletonConcorrenciaContabil.bloquearUnidadeDespesaExtra(pag.getUnidadeOrganizacional());
    }

    private void desbloquearUnidade(PagamentoExtra pag) {
        singletonConcorrenciaContabil.desbloquearUnidadeDespesaExtra(pag.getUnidadeOrganizacional());
    }

    public void desbloquearDeferir(PagamentoExtra entity) {
        desbloquearReceitaExtra(entity);
        desbloquearDespesaExtra(entity);
        desbloquearUnidade(entity);
    }

    public void validarConcorrencia(PagamentoExtra pagamento) {
        ValidacaoException ve = new ValidacaoException();
        if (!isDisponivelDespesaExtra(pagamento)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A despesa extra " + pagamento + " está sendo utilizado por outro usuário. Caso o problema persistir, selecione novamente a despesa.");
        }
        if (!isDisponivelUnidadeDespesaExtra(pagamento)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A unidade  " + pagamento.getUnidadeOrganizacional() + " está realizando movimentos para outro pagamento. Por favor, aguarde alguns instantes e clique em deferir novamente.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    public List<PagamentoExtra> listaFiltrandoDespesaExtraPorUnidadeExercicio(String parte, Exercicio ex, UnidadeOrganizacional und) {
        String sql = " select pag.* from pagamentoextra pag " +
            " where extract(year from pag.previstopara) = :ano " +
            " and pag.unidadeorganizacional_id = :idUnidade" +
            " and (pag.status = 'INDEFERIDO' " +
            "  or pag.status = 'DEFERIDO') " +
            " and pag.saldo > 0 " +
            " and pag.numero like :parte " +
            " order by pag.numero desc, pag.previstopara ";
        Query q = em.createNativeQuery(sql, PagamentoExtra.class);
        q.setParameter("ano", ex.getAno().toString());
        q.setParameter("idUnidade", und.getId());
        q.setParameter("parte", "%" + parte.toLowerCase().trim() + "%");
        q.setMaxResults(10);
        List<PagamentoExtra> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return resultado;
        }
    }

    public List<PagamentoExtra> listaDespesaExtraParaDeferir(Date dtInicio, Date dtFim, SubConta contaInicial, SubConta contaFinal, UnidadeOrganizacional unidadeOrganizacional, Exercicio ex, Conta contaExtra) {

        String sql = " SELECT P.*  FROM PAGAMENTOEXTRA P "
            + " INNER JOIN SUBCONTA SUB ON SUB.ID = P.SUBCONTA_ID "
            + " WHERE P.SALDO > 0 "
            + " AND P.EXERCICIO_ID = :idExercicio "
            + " AND P.UNIDADEORGANIZACIONAL_ID = :idUnidade "
            + " AND P.STATUS = '" + StatusPagamento.ABERTO.name() + "'"
            + " AND trunc(P.PREVISTOPARA) BETWEEN to_date(:dataInicial, 'dd/mm/yyyy') AND to_date(:dataFinal, 'dd/mm/yyyy') ";
        if (contaInicial != null && contaFinal != null) {
            sql += " AND SUB.CODIGO BETWEEN '" + contaInicial.getCodigo() + "' and '" + contaFinal.getCodigo() + "'";
        }
        if (contaExtra != null) {
            if (contaExtra.getId() != null) {
                sql += " AND P.CONTAEXTRAORCAMENTARIA_ID = :idContaExtra";
            }
        }
        sql += " order by P.PREVISTOPARA desc, P.NUMEROPAGAMENTO desc";
        Query q = em.createNativeQuery(sql, PagamentoExtra.class);
        q.setParameter("idUnidade", unidadeOrganizacional.getId());
        q.setParameter("idExercicio", ex.getId());
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dtInicio));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dtFim));
        if (contaExtra != null) {
            if (contaExtra.getId() != null) {
                q.setParameter("idContaExtra", contaExtra.getId());
            }
        }
        List<PagamentoExtra> pagamentos = q.getResultList();
        if (!pagamentos.isEmpty()) {
            return pagamentos;
        } else {
            return new ArrayList<>();
        }
    }

    public List<PagamentoExtra> listaPorSubContaStatus(SubConta conta, UnidadeOrganizacional unidade, StatusPagamento status) {
        String sql = "select pag.* from pagamentoextra pag "
            + " where pag.status = :status "
            + " and pag.subconta_id = :conta "
            + " and pag.unidadeorganizacional_id = :unidade ";
        Query q = em.createNativeQuery(sql, PagamentoExtra.class);
        q.setParameter("status", status.name());
        q.setParameter("conta", conta.getId());
        q.setParameter("unidade", unidade.getId());
        return q.getResultList();
    }

    public List<PagamentoExtra> listaPorBancoUnidade(Banco banco, UnidadeOrganizacional unidadeOrganizacional, SubConta subConta, Exercicio exercicio) {
        String sql = "SELECT PAG.* "
            + " FROM PAGAMENTOEXTRA PAG "
            + " INNER JOIN SUBCONTA SC ON PAG.SUBCONTA_ID = SC.ID "
            + " INNER JOIN CONTABANCARIAENTIDADE CBE ON SC.CONTABANCARIAENTIDADE_ID = CBE.ID "
            + " INNER JOIN AGENCIA AG ON CBE.AGENCIA_ID = AG.ID "
            + " INNER JOIN BANCO BC ON AG.BANCO_ID = BC.ID "
            + " WHERE (PAG.STATUS = '" + StatusPagamento.DEFERIDO.name() + "' or  PAG.STATUS = '" + StatusPagamento.INDEFERIDO.name() + "')"
            + "   AND PAG.TIPOOPERACAOPAGTO <> '" + TipoOperacaoPagto.BAIXA_PARA_REGULARIZACAO + "' "
            + "   AND BC.ID = :banco "
            + "   AND SC.ID = :idSubconta "
            + "   AND PAG.EXERCICIO_ID = :idExercicio "
            + "   AND PAG.UNIDADEORGANIZACIONAL_ID = :uo "
            + " ORDER BY PAG.DATAPAGTO, PAG.NUMERO ";
        Query q = em.createNativeQuery(sql, PagamentoExtra.class);
        q.setParameter("uo", unidadeOrganizacional.getId());
        q.setParameter("banco", banco.getId());
        q.setParameter("idSubconta", subConta.getId());
        q.setParameter("idExercicio", exercicio.getId());
        return q.getResultList();
    }

    public ContaFacade getContaExtraorcamentariaFacade() {
        return contaExtraorcamentariaFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public PessoaFacade getFornecedorFacade() {
        return fornecedorFacade;
    }

    public HistoricoContabilFacade getHistoricoContabilFacade() {
        return historicoContabilFacade;
    }

    public SaldoExtraorcamentarioFacade getSaldoExtraorcamentarioFacade() {
        return saldoExtraorcamentarioFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public ContabilizacaoFacade getContabilizacaoFacade() {
        return contabilizacaoFacade;
    }

    public SaldoSubContaFacade getSaldoSubContaFacade() {
        return saldoSubContaFacade;
    }

    public ReceitaExtraFacade getReceitaExtraFacade() {
        return receitaExtraFacade;
    }

    public ConfigDespesaExtraFacade getConfigDespesaExtraFacade() {
        return configDespesaExtraFacade;
    }

    public ClasseCredorFacade getClasseCredorFacade() {
        return classeCredorFacade;
    }


    private void contabilizarDespesaExtra(PagamentoExtra entity) throws ExcecaoNegocioGenerica {
        ConfigDespesaExtra ce = configDespesaExtraFacade.recuperaEvento(
            TipoLancamento.NORMAL,
            ((ContaExtraorcamentaria) entity.getContaExtraorcamentaria()).getTipoContaExtraorcamentaria(),
            entity.getDataPagto());

        validarEventoContabil(ce.getEventoContabil());
        entity = recuperarObjetosParaHistorico(entity);
        entity.setEventoContabil(ce.getEventoContabil());
        entity.gerarHistoricos();
        contabilizacaoFacade.contabilizar(getParametroEvento(entity));
    }

    private void validarEventoContabil(EventoContabil eventoContabil) {
        ValidacaoException ve = new ValidacaoException();
        if (eventoContabil == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Evento contábil não encontrado.");
        }
        ve.lancarException();
    }

    public ParametroEvento getParametroEvento(EntidadeContabil entidadeContabil) {
        try {
            PagamentoExtra entity = (PagamentoExtra) entidadeContabil;
            ParametroEvento parametroEvento = criarParametroEvento(entity);
            ItemParametroEvento item = criarItemParametroEvento(entity, parametroEvento);
            adicionarObjetosParametros(entity, item);
            parametroEvento.getItensParametrosEvento().add(item);
            return parametroEvento;
        } catch (Exception e) {
            return null;
        }
    }

    private ParametroEvento criarParametroEvento(PagamentoExtra entity) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataPagto());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setExercicio(entity.getExercicio());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
        parametroEvento.setIdOrigem(entity.getId() == null ? null : entity.getId().toString());
        return parametroEvento;
    }

    private ItemParametroEvento criarItemParametroEvento(PagamentoExtra entity, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(entity.getValor());
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        item.setOperacaoClasseCredor(classeCredorFacade.recuperaOperacaoAndVigenciaClasseCredor(entity.getFornecedor(), entity.getClasseCredor(), entity.getPrevistoPara()));
        return item;
    }

    private void adicionarObjetosParametros(PagamentoExtra entity, ItemParametroEvento item) {
        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(entity.getId().toString(), PagamentoExtra.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(entity.getContaExtraorcamentaria().getId().toString(), ContaExtraorcamentaria.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(entity.getSubConta().getId().toString(), SubConta.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(entity.getClasseCredor().getId().toString(), ClasseCredor.class.getSimpleName(), item));
        item.setObjetoParametros(objetos);
    }

    private PagamentoExtra recuperarObjetosParaHistorico(PagamentoExtra entity) {
        if (!Hibernate.isInitialized(entity.getPagamentoReceitaExtras())) {
            entity = recuperar(entity.getId());
        }
        for (PagamentoReceitaExtra pagamentoReceitaExtra : entity.getPagamentoReceitaExtras()) {
            if (pagamentoReceitaExtra.getReceitaExtra().getRetencaoPgto() != null) {
                RetencaoPgto retencaoPgto = pagamentoReceitaExtra.getReceitaExtra().getRetencaoPgto();
                if (!Hibernate.isInitialized(retencaoPgto.getPagamento().getLiquidacao().getDesdobramentos())) {
                    retencaoPgto.getPagamento().setLiquidacao(liquidacaoFacade.recuperar(retencaoPgto.getPagamento().getLiquidacao().getId()));
                }
            }
        }
        return entity;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    @Override
    public void remover(PagamentoExtra entity) {
        em.remove(em.merge(entity));
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

    public ContaCorrenteBancariaFacade getContaCorrenteBancariaFacade() {
        return contaCorrenteBancariaFacade;
    }

    public ContaCorrenteBancPessoaFacade getContaCorrenteBancPessoaFacade() {
        return contaCorrenteBancPessoaFacade;
    }

    public ConfigContaBancariaPessoaFacade getConfigContaBancariaPessoaFacade() {
        return configContaBancariaPessoaFacade;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        contabilizarDespesaExtra((PagamentoExtra) entidadeContabil);
    }

    public void estornarConciliacao(PagamentoExtra selecionado) {
        try {
            selecionado.setDataConciliacao(null);
            getEntityManager().merge(selecionado);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a baixa da despesa extra. Consulte o suporte!");
        }
    }

    public void estornarBaixa(PagamentoExtra pagamento, StatusPagamento statusPagamento) {
        try {
            if (listarEstornosPagamentoExtra(pagamento).isEmpty()) {
                pagamento.setSaldo(pagamento.getSaldo().add(pagamento.getValor()));
                pagamento.setStatus(statusPagamento);
            } else {
                BigDecimal valor;
                valor = pagamento.getValor().subtract(getSomaEstornosPagamento(pagamento));
                pagamento.setSaldo(pagamento.getSaldo().add(valor));
                pagamento.setStatus(statusPagamento);
            }

            Pagamento p;
            for (PagamentoReceitaExtra pr : pagamento.getPagamentoReceitaExtras()) {
                if (pr.getReceitaExtra().getRetencaoPgto() != null) {
                    p = pr.getReceitaExtra().getRetencaoPgto().getPagamento();
                    p.setSaldo(p.getSaldo().add(pr.getReceitaExtra().getValor()));
                    p.setStatus(statusPagamento);
                    em.merge(p);
                }
            }
            baixarGuiaDespesaExtra(pagamento, SituacaoGuiaPagamento.ABERTA, NAO_AUTENTICADA);
            getEntityManager().merge(pagamento);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a baixa. Consulte o suporte!");
        }
    }

    public void baixarGuiaDespesaExtra(PagamentoExtra pagamentoExtra, SituacaoGuiaPagamento situacaoGuiaPagamento, String autenticacao) {
        if (!pagamentoExtra.getGuiaPagamentoExtras().isEmpty()) {
            for (GuiaPagamentoExtra guiaPagamentoExtra : pagamentoExtra.getGuiaPagamentoExtras()) {
                guiaPagamentoExtra.setSituacaoGuiaPagamento(situacaoGuiaPagamento);
                guiaPagamentoExtra.setNumeroAutenticacao(autenticacao);
            }
        }
    }

    public void baixar(PagamentoExtra despesaExtra, StatusPagamento statusPagamento) {
        despesaExtra.setSaldo(despesaExtra.getSaldo().subtract(despesaExtra.getSaldo()));
        despesaExtra.setStatus(statusPagamento);

        Pagamento p;
        for (PagamentoReceitaExtra pr : despesaExtra.getPagamentoReceitaExtras()) {
            if (pr.getReceitaExtra().getRetencaoPgto() != null) {
                p = pr.getReceitaExtra().getRetencaoPgto().getPagamento();
                p.setSaldo(p.getSaldo().subtract(pr.getReceitaExtra().getValor()));
                if (p.getSaldo().compareTo(BigDecimal.ZERO) == 0) {
                    p.setStatus(statusPagamento);
                }
                em.merge(p);
            }
        }
        baixarGuiaDespesaExtra(despesaExtra, SituacaoGuiaPagamento.DEFERIDA, AUTENTICACAO_MANUAL);
        getEntityManager().merge(despesaExtra);
    }

    public List<PagamentoExtraEstorno> listarEstornosPagamentoExtra(PagamentoExtra pag) {
        try {
            return pagamentoExtraEstornoFacade.recuperarEstornosPagamentoExtra(pag);
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar estorno de pgamento " + ex.getMessage());
        }
    }

    public BigDecimal getSomaEstornosPagamento(PagamentoExtra pag) {
        BigDecimal estornos = new BigDecimal(BigInteger.ZERO);
        for (PagamentoExtraEstorno est : listarEstornosPagamentoExtra(pag)) {
            estornos = estornos.add(est.getValor());
        }
        return estornos;
    }

    public PessoaFacade getPessoaFacade() {
        return pessoaFacade;
    }

    public PessoaFisicaFacade getPessoaFisicaFacade() {
        return pessoaFisicaFacade;
    }

    public List<PagamentoExtra> recuperarPagamentoExtraDaPessoa(Pessoa pessoa) {
        String sql = " select p.* from pagamentoextra p    " +
            "       where p.fornecedor_id  = :pessoa   ";
        Query q = em.createNativeQuery(sql, PagamentoExtra.class);
        q.setParameter("pessoa", pessoa.getId());
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        }
        return q.getResultList();
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    public BancoFacade getBancoFacade() {
        return bancoFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public NotaOrcamentariaFacade getNotaOrcamentariaFacade() {
        return notaOrcamentariaFacade;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(PagamentoExtra.class, filtros);
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataPagto)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataPagto)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "obj.fornecedor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "obj.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "obj.subConta_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.FONTE_DE_RECURSO, "obj.fonteDeRecursos_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_EXTRA, "obj.contaExtraorcamentaria_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        return Arrays.asList(consulta);
    }

    public NotaExecucaoOrcamentaria buscarNotaPagamentoExtra(PagamentoExtra pagamentoExtra, BigDecimal saldoAtual) {
        String sql = " select " +
            "     nota.numero||'/'||exerc.ano as numero, " +
            "     nota.datapagto as data_nota, " +
            "     coalesce(nota.historiconota, 'Histórico não cadastrado') as historico_nota, " +
            "     trim(vworg.codigo) as cd_orgao, " +
            "     trim(vworg.descricao) as desc_oragao, " +
            "     trim(vw.codigo) as cd_unidade, " +
            "     trim(vw.descricao) as desc_unidade, " +
            "     coalesce(pf.nome, pj.razaosocial) as nome_pessoa, " +
            "     formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpf_cnpj, " +
            "     fonte.codigo || ' - ' || fonte.descricao as descricaodestinacao, " +
            "     nota.valor as valor, " +
            "     c.codigo || ' - ' || c.descricao as contaextra, " +
            "     (select sum(rec.valor) from pagamentoreceitaextra pre " +
            "     inner join receitaextra rec on pre.receitaextra_id = rec.id " +
            "     where pre.pagamentoextra_id = nota.id) as saldo_anterior, " +
            "     nota.saldo as saldo_atual, " +
            "     frt_extenso_monetario(nota.valor)||'  ***********************' as valor_extenso, " +
            "     classe.codigo || ' - ' || classe.descricao as desc_classepessoa, " +
            "     coalesce(endereco.bairro,'Pessoa não possui bairro cadastrado') as bairro, " +
            "     coalesce(endereco.uf,'sem UF ') as uf, " +
            "     coalesce(substr(endereco.cep,1,5)||'-'||substr(endereco.cep,6,3),'sem CEP') as cep, " +
            "     coalesce(endereco.logradouro,'Pessoa não possui Logradouro') as logradouro, " +
            "     coalesce(endereco.localidade,'Pessoa não possui Localidade') as localidade, " +
            "     coalesce(ag.nomeagencia,'Pessoa não possui agência cadastrada') as nomeagencia, " +
            "     coalesce(banco.descricao,'Pessoa não possui banco cadastrado') as descricao, " +
            "     banco.numerobanco as numerobanco, " +
            "     ag.numeroagencia as numeroagencia, " +
            "     ag.digitoverificador as digitoverificador, " +
            "     cc.numeroconta as numerocontacorrente, " +
            "     cc.digitoverificador as digitoverificadorcontacorrente, " +
            "     cbe.tipocontabancaria as tipocontabancaria, " +
            "     cbe.situacao as situacaocontabancaria, " +
            "     coalesce(replace(banco.numerobanco||' / '||ag.numeroagencia||' / '||cc.numeroconta||'-'||cc.digitoverificador,' //- '),'Pessoa sem Dados Bancarios') as banco_gencia_conta, " +
            "     coalesce(bco.numerobanco||' / '||age.numeroagencia || '-' || age.digitoverificador ||' / '||cbe.numeroconta||'-'||cbe.digitoverificador || '/' || sc.codigo || '-' || sc.descricao,' ') as banco_agencia_conta, " +
            "     bco.numerobanco as banco, " +
            "     nota.unidadeorganizacional_id as idunidade " +
            "    from pagamentoextra nota " +
            "       inner join fontederecursos fonte on nota.fontederecursos_id = fonte.id " +
            "       inner join conta c on contaextraorcamentaria_id = c.id " +
            "       inner join pessoa pes on nota.fornecedor_id = pes.id " +
            "       left join pessoafisica pf on pes.id = pf.id " +
            "       left join pessoajuridica pj on pes.id = pj.id " +
            "       left join enderecocorreio endereco on pes.enderecoprincipal_id = endereco.id " +
            "       left join contacorrentebancaria cc on nota.contacorrentebancaria_id = cc.id " +
            "       left join agencia ag on cc.agencia_id =ag.id " +
            "       left join banco banco on ag.banco_id=banco.id " +
            "       inner join vwhierarquiaorcamentaria vw on nota.unidadeorganizacional_id  = vw.subordinada_id " +
            "       inner join vwhierarquiaorcamentaria vworg on vw.superior_id  = vworg.subordinada_id " +
            "       inner join exercicio exerc on nota.exercicio_id = exerc.id " +
            "       inner join subconta sc on nota.subconta_id = sc.id " +
            "       inner join contabancariaentidade cbe on sc.contabancariaentidade_id = cbe.id " +
            "       inner join agencia age on cbe.agencia_id = age.id " +
            "       inner join banco bco on age.banco_id = bco.id " +
            "       left join classecredor classe on nota.classecredor_id = classe.id " +
            "    where trunc(nota.previstopara) between vw.iniciovigencia and coalesce(vw.fimvigencia, trunc(nota.previstopara)) " +
            "      and trunc(nota.previstopara) between vworg.iniciovigencia and coalesce(vworg.fimvigencia, trunc(nota.previstopara)) " +
            "      and nota.id = :idPagamentoExtra ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPagamentoExtra", pagamentoExtra.getId());
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentaria> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentaria nota = new NotaExecucaoOrcamentaria();
                nota.setId(pagamentoExtra.getId());
                nota.setNumeroDocumento((String) obj[0]);
                nota.setData(DataUtil.getDataFormatada((Date) obj[1]));
                nota.setHistorico((String) obj[2] + " Usuário Emissor: " + sistemaFacade.getUsuarioCorrente().getNome());
                nota.setCodigoOrgao((String) obj[3]);
                nota.setDescricaoOrgao((String) obj[4]);
                nota.setCodigoUnidade((String) obj[5]);
                nota.setDescricaoUnidade((String) obj[6]);
                nota.setNomePessoa((String) obj[7]);
                nota.setCpfCnpj((String) obj[8]);
                nota.setDescricaoDestinacao((String) obj[9]);
                nota.setValor((BigDecimal) obj[10]);
                nota.setContaExtraorcamentaria((String) obj[11]);
                nota.setSaldoAnterior(saldoAtual.add(nota.getValor()));
                nota.setSaldoAtual(saldoAtual);
                nota.setValorPorExtenso((String) obj[14]);
                nota.setClassePessoa((String) obj[15]);
                nota.setBairro((String) obj[16]);
                nota.setUf((String) obj[17]);
                nota.setCep((String) obj[18]);
                nota.setLogradouro((String) obj[19]);
                nota.setLocalidade((String) obj[20]);
                nota.setDescricaoAgencia((String) obj[21]);
                nota.setDescricaoBanco((String) obj[22]);
                nota.setNumeroBanco((String) obj[23]);
                nota.setNumeroAgencia((String) obj[24]);
                nota.setDigitoVerificador((String) obj[25]);
                nota.setNumeroContaCorrente((String) obj[26]);
                nota.setDigitoVerificadorContaCorrente((String) obj[27]);
                nota.setTipoContaBancaria((String) obj[28]);
                nota.setSituacaoContaBancaria((String) obj[29]);
                nota.setBancoAgenciaConta((String) obj[30]);
                nota.setContaFinanceira((String) obj[31]);
                nota.setBancoContaBancariaEntidade((String) obj[32]);
                nota.setIdUnidadeOrcamentaria((BigDecimal) obj[33]);
                nota.setReceitasExtras(buscarReceitasExtras(nota.getId()));
                nota.setBancoAgenciaConta(nota.getNumeroAgencia() != null ?
                    nota.getNumeroBanco() + "/" + nota.getNumeroAgencia() +
                        (nota.getDigitoVerificador() != null ? " - " + nota.getDigitoVerificador() : "") + "/" +
                        nota.getNumeroContaCorrente() + " - " + nota.getDigitoVerificadorContaCorrente() + " - " +
                        TipoContaBancaria.valueOf(nota.getTipoContaBancaria()).getDescricao() + " - " +
                        SituacaoConta.valueOf(nota.getSituacaoContaBancaria()).getDescricao()
                    : "Pessoa sem Dados Bancários");
                nota.setFaturas(buscarFaturas(nota.getId()));
                nota.setConvenios(buscarConvenios(nota.getId()));
                nota.setGps(buscarGps(nota.getId()));
                nota.setDarfs(buscarDarfs(nota.getId()));
                nota.setDarfsSimples(buscarDarfsSimples(nota.getId()));
                retorno.add(nota);
            }
        }
        return retorno.get(0);
    }

    private List<NotaExecucaoOrcamentariaReceitaExtra> buscarReceitasExtras(Long idPagamentoExtra) {
        String sql = " select " +
            "        rec.numero, " +
            "        rec.datareceita, " +
            "        coalesce(rec.complementohistorico, ' ') as complementohistorico, " +
            "        eve.codigo as evento, " +
            "        rec.valor " +
            " from receitaextra rec " +
            "   inner join pagamentoreceitaextra pagrec on rec.id = pagrec.receitaextra_id " +
            "   left join eventocontabil eve on rec.eventocontabil_id = eve.id " +
            " where pagrec.pagamentoextra_id = :idPagamentoExtra ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPagamentoExtra", idPagamentoExtra);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaReceitaExtra> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaReceitaExtra receitaExtra = new NotaExecucaoOrcamentariaReceitaExtra();
                receitaExtra.setNumero((String) obj[0]);
                receitaExtra.setDataReceita((Date) obj[1]);
                receitaExtra.setHistorico((String) obj[2]);
                receitaExtra.setEvento((String) obj[3]);
                receitaExtra.setValor((BigDecimal) obj[4]);
                retorno.add(receitaExtra);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaFatura> buscarFaturas(Long idPagamentoExtra) {
        String sql = " select " +
            "       coalesce(guia.numeroautenticacao, 'Não Autenticada') as autenticacao, " +
            "       'Fatura' as tipoguia, " +
            "       fat.codigobarra as codigobarra, " +
            "       fat.datavencimento as datavencimento, " +
            "       (coalesce(fat.valornominal, 0) + coalesce(fat.valorjuros, 0)) - coalesce(fat.valordescontos, 0) as valor " +
            " from guiafatura fat " +
            "   inner join guiapagamentoextra guia on fat.id = guia.guiafatura_id " +
            "   inner join pagamentoextra pag on pag.id = guia.pagamentoextra_id " +
            " where pag.id = :idPagamentoExtra ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPagamentoExtra", idPagamentoExtra);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaFatura> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaFatura fatura = new NotaExecucaoOrcamentariaFatura();
                fatura.setTipoGuia((String) obj[1]);
                fatura.setCodigoBarra((String) obj[2]);
                fatura.setDataVencimento((Date) obj[3]);
                fatura.setValor((BigDecimal) obj[4]);
                retorno.add(fatura);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaConvenio> buscarConvenios(Long idPagamentoExtra) {
        String sql = " select " +
            "       coalesce(guia.numeroautenticacao, 'Não Autenticada') as autenticacao, " +
            "       'Guia Convênio' as tipoguia, " +
            "       conv.codigobarra as codigobarra " +
            " from guiaconvenio conv " +
            "   inner join guiapagamentoextra guia on conv.id = guia.guiaconvenio_id " +
            "   inner join pagamentoextra pag on pag.id = guia.pagamentoextra_id " +
            " where pag.id = :idPagamentoExtra ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPagamentoExtra", idPagamentoExtra);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaConvenio> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaConvenio convenio = new NotaExecucaoOrcamentariaConvenio();
                convenio.setTipoGuia((String) obj[1]);
                convenio.setCodigoBarra((String) obj[2]);
                retorno.add(convenio);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaGps> buscarGps(Long idPagamentoExtra) {
        String sql = " select " +
            "       coalesce(guia.numeroautenticacao, 'Não Autenticada') as autenticacao, " +
            "       'Guia GPS' as tipoguia, " +
            "       gps.codigoreceitatributo as coditotributo, " +
            "       gps.periodocompetencia as periodocomp, " +
            "       gps.codigoidentificacaotributo as digitotributo, " +
            "       coalesce(gps.valorprevistoinss, 0) + coalesce(gps.valoroutrasentidades, 0) + coalesce(gps.atualizacaomonetaria, 0) as valor " +
            " from guiagps gps " +
            "   inner join guiapagamentoextra guia on gps.id = guia.guiagps_id " +
            "   inner join pagamentoextra pag on pag.id = guia.pagamentoextra_id " +
            " where pag.id = :idPagamentoExtra ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPagamentoExtra", idPagamentoExtra);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaGps> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaGps gps = new NotaExecucaoOrcamentariaGps();
                gps.setTipoGuia((String) obj[1]);
                gps.setCodigoTributo((String) obj[2]);
                gps.setPeriodoComp((Date) obj[3]);
                gps.setDigitoTributo((String) obj[4]);
                gps.setValor((BigDecimal) obj[5]);
                retorno.add(gps);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaDarf> buscarDarfs(Long idPagamentoExtra) {
        String sql = " select " +
            "       coalesce(guia.numeroautenticacao, 'Não Autenticada') as autenticacao, " +
            "       'Guia Darf' as tipoguia, " +
            "       darf.datavencimento as datavencimento, " +
            "       darf.codigoreceitatributo as coditotributo, " +
            "       darf.codigoidentificacaotributo as codigoidentificador, " +
            "       coalesce(darf.valorprincipal, 0) + coalesce(darf.valormulta, 0) + coalesce(darf.valorjuros, 0) as valor " +
            " from guiadarf darf " +
            "   inner join guiapagamentoextra guia on darf.id = guia.guiadarf_id " +
            "   inner join pagamentoextra pag on pag.id = guia.pagamentoextra_id " +
            " where pag.id = :idPagamentoExtra ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPagamentoExtra", idPagamentoExtra);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaDarf> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaDarf darf = new NotaExecucaoOrcamentariaDarf();
                darf.setTipoGuia((String) obj[1]);
                darf.setDataVencimento((Date) obj[2]);
                darf.setCodigoTributo((String) obj[3]);
                darf.setCodigoIdentificador((String) obj[4]);
                darf.setValorPrincipal((BigDecimal) obj[5]);
                retorno.add(darf);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaDarf> buscarDarfsSimples(Long idPagamentoExtra) {
        String sql = " select " +
            "       coalesce(guia.numeroautenticacao, 'Não Autenticada') as autenticacao, " +
            "       'Guia Darf Simples' as tipoguia, " +
            "       darf.periodoapuracao as periodoapuracao, " +
            "       darf.codigoreceitatributo as coditotributo, " +
            "       darf.codigoidentificacaotributo as codigoidentificador, " +
            "       coalesce(darf.valorprincipal, 0) + coalesce(darf.valormulta, 0) + coalesce(darf.valorjuros, 0) as valor " +
            " from guiadarfsimples darf " +
            "   inner join guiapagamentoextra guia on darf.id = guia.guiadarfsimples_id " +
            "   inner join pagamentoextra pag on pag.id = guia.pagamentoextra_id " +
            " where pag.id = :idPagamentoExtra ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idPagamentoExtra", idPagamentoExtra);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaDarf> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaDarf darf = new NotaExecucaoOrcamentariaDarf();
                darf.setTipoGuia((String) obj[1]);
                darf.setDataVencimento((Date) obj[2]);
                darf.setCodigoTributo((String) obj[3]);
                darf.setCodigoIdentificador((String) obj[4]);
                darf.setValorPrincipal((BigDecimal) obj[5]);
                retorno.add(darf);
            }
        }
        return retorno;
    }
}

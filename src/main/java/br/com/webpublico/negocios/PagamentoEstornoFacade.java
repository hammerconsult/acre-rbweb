/*
 * Codigo gerado automaticamente em Wed Dec 14 09:52:41 BRST 2011
 * Gerador de Facace
 */
package br.com.webpublico.negocios;

import br.com.webpublico.controle.portaltransparencia.entidades.DiariaPortal;
import br.com.webpublico.controle.portaltransparencia.entidades.PagamentoEstornoPortal;
import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.contabil.execucao.DesdobramentoPagamentoEstorno;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentaria;
import br.com.webpublico.entidadesauxiliares.NotaExecucaoOrcamentariaRetencao;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.interfaces.EntidadeContabil;
import br.com.webpublico.negocios.contabil.execucao.DesdobramentoPagamentoFacade;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoLancamentoContabilSingleton;
import br.com.webpublico.negocios.contabil.reprocessamento.SuperFacadeContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.ConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.reprocessamento.consulta.FiltroConsultaMovimentoContabil;
import br.com.webpublico.negocios.contabil.singleton.SingletonConcorrenciaContabil;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Persistencia;
import br.com.webpublico.webreportdto.dto.contabil.*;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Stateless
public class PagamentoEstornoFacade extends SuperFacadeContabil<PagamentoEstorno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private SaldoSubContaFacade saldoSubContaFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private LiquidacaoFacade liquidacaoFacade;
    @EJB
    private ReceitaExtraEstornoFacade receitaExtraEstornoFacade;
    @EJB
    private HistoricoContabilFacade historicoContabilFacade;
    @EJB
    private DiariaContabilizacaoFacade diariaContabilizacaoFacade;
    @EJB
    private ContabilizacaoFacade contabilizacaoFacade;
    @EJB
    private ConfigPagamentoFacade configuracaoFacade;
    @EJB
    private ConfigPagamentoRestoPagarFacade configPagamentoRestoPagarFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private ReprocessamentoLancamentoContabilSingleton reprocessamentoLancamentoContabilSingleton;
    @EJB
    private SaldoDividaPublicaFacade saldoDividaPublicaFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonConcorrenciaContabil singletonConcorrenciaContabil;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    @EJB
    private DesdobramentoPagamentoFacade desdobramentoPagamentoFacade;
    @EJB
    private ConciliacaoBancariaFacade conciliacaoBancariaFacade;
    @EJB
    private NotaOrcamentariaFacade notaOrcamentariaFacade;

    public ConciliacaoBancariaFacade getConciliacaoBancariaFacade() { return conciliacaoBancariaFacade; }

    public NotaOrcamentariaFacade getNotaOrcamentariaFacade() {
        return notaOrcamentariaFacade;
    }

    public PagamentoEstornoFacade() {
        super(PagamentoEstorno.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public List<PagamentoEstorno> listaPorEmpenho(Empenho e) {
        String sql = "SELECT pe.* FROM pagamentoestorno pe "
            + "INNER JOIN pagamento pa ON pa.id = pe.pagamento_id "
            + "INNER JOIN liquidacao li ON li.id = pa.liquidacao_id "
            + "WHERE li.empenho_id = :param";
        Query q = em.createNativeQuery(sql, PagamentoEstorno.class);
        q.setParameter("param", e.getId());
        return q.getResultList();
    }

    public List<PagamentoEstorno> listaPorLiquidacao(Liquidacao li) {
        String sql = "SELECT pe.* FROM pagamentoestorno pe "
            + "INNER JOIN pagamento pa ON pa.id = pe.pagamento_id "
            + "WHERE pa.liquidacao_id = :param";
        Query q = em.createNativeQuery(sql, PagamentoEstorno.class);
        q.setParameter("param", li.getId());
        return q.getResultList();
    }

    public List<PagamentoEstorno> listaEstornosPagamento(Pagamento pag) {
        String sql = "SELECT pe.* FROM pagamentoestorno pe "
            + "INNER JOIN pagamento pa ON pa.id = pe.pagamento_id "
            + "WHERE pe.pagamento_id = :param";
        Query q = em.createNativeQuery(sql, PagamentoEstorno.class);
        q.setParameter("param", pag.getId());
        return q.getResultList();
    }

    public void alteraSituacaoDiaria(PagamentoEstorno estorno) {
        PropostaConcessaoDiaria pcd = estorno.getPagamento().getLiquidacao().getEmpenho().getPropostaConcessaoDiaria();
        if (pcd != null) {
            if (pcd.getTipoProposta().equals(TipoProposta.CONCESSAO_DIARIA)
                || (pcd.getTipoProposta().equals(TipoProposta.SUPRIMENTO_FUNDO))) {
                pcd.setSituacaoDiaria(SituacaoPropostaConcessaoDiaria.A_COMPROVAR);
                pcd = em.merge(pcd);
            }
        }
    }

    public void geraNumeroEstornoPagamento(PagamentoEstorno entity) {
        entity.setNumero(singletonGeradorCodigoContabil.getNumeroPagamento(entity.getPagamento().getExercicio(), entity.getUnidadeOrganizacional(), entity.getDataEstorno()));
    }

    private void geraMovimentoDespesaORC(PagamentoEstorno entity) {
        if (entity.getCategoriaOrcamentaria().isNormal()) {
            SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                entity.getPagamento().getLiquidacao().getEmpenho().getFonteDespesaORC(),
                OperacaoORC.PAGAMENTO,
                TipoOperacaoORC.ESTORNO,
                entity.getValor(),
                entity.getDataEstorno(),
                entity.getUnidadeOrganizacional(),
                entity.getId().toString(),
                entity.getClass().getSimpleName(),
                entity.getNumero(),
                entity.getComplementoHistorico());
            MovimentoDespesaORC mdorc = saldoFonteDespesaORCFacade.gerarSaldoOrcamentario(vo);
            entity.setMovimentoDespesaORC(mdorc);
        }
    }

    public void salvarNovoEstornoPagamento(PagamentoEstorno entity, List<ReceitaExtra> receitaExtrasSelecionadas) {
        try {
            if (pagamentoFacade.getReprocessamentoLancamentoContabilSingleton().isCalculando()) {
                throw new ExcecaoNegocioGenerica(pagamentoFacade.getReprocessamentoLancamentoContabilSingleton().getMensagemConcorrenciaEnquantoReprocessa());
            } else {
                hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataEstorno());
                validarConcorrencia(entity);
                singletonConcorrenciaContabil.bloquear(entity.getPagamento());
                movimentarLiquidacao(entity);
                movimentarPagamento(entity, receitaExtrasSelecionadas);
                gerarInscricaoDiaria(entity);
                if (entity.getId() == null) {
                    geraNumeroEstornoPagamento(entity);
                    entity.gerarHistoricos();
                    em.persist(entity);
                } else {
                    entity.gerarHistoricos();
                    entity.setMovimentoDespesaORC(null);
                    entity = em.merge(entity);
                }
                geraMovimentoDespesaORC(entity);
                gerarSaldoDividaPublica(entity);
                geraSaldoContaFinanceira(entity);
                contabilizaEstornoDePagamento(entity);
                salvarPortal(entity);
                singletonConcorrenciaContabil.desbloquear(entity.getPagamento());
            }
        } catch (ValidacaoException ve) {
            throw ve;
        } catch (Exception e) {
            singletonConcorrenciaContabil.desbloquear(entity.getPagamento());
            throw new ExcecaoNegocioGenerica(e.getMessage());
        }
    }

    private void validarConcorrencia(PagamentoEstorno entity) {
        ValidacaoException ve = new ValidacaoException();
        if (!singletonConcorrenciaContabil.isDisponivel(entity.getPagamento())) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("O Pagamento: " + entity.getPagamento() + " está sendo utilizado por outro usuário. Caso o problema persistir, selecione novamente o Pagamento.");
        }
        if (ve.temMensagens()) {
            throw ve;
        }
    }

    private void salvarPortal(PagamentoEstorno entity) {
        pagamentoFacade.getEmpenhoFacade().getPortalTransparenciaNovoFacade().salvarPortal(new PagamentoEstornoPortal(entity));
        TipoContaDespesa tipoContaDespesa = entity.getPagamento().getLiquidacao().getEmpenho().getTipoContaDespesa();
        if (entity.getPagamento().getLiquidacao().getEmpenho().getPropostaConcessaoDiaria() != null
            && (tipoContaDespesa.equals(TipoContaDespesa.DIARIA_CIVIL)
            || tipoContaDespesa.equals(TipoContaDespesa.DIARIA_CAMPO)
            || tipoContaDespesa.equals(TipoContaDespesa.SUPRIMENTO_FUNDO))) {
            pagamentoFacade.getEmpenhoFacade().getPortalTransparenciaNovoFacade().salvarPortal(new DiariaPortal(entity.getPagamento().getLiquidacao().getEmpenho().getPropostaConcessaoDiaria()));
        }
    }

    private void gerarSaldoDividaPublica(PagamentoEstorno entity) {
        if (entity.getPagamento().getLiquidacao().getEmpenho().getDividaPublica() != null) {
            ConfiguracaoContabil configuracaoContabil = configuracaoContabilFacade.configuracaoContabilVigente();
            if (hasContaDeDespesaConfiguradaParaGerarSaldoDividaPublica(entity, configuracaoContabil)) {
                saldoDividaPublicaFacade.gerarMovimento(entity.getDataEstorno(),
                    entity.getValor(),
                    entity.getUnidadeOrganizacional(),
                    entity.getPagamento().getLiquidacao().getEmpenho().getDividaPublica(),
                    OperacaoMovimentoDividaPublica.PAGAMENTO_AMORTIZACAO,
                    true,
                    OperacaoDiarioDividaPublica.EMPENHO,
                    Intervalo.CURTO_PRAZO,
                    entity.getPagamento().getLiquidacao().getEmpenho().getFonteDespesaORC().getProvisaoPPAFonte().getDestinacaoDeRecursosAsContaDeDestinacao(),
                    false);
            }
        }
    }

    private boolean hasContaDeDespesaConfiguradaParaGerarSaldoDividaPublica(PagamentoEstorno estorno, ConfiguracaoContabil configuracaoContabil) {
        for (ConfiguracaoContabilContaDespesa configuracaoContabilContaDespesa : configuracaoContabil.getContasDespesa()) {
            if (estorno.getPagamento().getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getCodigo().startsWith(configuracaoContabilContaDespesa.getContaDespesa().getCodigoSemZerosAoFinal())) {
                return true;
            }
        }
        return false;
    }

    private void geraSaldoContaFinanceira(PagamentoEstorno entity) {
        saldoSubContaFacade.gerarSaldoFinanceiro(entity.getUnidadeOrganizacional(),
            entity.getPagamento().getSubConta(),
            entity.getPagamento().getLiquidacao().getEmpenho().getContaDeDestinacao(),
            entity.getValorFinal(),
            TipoOperacao.CREDITO,
            entity.getDataEstorno(),
            entity.getEventoContabil(),
            entity.getHistoricoRazao(),
            MovimentacaoFinanceira.ESTORNO_DE_PAGAMENTO,
            entity.getUuid(),
            true);
    }

    @Override
    public void salvar(PagamentoEstorno entity) {
        entity.gerarHistoricos();
        em.merge(entity);
    }

    private void movimentarLiquidacao(PagamentoEstorno entity) {
        Liquidacao li = liquidacaoFacade.recuperar(entity.getPagamento().getLiquidacao().getId());
        li.setSaldo(li.getSaldo().add(entity.getValor()));
        em.merge(li);
    }

    public BigDecimal getValorReceitaExtra(List<ReceitaExtra> receitaExtrasSelecionadas) {
        BigDecimal valor = BigDecimal.ZERO;
        if (receitaExtrasSelecionadas != null) {
            for (ReceitaExtra receitaExtra : receitaExtrasSelecionadas) {
                valor = valor.add(receitaExtra.getValor());
            }
        }
        return valor;
    }

    private void movimentarPagamento(PagamentoEstorno entity, List<ReceitaExtra> receitaExtrasSelecionadas) {
        Pagamento pagamento = entity.getPagamento();
        BigDecimal valorFinalPagamento = entity.getValorFinal();

        entity.setUnidadeOrganizacional(pagamento.getUnidadeOrganizacional());
        entity.setUnidadeOrganizacionalAdm(pagamento.getUnidadeOrganizacionalAdm());
        entity.setUsuarioSistema(sistemaFacade.getUsuarioCorrente());

        entity.setValor(valorFinalPagamento.add(getValorReceitaExtra(receitaExtrasSelecionadas)));

        pagamento.setSaldo(pagamento.getSaldo().subtract(entity.getValor()));

        pagamento.setSaldoFinal(pagamento.getSaldoFinal().subtract(valorFinalPagamento));

        if (pagamento.getSaldo().compareTo(BigDecimal.ZERO) == 0
            && pagamento.getSaldoFinal().compareTo(BigDecimal.ZERO) == 0
            && !pagamento.getStatus().equals(StatusPagamento.PAGO)) {
            if (pagamento.getDataConciliacao() == null) {
                pagamento.setDataConciliacao(sistemaFacade.getDataOperacao());
            }
            entity.setDataConciliacao(sistemaFacade.getDataOperacao());
            pagamento.setStatus(StatusPagamento.ESTORNADO);
        }

        for (DesdobramentoPagamentoEstorno desdobramentoPagamento : entity.getDesdobramentos()) {
            desdobramentoPagamento.getDesdobramentoPagamento().setSaldo(desdobramentoPagamento.getDesdobramentoPagamento().getSaldo().subtract(desdobramentoPagamento.getValor()));
            em.merge(desdobramentoPagamento.getDesdobramentoPagamento());
            desdobramentoPagamento.getDesdobramentoPagamento().getDesdobramento().setSaldo(desdobramentoPagamento.getDesdobramentoPagamento().getDesdobramento().getSaldo().add(desdobramentoPagamento.getValor()));
            em.merge(desdobramentoPagamento.getDesdobramentoPagamento().getDesdobramento());
        }
        em.merge(pagamento);
    }

    public void gerarInscricaoDiaria(PagamentoEstorno entity) {
        TipoContaDespesa tipoContaDespesa = entity.getPagamento().getLiquidacao().getEmpenho().getTipoContaDespesa();
        if (entity.getPagamento().getLiquidacao().getEmpenho().getPropostaConcessaoDiaria() != null) {
            PropostaConcessaoDiaria pcd = entity.getPagamento().getLiquidacao().getEmpenho().getPropostaConcessaoDiaria();

            DiariaContabilizacao dc = new DiariaContabilizacao();
            dc.setOperacaoDiariaContabilizacao(OperacaoDiariaContabilizacao.INSCRICAO);
            dc.setTipoLancamento(TipoLancamento.ESTORNO);
            dc.setPropostaConcessaoDiaria(pcd);
            dc.setValor(entity.getValor());
            dc.setHistorico(entity.getComplementoHistorico());
            dc.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
            dc.setDataDiaria(entity.getDataEstorno());
            dc.setExercicio(entity.getExercicio());
            switch (tipoContaDespesa) {
                case DIARIA_CAMPO:
                    dc.setTipoProposta(TipoProposta.CONCESSAO_DIARIACAMPO);
                    break;

                case SUPRIMENTO_FUNDO:
                    dc.setTipoProposta(TipoProposta.SUPRIMENTO_FUNDO);
                    break;

                default:
                    dc.setTipoProposta(TipoProposta.CONCESSAO_DIARIA);
                    break;
            }
            diariaContabilizacaoFacade.salvarNovo(dc, true);
        }
    }

    @Override
    public PagamentoEstorno recarregar(PagamentoEstorno entity) {
        if (entity == null) {
            return null;
        }
        Object chave = Persistencia.getId(entity);
        if (chave == null) {
            return entity;
        }
        Query q;
        q = getEntityManager().createQuery("From PagamentoEstorno p left join fetch p.pagamento pgto left join fetch pgto.liquidacao liq left join fetch liq.empenho e left join fetch e.despesaORC where p = :param");
        q.setParameter("param", entity);
        PagamentoEstorno p = (PagamentoEstorno) q.getSingleResult();
        return p;
    }

    public void estornoRetencoes(PagamentoEstorno entity, List<ReceitaExtraEstorno> estornos, List<ReceitaExtra> receitaExtrasSelecionadas) throws ExcecaoNegocioGenerica {
        try {
            hierarquiaOrganizacionalFacade.validaVigenciaHIerarquiaAdministrativaOrcamentaria(entity.getUnidadeOrganizacionalAdm(), entity.getUnidadeOrganizacional(), entity.getDataEstorno());
            salvarNovoEstornoPagamento(entity, receitaExtrasSelecionadas);
            if (!estornos.isEmpty()) {
                for (ReceitaExtraEstorno ree : estornos) {
                    estornaRetencao(ree.getReceitaExtra(), entity);

                    Liquidacao liquidacao = liquidacaoFacade.recuperar(entity.getPagamento().getLiquidacao().getId());
                    liquidacao.getDesdobramentos().size();

                    ConfigReceitaExtra config = receitaExtraEstornoFacade.getConfigReceitaExtraFacade().recuperaConfiguracaoEventoContabil(ree.getReceitaExtra());
                    ree.setEventoContabil(config.getEventoContabil());
                    ree.setExercicio(entity.getPagamento().getExercicio());
                    ree.setStatusPagamento(StatusPagamento.EFETUADO);
                    ree.getReceitaExtra().getRetencaoPgto().getPagamento().setLiquidacao(liquidacao);
                    receitaExtraEstornoFacade.salvarNovoEstorno(ree);
                }
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public void estornaRetencao(ReceitaExtra re, PagamentoEstorno pag) {
        RetencaoPgto ret = pagamentoFacade.recuperaPorReceitaExtra(re);
        ret.setEstornado(Boolean.TRUE);
        ret.setPagamentoEstorno(pag);
        em.merge(ret);
    }

    @Override
    public List<PagamentoEstorno> lista() {
        Query q = getEntityManager().createQuery("from PagamentoEstorno order by id");
        getEntityManager().flush();
        return q.getResultList();
    }

    public List listaNoExercicio(Exercicio ex) {
        String sql = " SELECT PE.ID, PE.DATAESTORNO, PE.NUMERO, PE.VALOR, E.NUMERO, PF.NOME"
            + " FROM PAGAMENTOESTORNO PE"
            + " INNER JOIN PAGAMENTO P ON P.ID = PE.PAGAMENTO_ID"
            + " INNER JOIN LIQUIDACAO L ON L.ID = P.LIQUIDACAO_ID"
            + " INNER JOIN EMPENHO E ON E.ID = L.EMPENHO_ID"
            + " INNER JOIN PESSOAFISICA PF ON PF.ID = E.FORNECEDOR_ID"
            + " WHERE TO_CHAR(PE.DATAESTORNO, 'YYYY') = :ano AND PE.CATEGORIAORCAMENTARIA = 'NORMAL'"
            + " UNION"
            + " SELECT PE.ID, PE.DATAESTORNO, PE.NUMERO, PE.VALOR, E.NUMERO, PJ.RAZAOSOCIAL"
            + " FROM PAGAMENTOESTORNO PE"
            + " INNER JOIN PAGAMENTO P ON P.ID = PE.PAGAMENTO_ID"
            + " INNER JOIN LIQUIDACAO L ON L.ID = P.LIQUIDACAO_ID"
            + " INNER JOIN EMPENHO E ON E.ID = L.EMPENHO_ID"
            + " INNER JOIN PESSOAJURIDICA PJ ON PJ.ID = E.FORNECEDOR_ID"
            + " WHERE extract(year from PE.DATAESTORNO) = :ano AND PE.CATEGORIAORCAMENTARIA = 'NORMAL'";
        Query q = em.createNativeQuery(sql);
        q.setParameter("ano", ex.getAno().toString());
        List lista = q.getResultList();
        if (lista.isEmpty()) {
            return new ArrayList<PagamentoEstorno>();
        } else {
            return lista;
        }
    }

    public List<ReceitaExtra> listaReceitaExtraPgtoNaoEstornada(Pagamento p) {
        try {
            Preconditions.checkNotNull(p, "O pagamento utilizado como parametro para recuperar a Receita Extra esta vazia!");
            String sql = "SELECT  re.*  FROM receitaextra re "
                + " INNER JOIN retencaopgto rp ON re.retencaopgto_id = rp.id AND rp.estornado = 0 "
                + " INNER JOIN pagamento p ON rp.pagamento_id = p.id "
                + " WHERE p.id = :pag "
                + " AND RE.SITUACAORECEITAEXTRA = 'ABERTO' ";
            Query q = em.createNativeQuery(sql, ReceitaExtra.class);
            q.setParameter("pag", p.getId());
            if (q.getResultList().isEmpty()) {
                return new ArrayList<ReceitaExtra>();
            } else {
                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar receitas extras. " + ex.getMessage());
        }
    }

    public List<ReceitaExtra> listaReceitaExtraPgtoEstornadas(PagamentoEstorno p) {
        try {
            Preconditions.checkNotNull(p, " O pagamento utilizado como parametro para recuperar a Receita Extra esta vazia!");
            String sql = "SELECT  re.*  FROM receitaextra re "
                + " INNER JOIN retencaopgto rp ON re.retencaopgto_id = rp.id "
                + " INNER JOIN PAGAMENTOESTORNO PE ON PE.ID = RP.PAGAMENTOESTORNO_ID"
                + " WHERE pE.id = :pag ";
            Query q = em.createNativeQuery(sql, ReceitaExtra.class);
            q.setParameter("pag", p.getId());

            if (q.getResultList().isEmpty()) {
                return new ArrayList<ReceitaExtra>();
            } else {
                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar receitas extras. " + ex.getMessage());
        }
    }

    public List<ReceitaExtraEstorno> listaReceitaExtraEstornadaPorEstornoPagamento(PagamentoEstorno p) {
        try {
            Preconditions.checkNotNull(p, "O pagamento utilizado como parametro para recuperar a Estorno da Receita Extra esta vazio!");
            String sql = " SELECT REE.* " +
                "      FROM RECEITAEXTRAESTORNO REE " +
                "      INNER JOIN RECEITAEXTRA RE ON RE.ID = REE.RECEITAEXTRA_ID " +
                "      INNER JOIN RETENCAOPGTO RET ON RET.ID = RE.RETENCAOPGTO_ID " +
                "      INNER JOIN PAGAMENTOESTORNO PE ON PE.ID = RET.PAGAMENTOESTORNO_ID " +
                "      WHERE PE.ID = :pag";
            Query q = em.createNativeQuery(sql, ReceitaExtraEstorno.class);
            q.setParameter("pag", p.getId());
            if (q.getResultList().isEmpty()) {
                return new ArrayList<ReceitaExtraEstorno>();
            } else {
                return q.getResultList();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new ExcecaoNegocioGenerica("Erro ao recuperar estornos de receitas extraorçamentárias. " + ex.getMessage());
        }
    }

    public List<ReceitaExtra> listaReceitaExtraPorEstornoPagamento(PagamentoEstorno p) {
        try {
            Preconditions.checkNotNull(p, "O pagamento utilizado como parametro para recuperar a Estorno da Receita Extra esta vazio!");
            String sql = " SELECT RE.* FROM RECEITAEXTRA RE " +
                "      INNER JOIN RETENCAOPGTO RET ON RET.ID = RE.RETENCAOPGTO_ID " +
                "      INNER JOIN PAGAMENTO P ON P.ID = RET.PAGAMENTO_ID " +
                "      WHERE P.ID = :pag";
            Query q = em.createNativeQuery(sql, ReceitaExtra.class);
            q.setParameter("pag", p.getPagamento().getId());
            if (q.getResultList().isEmpty()) {
                return new ArrayList<ReceitaExtra>();
            } else {
                return q.getResultList();
            }
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica("Erro ao recuperar receitas extras. " + ex.getMessage());
        }
    }

    public List<PagamentoEstorno> buscarPagamentoEstornoPorUnidadesExercicio(List<HierarquiaOrganizacional> listaUnidades, Exercicio exercicio, CategoriaOrcamentaria categoria) {
        List<UnidadeOrganizacional> unidades = Lists.newArrayList();
        for (HierarquiaOrganizacional lista : listaUnidades) {
            unidades.add(lista.getSubordinada());
        }
        String sql = "SELECT est "
            + " FROM PagamentoEstorno est"
            + " WHERE  est.pagamento.exercicio.id = :exercicio"
            + " and est.unidadeOrganizacional in ( :unidades )" +
            " and est.categoriaOrcamentaria = :categoria";
        Query q = em.createQuery(sql, PagamentoEstorno.class);
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("unidades", unidades);
        q.setParameter("categoria", categoria);
        if (q.getResultList().isEmpty()) {
            return new ArrayList<>();
        } else {
            return q.getResultList();
        }
    }

    public LiquidacaoFacade getLiquidacaoFacade() {
        return liquidacaoFacade;
    }

    public PagamentoFacade getPagamentoFacade() {
        return pagamentoFacade;
    }

    public ReceitaExtraEstornoFacade getReceitaExtraEstornoFacade() {
        return receitaExtraEstornoFacade;
    }

    public SaldoFonteDespesaORCFacade getSaldoFonteDespesaORCFacade() {
        return saldoFonteDespesaORCFacade;
    }

    public HistoricoContabilFacade getHistoricoContabilFacade() {
        return historicoContabilFacade;
    }

    public ConfigPagamentoFacade getConfiguracaoFacade() {
        return configuracaoFacade;
    }

    public ConfigPagamentoRestoPagarFacade getConfigPagamentoRestoPagarFacade() {
        return configPagamentoRestoPagarFacade;
    }

    @Override
    public PagamentoEstorno recuperar(Object id) {
        Query consulta = em.createNativeQuery("SELECT p.* FROM PagamentoEstorno p WHERE p.id = :id", PagamentoEstorno.class);
        consulta.setParameter("id", id);
        PagamentoEstorno singleResult = (PagamentoEstorno) consulta.getSingleResult();
        singleResult.getDesdobramentos().size();
        return singleResult;
    }

    public void contabilizaEstornoDePagamento(PagamentoEstorno pagamentoEstorno) {
        if (pagamentoEstorno.getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.NORMAL)) {
            contabilizarPagamentoEstorno(pagamentoEstorno);
        } else {
            contabilizarPagamentoEstornoResto(pagamentoEstorno);
        }
    }

    private void contabilizarPagamentoEstorno(PagamentoEstorno entity) {
        Empenho empenho = entity.getPagamento().getLiquidacao().getEmpenho();
        Conta contaDespesa = (ContaDespesa) empenho.getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa();
        ConfigPagamento configuracao = configuracaoFacade.recuperaEventoPorContaDespesa(contaDespesa, TipoLancamento.ESTORNO, empenho.getTipoContaDespesa(), entity.getDataEstorno());

        if (configuracao != null && configuracao.getEventoContabil() != null) {
            entity.setEventoContabil(configuracao.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Pagamento Estorno.");
        }

        if (configuracao != null && configuracao.getId() != null) {
            entity.gerarHistoricos();
            ParametroEvento parametroEvento = getParametroEvento(entity);
            contabilizacaoFacade.contabilizar(parametroEvento);
        } else {
            throw new ExcecaoNegocioGenerica("Não existe configuração de Pagamento");
        }
    }

    private void contabilizarPagamentoEstornoResto(PagamentoEstorno entity) {
        ConfigPagamentoRestoPagar configuracao = configPagamentoRestoPagarFacade.recuperaEventoRestoPorContaDespesa((ContaDespesa) entity.getPagamento().getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa(), TipoLancamento.ESTORNO, entity.getDataEstorno(), entity.getPagamento().getLiquidacao().getEmpenho().getTipoContaDespesa(), entity.getPagamento().getLiquidacao().getEmpenho().getTipoRestosProcessados());

        if (configuracao != null && configuracao.getEventoContabil() != null) {
            entity.setEventoContabil(configuracao.getEventoContabil());
        } else {
            throw new ExcecaoNegocioGenerica("Não foi encontrado um Evento Contábil para a operação selecionada na contabilização de Estorno de Pagamento de Resto a Pagar.");
        }
        if (configuracao != null && configuracao.getId() != null) {
            entity.gerarHistoricos();
            ParametroEvento parametroEvento = getParametroEvento(entity);
            contabilizacaoFacade.contabilizar(parametroEvento);
        } else {
            throw new ExcecaoNegocioGenerica("Configuração não encontrada para o Estorno de Pagamento de Resto a Pagar.");
        }
    }

    private ParametroEvento getParametroEvento(PagamentoEstorno entity) {
        ParametroEvento parametroEvento = criarParametroEvento(entity);
        ItemParametroEvento item = criarItemParametroEvento(entity, parametroEvento);
        adicionarObjetosParametro(entity, item);
        parametroEvento.getItensParametrosEvento().add(item);
        return parametroEvento;
    }

    private ParametroEvento criarParametroEvento(PagamentoEstorno entity) {
        ParametroEvento parametroEvento = new ParametroEvento();
        parametroEvento.setComplementoHistorico(entity.getHistoricoRazao());
        parametroEvento.setDataEvento(entity.getDataEstorno());
        parametroEvento.setUnidadeOrganizacional(entity.getUnidadeOrganizacional());
        parametroEvento.setEventoContabil(entity.getEventoContabil());
        parametroEvento.setExercicio(entity.getPagamento().getExercicio());
        parametroEvento.setClasseOrigem(entity.getClass().getSimpleName());
        parametroEvento.setIdOrigem(entity.getId().toString());
        return parametroEvento;
    }

    private ItemParametroEvento criarItemParametroEvento(PagamentoEstorno entity, ParametroEvento parametroEvento) {
        ItemParametroEvento item = new ItemParametroEvento();
        item.setValor(entity.getValorFinal());
        item.setParametroEvento(parametroEvento);
        item.setTagValor(TagValor.LANCAMENTO);
        return item;
    }

    private void adicionarObjetosParametro(PagamentoEstorno entity, ItemParametroEvento item) {
        List<ObjetoParametro> objetos = Lists.newArrayList();
        objetos.add(new ObjetoParametro(entity.getId().toString(), PagamentoEstorno.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(entity.getPagamento().getLiquidacao().getEmpenho().getDespesaORC().getProvisaoPPADespesa().getContaDeDespesa().getId().toString(), ContaDespesa.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(entity.getPagamento().getSubConta().getId().toString(), SubConta.class.getSimpleName(), item));
        objetos.add(new ObjetoParametro(entity.getPagamento().getLiquidacao().getEmpenho().getClasseCredor().getId().toString(), ClasseCredor.class.getSimpleName(), item));
        if (entity.getPagamento().getLiquidacao().getEmpenho().getDividaPublica() != null) {
            objetos.add(new ObjetoParametro(entity.getPagamento().getLiquidacao().getEmpenho().getDividaPublica().getCategoriaDividaPublica().getId().toString(), CategoriaDividaPublica.class.getSimpleName(), item));
        }
        item.setObjetoParametros(objetos);
    }

    public ReprocessamentoLancamentoContabilSingleton getReprocessamentoLancamentoContabilSingleton() {
        return reprocessamentoLancamentoContabilSingleton;
    }

    @Override
    public void contabilizarReprocessamento(EntidadeContabil entidadeContabil) {
        PagamentoEstorno pagamentoEstorno = (PagamentoEstorno) entidadeContabil;
        if (pagamentoEstorno.getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.NORMAL)) {
            contabilizarPagamentoEstorno(pagamentoEstorno);
        } else if (pagamentoEstorno.getCategoriaOrcamentaria().equals(CategoriaOrcamentaria.RESTO)) {
            contabilizarPagamentoEstornoResto(pagamentoEstorno);
        }
    }

    public void estornarConciliacao(PagamentoEstorno pagamentoEstorno) {
        try {
            pagamentoEstorno.setDataConciliacao(null);
            getEntityManager().merge(pagamentoEstorno);
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Erro ao estornar a conciliação. Consulte o suporte!");
        }
    }

    public SingletonConcorrenciaContabil getSingletonConcorrenciaContabil() {
        return singletonConcorrenciaContabil;
    }

    @Override
    public List<ConsultaMovimentoContabil> criarConsulta(List<FiltroConsultaMovimentoContabil> filtros) {
        ConsultaMovimentoContabil consulta = new ConsultaMovimentoContabil(PagamentoEstorno.class, filtros);
        consulta.incluirJoinsComplementar(" inner join pagamento pag on obj.pagamento_id = pag.id ");
        consulta.incluirJoinsComplementar(" inner join liquidacao liq on pag.liquidacao_id = liq.id ");
        consulta.incluirJoinsComplementar(" inner join empenho emp on liq.empenho_id = emp.id ");
        consulta.incluirJoinsOrcamentoDespesa(" emp.fontedespesaorc_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_INICIAL, "trunc(obj.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.DATA_FINAL, "trunc(obj.dataEstorno)");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.LISTA_UNIDADE, "obj.unidadeOrganizacional_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.PESSOA, "emp.fornecedor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CLASSE, "emp.classecredor_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.EVENTO_CONTABIL, "obj.eventocontabil_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.NUMERO, "obj.numero");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.CONTA_FINANCEIRA, "pag.subConta_id");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.HISTORICO, "obj.historicoRazao");
        consulta.alterarCondicaoFiltro(ConsultaMovimentoContabil.Campo.VALOR, "obj.valor");
        return Arrays.asList(consulta);
    }

    public DesdobramentoPagamentoFacade getDesdobramentoPagamentoFacade() {
        return desdobramentoPagamentoFacade;
    }

    public List<NotaExecucaoOrcamentaria> buscarNotaDeEstornoPagamento(String condicao, CategoriaOrcamentaria categoriaOrcamentaria) {
        String sql = " SELECT " +
            "       nota.numero||'/'||exerc.ano as numero, " +
            "       nota.dataestorno as data_nota, " +
            "       coalesce(nota.historiconota,  '') || '. ' || coalesce(nota.COMPLEMENTOHISTORICO, '') as historico_nota, " +
            "       EMP.TIPOEMPENHO AS TIPO, " +
            "       TRIM(VWORG.CODIGO) AS CD_ORGAO, " +
            "       TRIM(VWORG.DESCRICAO) AS DESC_ORAGAO, " +
            "       TRIM(vw.CODIGO) AS CD_UNIDADE, " +
            "       TRIM(vw.DESCRICAO) AS DESC_UNIDADE, " +
            "       f.codigo || '.' || sf.codigo || '.' || p.codigo || '.' || tpa.codigo || a.codigo || '.' || sub.codigo as CD_PROG_TRABALHO, " +
            "       ct_desp.codigo as elemento, " +
            "       ct_desp.descricao as especificao_despesa, " +
            "       coalesce(pf.nome, pj.razaosocial) as nome_pessoa, " +
            "       formatacpfcnpj(coalesce(pf.cpf, pj.cnpj)) as cpf_cnpj, " +
            "       fonte_r.codigo || ' - ' || trim(fonte_r.descricao)  || ' (' || cdest.codigo || ')' as desc_destinacao, " +
            "       nota.VALORFINAL as valor, " +
            "       frt_extenso_monetario(NOTA.valor)||'  ***********************' as valor_extenso, " +
            "       COALESCE(ENDERECO.LOGRADOURO,'Pessoa não possui Logradouro') AS LOGRADOURO, " +
            "       coalesce(endereco.localidade,'Pessoa não possui Localidade') as localidade, " +
            "       coalesce(ag.nomeagencia,'Pessoa não possui agência cadastrada') as nomeagencia, " +
            "       coalesce(banco.descricao,'Pessoa não possui banco cadastrado') as descricao, " +
            "       banco.numerobanco as numerobanco, " +
            "       ag.numeroagencia as numeroagencia, " +
            "       AG.DIGITOVERIFICADOR as digitoverificador, " +
            "       cc.numeroconta as numerocontacorrente, " +
            "       cc.digitoverificador as digitoverificadorcontacorrente, " +
            "       cbe.TIPOCONTABANCARIA as tipocontabancaria, " +
            "       cbe.SITUACAO as situacaocontabancaria, " +
            "       coalesce(banco.numerobanco||'/'||ag.numeroagencia || '-' ||ag.DIGITOVERIFICADOR ||'/'||cc.numeroconta||'-'||cc.digitoverificador||' '||cbe.TIPOCONTABANCARIA||' '||cbe.SITUACAO , 'Pessoa sem Dados Bancarios') as banco_agencia_conta, " +
            "       banco.numerobanco as banco, " +
            "       A.DESCRICAO AS DESC_ACAO, " +
            "       coalesce(lic.numero, disp.numeroDaDispensa, regext.numeromodalidade)  || ' - Processo Licitatório ' || coalesce(lic.numerolicitacao, disp.numerodadispensa, regext.numeroregistro) || ' - ' || to_char(coalesce(lic.emitidaem, disp.datadadispensa, regext.dataregistrocarona),'dd/MM/YYYY') as modalidade, " +
            "       emp.MODALIDADELICITACAO as MODALIDADELICITACAO, " +
            "       classe.codigo || ' - ' || classe.descricao as desc_classepessoa, " +
            "       COALESCE(pag.VALOR ,0) AS VALOR_PAGO, " +
            "       COALESCE(PAG.SALDO, 0) AS SALDO_ESTORNAR, " +
            "       COALESCE(endereco.bairro,'Pessoa não possui bairro cadastrado') as bairro, " +
            "       COALESCE(ENDERECO.uf,'sem UF ') AS UF, " +
            "       COALESCE(SUBSTR(ENDERECO.CEP,1,5)||'-'||SUBSTR(ENDERECO.CEP,6,3),'sem CEP') AS CEP, " +
            "       VW.SUBORDINADA_ID AS idUnidadeOrcamentaria, " +
            "       nota.id as idNota " +
            " FROM pagamentoestorno NOTA " +
            "  inner join pagamento pag on nota.pagamento_id = pag.id " +
            "  INNER JOIN LIQUIDACAO LIQ ON LIQ.ID = pag.liquidacao_id " +
            "  INNER JOIN EMPENHO EMP ON LIQ.EMPENHO_ID = EMP.ID " +
            (categoriaOrcamentaria.isResto() ? " inner join empenho emp_original on emp.empenho_id = emp_original.id " : "") +
            "  INNER JOIN fontedespesaorc FONTE ON EMP.fontedespesaorc_id = fonte.id " +
            "  INNER JOIN despesaorc DESPESA ON fonte.despesaorc_id = despesa.id " +
            "  INNER JOIN provisaoppadespesa PROVISAO ON despesa.provisaoppadespesa_id= provisao.id " +
            "  inner join provisaoppafonte ppf on ppf.id = fonte.PROVISAOPPAFONTE_ID " +
            "  INNER JOIN SUBACAOPPA SUB ON SUB.ID = provisao.subacaoppa_id " +
            "  INNER JOIN ACAOPPA A ON A.ID = sub.acaoppa_id " +
            "  INNER JOIN TIPOACAOPPA TPA ON TPA.ID = A.TIPOACAOPPA_ID " +
            "  INNER JOIN programappa P ON P.ID = A.programa_id " +
            "  INNER JOIN FUNCAO F ON F.ID = A.funcao_id " +
            "  INNER JOIN SUBFUNCAO SF ON SF.ID = A.subfuncao_id " +
            "  INNER JOIN CONTA CT_DESP ON provisao.contadedespesa_id = CT_DESP.ID " +
            "  INNER JOIN EXERCICIO EXERC ON EMP.EXERCICIO_ID = EXERC.ID " +
            "  INNER JOIN VWHIERARQUIAORCAMENTARIA VW ON EMP.UNIDADEORGANIZACIONAL_ID  = VW.SUBORDINADA_ID " +
            "  inner join vwhierarquiaorcamentaria vworg on vw.superior_id  = vworg.subordinada_id " +
            "  INNER JOIN PESSOA Pes ON emp.fornecedor_id = Pes.id " +
            "  inner join PAGAMENTO pg on pg.id = nota.PAGAMENTO_ID " +
            "  inner join subconta sc on pg.SUBCONTA_ID = sc.id " +
            "  inner join contabancariaentidade cbe on sc.CONTABANCARIAENTIDADE_ID = cbe.id " +
            "  left join pessoafisica pf on pes.id = pf.id " +
            "  left join pessoajuridica pj on pes.id = pj.id " +
            "  left join enderecocorreio endereco on pes.enderecoprincipal_id = endereco.id " +
            "  left join ContaCorrenteBancaria cc on pag.contapgto_id = cc.id " +
            "  left join agencia ag on cc.agencia_id =ag.id " +
            "  left join banco banco on ag.banco_id=banco.id " +
            "  left join contadedestinacao cd on cd.id = ppf.destinacaoderecursos_id " +
            "  left join conta cdest on cdest.id = cd.id " +
            "  left join fontederecursos fonte_r on fonte_r.id = cd.fontederecursos_id " +
            (categoriaOrcamentaria.isResto() ? "  and fonte_r.exercicio_id = emp_original.exercicio_id " : "  and fonte_r.exercicio_id = exerc.id ") +
            "  left join classecredor classe on emp.classecredor_id = classe.id " +
            "  LEFT JOIN CONTRATO CONTRATO ON EMP.CONTRATO_ID = CONTRATO.ID " +
            " left join conlicitacao conlic on conlic.contrato_id = contrato.id " +
            " left join licitacao lic on lic.id = conlic.licitacao_id " +
            "  left join condispensalicitacao conDisp on condisp.contrato_id = contrato.id " +
            "  left join dispensadelicitacao disp on disp.id = condisp.dispensadelicitacao_id " +
            "  left join CONREGPRECOEXTERNO conReg on conreg.contrato_id = contrato.id " +
            "  left join REGISTROSOLMATEXT regExt on regExt.id = conreg.regsolmatext_id " +
            "  WHERE trunc(NOTA.DATAESTORNO) BETWEEN VW.INICIOVIGENCIA AND COALESCE(VW.FIMVIGENCIA, trunc(NOTA.DATAESTORNO)) " +
            "      and trunc(nota.dataestorno) between vworg.iniciovigencia and coalesce(vworg.fimvigencia, trunc(nota.dataestorno)) " +
            "      and nota.categoriaorcamentaria = :categoriaOrcamentaria " +
            condicao;
        Query q = em.createNativeQuery(sql);
        q.setParameter("categoriaOrcamentaria", categoriaOrcamentaria.name());
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentaria> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentaria nota = new NotaExecucaoOrcamentaria();
                nota.setNumeroDocumento((String) obj[0]);
                nota.setDataEstorno(DataUtil.getDataFormatada((Date) obj[1]));
                nota.setHistorico((String) obj[2]);
                nota.setTipoEmpenho(obj[3] != null ? TipoEmpenhoDTO.valueOf((String) obj[3]).getDescricao() : "");
                nota.setCodigoOrgao((String) obj[4]);
                nota.setDescricaoOrgao((String) obj[5]);
                nota.setCodigoUnidade((String) obj[6]);
                nota.setDescricaoUnidade((String) obj[7]);
                nota.setCodigoProgramaTrabalho((String) obj[8]);
                nota.setNaturezaDespesa((String) obj[9]);
                nota.setEspecificacaoDespesa((String) obj[10]);
                nota.setNomePessoa((String) obj[11]);
                nota.setCpfCnpj((String) obj[12]);
                nota.setDescricaoDestinacao((String) obj[13]);
                nota.setValorEstorno((BigDecimal) obj[14]);
                nota.setValorPorExtenso((String) obj[15]);
                nota.setLogradouro((String) obj[16]);
                nota.setCidade((String) obj[17]);
                nota.setDescricaoAgencia((String) obj[18]);
                nota.setDescricaoBanco((String) obj[19]);
                nota.setNumeroBanco((String) obj[20]);
                nota.setNumeroAgencia((String) obj[21]);
                nota.setDigitoVerificador((String) obj[22]);
                nota.setNumeroContaCorrente((String) obj[23]);
                nota.setDigitoVerificadorContaCorrente((String) obj[24]);
                nota.setTipoContaBancaria(obj[25] != null ? TipoContaBancariaDTO.valueOf((String) obj[25]).getDescricao() : "");
                nota.setSituacaoContaBancaria(obj[26] != null ? SituacaoContaDTO.valueOf((String) obj[26]).getDescricao() : "");
                nota.setBancoAgenciaConta((String) obj[27]);
                nota.setBancoContaBancariaEntidade((String) obj[28]);
                nota.setDescricaoProjetoAtividade((String) obj[29]);
                nota.setModalidadeLicitacao((obj[31] != null ? ModalidadeLicitacaoEmpenhoDTO.valueOf((String) obj[31]).getDescricao() : "") + " " + obj[30]);
                nota.setClassePessoa((String) obj[32]);
                nota.setValor((BigDecimal) obj[33]);
                nota.setSaldoAtual((BigDecimal) obj[34]);
                nota.setBairro((String) obj[35]);
                nota.setUf((String) obj[36]);
                nota.setCep((String) obj[37]);
                nota.setIdUnidadeOrcamentaria((BigDecimal) obj[38]);
                nota.setId(((BigDecimal) obj[39]).longValue());
                nota.setRetencoes(buscarRetencoes(nota.getId()));
                retorno.add(nota);
            }
        }
        return retorno;
    }

    private List<NotaExecucaoOrcamentariaRetencao> buscarRetencoes(Long idEstPagamento) {
        String sql = " select " +
            "        'Nº ' || coalesce(REE.NUMERO, ret.numero) as numero, " +
            "        REE.dataestorno as dataRet, " +
            "       'Conta Extra: ' || C.CODIGO || ' - ' || TRIM(C.DESCRICAO) AS RETENCAO, " +
            "       COALESCE(RET.VALOR, 0) AS VALOR_RETENCAO " +
            " FROM RETENCAOPGTO RET " +
            " LEFT JOIN RECEITAEXTRA RE ON RET.ID = RE.RETENCAOPGTO_ID " +
            " left join RECEITAEXTRAESTORNO REE ON RE.ID = REE.RECEITAEXTRA_ID " +
            " INNER JOIN CONTA C ON C.ID = RET.CONTAEXTRAORCAMENTARIA_ID " +
            " inner join subconta sub on sub.id = ret.subconta_id " +
            " WHERE RET.PAGAMENTOESTORNO_ID = :idEstPagamento ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idEstPagamento", idEstPagamento);
        List<Object[]> resultado = q.getResultList();
        List<NotaExecucaoOrcamentariaRetencao> retorno = Lists.newArrayList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                NotaExecucaoOrcamentariaRetencao retencao = new NotaExecucaoOrcamentariaRetencao();
                retencao.setNumero((String) obj[0]);
                retencao.setDataRet((Date) obj[1]);
                retencao.setContaExtra((String) obj[2]);
                retencao.setValor((BigDecimal) obj[3]);
                retorno.add(retencao);
            }
        }
        return retorno;
    }
}

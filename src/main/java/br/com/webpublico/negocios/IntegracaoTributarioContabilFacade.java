package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.IntegracaoTributarioContabilFiltros;
import br.com.webpublico.enums.*;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.negocios.tributario.dao.JdbcArrecadacaoDAO;
import br.com.webpublico.singletons.SingletonArrecadacaoTributaria;
import br.com.webpublico.singletons.SingletonGeradorCodigoContabil;
import br.com.webpublico.util.AssistenteBarraProgresso;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.FacesUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jboss.ejb3.annotation.TransactionTimeout;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.faces.application.FacesMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Stateless
public class IntegracaoTributarioContabilFacade implements Serializable {

    private final Long SEQUENCIA_FONTE = 1L;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ReceitaLOAFacade receitaLOAFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private DividaAtivaContabilFacade dividaAtivaContabilFacade;
    @EJB
    private CreditoReceberFacade creditoReceberFacade;
    @EJB
    private SingletonGeradorCodigoContabil singletonGeradorCodigoContabil;
    @EJB
    private LancamentoReceitaOrcFacade lancamentoReceitaOrcFacade;
    @EJB
    private ReceitaORCEstornoFacade receitaORCEstornoFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SingletonArrecadacaoTributaria singletonArrecadacaoTributaria;
    private JdbcArrecadacaoDAO arrecadacaoDAO;

    public JdbcArrecadacaoDAO getArrecadacaoDAO() {
        if (arrecadacaoDAO == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            arrecadacaoDAO = (JdbcArrecadacaoDAO) ap.getBean("arrecadacaoDAO");
        }
        return arrecadacaoDAO;
    }

    public SingletonArrecadacaoTributaria getSingletonArrecadacaoTributaria() {
        return singletonArrecadacaoTributaria;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<CreditoReceber> getCreditoReceberArrecadacaoContabilizar(IntegracaoTributarioContabilFiltros filtros, TipoIntegracao tipoIntegracao) {
        List<CreditoReceber> creditos = Lists.newArrayList();
        Map<LoteIntegracaoTributarioContabil, BigDecimal> arrecadacoesPrincipais = getMapaIntegracao(tipoIntegracao, filtros,
            OperacaoReceita.RECEITA_CREDITOS_RECEBER_BRUTA, OperacaoIntegracaoTribCont.PRINCIPAL);

        Map<LoteIntegracaoTributarioContabil, BigDecimal> arrecadacoesMultasEJuros = getMapaIntegracao(tipoIntegracao, filtros,
            OperacaoReceita.RECEITA_CREDITOS_RECEBER_BRUTA, OperacaoIntegracaoTribCont.MULTA_JUROS_CORRECAO);

        if (TipoIntegracao.ARRECADACAO.equals(tipoIntegracao)) {
            creditos.addAll(gerarCreditoReceber(arrecadacoesMultasEJuros, OperacaoCreditoReceber.ATUALIZACAO_DE_CREDITOS_A_RECEBER, TipoLancamento.NORMAL));
            creditos.addAll(gerarCreditoReceber(arrecadacoesPrincipais, OperacaoCreditoReceber.RECONHECIMENTO_CREDITO_A_RECEBER, TipoLancamento.NORMAL));
        } else {
            creditos.addAll(gerarCreditoReceber(arrecadacoesMultasEJuros, OperacaoCreditoReceber.ATUALIZACAO_DE_CREDITOS_A_RECEBER, TipoLancamento.ESTORNO));
            creditos.addAll(gerarCreditoReceber(arrecadacoesPrincipais, OperacaoCreditoReceber.RECONHECIMENTO_CREDITO_A_RECEBER, TipoLancamento.ESTORNO));
        }

        Collections.sort(creditos, new Comparator<CreditoReceber>() {
            @Override
            public int compare(CreditoReceber sl1, CreditoReceber sl2) {
                return sl1.getLoteBaixa().getCodigoLote().compareTo(sl2.getLoteBaixa().getCodigoLote()) | sl1.getDataCredito().compareTo(sl2.getDataCredito());
            }
        });
        return creditos;
    }

    @Deprecated
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<CreditoReceber> getCreditoReceberContabilizar(IntegracaoTributarioContabilFiltros filtros) {
        List<CreditoReceber> creditos = Lists.newArrayList();
        Map<LoteIntegracaoTributarioContabil, BigDecimal> inscricao = getMapaIntegracao(TipoIntegracao.INSCRICAO, filtros, null, null);
        Map<LoteIntegracaoTributarioContabil, BigDecimal> cancelamento = getMapaIntegracao(TipoIntegracao.ESTORNO_INSCRICAO, filtros, null, null);
        Map<LoteIntegracaoTributarioContabil, BigDecimal> baixa = getMapaIntegracao(TipoIntegracao.BAIXA_INSCRICAO, filtros, null, null);
        em.flush();
        creditos.addAll(gerarCreditoReceber(inscricao, OperacaoCreditoReceber.RECONHECIMENTO_CREDITO_A_RECEBER, TipoLancamento.NORMAL));
        creditos.addAll(gerarCreditoReceber(baixa, OperacaoCreditoReceber.BAIXA_RECONHECIMENTO_CREDITO_A_RECEBER, TipoLancamento.NORMAL));
        creditos.addAll(gerarCreditoReceber(cancelamento, OperacaoCreditoReceber.RECONHECIMENTO_CREDITO_A_RECEBER, TipoLancamento.ESTORNO));

        Collections.sort(creditos, new Comparator<CreditoReceber>() {
            @Override
            public int compare(CreditoReceber sl1, CreditoReceber sl2) {
                return sl1.getDataCredito().compareTo(sl2.getDataCredito());
            }
        });

        return creditos;
    }

    private List<CreditoReceber> gerarCreditoReceber(Map<LoteIntegracaoTributarioContabil, BigDecimal> mapa, OperacaoCreditoReceber operacao, TipoLancamento tipo) {
        List<CreditoReceber> creditos = Lists.newArrayList();
        for (LoteIntegracaoTributarioContabil lote : mapa.keySet()) {
            ReceitaLOA receitaLOA = receitaLOAFacade.receitaPorContaUnidadeExercicio(lote.getContaReceita(), lote.getOrcamentaria(),
                sistemaFacade.getExercicioCorrente(), lote.getSubConta(), lote.getOperacaoReceitaRealizada(),
                lote.getItens().get(0).getItem().getFonteDeRecursos());
            ReceitaLOAFonte receitaLOAFonte = buscarReceitaLoaFonte(lote, receitaLOA);

            lote.setValor(mapa.get(lote));
            CreditoReceber credito = new CreditoReceber();
            credito.setReceitaLOA(receitaLOA);
            credito.setValor(lote.getValor());
            credito.setUnidadeOrganizacionalAdm(lote.getAdministrativa());
            credito.setUnidadeOrganizacional(lote.getOrcamentaria());
            credito.setDataCredito(lote.getDataIntegracao());
            credito.setDataReferencia(lote.getDataIntegracao());
            credito.setOperacaoCreditoReceber(operacao);
            credito.setTipoLancamento(tipo);
            credito.setIntegracaoTribCont(lote);
            credito.setExercicio(sistemaFacade.getExercicioCorrente());
            credito.setLoteBaixa(lote.getLoteBaixa());
            credito.setIntegracao(true);
            credito.setIntervalo(Intervalo.CURTO_PRAZO);
            credito.setNaturezaCreditoReceber(NaturezaDividaAtivaCreditoReceber.ORIGINAL);
            credito.setContaDeDestinacao(contaFacade.recuperarContaDestinacaoPorFonte(receitaLOAFonte.getFonteRecurso()));

            if (lote.getLoteBaixa() != null) {
                credito.setHistorico(lote.getContaReceita() + ", Lote: " + lote.getLoteBaixa().getCodigoLote());
            } else {
                credito.setHistorico(lote.getContaReceita() + ", Lote gerado automaticamente.");
            }
            creditos.add(credito);
        }
        return creditos;
    }

    private ReceitaLOAFonte buscarReceitaLoaFonte(LoteIntegracaoTributarioContabil lote, ReceitaLOA receitaLOA) {
        ReceitaLOAFonte receitaLOAFonte = null;
        Boolean achou = Boolean.FALSE;

        if (lote.getItens() != null && !lote.getItens().isEmpty()) {
            FonteDeRecursos fonteDeRecursos = lote.getItens().get(0).getItem().getFonteDeRecursos();
            if (fonteDeRecursos != null) {
                for (ReceitaLOAFonte receitaLoaFonte : receitaLOA.getReceitaLoaFontes()) {
                    if (receitaLoaFonte.getFonteRecurso().equals(fonteDeRecursos)) {
                        return receitaLoaFonte;
                    }
                }
            }
        }

        for (SubContaFonteRec subContaFonteRec : lote.getSubConta().getSubContaFonteRecs()) {
            if (subContaFonteRec.getFonteDeRecursos().getExercicio().equals(sistemaFacade.getExercicioCorrente())) {
                for (ReceitaLOAFonte fonte : receitaLOA.getReceitaLoaFontes()) {
                    if (subContaFonteRec.getFonteDeRecursos().getId().equals(fonte.getFonteRecurso().getId())) {
                        if (achou) {
                            throw new ExcecaoNegocioGenerica("Foi encontrada mais de uma fonte de recurso para a receita LOA " + receitaLOA.getContaDeReceita().getCodigo() + " e conta financeira " + lote.getSubConta().getCodigo() + ".");
                        }
                        receitaLOAFonte = fonte;
                        achou = Boolean.TRUE;
                    }
                }
            }
        }
        if (receitaLOAFonte == null) {
            throw new ExcecaoNegocioGenerica("Nenhuma fonte foi encontrada para a receita LOA " + receitaLOA.getContaDeReceita().getCodigo() + " e conta financeira " + lote.getSubConta().getCodigo() + ".");
        }
        return receitaLOAFonte;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<DividaAtivaContabil> getDividaAtivaArrecadacaoContabilizar(IntegracaoTributarioContabilFiltros filtros, TipoIntegracao tipoIntegracao) {
        List<DividaAtivaContabil> dividas = Lists.newArrayList();
        Map<LoteIntegracaoTributarioContabil, BigDecimal> arrecadacoes = getMapaIntegracao(tipoIntegracao, filtros,
            OperacaoReceita.RECEITA_DIVIDA_ATIVA_BRUTA, OperacaoIntegracaoTribCont.MULTA_JUROS_CORRECAO);

        if (TipoIntegracao.ARRECADACAO.equals(tipoIntegracao)) {
            dividas.addAll(gerarLancamentoDividaAtiva(arrecadacoes, OperacaoDividaAtiva.ATUALIZACAO, TipoLancamento.NORMAL));
        } else {
            dividas.addAll(gerarLancamentoDividaAtiva(arrecadacoes, OperacaoDividaAtiva.ATUALIZACAO, TipoLancamento.ESTORNO));
        }

        Collections.sort(dividas, new Comparator<DividaAtivaContabil>() {
            @Override
            public int compare(DividaAtivaContabil sl1, DividaAtivaContabil sl2) {
                return sl1.getLoteBaixa().getCodigoLote().compareTo(sl2.getLoteBaixa().getCodigoLote()) | sl1.getDataDivida().compareTo(sl2.getDataDivida());
            }
        });
        return dividas;
    }

    @Deprecated
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<DividaAtivaContabil> getDividaAtivaContabilizar(IntegracaoTributarioContabilFiltros filtros) {
        List<DividaAtivaContabil> lancamentos = Lists.newArrayList();
        Map<LoteIntegracaoTributarioContabil, BigDecimal> inscricao = getMapaIntegracao(TipoIntegracao.DIVIDA_ATIVA, filtros, null, null);
        Map<LoteIntegracaoTributarioContabil, BigDecimal> cancelamento = getMapaIntegracao(TipoIntegracao.CANCELAMENTO_DIVIDA_ATIVA, filtros, null, null);
        Map<LoteIntegracaoTributarioContabil, BigDecimal> baixa = getMapaIntegracao(TipoIntegracao.BAIXA_DIVIDA_ATIVA, filtros, null, null);

        lancamentos.addAll(gerarLancamentoDividaAtiva(inscricao, OperacaoDividaAtiva.INSCRICAO, TipoLancamento.NORMAL));
        lancamentos.addAll(gerarLancamentoDividaAtiva(cancelamento, OperacaoDividaAtiva.INSCRICAO, TipoLancamento.ESTORNO));
        lancamentos.addAll(gerarLancamentoDividaAtiva(baixa, OperacaoDividaAtiva.BAIXA, TipoLancamento.NORMAL));

        Collections.sort(lancamentos, new Comparator<DividaAtivaContabil>() {
            @Override
            public int compare(DividaAtivaContabil sl1, DividaAtivaContabil sl2) {
                return sl1.getDataDivida().compareTo(sl2.getDataDivida());
            }
        });
        return lancamentos;
    }

    private List<DividaAtivaContabil> gerarLancamentoDividaAtiva(Map<LoteIntegracaoTributarioContabil, BigDecimal> mapa, OperacaoDividaAtiva operacao, TipoLancamento tipo) {
        List<DividaAtivaContabil> lancamentos = Lists.newArrayList();
        for (LoteIntegracaoTributarioContabil lote : mapa.keySet()) {

            lote.setValor(mapa.get(lote));
            ReceitaLOA receitaLOA = receitaLOAFacade.receitaPorContaUnidadeExercicio(lote.getContaReceita(), lote.getOrcamentaria(),
                sistemaFacade.getExercicioCorrente(), lote.getSubConta(), lote.getOperacaoReceitaRealizada(),
                lote.getItens().get(0).getItem().getFonteDeRecursos());
            ReceitaLOAFonte receitaLOAFonte = buscarReceitaLoaFonte(lote, receitaLOA);

            DividaAtivaContabil lancamento = new DividaAtivaContabil();
            lancamento.setReceitaLOA(receitaLOA);
            lancamento.setValor((lote.getValor().multiply(receitaLOAFonte.getPercentual())).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
            lancamento.setUnidadeOrganizacionalAdm(lote.getAdministrativa());
            lancamento.setUnidadeOrganizacional(lote.getOrcamentaria());
            lancamento.setTipoLancamento(tipo);
            lancamento.setDataDivida(lote.getDataIntegracao());
            lancamento.setDataReferencia(lote.getDataIntegracao());
            lancamento.setOperacaoDividaAtiva(operacao);
            lancamento.setIntegracaoTribCont(lote);
            lancamento.setLoteBaixa(lote.getLoteBaixa());
            lancamento.setIntervalo(Intervalo.CURTO_PRAZO);
            lancamento.setNaturezaDividaAtiva(NaturezaDividaAtivaCreditoReceber.ORIGINAL);
            lancamento.setContaDeDestinacao(contaFacade.recuperarContaDestinacaoPorFonte(receitaLOAFonte.getFonteRecurso()));
            lancamento.setIntegracao(true);
            if (lote.getLoteBaixa() != null) {
                lancamento.setHistorico(lote.getContaReceita() + ", Lote: " + lote.getLoteBaixa().getCodigoLote());
            } else {
                lancamento.setHistorico(lote.getContaReceita() + ", Lote gerado automaticamente.");
            }
            lancamento.setExercicio(sistemaFacade.getExercicioCorrente());
            lancamentos.add(lancamento);
        }
        return lancamentos;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<LancamentoReceitaOrc> getLancamentoReceitaOrcContabilizar(IntegracaoTributarioContabilFiltros filtros) {
        List<LancamentoReceitaOrc> lancamentos = Lists.newArrayList();
        Map<LoteIntegracaoTributarioContabil, BigDecimal> arrecadacoes = getMapaIntegracao(TipoIntegracao.ARRECADACAO, filtros, null, null);
        lancamentos.addAll(gerarLancamentoReceitaOrc(arrecadacoes));
        lancamentos.sort((o1, o2) -> o1.getLote().compareTo(o2.getLote()) | o1.getDataLancamento().compareTo(o2.getDataLancamento()));
        return lancamentos;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public List<ReceitaORCEstorno> getLancamentoReceitaOrcEstornoContabilizar(IntegracaoTributarioContabilFiltros filtros) {
        List<ReceitaORCEstorno> lancamentos = Lists.newArrayList();
        Map<LoteIntegracaoTributarioContabil, BigDecimal> estornos = getMapaIntegracao(TipoIntegracao.ESTORNO_ARRECADACAO, filtros, null, null);
        lancamentos.addAll(gerarLancamentoReceitaOrcEstorno(estornos));
        Collections.sort(lancamentos, new Comparator<ReceitaORCEstorno>() {
            @Override
            public int compare(ReceitaORCEstorno sl1, ReceitaORCEstorno sl2) {
                return sl1.getDataEstorno().compareTo(sl2.getDataEstorno());
            }
        });
        return lancamentos;
    }

    private List<LancamentoReceitaOrc> gerarLancamentoReceitaOrc(Map<LoteIntegracaoTributarioContabil, BigDecimal> mapa) {
        List<LancamentoReceitaOrc> lancamentos = Lists.newArrayList();
        for (LoteIntegracaoTributarioContabil lote : mapa.keySet()) {
            lote.setValor(mapa.get(lote));
            LancamentoReceitaOrc lancamento = new LancamentoReceitaOrc();

            ReceitaLOA receitaLOA = receitaLOAFacade.receitaPorContaUnidadeExercicio(lote.getContaReceita(),
                lote.getOrcamentaria(), sistemaFacade.getExercicioCorrente(), lote.getSubConta(), lote.getOperacaoReceitaRealizada(),
                lote.getItens().get(0).getItem().getFonteDeRecursos());

            lancamento.setReceitaLOA(receitaLOA);
            lancamento.setValor(lote.getValor());
            lancamento.setSaldo(lote.getValor());
            lancamento.setUnidadeOrganizacionalAdm(lote.getAdministrativa());
            lancamento.setUnidadeOrganizacional(lote.getOrcamentaria());
            lancamento.setDataLancamento(lote.getDataIntegracao());
            lancamento.setDataConciliacao(lote.getDataConciliacao());
            lancamento.setDataReferencia(lote.getDataIntegracao());
            lancamento.setSubConta(lote.getSubConta());
            lancamento.setIntegracaoTribCont(lote);
            lancamento.setOperacaoReceitaRealizada(lancamento.getReceitaLOA().getOperacaoReceita());
            lancamento.setTipoOperacao(TipoOperacaoORC.NORMAL);

            if (lote.getItens() != null && !lote.getItens().isEmpty()) {
                FonteDeRecursos fonteDeRecursos = lote.getItens().get(0).getItem().getFonteDeRecursos();
                if (fonteDeRecursos != null) {
                    LancReceitaFonte fonte = new LancReceitaFonte();
                    fonte.setItem(SEQUENCIA_FONTE);
                    fonte.setValor(lancamento.getValor());
                    fonte.setLancReceitaOrc(lancamento);
                    for (ReceitaLOAFonte receitaLoaFonte : receitaLOA.getReceitaLoaFontes()) {
                        if (receitaLoaFonte.getFonteRecurso().equals(fonteDeRecursos)) {
                            fonte.setReceitaLoaFonte(receitaLoaFonte);
                        }
                    }
                    if (fonte.getReceitaLoaFonte() == null) {
                        throw new ValidacaoException("A Fonte de Recursos utilizada para a Conta <b>" + lote.getContaReceita() + "</b> é a <b>" + fonteDeRecursos + "</b> " +
                            "e não foi encontrada na Receita Loa <a href=" + FacesUtil.getRequestContextPath() + "/receita-loa/passo-a-passo/contas/" + receitaLOA.getLoa().getId() + "/editar-conta/" + receitaLOA.getId() +
                            "/ target='blank' style='font-weight: bold !important; color: #497692'>" + receitaLOA + "</a>."
                        );
                    }
                    lancamento.setLancReceitaFonte(Lists.newArrayList(fonte));
                }
            }

            if (lote.getLoteBaixa() != null) {
                lancamento.setLote(lote.getLoteBaixa().getCodigoLote());
                lancamento.setComplemento(lote.getContaReceita() + ", Lote: " + lote.getLoteBaixa().getCodigoLote());
            } else {
                lancamento.setComplemento(lote.getContaReceita() + ", Lote gerado automaticamente.");
            }
            lancamento.setExercicio(sistemaFacade.getExercicioCorrente());
            lancamentos.add(lancamento);
        }
        return lancamentos;
    }

    private List<ReceitaORCEstorno> gerarLancamentoReceitaOrcEstorno(Map<LoteIntegracaoTributarioContabil, BigDecimal> mapa) {
        List<ReceitaORCEstorno> lancamentos = Lists.newArrayList();
        for (LoteIntegracaoTributarioContabil lote : mapa.keySet()) {
            lote.setValor(mapa.get(lote));
            ReceitaORCEstorno lancamento = new ReceitaORCEstorno();

            ReceitaLOA receitaLOA = receitaLOAFacade.receitaPorContaUnidadeExercicio(lote.getContaReceita(),
                lote.getOrcamentaria(), sistemaFacade.getExercicioCorrente(), lote.getSubConta(), lote.getOperacaoReceitaRealizada(),
                lote.getItens().get(0).getItem().getFonteDeRecursos());

            lancamento.setReceitaLOA(receitaLOA);
            lancamento.setValor(lote.getValor());
            lancamento.setUnidadeOrganizacionalAdm(lote.getAdministrativa());
            lancamento.setUnidadeOrganizacionalOrc(lote.getOrcamentaria());
            lancamento.setDataEstorno(lote.getDataIntegracao());
            lancamento.setDataConciliacao(lote.getDataConciliacao());
            lancamento.setDataReferencia(lote.getDataIntegracao());
            lancamento.setContaFinanceira(lote.getSubConta());
            lancamento.setOperacaoReceitaRealizada(lancamento.getReceitaLOA().getOperacaoReceita());
            lancamento.setIntegracaoTribCont(lote);

            if (lote.getItens() != null && !lote.getItens().isEmpty()) {
                FonteDeRecursos fonteDeRecursos = lote.getItens().get(0).getItem().getFonteDeRecursos();
                if (fonteDeRecursos != null) {
                    ReceitaORCFonteEstorno fonte = new ReceitaORCFonteEstorno();
                    fonte.setItem(SEQUENCIA_FONTE);
                    fonte.setValor(lancamento.getValor());
                    fonte.setReceitaORCEstorno(lancamento);
                    for (ReceitaLOAFonte receitaLoaFonte : receitaLOA.getReceitaLoaFontes()) {
                        if (receitaLoaFonte.getFonteRecurso().equals(fonteDeRecursos)) {
                            fonte.setReceitaLoaFonte(receitaLoaFonte);
                        }
                    }
                    lancamento.setReceitaORCFonteEstorno(Lists.newArrayList(fonte));
                }
            }

            if (lote.getLoteBaixa() != null) {
                lancamento.setLote(lote.getLoteBaixa().getCodigoLote());
                lancamento.setComplementoHistorico(lote.getContaReceita() + ", Lote: " + lote.getLoteBaixa().getCodigoLote());
            } else {
                lancamento.setComplementoHistorico(lote.getContaReceita() + ", Lote gerado automaticamente.");
            }
            lancamento.setExercicio(sistemaFacade.getExercicioCorrente());
            lancamentos.add(lancamento);
        }
        return lancamentos;
    }

    private Map<LoteIntegracaoTributarioContabil, BigDecimal> getMapaIntegracao(TipoIntegracao tipo, IntegracaoTributarioContabilFiltros filtros, OperacaoReceita operacaoReceita, OperacaoIntegracaoTribCont operacaoIntegracaoTribCont) {
        Map<LoteIntegracaoTributarioContabil, BigDecimal> mapa = new HashMap<>();
        List<ItemIntegracaoTributarioContabil> itens = buscarItensIntegracaoTributarioCOntabil(tipo, filtros, operacaoReceita, operacaoIntegracaoTribCont);
        Map<KeyIntegracao, List<ItemIntegracaoTributarioContabil>> mapaPrevio = Maps.newHashMap();

        for (ItemIntegracaoTributarioContabil item : itens) {
            KeyIntegracao key = new KeyIntegracao(item);
            if (!mapaPrevio.containsKey(key)) {
                mapaPrevio.put(key, new ArrayList<ItemIntegracaoTributarioContabil>());
            }
            mapaPrevio.get(key).add(item);
        }
        for (KeyIntegracao key : mapaPrevio.keySet()) {
            LoteIntegracaoTributarioContabil lote = new LoteIntegracaoTributarioContabil();
            lote.setAdministrativa(key.getAdministrativa());
            lote.setOrcamentaria(key.getOrcamentaria());
            lote.setContaReceita(key.getContareceita());
            lote.setSubConta(key.getSubConta());
            lote.setDataIntegracao(key.getDia());
            lote.setDataConciliacao(key.getDataConciliacao());
            lote.setLoteBaixa(key.getLoteBaixa());
            lote.setOperacaoReceitaRealizada(key.getOperacaoReceitaRealizada());
            if (!mapa.containsKey(lote)) {
                mapa.put(lote, BigDecimal.ZERO);
            }
            for (ItemIntegracaoTributarioContabil item : mapaPrevio.get(key)) {
                IntegracaoTributarioContabil integracao = new IntegracaoTributarioContabil();
                integracao.setItem(item);
                integracao.setLote(lote);
                lote.getItens().add(integracao);
                mapa.put(lote, mapa.get(lote).add(item.getValor()));
            }
        }
        return mapa;
    }

    private List<ItemIntegracaoTributarioContabil> buscarItensIntegracaoTributarioCOntabil(TipoIntegracao tipo, IntegracaoTributarioContabilFiltros filtros, OperacaoReceita operacaoReceita, OperacaoIntegracaoTribCont operacaoIntegracaoTribCont) {
        StringBuilder hql = new StringBuilder("select * from itemintegracaotribcont " +
            " where ID NOT IN (SELECT ITEM_ID FROM INTEGRACAOTRIBCONT) and tipo = :tipo");
        if (filtros.getUnidadeOrganizacional() != null) {
            hql.append(" and orcamentaria_id = :unidade ");
        }
        if (filtros.getDataInicial() != null) {
            hql.append(" and trunc(dataPagamento) >= to_date(:dataInicial, 'dd/MM/yyyy') ");
        }
        if (filtros.getDataFinal() != null) {
            hql.append(" and trunc(dataPagamento) <= to_date(:dataFinal, 'dd/MM/yyyy') ");
        }
        if (filtros.getContaReceita() != null) {
            hql.append(" and contaReceita_id = :conta ");
        }
        if (!filtros.getLotesBaixa().isEmpty()) {
            hql.append(" and lotebaixa_id in (:lote)");
        } else {
            hql.append(" and lotebaixa_id is not null");
        }
        if (operacaoReceita != null) {
            hql.append(" and operacaoReceitaRealizada = :operacaoReceita");
        }
        if (operacaoIntegracaoTribCont != null) {
            hql.append(" and operacaoIntegracaoTribCont = :operacaoIntegracao");
        }
        Query q = em.createNativeQuery(hql.toString(), ItemIntegracaoTributarioContabil.class);
        q.setParameter("tipo", tipo.name());
        if (filtros.getUnidadeOrganizacional() != null) {
            q.setParameter("unidade", filtros.getUnidadeOrganizacional().getId());
        }
        if (filtros.getDataInicial() != null) {
            q.setParameter("dataInicial", DataUtil.getDataFormatada(filtros.getDataInicial()));
        }
        if (filtros.getDataFinal() != null) {
            q.setParameter("dataFinal", DataUtil.getDataFormatada(filtros.getDataFinal()));
        }
        if (filtros.getContaReceita() != null) {
            q.setParameter("conta", filtros.getContaReceita().getId());
        }
        if (!filtros.getLotesBaixa().isEmpty()) {
            q.setParameter("lote", filtros.getLotesBaixa().stream().map(obj -> obj.getId()).collect(Collectors.toList()));
        }
        if (operacaoReceita != null) {
            q.setParameter("operacaoReceita", operacaoReceita.name());
        }
        if (operacaoIntegracaoTribCont != null) {
            q.setParameter("operacaoIntegracao", operacaoIntegracaoTribCont.name());
        }
        return q.getResultList();
    }

    public Map<Tributo, BigDecimal> getMapaTributoValorNaoIntegrados() {
        Map<Tributo, BigDecimal> naoIntegrados = Maps.newHashMap();
        Query q = em.createQuery("from ItemVDNaoIntegrado vd where coalesce(vd.resolvido, false) = false ");
        for (ItemVDNaoIntegrado vd : (List<ItemVDNaoIntegrado>) q.getResultList()) {
            BigDecimal valor = vd.getValor();
            if (naoIntegrados.containsKey(vd.getItemValorDivida().getTributo())) {
                valor = valor.add(naoIntegrados.get(vd.getItemValorDivida().getTributo()));
            }
            naoIntegrados.put(vd.getItemValorDivida().getTributo(), valor);
        }
        return naoIntegrados;
    }

    private void montarDescricaoProcessoAssincronoIntegracao(AssistenteBarraProgresso assistenteBarraProgresso, String descricaoEtapa, Integer etapaAtual, Integer totalEtapas, Object numeroLote, boolean estorno) {
        String textoPadrao = (estorno ? "Estorno de importação" : "Importação") + " de Receitas Tributárias na Contabilidade.";
        StringBuilder descricao = new StringBuilder(textoPadrao);
        if (etapaAtual != null && totalEtapas != null) {
            descricao.append(" Etapa ").append(etapaAtual).append("/").append(totalEtapas).append(".");
        }
        descricao.append((estorno ? " Estornando " : " Importando ")).append(descricaoEtapa).append(".");
        if (numeroLote != null) {
            descricao.append(" (lote:").append(numeroLote).append(")");
        }
        assistenteBarraProgresso.setDescricaoProcesso(descricao.toString());
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<LancamentoReceitaOrc> importarReceitasTributariasArrecadacao(List<LancamentoReceitaOrc> lancamentoIntegracao, List<CreditoReceber> creditosReceberIntegracao, List<DividaAtivaContabil> dividasAtivaIntegracao, AssistenteBarraProgresso assistenteBarraProgresso) throws ValidacaoException, ExcecaoNegocioGenerica {
        List<LancamentoReceitaOrc> lancamentosLancados = importarReceitasRealizadas(lancamentoIntegracao, assistenteBarraProgresso);
        importarReceitaCreditoReceber(creditosReceberIntegracao, assistenteBarraProgresso, false);
        importarReceitasDividaAtiva(dividasAtivaIntegracao, assistenteBarraProgresso, false);
        singletonArrecadacaoTributaria.setExecutandoIntegracao(false);
        return lancamentosLancados;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 4)
    public List<ReceitaORCEstorno> importarReceitasTributariasEstorno(List<ReceitaORCEstorno> lancamentoIntegracao, List<CreditoReceber> creditosReceberIntegracao, List<DividaAtivaContabil> dividasAtivaIntegracao, AssistenteBarraProgresso assistenteBarraProgresso) throws ValidacaoException, ExcecaoNegocioGenerica {
        List<ReceitaORCEstorno> lancamentosLancados = importarReceitasRealizadasEstorno(lancamentoIntegracao, assistenteBarraProgresso);
        importarReceitaCreditoReceber(creditosReceberIntegracao, assistenteBarraProgresso, true);
        importarReceitasDividaAtiva(dividasAtivaIntegracao, assistenteBarraProgresso, true);
        singletonArrecadacaoTributaria.setExecutandoIntegracao(false);
        return lancamentosLancados;
    }

    private List<LancamentoReceitaOrc> importarReceitasRealizadas(List<LancamentoReceitaOrc> lancamentos, AssistenteBarraProgresso assistenteBarraProgresso) throws ValidacaoException, ExcecaoNegocioGenerica {
        List<LancamentoReceitaOrc> lancamentoIntegracaoDeuCerto = Lists.newArrayList();
        for (LancamentoReceitaOrc lro : lancamentos) {
            try {
                montarDescricaoProcessoAssincronoIntegracao(assistenteBarraProgresso, "receitas realizadas", 1, 3, lro.getLote(), false);
                definirEventoReceitaRealizada(lro);
                lancamentoReceitaOrcFacade.gerarLancamentoIntegracao(lro);
                validarLancamentoReceita(lro);
                lancamentoReceitaOrcFacade.salvarNovaReceita(lro, null);
                lancamentoIntegracaoDeuCerto.add(lro);
                assistenteBarraProgresso.conta();
            } catch (ValidacaoException ve) {
                for (FacesMessage facesMessage : ve.getMensagens()) {
                    assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(facesMessage);
                }
            } catch (Exception ex) {
                assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(ex.getMessage()));
            }
        }
        return lancamentoIntegracaoDeuCerto;
    }

    private List<ReceitaORCEstorno> importarReceitasRealizadasEstorno(List<ReceitaORCEstorno> lancamentos, AssistenteBarraProgresso assistenteBarraProgresso) throws ValidacaoException, ExcecaoNegocioGenerica {
        List<ReceitaORCEstorno> lancamentoIntegracaoDeuCerto = Lists.newArrayList();
        for (ReceitaORCEstorno roe : lancamentos) {
            montarDescricaoProcessoAssincronoIntegracao(assistenteBarraProgresso, "receitas realizadas", 1, 3, roe.getLote(), true);
            try {
                definirEventoReceitaRealizadaEstorno(roe);
                receitaORCEstornoFacade.gerarLancamentoIntegracao(roe);
                validarEstornoReceita(roe);
                roe = receitaORCEstornoFacade.salvaEstornoReceitaRealizada(roe);
                receitaORCEstornoFacade.geraSaldosReceitarOrcEstorno(roe);
                receitaORCEstornoFacade.contabilizarReceitaOrcEstorno(roe);
                lancamentoIntegracaoDeuCerto.add(roe);
                assistenteBarraProgresso.conta();
            } catch (ValidacaoException ve) {
                for (FacesMessage facesMessage : ve.getMensagens()) {
                    assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(facesMessage);
                }
            } catch (Exception ex) {
                assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(ex.getMessage()));
            }
        }
        return lancamentoIntegracaoDeuCerto;
    }

    public void importarReceitasDividaAtiva(List<DividaAtivaContabil> lancamentos, AssistenteBarraProgresso assistenteBarraProgresso, boolean estorno) throws ValidacaoException {
        for (DividaAtivaContabil dividaAtiva : lancamentos) {
            try {
                if (dividaAtiva.getLoteBaixa() != null) {
                    montarDescricaoProcessoAssincronoIntegracao(assistenteBarraProgresso, "receitas de dívida ativa", 3, 3, dividaAtiva.getLoteBaixa().getCodigoLote(), estorno);
                }
                dividaAtivaContabilFacade.definirContaReceitaNaDividaAtiva(dividaAtiva);
                if (dividaAtiva.getNumero() == null) {
                    dividaAtiva.setNumero(singletonGeradorCodigoContabil.getNumeroDividaAtivaContabil(dividaAtiva.getExercicio(), dividaAtiva.getDataDivida()));
                }
                definirEventoDividaAtiva(dividaAtiva);
                dividaAtiva = dividaAtivaContabilFacade.salvarRetornando(dividaAtiva);
                dividaAtivaContabilFacade.gerarSaldoDividaAtiva(dividaAtiva);
                dividaAtivaContabilFacade.contabilizarDividaAtiva(dividaAtiva);
                assistenteBarraProgresso.conta();
            } catch (ValidacaoException ve) {
                for (FacesMessage facesMessage : ve.getMensagens()) {
                    assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(facesMessage);
                }
            } catch (Exception ex) {
                assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(ex.getMessage()));
            }
        }
    }

    public void importarReceitaCreditoReceber(List<CreditoReceber> lancamentos, AssistenteBarraProgresso assistenteBarraProgresso, boolean estorno) throws ValidacaoException {
        for (CreditoReceber creditoReceber : lancamentos) {
            try {
                if (creditoReceber.getLoteBaixa() != null) {
                    montarDescricaoProcessoAssincronoIntegracao(assistenteBarraProgresso, "receitas de crédito a receber", 2, 3, creditoReceber.getLoteBaixa().getCodigoLote(), estorno);
                }
                if (creditoReceber.getNumero() == null) {
                    creditoReceber.setNumero(singletonGeradorCodigoContabil.getNumeroCreditoReceber(creditoReceber.getExercicio(), creditoReceber.getDataCredito()));
                }
                definirEventoCreditoReceber(creditoReceber);
                creditoReceber = creditoReceberFacade.salvarRetornando(creditoReceber);
                creditoReceberFacade.gerarSaldoCreditoReceber(creditoReceber);
                creditoReceberFacade.contabilizarCreditoReceber(creditoReceber);
                assistenteBarraProgresso.conta();
            } catch (ValidacaoException ve) {
                for (FacesMessage facesMessage : ve.getMensagens()) {
                    assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(facesMessage);
                }
            } catch (Exception e) {
                assistenteBarraProgresso.getMensagensValidacaoFacesUtil().add(new FacesMessage(e.getMessage()));
            }
        }
    }

    private void definirEventoCreditoReceber(CreditoReceber cr) {
        try {
            if (cr.getOperacaoCreditoReceber() != null && cr.getReceitaLOA() != null) {
                creditoReceberFacade.definirEventoCreditoReceber(cr);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void definirEventoDividaAtiva(DividaAtivaContabil da) {
        try {
            if (da.getOperacaoDividaAtiva() != null && da.getReceitaLOA() != null) {
                dividaAtivaContabilFacade.definirEventoDividaAtiva(da);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    private void validarLancamentoReceita(LancamentoReceitaOrc receita) {
        ValidacaoException ve = new ValidacaoException();
        receita.realizarValidacoes();
        if (receita.getLancReceitaFonte().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("Não foi feito lançamentos de receitas fontes para essa receita realizada.");
        }
        if (receita.getValor().compareTo(BigDecimal.ZERO) <= 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" O campo Valor deve ser maior que zero(0).");
        }
        if (receita.getDataConciliacao() != null) {
            if (Util.getDataHoraMinutoSegundoZerado(receita.getDataConciliacao()).before(Util.getDataHoraMinutoSegundoZerado(receita.getDataLancamento()))) {
                ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Conciliação deve ser igual ou superior a Data de Lançamento da Receita.");
            }
        }
        if (receita.getEventoContabil() == null) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Evento contábil não encontrado para esta receita realizada.");
        }
        ve.lancarException();
    }

    private void validarEstornoReceita(ReceitaORCEstorno receitaORCEstorno) {
        ValidacaoException ve = new ValidacaoException();
        receitaORCEstorno.realizarValidacoes();

        if (receitaORCEstorno.getReceitaORCFonteEstorno().isEmpty()) {
            ve.adicionarMensagemDeOperacaoNaoPermitida(" Para salvar, é necessário efetuar o cálculo para as receitas fontes clicando no botão 'Calcular Laçamentos'.");
        }

        ve.lancarException();
    }

    public void definirEventoReceitaRealizada(LancamentoReceitaOrc l) {
        try {
            if (l.getOperacaoReceitaRealizada() != null && l.getReceitaLOA() != null) {
                lancamentoReceitaOrcFacade.definirEventoReceitaRealizada(l);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public void definirEventoReceitaRealizadaEstorno(ReceitaORCEstorno roe) {
        try {
            if (roe.getOperacaoReceitaRealizada() != null && roe.getReceitaLOA() != null) {
                receitaORCEstornoFacade.definirEventoContabil(roe);
            }
        } catch (Exception e) {
            throw e;
        }
    }

    public BigDecimal buscarTotalDeItens(LoteBaixa loteBaixa, TipoIntegracao tipoIntegracao) {
        String sql = " select coalesce(sum(valor), 0) from itemIntegracaoTribCont where LOTEBAIXA_ID = :lote " +
            " and tipo = :tipo ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("lote", loteBaixa.getId());
        q.setParameter("tipo", tipoIntegracao.name());
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return BigDecimal.ZERO;
    }

    public void marcarLoteBaixaComoIntegradoArrecadacao(LoteBaixa loteBaixa) {
        getArrecadacaoDAO().updateIntegracaoArrecadacaoLoteBaixa(loteBaixa.getId());
    }

    public void marcarLoteBaixaComoIntegradoEstorno(LoteBaixa loteBaixa) {
        getArrecadacaoDAO().updateIntegracaoEstornoLoteBaixa(loteBaixa.getId());
    }

    public class KeyIntegracao {
        private UnidadeOrganizacional orcamentaria;
        private UnidadeOrganizacional administrativa;
        private ContaReceita contareceita;
        private SubConta subConta;
        private LoteBaixa loteBaixa;
        private Date dia;
        private OperacaoReceita operacaoReceitaRealizada;
        private Date dataConciliacao;
        private FonteDeRecursos fonteDeRecursos;

        public KeyIntegracao(ItemIntegracaoTributarioContabil item) {
            this.orcamentaria = item.getOrcamentaria();
            this.contareceita = item.getContaReceita();
            this.dia = item.getDataPagamento();
            this.subConta = item.getSubConta();
            this.administrativa = item.getAdministrativa();
            this.dataConciliacao = item.getDataCredito();
            this.loteBaixa = item.getLoteBaixa();
            this.operacaoReceitaRealizada = item.getOperacaoReceitaRealizada();
            this.fonteDeRecursos = item.getFonteDeRecursos();
        }

        public OperacaoReceita getOperacaoReceitaRealizada() {
            return operacaoReceitaRealizada;
        }

        public void setOperacaoReceitaRealizada(OperacaoReceita operacaoReceitaRealizada) {
            this.operacaoReceitaRealizada = operacaoReceitaRealizada;
        }

        public UnidadeOrganizacional getOrcamentaria() {
            return orcamentaria;
        }

        public void setOrcamentaria(UnidadeOrganizacional orcamentaria) {
            this.orcamentaria = orcamentaria;
        }

        public ContaReceita getContareceita() {
            return contareceita;
        }

        public void setContareceita(ContaReceita contareceita) {
            this.contareceita = contareceita;
        }

        public Date getDia() {
            return dia;
        }

        public void setDia(Date dia) {
            this.dia = dia;
        }

        public LoteBaixa getLoteBaixa() {
            return loteBaixa;
        }

        public void setLoteBaixa(LoteBaixa loteBaixa) {
            this.loteBaixa = loteBaixa;
        }

        public FonteDeRecursos getFonteDeRecursos() {
            return fonteDeRecursos;
        }

        public void setFonteDeRecursos(FonteDeRecursos fonteDeRecursos) {
            this.fonteDeRecursos = fonteDeRecursos;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof KeyIntegracao)) return false;

            KeyIntegracao that = (KeyIntegracao) o;

            if (administrativa != null ? !administrativa.equals(that.administrativa) : that.administrativa != null)
                return false;
            if (contareceita != null ? !contareceita.equals(that.contareceita) : that.contareceita != null)
                return false;
            if (dia != null ? !dia.equals(that.dia) : that.dia != null) return false;
            if (orcamentaria != null ? !orcamentaria.equals(that.orcamentaria) : that.orcamentaria != null)
                return false;
            if (subConta != null ? !subConta.equals(that.subConta) : that.subConta != null) return false;
            if (loteBaixa != null ? !loteBaixa.equals(that.loteBaixa) : that.loteBaixa != null) return false;
            if (dataConciliacao != null ? !dataConciliacao.equals(that.dataConciliacao) : that.dataConciliacao != null)
                return false;
            if (operacaoReceitaRealizada != null ? !operacaoReceitaRealizada.equals(that.operacaoReceitaRealizada) : that.operacaoReceitaRealizada != null)
                return false;
            if (fonteDeRecursos != null ? !fonteDeRecursos.equals(that.fonteDeRecursos) : that.fonteDeRecursos != null)
                return false;
            return true;
        }

        @Override
        public int hashCode() {
            int result = orcamentaria != null ? orcamentaria.hashCode() : 0;
            result = 31 * result + (administrativa != null ? administrativa.hashCode() : 0);
            result = 31 * result + (contareceita != null ? contareceita.hashCode() : 0);
            result = 31 * result + (subConta != null ? subConta.hashCode() : 0);
            result = 31 * result + (dia != null ? dia.hashCode() : 0);
            result = 31 * result + (loteBaixa != null ? loteBaixa.hashCode() : 0);
            result = 31 * result + (dataConciliacao != null ? dataConciliacao.hashCode() : 0);
            result = 31 * result + (operacaoReceitaRealizada != null ? operacaoReceitaRealizada.hashCode() : 0);
            result = 31 * result + (fonteDeRecursos != null ? fonteDeRecursos.hashCode() : 0);
            return result;
        }

        public UnidadeOrganizacional getAdministrativa() {

            return administrativa;
        }

        public void setAdministrativa(UnidadeOrganizacional administrativa) {
            this.administrativa = administrativa;
        }

        public SubConta getSubConta() {
            return subConta;
        }

        public void setSubConta(SubConta subConta) {
            this.subConta = subConta;
        }

        public Date getDataConciliacao() {
            return dataConciliacao;
        }

        public void setDataConciliacao(Date dataConciliacao) {
            this.dataConciliacao = dataConciliacao;
        }

        @Override
        public String toString() {
            return "KeyIntegracao{" +
                "orcamentaria=" + orcamentaria.getId() +
                ", administrativa=" + administrativa.getId() +
                ", contareceita=" + contareceita.getId() +
                ", subConta=" + (subConta != null ? subConta.getId() : "null") +
                ", loteBaixa=" + (loteBaixa != null ? loteBaixa.getCodigoLote() : "null") +
                ", dia=" + dia +
                '}';
        }
    }
}

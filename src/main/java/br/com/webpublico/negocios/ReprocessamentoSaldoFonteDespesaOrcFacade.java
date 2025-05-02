package br.com.webpublico.negocios;

import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.FonteDespesaORC;
import br.com.webpublico.entidades.ReprocessamentoHistorico;
import br.com.webpublico.entidades.UnidadeOrganizacional;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.entidadesauxiliares.ReprocessamentoSaldoFonteDespesaOrc;
import br.com.webpublico.entidadesauxiliares.SaldoFonteDespesaOrcReprocessamento;
import br.com.webpublico.entidadesauxiliares.contabil.IdentificadorFonteDespesaORC;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.negocios.contabil.reprocessamento.ReprocessamentoHistoricoFacade;
import br.com.webpublico.seguranca.service.QueryReprocessamentoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by Mateus on 08/02/2015.
 */
@Stateless
public class ReprocessamentoSaldoFonteDespesaOrcFacade {
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private UnidadeOrganizacionalFacade unidadeOrganizacionalFacade;
    @EJB
    private SaldoFonteDespesaORCFacade saldoFonteDespesaORCFacade;
    @EJB
    private FonteDespesaORCFacade fonteDespesaORCFacade;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private FonteDeRecursosFacade fonteDeRecursosFacade;
    @EJB
    private LOAFacade loaFacade;
    @EJB
    private DespesaORCFacade despesaORCFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ReprocessamentoHistoricoFacade reprocessamentoHistoricoFacade;
    private HashMap<Long, UnidadeOrganizacional> unidades;
    private HashMap<IdentificadorFonteDespesaORC, FonteDespesaORC> fontes;

    @Asynchronous
    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public Future<AssistenteReprocessamento> reprocessar(AssistenteReprocessamento assistente) {
        AsyncResult<AssistenteReprocessamento> retorno = new AsyncResult<>(assistente);
        assistente.getReprocessamentoSaldoFonteDespesaOrc().setQueryDotacao(QueryReprocessamentoService.getService().getReprocessamentoOrcamentarioDotacao());
        assistente.getReprocessamentoSaldoFonteDespesaOrc().setQuerySolicitacoEmpenho(QueryReprocessamentoService.getService().getReprocessamentoOrcamentarioSolicitacoEmpenho());
        assistente.getReprocessamentoSaldoFonteDespesaOrc().setQueryAlteracaoOrcamentaria(QueryReprocessamentoService.getService().getReprocessamentoOrcamentarioAlteracaoOrcamentaria());
        assistente.getReprocessamentoSaldoFonteDespesaOrc().setQueryEmpenhado(QueryReprocessamentoService.getService().getReprocessamentoOrcamentarioEmpenhado());
        assistente.getReprocessamentoSaldoFonteDespesaOrc().setQueryLiquidado(QueryReprocessamentoService.getService().getReprocessamentoOrcamentarioLiquidado());
        assistente.getReprocessamentoSaldoFonteDespesaOrc().setQueryPago(QueryReprocessamentoService.getService().getReprocessamentoOrcamentarioPago());
        assistente.getReprocessamentoSaldoFonteDespesaOrc().setQueryReservaDotacao(QueryReprocessamentoService.getService().getReprocessamentoOrcamentarioReservaDotacao());
        assistente.getReprocessamentoSaldoFonteDespesaOrc().setQueryReservadoPorLicitacaoNormal(QueryReprocessamentoService.getService().getReprocessamentoOrcamentarioReservadoPorLicitacaoNormal());
        assistente.getReprocessamentoSaldoFonteDespesaOrc().setQueryCancelamentoReservaDotacao(QueryReprocessamentoService.getService().getReprocessamentoOrcamentarioCancelamentoReservaDotacao());
        assistente.getReprocessamentoSaldoFonteDespesaOrc().setQueryReservadoPorLicitacaoEstorno(QueryReprocessamentoService.getService().getReprocessamentoOrcamentarioReservadoPorLicitacaoEstorno());
        assistente.getAssistenteBarraProgresso().inicializa();

        unidades = Maps.newHashMap();
        fontes = Maps.newHashMap();
        saldoFonteDespesaORCFacade.excluirSaldosNoPeriodo(assistente);

        List<SaldoFonteDespesaOrcReprocessamento> itens = recuperarItens(assistente);

        for (SaldoFonteDespesaOrcReprocessamento item : itens) {
            if (item.getReservadoPorLicitacao().equals(new BigDecimal("388.85")) && item.getEmpenhado().equals(new BigDecimal("388.85"))){
                System.out.println("VAI TEIA>>> " +  item.getIdOrigem());
            }
        }
        assistente.setTotal(itens.size());
        for (SaldoFonteDespesaOrcReprocessamento reprocessamento : itens) {
            assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Gerando saldo para o dia " + DataUtil.getDataFormatada(reprocessamento.getDataSaldo()) + "...</font> </b>");
            reprocessarSaldos(reprocessamento, assistente);
        }

        ordenarItens(itens);
        assistente.finalizar();
        salvarHistorico(assistente.getHistorico());
        return retorno;
    }

    private void ordenarItens(List<SaldoFonteDespesaOrcReprocessamento> itens) {
        Collections.sort(itens, new Comparator<SaldoFonteDespesaOrcReprocessamento>() {
            @Override
            public int compare(SaldoFonteDespesaOrcReprocessamento o1, SaldoFonteDespesaOrcReprocessamento o2) {
                return o1.getDataSaldo().compareTo(o2.getDataSaldo());
            }
        });
    }

    public void reprocessarSaldos(SaldoFonteDespesaOrcReprocessamento saldoReprocessamento, AssistenteReprocessamento assistente) {
        String objetosUtilizados = "Operação: " + getOperacaoOrc(saldoReprocessamento) +
            "; Tipo: " + saldoReprocessamento.getTipoOperacaoORC() +
            "; Valor: " + Util.formataValor(getValor(saldoReprocessamento)) +
            "; Data Saldo: " + DataUtil.getDataFormatada(saldoReprocessamento.getDataSaldo()) +
            "; Unidade: " + saldoReprocessamento.getUnidadeOrganizacional() +
            "; Id Origem: " + saldoReprocessamento.getIdOrigem() +
            "; Classe Origem: " + saldoReprocessamento.getClasseOrigem();
        try {
            SaldoFonteDespesaORCVO vo = new SaldoFonteDespesaORCVO(
                saldoReprocessamento.getFonteDespesaORC(),
                getOperacaoOrc(saldoReprocessamento),
                saldoReprocessamento.getTipoOperacaoORC(),
                getValor(saldoReprocessamento),
                saldoReprocessamento.getDataSaldo(),
                saldoReprocessamento.getUnidadeOrganizacional(),
                saldoReprocessamento.getIdOrigem(),
                saldoReprocessamento.getClasseOrigem(),
                saldoReprocessamento.getNumeroMovimento(),
                saldoReprocessamento.getHistorico());
            saldoFonteDespesaORCFacade.gerarSaldoOrcamentarioSemRealizarValidacao(vo);
            assistente.adicionarHistoricoLogSemErro(saldoReprocessamento.getFonteDespesaORC(), objetosUtilizados);
            assistente.historicoContaSemErro();
        } catch (OptimisticLockException optex) {
            throw new ExcecaoNegocioGenerica("Saldo orçamentário está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
        } catch (Exception ex) {
            assistente.adicionarReprocessandoErroLog(ex.getMessage(), saldoReprocessamento.getFonteDespesaORC(), objetosUtilizados);
            assistente.historicoContaComErro();
        } finally {
            assistente.historicoConta();
        }
    }

    private OperacaoORC getOperacaoOrc(SaldoFonteDespesaOrcReprocessamento saldoFonteDespesaOrcReprocessamento) {
        if (saldoFonteDespesaOrcReprocessamento.getDotacao().compareTo(BigDecimal.ZERO) != 0) {
            return OperacaoORC.DOTACAO;
        } else if (saldoFonteDespesaOrcReprocessamento.getEmpenhado().compareTo(BigDecimal.ZERO) != 0) {
            return OperacaoORC.EMPENHO;
        } else if (saldoFonteDespesaOrcReprocessamento.getLiquidado().compareTo(BigDecimal.ZERO) != 0) {
            return OperacaoORC.LIQUIDACAO;
        } else if (saldoFonteDespesaOrcReprocessamento.getPago().compareTo(BigDecimal.ZERO) != 0) {
            return OperacaoORC.PAGAMENTO;
        } else if (saldoFonteDespesaOrcReprocessamento.getEmpenhadoNaoProcessado().compareTo(BigDecimal.ZERO) != 0) {
            return OperacaoORC.EMPENHORESTO_NAO_PROCESSADO;
        } else if (saldoFonteDespesaOrcReprocessamento.getLiquidadoNaoProcessado().compareTo(BigDecimal.ZERO) != 0) {
            return OperacaoORC.LIQUIDACAORESTO_NAO_PROCESSADO;
        } else if (saldoFonteDespesaOrcReprocessamento.getPagoNaoProcessado().compareTo(BigDecimal.ZERO) != 0) {
            return OperacaoORC.PAGAMENTORESTO_NAO_PROCESSADO;
        } else if (saldoFonteDespesaOrcReprocessamento.getEmpenhadoProcessado().compareTo(BigDecimal.ZERO) != 0) {
            return OperacaoORC.EMPENHORESTO_PROCESSADO;
        } else if (saldoFonteDespesaOrcReprocessamento.getLiquidadoProcessado().compareTo(BigDecimal.ZERO) != 0) {
            return OperacaoORC.LIQUIDACAORESTO_PROCESSADO;
        } else if (saldoFonteDespesaOrcReprocessamento.getPagoProcessado().compareTo(BigDecimal.ZERO) != 0) {
            return OperacaoORC.PAGAMENTORESTO_PROCESSADO;
        } else if (saldoFonteDespesaOrcReprocessamento.getReservado().compareTo(BigDecimal.ZERO) != 0) {
            return OperacaoORC.RESERVADO;
        } else if (saldoFonteDespesaOrcReprocessamento.getReduzido().compareTo(BigDecimal.ZERO) != 0) {
            return OperacaoORC.ANULACAO;
        } else if (saldoFonteDespesaOrcReprocessamento.getSuplementado().compareTo(BigDecimal.ZERO) != 0) {
            return OperacaoORC.SUPLEMENTACAO;
        } else if (saldoFonteDespesaOrcReprocessamento.getReservadoPorLicitacao().compareTo(BigDecimal.ZERO) != 0) {
            return OperacaoORC.RESERVADO_POR_LICITACAO;
        }
        return OperacaoORC.DOTACAO;
    }

    private BigDecimal getValor(SaldoFonteDespesaOrcReprocessamento saldoFonteDespesaOrcReprocessamento) {
        if (saldoFonteDespesaOrcReprocessamento.getDotacao().compareTo(BigDecimal.ZERO) != 0) {
            return saldoFonteDespesaOrcReprocessamento.getDotacao();
        } else if (saldoFonteDespesaOrcReprocessamento.getEmpenhado().compareTo(BigDecimal.ZERO) != 0) {
            return saldoFonteDespesaOrcReprocessamento.getEmpenhado();
        } else if (saldoFonteDespesaOrcReprocessamento.getLiquidado().compareTo(BigDecimal.ZERO) != 0) {
            return saldoFonteDespesaOrcReprocessamento.getLiquidado();
        } else if (saldoFonteDespesaOrcReprocessamento.getPago().compareTo(BigDecimal.ZERO) != 0) {
            return saldoFonteDespesaOrcReprocessamento.getPago();
        } else if (saldoFonteDespesaOrcReprocessamento.getEmpenhadoNaoProcessado().compareTo(BigDecimal.ZERO) != 0) {
            return saldoFonteDespesaOrcReprocessamento.getEmpenhadoNaoProcessado();
        } else if (saldoFonteDespesaOrcReprocessamento.getLiquidadoNaoProcessado().compareTo(BigDecimal.ZERO) != 0) {
            return saldoFonteDespesaOrcReprocessamento.getLiquidadoNaoProcessado();
        } else if (saldoFonteDespesaOrcReprocessamento.getPagoNaoProcessado().compareTo(BigDecimal.ZERO) != 0) {
            return saldoFonteDespesaOrcReprocessamento.getPagoNaoProcessado();
        } else if (saldoFonteDespesaOrcReprocessamento.getEmpenhadoProcessado().compareTo(BigDecimal.ZERO) != 0) {
            return saldoFonteDespesaOrcReprocessamento.getEmpenhadoProcessado();
        } else if (saldoFonteDespesaOrcReprocessamento.getLiquidadoProcessado().compareTo(BigDecimal.ZERO) != 0) {
            return saldoFonteDespesaOrcReprocessamento.getLiquidadoProcessado();
        } else if (saldoFonteDespesaOrcReprocessamento.getPagoProcessado().compareTo(BigDecimal.ZERO) != 0) {
            return saldoFonteDespesaOrcReprocessamento.getPagoProcessado();
        } else if (saldoFonteDespesaOrcReprocessamento.getReservado().compareTo(BigDecimal.ZERO) != 0) {
            return saldoFonteDespesaOrcReprocessamento.getReservado();
        } else if (saldoFonteDespesaOrcReprocessamento.getReduzido().compareTo(BigDecimal.ZERO) != 0) {
            return saldoFonteDespesaOrcReprocessamento.getReduzido();
        } else if (saldoFonteDespesaOrcReprocessamento.getSuplementado().compareTo(BigDecimal.ZERO) != 0) {
            return saldoFonteDespesaOrcReprocessamento.getSuplementado();
        } else if (saldoFonteDespesaOrcReprocessamento.getReservadoPorLicitacao().compareTo(BigDecimal.ZERO) != 0) {
            return saldoFonteDespesaOrcReprocessamento.getReservadoPorLicitacao();
        }
        return BigDecimal.ZERO;
    }

    @TransactionTimeout(unit = TimeUnit.HOURS, value = 2)
    public List<SaldoFonteDespesaOrcReprocessamento> recuperarItens(AssistenteReprocessamento assistente) {
        List<SaldoFonteDespesaOrcReprocessamento> retorno = Lists.newArrayList();
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getReprocessarSaldoInicial()) {
            assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Recuperando Dotação Inicial...</font> </b>");
            if (!assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryDotacao().isEmpty()) {
                retorno.addAll(getRetornoDotacao(assistente));
            }
        }
        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Recuperando Solicitação de Empenho...</font> </b>");
        if (!assistente.getReprocessamentoSaldoFonteDespesaOrc().getQuerySolicitacoEmpenho().isEmpty()) {
            retorno.addAll(getRetorno(getSqlSolicitacoEmpenho(assistente.getReprocessamentoSaldoFonteDespesaOrc()).toString(), assistente));
        }

        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Recuperando Alteração Orçamentária...</font> </b>");
        if (!assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryAlteracaoOrcamentaria().isEmpty()) {
            retorno.addAll(getRetorno(getSqlAlteracaoOrcamentaria(assistente.getReprocessamentoSaldoFonteDespesaOrc()).toString(), assistente));
        }

        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Recuperando Empenho...</font> </b>");
        if (!assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryEmpenhado().isEmpty()) {
            retorno.addAll(getRetorno(getSqlEmpenhado(assistente.getReprocessamentoSaldoFonteDespesaOrc()).toString(), assistente));
        }

        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Recuperando Liquidação...</font> </b>");
        if (!assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryLiquidado().isEmpty()) {
            retorno.addAll(getRetorno(getSqlLiquidado(assistente.getReprocessamentoSaldoFonteDespesaOrc()).toString(), assistente));
        }

        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Recuperando Pagamento...</font> </b>");
        if (!assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryPago().isEmpty()) {
            retorno.addAll(getRetorno(getSqlPago(assistente.getReprocessamentoSaldoFonteDespesaOrc()).toString(), assistente));
        }

        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Recuperando Reserva...</font> </b>");
        if (!assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryReservaDotacao().isEmpty()) {
            retorno.addAll(getRetorno(getSqlReservaDotacao(assistente.getReprocessamentoSaldoFonteDespesaOrc()).toString(), assistente));
        }

        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Recuperando Cancelamento de Reserva ...</font> </b>");
        if (!assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryCancelamentoReservaDotacao().isEmpty()) {
            retorno.addAll(getRetorno(getSqlCancelamentoReservaDotacao(assistente.getReprocessamentoSaldoFonteDespesaOrc()).toString(), assistente));
        }

        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Recuperando Reservado por Licitação - Normal...</font> </b>");
        if (!assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryReservadoPorLicitacaoNormal().isEmpty()) {
            retorno.addAll(getRetornoSemExercicio(getSqlReservadoPorLicitacaoNormal(assistente.getReprocessamentoSaldoFonteDespesaOrc()).toString(), assistente));
        }

        assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Recuperando Reservado por Licitação - Estorno...</font> </b>");
        if (!assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryReservadoPorLicitacaoEstorno().isEmpty()) {
            retorno.addAll(getRetorno(getSqlReservadoPorLicitacaoEstorno(assistente.getReprocessamentoSaldoFonteDespesaOrc()).toString(), assistente));
        }
        return retorno;
    }


    public List<SaldoFonteDespesaOrcReprocessamento> getRetornoDotacao(AssistenteReprocessamento assistente) {
        List<SaldoFonteDespesaOrcReprocessamento> retorno = Lists.newArrayList();
        Query q = em.createNativeQuery(getSqlDotacao(assistente.getReprocessamentoSaldoFonteDespesaOrc()).toString());
        q.setParameter("EXERCICIO_ID", assistente.getExercicio().getId());
        adicionarParametros(assistente.getReprocessamentoSaldoFonteDespesaOrc(), q);
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                SaldoFonteDespesaOrcReprocessamento item = new SaldoFonteDespesaOrcReprocessamento();
                item.setDataSaldo((Date) obj[0]);
                recuperarUnidade(obj[1], item);
                recuperarFonteDespesaOrc(assistente.getExercicio(), obj, item);
                item.setDotacao((BigDecimal) obj[4]);
                item.setEmpenhado((BigDecimal) obj[5]);
                item.setLiquidado((BigDecimal) obj[6]);
                item.setPago((BigDecimal) obj[7]);
                item.setEmpenhadoNaoProcessado((BigDecimal) obj[8]);
                item.setLiquidadoNaoProcessado((BigDecimal) obj[9]);
                item.setPagoNaoProcessado((BigDecimal) obj[10]);
                item.setEmpenhadoProcessado((BigDecimal) obj[11]);
                item.setLiquidadoProcessado((BigDecimal) obj[12]);
                item.setPagoProcessado((BigDecimal) obj[13]);
                item.setReservado((BigDecimal) obj[14]);
                item.setSuplementado((BigDecimal) obj[15]);
                item.setReduzido((BigDecimal) obj[16]);
                item.setReservadoPorLicitacao((BigDecimal) obj[17]);
                item.setTipoOperacaoORC(TipoOperacaoORC.valueOf((String) obj[18]));
                item.setIdOrigem((String) obj[19]);
                item.setClasseOrigem((String) obj[20]);
                item.setNumeroMovimento((String) obj[21]);
                item.setHistorico((String) obj[22]);
                retorno.add(item);
            }
        }
        return retorno;
    }

    public List<SaldoFonteDespesaOrcReprocessamento> getRetorno(String sql, AssistenteReprocessamento assistente) {
        List<SaldoFonteDespesaOrcReprocessamento> retorno = Lists.newArrayList();
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        q.setParameter("EXERCICIO_ID", assistente.getExercicio().getId());
        adicionarParametros(assistente.getReprocessamentoSaldoFonteDespesaOrc(), q);
        Util.imprimeSQL(sql, q);
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                SaldoFonteDespesaOrcReprocessamento item = new SaldoFonteDespesaOrcReprocessamento();
                item.setDataSaldo((Date) obj[0]);
                recuperarUnidade(item.getDataSaldo(), item.getDataSaldo(), obj[1], item);
                recuperarFonteDespesaOrc(assistente.getExercicio(), obj, item);
                item.setDotacao((BigDecimal) obj[4]);
                item.setEmpenhado((BigDecimal) obj[5]);
                item.setLiquidado((BigDecimal) obj[6]);
                item.setPago((BigDecimal) obj[7]);
                item.setEmpenhadoNaoProcessado((BigDecimal) obj[8]);
                item.setLiquidadoNaoProcessado((BigDecimal) obj[9]);
                item.setPagoNaoProcessado((BigDecimal) obj[10]);
                item.setEmpenhadoProcessado((BigDecimal) obj[11]);
                item.setLiquidadoProcessado((BigDecimal) obj[12]);
                item.setPagoProcessado((BigDecimal) obj[13]);
                item.setReservado((BigDecimal) obj[14]);
                item.setSuplementado((BigDecimal) obj[15]);
                item.setReduzido((BigDecimal) obj[16]);
                item.setReservadoPorLicitacao((BigDecimal) obj[17]);
                item.setTipoOperacaoORC(TipoOperacaoORC.valueOf((String) obj[18]));
                item.setIdOrigem((String) obj[19]);
                item.setClasseOrigem((String) obj[20]);
                item.setNumeroMovimento((String) obj[21]);
                item.setHistorico((String) obj[22]);
                retorno.add(item);
            }
        }
        return retorno;
    }

    public List<SaldoFonteDespesaOrcReprocessamento> getRetornoSemExercicio(String sql, AssistenteReprocessamento assistente) {
        List<SaldoFonteDespesaOrcReprocessamento> retorno = Lists.newArrayList();
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        adicionarParametros(assistente.getReprocessamentoSaldoFonteDespesaOrc(), q);
        Util.imprimeSQL(sql, q);
        List<Object[]> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                SaldoFonteDespesaOrcReprocessamento item = new SaldoFonteDespesaOrcReprocessamento();
                item.setDataSaldo((Date) obj[0]);
                recuperarUnidade(item.getDataSaldo(), item.getDataSaldo(), obj[1], item);
                recuperarFonteDespesaOrc(assistente.getExercicio(), obj, item);
                item.setDotacao((BigDecimal) obj[4]);
                item.setEmpenhado((BigDecimal) obj[5]);
                item.setLiquidado((BigDecimal) obj[6]);
                item.setPago((BigDecimal) obj[7]);
                item.setEmpenhadoNaoProcessado((BigDecimal) obj[8]);
                item.setLiquidadoNaoProcessado((BigDecimal) obj[9]);
                item.setPagoNaoProcessado((BigDecimal) obj[10]);
                item.setEmpenhadoProcessado((BigDecimal) obj[11]);
                item.setLiquidadoProcessado((BigDecimal) obj[12]);
                item.setPagoProcessado((BigDecimal) obj[13]);
                item.setReservado((BigDecimal) obj[14]);
                item.setSuplementado((BigDecimal) obj[15]);
                item.setReduzido((BigDecimal) obj[16]);
                item.setReservadoPorLicitacao((BigDecimal) obj[17]);
                item.setTipoOperacaoORC(TipoOperacaoORC.valueOf((String) obj[18]));
                item.setIdOrigem((String) obj[19]);
                item.setClasseOrigem((String) obj[20]);
                item.setNumeroMovimento((String) obj[21]);
                item.setHistorico((String) obj[22]);
                retorno.add(item);
            }
        }
        return retorno;
    }

    private void adicionarParametros(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc, Query q) {
        if (reprocessamentoSaldoFonteDespesaOrc.getHierarquiaOrganizacional() != null) {
            q.setParameter("unidadeOrganizacional", reprocessamentoSaldoFonteDespesaOrc.getHierarquiaOrganizacional().getSubordinada().getId());
        }
        if (reprocessamentoSaldoFonteDespesaOrc.getDespesaORC() != null) {
            q.setParameter("despesaOrc", reprocessamentoSaldoFonteDespesaOrc.getDespesaORC().getId());
        }
        if (reprocessamentoSaldoFonteDespesaOrc.getFonteDespesaORC() != null) {
            q.setParameter("fonteDesp", reprocessamentoSaldoFonteDespesaOrc.getFonteDespesaORC().getId());
        }
    }

    private void recuperarFonteDespesaOrc(Exercicio exercicio, Object[] obj, SaldoFonteDespesaOrcReprocessamento item) {
        Long idFonteRecurso = ((BigDecimal) obj[2]).longValue();
        Long idDespesaORC = ((BigDecimal) obj[3]).longValue();

        item.setFonteDespesaORC(recuperarFonteDespesaOrc(item.getUnidadeOrganizacional().getId(), idFonteRecurso, idDespesaORC, exercicio.getId()));
    }

    private void recuperarUnidade(Object o, SaldoFonteDespesaOrcReprocessamento item) {
        Long idUnidade = ((BigDecimal) o).longValue();
        if (!unidades.containsKey(idUnidade)) {
            UnidadeOrganizacional unidadePelaSubordinada = unidadeOrganizacionalFacade.recuperar(idUnidade);
            unidades.put(unidadePelaSubordinada.getId(), unidadePelaSubordinada);
            item.setUnidadeOrganizacional(unidadePelaSubordinada);
        } else {
            item.setUnidadeOrganizacional(unidades.get(idUnidade));
        }
    }

    private void recuperarUnidade(Date dataInicial, Date dataFinal, Object o, SaldoFonteDespesaOrcReprocessamento item) {
        Long idUnidade = ((BigDecimal) o).longValue();

        if (!unidades.containsKey(idUnidade)) {
            UnidadeOrganizacional unidadePelaSubordinada = unidadeOrganizacionalFacade.getUnidadePelaSubordinada(idUnidade, dataInicial, dataFinal);
            unidades.put(unidadePelaSubordinada.getId(), unidadePelaSubordinada);
            item.setUnidadeOrganizacional(unidadePelaSubordinada);
        } else {
            item.setUnidadeOrganizacional(unidades.get(idUnidade));
        }
    }

    private FonteDespesaORC recuperarFonteDespesaOrc(Long idUnidade, Long idFonteRecurso, Long idDespesaOrc, Long idExercicio) {
        IdentificadorFonteDespesaORC identificadorFonteDespesaORC = new IdentificadorFonteDespesaORC(idUnidade, idFonteRecurso, idDespesaOrc, idExercicio);
        if (!fontes.containsKey(identificadorFonteDespesaORC)) {

            String sql = "select f.* from fontedespesaorc f " +
                "inner join DESPESAORC d on f.DESPESAORC_ID = d.id " +
                "inner join PROVISAOPPAFONTE pf on f.PROVISAOPPAFONTE_ID = pf.id " +
                "inner join PROVISAOPPADESPESA pd on d.PROVISAOPPADESPESA_ID = pd.id " +
                "inner join CONTADEDESTINACAO cd on pf.DESTINACAODERECURSOS_ID = cd.id " +
                "inner join FONTEDERECURSOS fonte on cd.FONTEDERECURSOS_ID = fonte.id " +
                "where fonte.id = :idFonte " +
                "and pd.UNIDADEORGANIZACIONAL_ID = :idUnidade " +
                "and d.id = :idDespesaORC";
            Query consulta = em.createNativeQuery(sql, FonteDespesaORC.class);
            consulta.setParameter("idFonte", idFonteRecurso);
            consulta.setParameter("idUnidade", idUnidade);
            consulta.setParameter("idDespesaORC", idDespesaOrc);
            try {
                FonteDespesaORC singleResult = (FonteDespesaORC) consulta.getSingleResult();
                fontes.put(identificadorFonteDespesaORC, singleResult);
                return singleResult;
            } catch (Exception e) {
                throw new ExcecaoNegocioGenerica("Nao foi possivel recuperar a Fonte de Despesa Orçamentaria para o identificador " + identificadorFonteDespesaORC.toString());
            }
        } else {
            return fontes.get(identificadorFonteDespesaORC);
        }
    }

    private StringBuilder getSqlPago(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc) {
        StringBuilder sql = new StringBuilder();
        sql.append(reprocessamentoSaldoFonteDespesaOrc.getQueryPago()
            .replace("$UnidadeOrganizacional", adicionarFiltroUnidade(reprocessamentoSaldoFonteDespesaOrc, "pag"))
            .replace("$DespesaOrc", adicionarFiltroDespesaOrc(reprocessamentoSaldoFonteDespesaOrc))
            .replace("$FonteDesp", adicionarFiltroFonteDespesaOrc(reprocessamentoSaldoFonteDespesaOrc)));
        return sql;
    }

    private StringBuilder getSqlLiquidado(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc) {
        StringBuilder sql = new StringBuilder();
        sql.append(reprocessamentoSaldoFonteDespesaOrc.getQueryLiquidado()
            .replace("$UnidadeOrganizacional", adicionarFiltroUnidade(reprocessamentoSaldoFonteDespesaOrc, "liq"))
            .replace("$DespesaOrc", adicionarFiltroDespesaOrc(reprocessamentoSaldoFonteDespesaOrc))
            .replace("$FonteDesp", adicionarFiltroFonteDespesaOrc(reprocessamentoSaldoFonteDespesaOrc)));
        return sql;
    }

    private StringBuilder getSqlEmpenhado(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc) {
        StringBuilder sql = new StringBuilder();
        sql.append(reprocessamentoSaldoFonteDespesaOrc.getQueryEmpenhado()
            .replace("$UnidadeOrganizacional", adicionarFiltroUnidade(reprocessamentoSaldoFonteDespesaOrc, "emp"))
            .replace("$DespesaOrc", adicionarFiltroDespesaOrc(reprocessamentoSaldoFonteDespesaOrc))
            .replace("$FonteDesp", adicionarFiltroFonteDespesaOrc(reprocessamentoSaldoFonteDespesaOrc)));
        return sql;
    }

    private StringBuilder getSqlCancelamentoReservaDotacao(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc) {
        StringBuilder sql = new StringBuilder();
        sql.append(reprocessamentoSaldoFonteDespesaOrc.getQueryCancelamentoReservaDotacao()
            .replace("$UnidadeOrganizacional", adicionarFiltroUnidade(reprocessamentoSaldoFonteDespesaOrc, "sol"))
            .replace("$DespesaOrc", adicionarFiltroDespesaOrc(reprocessamentoSaldoFonteDespesaOrc))
            .replace("$FonteDesp", adicionarFiltroFonteDespesaOrc(reprocessamentoSaldoFonteDespesaOrc)));
        return sql;
    }

    private StringBuilder getSqlReservadoPorLicitacaoEstorno(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc) {
        StringBuilder sql = new StringBuilder();
        sql.append(reprocessamentoSaldoFonteDespesaOrc.getQueryReservadoPorLicitacaoEstorno()
            .replace("$UnidadeOrganizacional", adicionarFiltroUnidade(reprocessamentoSaldoFonteDespesaOrc, "prov"))
            .replace("$DespesaOrc", adicionarFiltroDespesaOrc(reprocessamentoSaldoFonteDespesaOrc))
            .replace("$FonteDesp", adicionarFiltroFonteDespesaOrc(reprocessamentoSaldoFonteDespesaOrc)));
        return sql;
    }

    private StringBuilder getSqlReservaDotacao(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc) {
        StringBuilder sql = new StringBuilder();
        sql.append(reprocessamentoSaldoFonteDespesaOrc.getQueryReservaDotacao()
            .replace("$UnidadeOrganizacional", adicionarFiltroUnidade(reprocessamentoSaldoFonteDespesaOrc, "prov"))
            .replace("$DespesaOrc", adicionarFiltroDespesaOrc(reprocessamentoSaldoFonteDespesaOrc))
            .replace("$FonteDesp", adicionarFiltroFonteDespesaOrc(reprocessamentoSaldoFonteDespesaOrc)));
        return sql;
    }

    private StringBuilder getSqlReservadoPorLicitacaoNormal(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc) {
        StringBuilder sql = new StringBuilder();
        sql.append(reprocessamentoSaldoFonteDespesaOrc.getQueryReservadoPorLicitacaoNormal()
            .replace("$UnidadeOrganizacional", adicionarFiltroUnidade(reprocessamentoSaldoFonteDespesaOrc, "prov"))
            .replace("$DespesaOrc", adicionarFiltroDespesaOrc(reprocessamentoSaldoFonteDespesaOrc))
            .replace("$FonteDesp", adicionarFiltroFonteDespesaOrc(reprocessamentoSaldoFonteDespesaOrc)));
        return sql;
    }

    private StringBuilder getSqlSolicitacoEmpenho(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc) {
        StringBuilder sql = new StringBuilder();
        sql.append(reprocessamentoSaldoFonteDespesaOrc.getQuerySolicitacoEmpenho()
            .replace("$UnidadeOrganizacional", adicionarFiltroUnidade(reprocessamentoSaldoFonteDespesaOrc, "sol"))
            .replace("$DespesaOrc", adicionarFiltroDespesaOrc(reprocessamentoSaldoFonteDespesaOrc))
            .replace("$FonteDesp", adicionarFiltroFonteDespesaOrc(reprocessamentoSaldoFonteDespesaOrc)));
        return sql;
    }

    private StringBuilder getSqlAlteracaoOrcamentaria(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc) {
        StringBuilder sql = new StringBuilder();
        sql.append(reprocessamentoSaldoFonteDespesaOrc.getQueryAlteracaoOrcamentaria()
            .replace("$UnidadeOrganizacional", adicionarFiltroUnidade(reprocessamentoSaldoFonteDespesaOrc, "prov"))
            .replace("$DespesaOrc", adicionarFiltroDespesaOrc(reprocessamentoSaldoFonteDespesaOrc))
            .replace("$FonteDesp", adicionarFiltroFonteDespesaOrc(reprocessamentoSaldoFonteDespesaOrc)));
        return sql;
    }

    private StringBuilder getSqlDotacao(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc) {
        StringBuilder sql = new StringBuilder();
        sql.append(reprocessamentoSaldoFonteDespesaOrc.getQueryDotacao()
            .replace("$UnidadeOrganizacional", adicionarFiltroUnidade(reprocessamentoSaldoFonteDespesaOrc, "prov"))
            .replace("$DespesaOrc", adicionarFiltroDespesaOrc(reprocessamentoSaldoFonteDespesaOrc))
            .replace("$FonteDesp", adicionarFiltroFonteDespesaOrc(reprocessamentoSaldoFonteDespesaOrc)));
        return sql;
    }

    private String adicionarFiltroFonteDespesaOrc(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc) {
        StringBuilder sql = new StringBuilder();
        if (reprocessamentoSaldoFonteDespesaOrc.getFonteDespesaORC() != null) {
            sql.append(" and fontdesp.id = :fonteDesp ");
        }
        return sql.toString();
    }

    private String adicionarFiltroDespesaOrc(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc) {
        StringBuilder sql = new StringBuilder();
        if (reprocessamentoSaldoFonteDespesaOrc.getDespesaORC() != null) {
            sql.append(" and desp.id = :despesaOrc ");
        }
        return sql.toString();
    }

    private String adicionarFiltroUnidade(ReprocessamentoSaldoFonteDespesaOrc reprocessamentoSaldoFonteDespesaOrc, String alias) {
        StringBuilder sql = new StringBuilder();
        if (reprocessamentoSaldoFonteDespesaOrc.getHierarquiaOrganizacional() != null) {
            sql.append(" and ").append(alias).append(".unidadeorganizacional_id = :unidadeOrganizacional ");
        }
        return sql.toString();
    }

    public UnidadeOrganizacionalFacade getUnidadeOrganizacionalFacade() {
        return unidadeOrganizacionalFacade;
    }

    public SaldoFonteDespesaORCFacade getSaldoFonteDespesaORCFacade() {
        return saldoFonteDespesaORCFacade;
    }

    public LOAFacade getLoaFacade() {
        return loaFacade;
    }

    public FonteDeRecursosFacade getFonteDeRecursosFacade() {
        return fonteDeRecursosFacade;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public FonteDespesaORCFacade getFonteDespesaORCFacade() {
        return fonteDespesaORCFacade;
    }

    public DespesaORCFacade getDespesaORCFacade() {
        return despesaORCFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    private void salvarHistorico(ReprocessamentoHistorico historico) {
        if (historico != null) {
            reprocessamentoHistoricoFacade.salvar(historico);
        }
    }
}

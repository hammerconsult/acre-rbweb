/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.entidadesauxiliares.contabil.SaldoFonteDespesaORCVO;
import br.com.webpublico.enums.OperacaoORC;
import br.com.webpublico.enums.TipoOperacaoORC;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author venon
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 50000)
public class SaldoFonteDespesaORCFacade implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(SaldoFonteDespesaORCFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public MovimentoDespesaORC gerarSaldoOrcamentarioSemRealizarValidacao(SaldoFonteDespesaORCVO saldoFonteDespesaORCVO) {
        preCondicoes(saldoFonteDespesaORCVO);
        return gerarSaldo(saldoFonteDespesaORCVO, false);
    }

    public MovimentoDespesaORC gerarSaldoOrcamentario(SaldoFonteDespesaORCVO saldoFonteDespesaORCVO) {
        preCondicoes(saldoFonteDespesaORCVO);
        return gerarSaldo(saldoFonteDespesaORCVO, true);
    }

    @Lock(LockType.WRITE)
    private MovimentoDespesaORC gerarSaldo(SaldoFonteDespesaORCVO saldo, Boolean validarSaldo) {
        try {
            MovimentoDespesaORC movimentoDespesaORC = criarMovimentoDespesaOrc(saldo);
            gerarSaldoOrcamentario(movimentoDespesaORC, validarSaldo);
            return movimentoDespesaORC;
        } catch (OptimisticLockException optex) {
            throw new ExcecaoNegocioGenerica("Saldo orçamentário está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(" " + ex.getMessage());
        }
    }

    private MovimentoDespesaORC criarMovimentoDespesaOrc(SaldoFonteDespesaORCVO saldo) {
        MovimentoDespesaORC movimento = new MovimentoDespesaORC();
        movimento.setDataMovimento(saldo.getData());
        movimento.setFonteDespesaORC(saldo.getFonte());
        movimento.setOperacaoORC(saldo.getOperacaoOrc());
        movimento.setTipoOperacaoORC(saldo.getTipoCredito());
        movimento.setValor(saldo.getValor());
        movimento.setUnidadeOrganizacional(saldo.getUnidade());
        movimento.setClasseOrigem(saldo.getClasseOrigem());
        movimento.setIdOrigem(saldo.getIdOrigem());
        movimento.setNumeroMovimento(saldo.getNumeroMovimento());
        movimento.setHistorico(saldo.getHistorico());
        em.persist(movimento);
        return movimento;
    }

    private void preCondicoes(SaldoFonteDespesaORCVO saldo) {
        String erro = "Não foi possível gerar saldo orçamentário. ";
        Preconditions.checkNotNull(saldo.getFonte(), erro + "A fonte de despesa orçamentária esta vazia.");
        Preconditions.checkNotNull(saldo.getOperacaoOrc(), erro + "A operação da despesa orçamentária esta vazia.");
        Preconditions.checkNotNull(saldo.getTipoCredito(), erro + "O tipo de crédito da despesa orçamentária esta vazia.!");
        Preconditions.checkNotNull(saldo.getValor(), erro + "O valor esta vazio.");
        Preconditions.checkNotNull(saldo.getData(), erro + "A data esta vazia.");
        Preconditions.checkNotNull(saldo.getUnidade(), erro + "A unidade organizacional orçamentária esta vazia.");
    }

    private void gerarSaldoOrcamentario(MovimentoDespesaORC movimento, Boolean validarValorNegativo) {
        SaldoFonteDespesaORC ultimoSaldo = buscarUltimoSaldoPorFonteDataAndUnidade(movimento.getFonteDespesaORC(), movimento.getDataMovimento(), movimento.getUnidadeOrganizacional());

        if (ultimoSaldo == null || ultimoSaldo.getId() == null) {
            gerarNovoSaldo(movimento);
        } else {
            if (DataUtil.dataSemHorario(ultimoSaldo.getDataSaldo()).compareTo(DataUtil.dataSemHorario(movimento.getDataMovimento())) == 0) {
                atualizarSaldoComMesmaData(movimento, ultimoSaldo, validarValorNegativo);
            } else {
                gerarNovoSaldoApartirDoUltimo(movimento, ultimoSaldo, validarValorNegativo);
            }
            gerarSaldoPosteriores(movimento, validarValorNegativo);
        }
    }

    private void gerarNovoSaldoApartirDoUltimo(MovimentoDespesaORC movimento, SaldoFonteDespesaORC ultimoSaldo, Boolean validarValorNegativo) {
        SaldoFonteDespesaORC novoSaldo = new SaldoFonteDespesaORC(movimento.getDataMovimento(), movimento.getFonteDespesaORC(), movimento.getUnidadeOrganizacional());
        atribuirValorApartirUltimoSaldo(ultimoSaldo, novoSaldo);
        novoSaldo = alterarValorSaldo(novoSaldo, movimento);
        if (validarValorIndividual(novoSaldo, movimento, validarValorNegativo)) {
            em.persist(novoSaldo);
        }
    }

    private void atualizarSaldoComMesmaData(MovimentoDespesaORC movimento, SaldoFonteDespesaORC ultimoSaldo, Boolean validarValorNegativo) {
        ultimoSaldo = alterarValorSaldo(ultimoSaldo, movimento);
        if (validarValorIndividual(ultimoSaldo, movimento, validarValorNegativo)) {
            em.merge(ultimoSaldo);
        }
    }

    private void gerarNovoSaldo(MovimentoDespesaORC movimento) {
        SaldoFonteDespesaORC novoSaldo = new SaldoFonteDespesaORC(movimento.getDataMovimento(), movimento.getFonteDespesaORC(), movimento.getUnidadeOrganizacional());
        novoSaldo = alterarValorSaldo(novoSaldo, movimento);
        em.persist(novoSaldo);
    }

    private void gerarSaldoPosteriores(MovimentoDespesaORC movimento, Boolean validarSaldoNegativo) {
        List<SaldoFonteDespesaORC> saldosPosteriores = buscarSaldosFuturos(movimento);
        if (saldosPosteriores != null && !saldosPosteriores.isEmpty()) {
            for (SaldoFonteDespesaORC saldoParaAtualizar : saldosPosteriores) {
                saldoParaAtualizar = alterarValorSaldo(saldoParaAtualizar, movimento);
                if (validarValorIndividual(saldoParaAtualizar, movimento, validarSaldoNegativo)) {
                    em.merge(saldoParaAtualizar);
                }
            }
        }
    }

    private void atribuirValorApartirUltimoSaldo(SaldoFonteDespesaORC ultimoSaldo, SaldoFonteDespesaORC novoSaldo) {
        novoSaldo.setDotacao(ultimoSaldo.getDotacao());
        novoSaldo.setEmpenhado(ultimoSaldo.getEmpenhado());
        novoSaldo.setLiquidado(ultimoSaldo.getLiquidado());
        novoSaldo.setPago(ultimoSaldo.getPago());
        novoSaldo.setAlteracao(ultimoSaldo.getAlteracao());
        novoSaldo.setReservado(ultimoSaldo.getReservado());
        novoSaldo.setSuplementado(ultimoSaldo.getSuplementado());
        novoSaldo.setReduzido(ultimoSaldo.getReduzido());
        novoSaldo.setEmpenhadoProcessado(ultimoSaldo.getEmpenhadoProcessado());
        novoSaldo.setEmpenhadoNaoProcessado(ultimoSaldo.getEmpenhadoNaoProcessado());
        novoSaldo.setLiquidadoProcessado(ultimoSaldo.getLiquidadoProcessado());
        novoSaldo.setLiquidadoNaoProcessado(ultimoSaldo.getLiquidadoNaoProcessado());
        novoSaldo.setPagoProcessado(ultimoSaldo.getPagoProcessado());
        novoSaldo.setPagoNaoProcessado(ultimoSaldo.getPagoNaoProcessado());
        novoSaldo.setReservadoPorLicitacao(ultimoSaldo.getReservadoPorLicitacao());
    }

    public SaldoFonteDespesaORC buscarUltimoSaldoPorFonteDataAndUnidade(FonteDespesaORC fonteDespesaOrc, Date data, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "select f.* from saldofontedespesaorc f "
            + " WHERE f.fonteDespesaORC_id = :fonte "
            + " AND f.unidadeOrganizacional_id = :unidade"
            + " AND trunc(f.dataSaldo) <= to_date(:data, 'dd/MM/yyyy')"
            + " ORDER BY trunc(f.dataSaldo) desc ";
        Query consulta = em.createNativeQuery(sql, SaldoFonteDespesaORC.class);
        consulta.setParameter("fonte", fonteDespesaOrc.getId());
        consulta.setParameter("unidade", unidadeOrganizacional.getId());
        consulta.setParameter("data", DataUtil.getDataFormatada(data));
        consulta.setMaxResults(1);
        try {
            return (SaldoFonteDespesaORC) consulta.getSingleResult();
        } catch (Exception ex) {
            return null;
        }
    }

    private boolean validarValorIndividual(SaldoFonteDespesaORC saldo, MovimentoDespesaORC movimento, Boolean validarValorNegativo) {
        if (validarValorNegativo) {
            if (saldo.getSaldoAtual().compareTo(BigDecimal.ZERO) < 0) {
                return getMensagemValidacao(saldo, movimento, saldo.getSaldoAtual());
            }
            if (!OperacaoORC.SUPLEMENTACAO.equals(movimento.getOperacaoORC()) && !OperacaoORC.ANULACAO.equals(movimento.getOperacaoORC())) {
                BigDecimal valorIndividual = recuperarValor(saldo, movimento.getOperacaoORC());
                if (valorIndividual.compareTo(BigDecimal.ZERO) < 0) {
                    return getMensagemValidacao(saldo, movimento, valorIndividual);
                }
            }
        }
        return true;
    }

    public boolean validarValorColunaIndividual(BigDecimal valorMovimento, FonteDespesaORC fonteDespesaORC, Date dataOperacao, UnidadeOrganizacional unidadeOrganizacional, OperacaoORC operacaoORC) {
        SaldoFonteDespesaORC saldoFonte = recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(fonteDespesaORC, dataOperacao, unidadeOrganizacional);

        BigDecimal valorIndividual = recuperarValor(saldoFonte, operacaoORC);
        if (valorMovimento.compareTo(valorIndividual) > 0) {
            throw new ExcecaoNegocioGenerica("O valor movimentado de " + Util.formataValor(valorMovimento)
                + " para operação " + operacaoORC.toString()
                + ", é maior que valor disponível de " + Util.formataValor(valorIndividual)
                + " para a dotação: " + fonteDespesaORC.getDescricaoContaDespesa() + " / " + fonteDespesaORC.getDescricaoFonteDeRecurso() + ".");
        }
        return true;
    }

    private boolean getMensagemValidacao(SaldoFonteDespesaORC saldo, MovimentoDespesaORC movimento, BigDecimal valorMovimento) {
        throw new ExcecaoNegocioGenerica("O Valor " + Util.formataValor(valorMovimento)
            + " do saldo orçamentário para operação: " + movimento.getOperacaoORC().toString()
            + "(" + movimento.getTipoOperacaoORC().getDescricao() + ") ficou negativo na data: " + DataUtil.getDataFormatada(saldo.getDataSaldo())
            + ". Saldo atual: " + Util.formataValor(saldo.getSaldoAtual()) + " - " + movimento.getFonteDespesaORC().getDescricaoContaDespesa());
    }

    public List<SaldoFonteDespesaORC> buscarSaldosFuturos(MovimentoDespesaORC movimento) {
        String sql = " select f.* from SaldoFonteDespesaORC f "
            + " where f.fonteDespesaORC_id = :fonte"
            + " and f.unidadeOrganizacional_id = :unidade"
            + " and trunc(f.dataSaldo) > to_date(:data, 'dd/MM/yyyy')"
            + " ORDER BY trunc(f.dataSaldo) ";
        try {
            Query consulta = em.createNativeQuery(sql, SaldoFonteDespesaORC.class);
            consulta.setParameter("fonte", movimento.getFonteDespesaORC().getId());
            consulta.setParameter("unidade", movimento.getUnidadeOrganizacional().getId());
            consulta.setParameter("data", DataUtil.getDataFormatada(movimento.getDataMovimento()));
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    private BigDecimal recuperarValor(SaldoFonteDespesaORC saldo, OperacaoORC operacaoORC) {
        if (operacaoORC != null) {
            switch (operacaoORC) {
                case DOTACAO:
                    return saldo.getDotacao();
                case SUPLEMENTACAO:
                    return saldo.getAlteracao();
                case ANULACAO:
                    return saldo.getAlteracao();
                case EMPENHO:
                    return saldo.getEmpenhado();
                case EMPENHORESTO_PROCESSADO:
                    return saldo.getEmpenhadoProcessado();
                case EMPENHORESTO_NAO_PROCESSADO:
                    return saldo.getEmpenhadoNaoProcessado();
                case LIQUIDACAO:
                    return saldo.getLiquidado();
                case LIQUIDACAORESTO_PROCESSADO:
                    return saldo.getLiquidadoProcessado();
                case LIQUIDACAORESTO_NAO_PROCESSADO:
                    return saldo.getLiquidadoNaoProcessado();
                case PAGAMENTO:
                    return saldo.getPago();
                case PAGAMENTORESTO_PROCESSADO:
                    return saldo.getPagoProcessado();
                case PAGAMENTORESTO_NAO_PROCESSADO:
                    return saldo.getPagoNaoProcessado();
                case RESERVADO:
                    return saldo.getReservado();
                case RESERVADO_POR_LICITACAO:
                    return saldo.getReservadoPorLicitacao();
            }
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal atualizarValorSaldoPorLancamento(BigDecimal valorSaldo, BigDecimal valorMovimento, TipoOperacaoORC tipoOperacaoORC) {
        if (TipoOperacaoORC.NORMAL.equals(tipoOperacaoORC)) {
            return valorSaldo.add(valorMovimento);
        } else {
            return valorSaldo.subtract(valorMovimento);
        }
    }

    private SaldoFonteDespesaORC alterarValorSaldo(SaldoFonteDespesaORC saldo, MovimentoDespesaORC movimento) {
        BigDecimal valor = movimento.getValor();
        if (movimento.getOperacaoORC() != null) {
            switch (movimento.getOperacaoORC()) {
                case DOTACAO:
                    saldo.setDotacao(atualizarValorSaldoPorLancamento(saldo.getDotacao(), valor, movimento.getTipoOperacaoORC()));
                    break;
                case SUPLEMENTACAO:
                    if (TipoOperacaoORC.NORMAL.equals(movimento.getTipoOperacaoORC())) {
                        saldo.setAlteracao(saldo.getAlteracao().add(valor));
                    } else {
                        saldo.setAlteracao(saldo.getAlteracao().subtract(valor));
                    }
                    saldo.setSuplementado(atualizarValorSaldoPorLancamento(saldo.getSuplementado(), valor, movimento.getTipoOperacaoORC()));
                    break;
                case ANULACAO:
                    if (TipoOperacaoORC.NORMAL.equals(movimento.getTipoOperacaoORC())) {
                        saldo.setAlteracao(saldo.getAlteracao().subtract(valor));
                    } else {
                        saldo.setAlteracao(saldo.getAlteracao().add(valor));
                    }
                    saldo.setReduzido(atualizarValorSaldoPorLancamento(saldo.getReduzido(), valor, movimento.getTipoOperacaoORC()));
                    break;
                case EMPENHO:
                    saldo.setEmpenhado(atualizarValorSaldoPorLancamento(saldo.getEmpenhado(), valor, movimento.getTipoOperacaoORC()));
                    break;
                case EMPENHORESTO_NAO_PROCESSADO:
                    saldo.setEmpenhadoNaoProcessado(atualizarValorSaldoPorLancamento(saldo.getEmpenhadoNaoProcessado(), valor, movimento.getTipoOperacaoORC()));
                    break;
                case EMPENHORESTO_PROCESSADO:
                    saldo.setEmpenhadoProcessado(atualizarValorSaldoPorLancamento(saldo.getEmpenhadoProcessado(), valor, movimento.getTipoOperacaoORC()));
                    break;
                case LIQUIDACAO:
                    saldo.setLiquidado(atualizarValorSaldoPorLancamento(saldo.getLiquidado(), valor, movimento.getTipoOperacaoORC()));
                    break;
                case LIQUIDACAORESTO_NAO_PROCESSADO:
                    saldo.setLiquidadoNaoProcessado(atualizarValorSaldoPorLancamento(saldo.getLiquidadoNaoProcessado(), valor, movimento.getTipoOperacaoORC()));
                    break;
                case LIQUIDACAORESTO_PROCESSADO:
                    saldo.setLiquidadoProcessado(atualizarValorSaldoPorLancamento(saldo.getLiquidadoProcessado(), valor, movimento.getTipoOperacaoORC()));
                    break;
                case PAGAMENTO:
                    saldo.setPago(atualizarValorSaldoPorLancamento(saldo.getPago(), valor, movimento.getTipoOperacaoORC()));
                    break;
                case PAGAMENTORESTO_NAO_PROCESSADO:
                    saldo.setPagoNaoProcessado(atualizarValorSaldoPorLancamento(saldo.getPagoNaoProcessado(), valor, movimento.getTipoOperacaoORC()));
                    break;
                case PAGAMENTORESTO_PROCESSADO:
                    saldo.setPagoProcessado(atualizarValorSaldoPorLancamento(saldo.getPagoProcessado(), valor, movimento.getTipoOperacaoORC()));
                    break;
                case RESERVADO:
                    saldo.setReservado(atualizarValorSaldoPorLancamento(saldo.getReservado(), valor, movimento.getTipoOperacaoORC()));
                    break;
                case RESERVADO_POR_LICITACAO:
                    saldo.setReservadoPorLicitacao(atualizarValorSaldoPorLancamento(saldo.getReservadoPorLicitacao(), valor, movimento.getTipoOperacaoORC()));
                    break;
            }
        }
        return saldo;
    }

    public SaldoFonteDespesaORC recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(FonteDespesaORC fonteDespesaOrc, Date data, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = "select f.* from SaldoFonteDespesaORC f"
            + " WHERE f.fonteDespesaORC_id = :fonte "
            + " AND f.unidadeOrganizacional_id = :unidade"
            + " AND trunc(f.dataSaldo) <= to_date(:data,'dd/mm/yyyy')"
            + " ORDER BY trunc(f.dataSaldo) desc";
        Query consulta = em.createNativeQuery(sql, SaldoFonteDespesaORC.class);
        consulta.setParameter("fonte", fonteDespesaOrc.getId());
        consulta.setParameter("unidade", unidadeOrganizacional.getId());
        consulta.setParameter("data", DataUtil.getDataFormatada(data));
        consulta.setMaxResults(1);
        try {
            return (SaldoFonteDespesaORC) consulta.getSingleResult();
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Saldo da Fonte de Despesa Orçamentária não encontrado para Data: " + DataUtil.getDataFormatada(data) + ", Unidade: " + unidadeOrganizacional + " e Fonte: " + fonteDespesaOrc);
        }
    }

    public BigDecimal saldoRealPorFonte(FonteDespesaORC fonte, Date data, UnidadeOrganizacional unidadeOrganizacional) {
        try {
            SaldoFonteDespesaORC saldo = recuperarUltimoSaldoPorFonteDespesaORCDataUnidadeOrganizacional(fonte, data, unidadeOrganizacional);
            if (saldo != null) {
                return saldo.getSaldoAtual();
            }
            return BigDecimal.ZERO;
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    public void excluirSaldosNoPeriodo(AssistenteReprocessamento assistente) {
        if (assistente.getExcluirSaldos() &&
            !assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryDotacao().isEmpty() &&
            !assistente.getReprocessamentoSaldoFonteDespesaOrc().getQuerySolicitacoEmpenho().isEmpty() &&
            !assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryAlteracaoOrcamentaria().isEmpty() &&
            !assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryEmpenhado().isEmpty() &&
            !assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryLiquidado().isEmpty() &&
            !assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryPago().isEmpty() &&
            !assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryReservaDotacao().isEmpty() &&
            !assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryReservadoPorLicitacaoNormal().isEmpty() &&
            !assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryCancelamentoReservaDotacao().isEmpty() &&
            !assistente.getReprocessamentoSaldoFonteDespesaOrc().getQueryReservadoPorLicitacaoEstorno().isEmpty()
        ) {
            assistente.getAssistenteBarraProgresso().setMensagem("<b> <font color='black'>...Excluindo Saldos...</font> </b>");
            executarUpdateEmpenho(assistente);
            executarUpdateEmpenhoEstorno(assistente);
            executarUpdateLiquidacao(assistente);
            executarUpdateLiquidacaoEstorno(assistente);
            executarUpdatePagamento(assistente);
            executarUpdatePagamentoEstorno(assistente);
            executarDeleteSaldoFonteDespesaOrc(assistente);
            executarDeleteMovimentoDespesaOrc(assistente);
        }
    }

    private void executarDeleteMovimentoDespesaOrc(AssistenteReprocessamento assistente) {
        String sql = " delete FROM MovimentoDespesaORC ssc "
            + " WHERE trunc(ssc.datamovimento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        sql += adicionarParametroUnidade(assistente.getReprocessamentoSaldoFonteDespesaOrc().getHierarquiaOrganizacional());
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getDespesaORC() != null) {
            sql += " and ssc.fontedespesaorc_id in (select id from fontedespesaorc where despesaorc_id = :despesaOrc) ";
        }
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getFonteDespesaORC() != null) {
            sql += " and ssc.fontedespesaorc_id = :fonteDespesaOrc ";
        }
        Query q = em.createNativeQuery(sql);
        adicionarParametrosAndExecutarUpdate(assistente, q);
    }

    private void executarDeleteSaldoFonteDespesaOrc(AssistenteReprocessamento assistente) {
        String sql = " delete FROM saldofontedespesaorc ssc "
            + " WHERE trunc(ssc.datasaldo) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        sql += adicionarParametroUnidade(assistente.getReprocessamentoSaldoFonteDespesaOrc().getHierarquiaOrganizacional());
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getDespesaORC() != null) {
            sql += " and fontedespesaorc_id in (select id from fontedespesaorc where despesaorc_id = :despesaOrc) ";
        }
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getFonteDespesaORC() != null) {
            sql += " and fontedespesaorc_id = :fonteDespesaOrc ";
        }
        Query q = em.createNativeQuery(sql);
        adicionarParametrosAndExecutarUpdate(assistente, q);
    }

    private void executarUpdatePagamentoEstorno(AssistenteReprocessamento assistente) {
        String sql = " update pagamentoestorno set movimentodespesaorc_id = null  "
            + " WHERE trunc(dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        sql += adicionarParametroUnidade(assistente.getReprocessamentoSaldoFonteDespesaOrc().getHierarquiaOrganizacional());
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getDespesaORC() != null) {
            sql += " and pagamento_id in (select pag.id from pagamento pag inner join liquidacao liq on liq.id = pag.liquidacao_id "
                + " inner join empenho emp on liq.empenho_id = emp.id where emp.despesaorc_id = :despesaOrc) ";
        }
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getFonteDespesaORC() != null) {
            sql += " and pagamento_id in (select pag.id from pagamento pag inner join liquidacao liq on liq.id = pag.liquidacao_id " +
                " inner join empenho emp on liq.empenho_id = emp.id where emp.fontedespesaorc_id = :fonteDespesaOrc) ";
        }
        Query q = em.createNativeQuery(sql);
        adicionarParametrosAndExecutarUpdate(assistente, q);
    }

    private void executarUpdatePagamento(AssistenteReprocessamento assistente) {
        String sql = " update pagamento set movimentodespesaorc_id = null  "
            + " WHERE trunc(datapagamento) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        sql += adicionarParametroUnidade(assistente.getReprocessamentoSaldoFonteDespesaOrc().getHierarquiaOrganizacional());
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getDespesaORC() != null) {
            sql += " and liquidacao_id in (select liq.id from liquidacao liq inner join empenho emp on liq.empenho_id = emp.id where emp.despesaorc_id = :despesaOrc) ";
        }
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getFonteDespesaORC() != null) {
            sql += " and liquidacao_id in (select liq.id from liquidacao liq inner join empenho emp on liq.empenho_id = emp.id where emp.fontedespesaorc_id = :fonteDespesaOrc) ";
        }
        Query q = em.createNativeQuery(sql);
        adicionarParametrosAndExecutarUpdate(assistente, q);
    }

    private void executarUpdateLiquidacaoEstorno(AssistenteReprocessamento assistente) {
        String sql = " update liquidacaoestorno set movimentodespesaorc_id = null  "
            + " WHERE trunc(dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        sql += adicionarParametroUnidade(assistente.getReprocessamentoSaldoFonteDespesaOrc().getHierarquiaOrganizacional());
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getDespesaORC() != null) {
            sql += " and liquidacao_id in (select liq.id from liquidacao liq inner join empenho emp on liq.empenho_id = emp.id where emp.despesaorc_id = :despesaOrc) ";
        }
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getFonteDespesaORC() != null) {
            sql += " and liquidacao_id in (select liq.id from liquidacao liq inner join empenho emp on liq.empenho_id = emp.id where emp.fontedespesaorc_id = :fonteDespesaOrc) ";
        }
        Query q = em.createNativeQuery(sql);
        adicionarParametrosAndExecutarUpdate(assistente, q);
    }

    private void executarUpdateLiquidacao(AssistenteReprocessamento assistente) {
        String sql = " update liquidacao set movimentodespesaorc_id = null  "
            + " WHERE trunc(dataliquidacao) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        sql += adicionarParametroUnidade(assistente.getReprocessamentoSaldoFonteDespesaOrc().getHierarquiaOrganizacional());
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getDespesaORC() != null) {
            sql += " and empenho_id in (select emp.id from empenho emp where emp.despesaorc_id = :despesaOrc) ";
        }
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getFonteDespesaORC() != null) {
            sql += " and empenho_id in (select id from empenho where fontedespesaorc_id = :fonteDespesaOrc) ";
        }
        Query q = em.createNativeQuery(sql);
        adicionarParametrosAndExecutarUpdate(assistente, q);
    }

    private void executarUpdateEmpenhoEstorno(AssistenteReprocessamento assistente) {
        String sql = " update empenhoestorno set movimentodespesaorc_id = null  "
            + " WHERE trunc(dataestorno) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        sql += adicionarParametroUnidade(assistente.getReprocessamentoSaldoFonteDespesaOrc().getHierarquiaOrganizacional());
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getDespesaORC() != null) {
            sql += " and empenho_id in (select id from empenho where despesaorc_id = :despesaOrc) ";
        }
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getFonteDespesaORC() != null) {
            sql += " and empenho_id in (select id from empenho where fontedespesaorc_id = :fonteDespesaOrc) ";
        }
        Query q = em.createNativeQuery(sql);
        adicionarParametrosAndExecutarUpdate(assistente, q);
    }

    private void executarUpdateEmpenho(AssistenteReprocessamento assistente) {
        String sql = " update empenho set movimentodespesaorc_id = null  "
            + " WHERE trunc(dataempenho) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        sql += adicionarParametroUnidade(assistente.getReprocessamentoSaldoFonteDespesaOrc().getHierarquiaOrganizacional());
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getDespesaORC() != null) {
            sql += " and despesaorc_id = :despesaOrc ";
        }
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getFonteDespesaORC() != null) {
            sql += " and fontedespesaorc_id = :fonteDespesaOrc ";
        }
        Query q = em.createNativeQuery(sql);
        adicionarParametrosAndExecutarUpdate(assistente, q);
    }

    private void adicionarParametrosAndExecutarUpdate(AssistenteReprocessamento assistente, Query q) {
        q.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getHierarquiaOrganizacional() != null) {
            q.setParameter("unidadeOrganizacional", assistente.getReprocessamentoSaldoFonteDespesaOrc().getHierarquiaOrganizacional().getSubordinada().getId());
        }
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getDespesaORC() != null) {
            q.setParameter("despesaOrc", assistente.getReprocessamentoSaldoFonteDespesaOrc().getDespesaORC().getId());
        }
        if (assistente.getReprocessamentoSaldoFonteDespesaOrc().getFonteDespesaORC() != null) {
            q.setParameter("fonteDespesaOrc", assistente.getReprocessamentoSaldoFonteDespesaOrc().getFonteDespesaORC().getId());
        }
        q.executeUpdate();
    }

    private String adicionarParametroUnidade(HierarquiaOrganizacional hierarquiaOrganizacional) {
        if (hierarquiaOrganizacional != null) {
            return " and unidadeorganizacional_id = :unidadeOrganizacional ";
        }
        return "";
    }

    public List<MovimentoDespesaORC> buscarMovimentos(FonteDespesaORC fonteDespesaORC, Date data, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = " select f.* from MovimentoDespesaORC f "
            + " where f.fonteDespesaORC_id = :fonte"
            + " and f.unidadeOrganizacional_id = :unidade"
            + " and trunc(f.dataMovimento) = to_date(:data, 'dd/MM/yyyy')"
            + " ORDER BY trunc(f.dataMovimento), f.id ";
        try {
            Query consulta = em.createNativeQuery(sql, MovimentoDespesaORC.class);
            consulta.setParameter("fonte", fonteDespesaORC.getId());
            consulta.setParameter("unidade", unidadeOrganizacional.getId());
            consulta.setParameter("data", DataUtil.getDataFormatada(data));
            return consulta.getResultList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    public void executarDeleteMovimentoDespesaOrcPorIdOrigem(Long idOrigem) {
        String sql = " delete FROM MovimentoDespesaORC mov where mov.idOrigem = :idOrigem ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idOrigem", idOrigem);
        q.executeUpdate();
    }
}

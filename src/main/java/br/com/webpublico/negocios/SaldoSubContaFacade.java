/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.MovimentacaoFinanceira;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.*;
import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author venon
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 50000)
public class SaldoSubContaFacade implements Serializable {

    protected static final Logger logger = LoggerFactory.getLogger(SaldoSubContaFacade.class);
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;


    @Lock(LockType.WRITE)
    public void gerarSaldoFinanceiro(UnidadeOrganizacional unidade, SubConta subConta, ContaDeDestinacao contaDeDestinacao, BigDecimal valor,
                                     TipoOperacao operacao, Date data, EventoContabil eventoContabil, String historicoRazao,
                                     MovimentacaoFinanceira tipoDeMovimento, String uuid, Boolean realizarValidacoes) {
        try {
            if (uuid == null || uuid.trim().isEmpty()) {
                try {
                    uuid = UUID.randomUUID().toString();
                } catch (Exception e) {
                    logger.error("Erro ao gerar UUID Saldo Financeiro : " + e.getMessage());
                }
            }
            if (uuid == null || uuid.trim().isEmpty()) {
                logger.error("UUID Saldo Financeiro nulo sem exceção " + unidade + " " + subConta + " " + tipoDeMovimento);
            }
            MovimentoContaFinanceira movimento = gerarMovimentoSaldoFinanceiro(unidade, subConta, contaDeDestinacao, valor, operacao, data, eventoContabil, historicoRazao, tipoDeMovimento, uuid);
            SaldoSubConta ultimoSaldo = getUltimoSaldoPorDataUnidadeConta(movimento);

            if (validarSaldoDisponivel(movimento, valor, ultimoSaldo, realizarValidacoes)) {
                if (ultimoSaldo == null || ultimoSaldo.getId() == null) {
                    ultimoSaldo = definirValorSaldoSubConta(movimento);
                    ultimoSaldo = alterarValorSaldo(ultimoSaldo, operacao, valor);
                    em.persist(ultimoSaldo);
                } else {
                    if (DataUtil.dataSemHorario(ultimoSaldo.getDataSaldo()).compareTo(DataUtil.dataSemHorario(data)) == 0) {
                        ultimoSaldo = alterarValorSaldo(ultimoSaldo, operacao, valor);
                        em.merge(ultimoSaldo);
                    } else {
                        SaldoSubConta novoSaldo = definirValorSaldoSubConta(movimento);
                        novoSaldo = copiarValoresDoSaldoRecuperado(ultimoSaldo, novoSaldo);
                        novoSaldo = alterarValorSaldo(novoSaldo, operacao, valor);
                        em.persist(novoSaldo);
                    }
                }
                gerarSaldoPosteriores(valor, operacao, movimento, realizarValidacoes);
            }
        } catch (OptimisticLockException optex) {
            throw new ExcecaoNegocioGenerica("Saldo financeiro está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(" " + ex.getMessage());
        }
    }

    public MovimentoContaFinanceira gerarMovimentoSaldoFinanceiro(UnidadeOrganizacional unidade, SubConta sub, ContaDeDestinacao contaDeDestinacao,
                                                                  BigDecimal valor, TipoOperacao op, Date data, EventoContabil eventoContabil,
                                                                  String historicoRazao, MovimentacaoFinanceira tipoDeMovimento, String uuid) {
        try {
            MovimentoContaFinanceira mov = new MovimentoContaFinanceira();
            mov.setDataSaldo(data);
            mov.setUnidadeOrganizacional(unidade);
            mov.setEventoContabil(eventoContabil);
            mov.setContaDeDestinacao(contaDeDestinacao);
            mov.setFonteDeRecursos(contaDeDestinacao.getFonteDeRecursos());
            mov.setHistorico(historicoRazao);
            mov.setSubConta(sub);
            mov.setMovimentacaoFinanceira(tipoDeMovimento);
            if (TipoOperacao.DEBITO.equals(op)) {
                mov.setTotalDebito(valor);
            } else {
                mov.setTotalCredito(valor);
            }
            mov.setUuid(uuid);
            if (uuid == null || uuid.trim().isEmpty()) {
                logger.error("Irá salvar UUID Saldo Financeiro nulo " + tipoDeMovimento);
            }
            mov = em.merge(mov);
            mov.setTipoOperacao(op);
            return mov;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    private void gerarSaldoPosteriores(BigDecimal valor, TipoOperacao op, MovimentoContaFinanceira movimento, Boolean realizarValidacao) {
        List<SaldoSubConta> saldosPosteriores = getSaldosPosterioresPorDataUnidadeConta(movimento);
        if (!saldosPosteriores.isEmpty()) {
            for (SaldoSubConta saldoParaAtualizar : saldosPosteriores) {
                saldoParaAtualizar = alterarValorSaldo(saldoParaAtualizar, op, valor);
                BigDecimal saldoDoDia = saldoParaAtualizar.getTotalCredito().subtract(saldoParaAtualizar.getTotalDebito());
                if (realizarValidacao) {
                    if (saldoDoDia.compareTo(BigDecimal.ZERO) < 0) {
                        throw new ExcecaoNegocioGenerica("O Saldo ficará negativo em <b> " + Util.formataValor(saldoDoDia) + "</b>, na data " + DataUtil.getDataFormatada(saldoParaAtualizar.getDataSaldo()) + " para a Conta Financeira " + saldoParaAtualizar.getSubConta() + ".");
                    }
                }
                em.merge(saldoParaAtualizar);
            }
        }
    }

    public SaldoSubConta copiarValoresDoSaldoRecuperado(SaldoSubConta saldo, SaldoSubConta novoSaldo) {
        try {
            novoSaldo.setTotalCredito(saldo.getTotalCredito());
            novoSaldo.setTotalDebito(saldo.getTotalDebito());
            return novoSaldo;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public SaldoSubConta definirValorSaldoSubConta(MovimentoContaFinanceira mov) {
        try {
            SaldoSubConta saldo = new SaldoSubConta();
            saldo.setDataSaldo(mov.getDataSaldo());
            saldo.setUnidadeOrganizacional(mov.getUnidadeOrganizacional());
            saldo.setSubConta(mov.getSubConta());
            saldo.setContaDeDestinacao(mov.getContaDeDestinacao());
            saldo.setFonteDeRecursos(mov.getFonteDeRecursos());
            saldo.setTotalCredito(BigDecimal.ZERO);
            saldo.setTotalDebito(BigDecimal.ZERO);
            saldo.setValor(BigDecimal.ZERO);
            return saldo;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public SaldoSubConta alterarValorSaldo(SaldoSubConta saldo, TipoOperacao operacao, BigDecimal valor) {
        try {
            if (operacao.equals(TipoOperacao.CREDITO)) {
                saldo.setTotalCredito(saldo.getTotalCredito().add(valor));
            } else {
                saldo.setTotalDebito(saldo.getTotalDebito().add(valor));
            }
            return saldo;
        } catch (Exception ex) {
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }
    }

    public Boolean validarSaldoDisponivel(MovimentoContaFinanceira mov, BigDecimal valor, SaldoSubConta ultimoSaldo, Boolean realizarValidacao) {

        if (realizarValidacao && TipoOperacao.DEBITO.equals(mov.getTipoOperacao())) {
            if (ultimoSaldo.getSaldoDoDia().compareTo(valor) < 0) {
                throw new ExcecaoNegocioGenerica(" O Saldo disponível de " + "<b>" + Util.formataValor(ultimoSaldo.getSaldoDoDia()) + "</b>" +
                    " na Conta Financeira: " + mov.getSubConta() +
                    " para a Conta de Destinação de Recursos: " + mov.getContaDeDestinacao() +
                    " é insuficiente para realizar a operação no valor de " + "<b>" + Util.formataValor(valor) + "</b>");
            }
        }
        return true;
    }

    public SaldoSubConta getUltimoSaldoPorDataUnidadeConta(MovimentoContaFinanceira movimento) {

        String sql = "select saldo.* from SaldoSubConta saldo "
            + " where trunc(saldo.dataSaldo) <= trunc(:dataSaldo) "
            + " and saldo.unidadeOrganizacional_id = :unidadeOrganizacional "
            + " and saldo.contaDeDestinacao_id = :contaDeDestinacao"
            + " and saldo.subConta_id = :subConta "
            + " order by saldo.dataSaldo desc";

        Query q = em.createNativeQuery(sql, SaldoSubConta.class);

        q.setParameter("dataSaldo", DataUtil.dataSemHorario(movimento.getDataSaldo()));
        q.setParameter("unidadeOrganizacional", movimento.getUnidadeOrganizacional().getId());
        q.setParameter("contaDeDestinacao", movimento.getContaDeDestinacao().getId());
        q.setParameter("subConta", movimento.getSubConta().getId());
        List<SaldoSubConta> resultado = q.getResultList();
        if (resultado != null && !resultado.isEmpty()) {
            return resultado.get(0);
        }
        return new SaldoSubConta();
    }

    public List<SaldoSubConta> getSaldosPosterioresPorDataUnidadeConta(MovimentoContaFinanceira mov) {
        String sql = " select saldo.* from SaldoSubConta saldo "
            + " where trunc(saldo.dataSaldo) > trunc(:dataSaldo)"
            + " and saldo.unidadeOrganizacional_id = :idUnidadeOrganizacional "
            + " and saldo.contaDeDestinacao_id  = :contaDeDestinacao"
            + " and saldo.subConta_id = :idSubConta "
            + " order by saldo.dataSaldo desc ";

        Query q = em.createNativeQuery(sql, SaldoSubConta.class);
        q.setParameter("dataSaldo", DataUtil.dataSemHorario(mov.getDataSaldo()));
        q.setParameter("idUnidadeOrganizacional", mov.getUnidadeOrganizacional().getId());
        q.setParameter("contaDeDestinacao", mov.getContaDeDestinacao().getId());
        q.setParameter("idSubConta", mov.getSubConta().getId());
        List<SaldoSubConta> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return resultado;
        }
    }

    public SaldoSubConta recuperaUltimoSaldoSubContaPorData(UnidadeOrganizacional unidade, SubConta sub, ContaDeDestinacao contaDeDestinacao, Date data) throws ExcecaoNegocioGenerica {
        try {
            Preconditions.checkNotNull(unidade, "A Unidade Organizacional para recuperar o saldo da conta financeira está vazia!");
            Preconditions.checkNotNull(sub, "A Conta Financeira para recuperar o saldo da conta financeira está vazia!");
            Preconditions.checkNotNull(contaDeDestinacao, "A Conta de Destinação de Recursos para recuperar o saldo da conta financeira está vazia!");
            Preconditions.checkNotNull(data, "A Data para recuperar o saldo da conta financeira está vazia!");

            String sql = "SELECT ssc.* FROM saldosubconta ssc "
                + "WHERE trunc(ssc.datasaldo) <= trunc(:data) "
                + "AND ssc.subconta_id = :subconta "
                + "AND ssc.unidadeorganizacional_id = :unidade ";
            if (contaDeDestinacao.getId() != null) {
                sql += "AND ssc.contaDeDestinacao_id = :contaDeDestinacao ";
            }

            sql += "ORDER BY ssc.datasaldo DESC";

            Query q = em.createNativeQuery(sql, SaldoSubConta.class);

            q.setParameter("subconta", sub.getId());
            q.setParameter("unidade", unidade.getId());
            if (contaDeDestinacao.getId() != null) {
                q.setParameter("contaDeDestinacao", contaDeDestinacao.getId());
            }
            q.setParameter("data", DataUtil.dataSemHorario(data));
            q.setMaxResults(1);
            List<SaldoSubConta> resultado = q.getResultList();
            if (resultado.isEmpty()) {
                SaldoSubConta saldoSub = new SaldoSubConta();
                saldoSub.setDataSaldo(data);
                saldoSub.setSubConta(sub);
                saldoSub.setContaDeDestinacao(contaDeDestinacao);
                saldoSub.setFonteDeRecursos(contaDeDestinacao.getFonteDeRecursos());
                saldoSub.setUnidadeOrganizacional(unidade);
                saldoSub.setTotalCredito(BigDecimal.ZERO);
                saldoSub.setTotalDebito(BigDecimal.ZERO);
                return saldoSub;
            } else {
                return resultado.get(0);
            }
        } catch (IllegalArgumentException e) {
            throw new ExcecaoNegocioGenerica(e.getMessage());
        } catch (Exception ex) {
            logger.error("Erro ao recuperar ultimo saldo da conta financeira: {}", ex);
            throw new ExcecaoNegocioGenerica(ex.getMessage());
        }

    }

    public BigDecimal buscarValorUltimoSaldoSubContaPorData(UnidadeOrganizacional unidade, SubConta sub, ContaDeDestinacao contaDeDestinacao, Date dataReferencia) {
        String sql = " SELECT coalesce(ssc.TOTALCREDITO, 0) - coalesce(ssc.TOTALDEBITO, 0) "
            + " FROM saldosubconta ssc "
            + " WHERE trunc(ssc.datasaldo) <= to_date(:dataReferencia, 'dd/MM/yyyy') "
            + "   AND ssc.subconta_id = :subconta "
            + "   AND ssc.unidadeorganizacional_id = :unidade "
            + (contaDeDestinacao != null ? " AND ssc.contaDeDestinacao_id = :contaDeDestinacao " : "")
            + " ORDER BY ssc.datasaldo DESC ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("subconta", sub.getId());
        q.setParameter("unidade", unidade.getId());
        q.setParameter("dataReferencia", DataUtil.getDataFormatada(dataReferencia));
        if (contaDeDestinacao != null) {
            q.setParameter("contaDeDestinacao", contaDeDestinacao.getId());
        }
        q.setMaxResults(1);
        try {
            return (BigDecimal) q.getSingleResult();
        } catch (NoResultException nre) {
            return BigDecimal.ZERO;
        }
    }

    //Método criado apenas para utilizar no Reprocessamento de Saldo Financeiro
    public void excluirSaldosPeriodo(Date dataInicial, Date dataFinal, List<Long> subContas, ContaDeDestinacao contaDeDestinacao) {
        String sql = " delete FROM saldosubconta ssc "
            + " WHERE trunc(ssc.datasaldo) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ";
        if (!subContas.isEmpty()) {
            sql += " and ssc.SUBCONTA_ID in :subConta ";
        }
        if (contaDeDestinacao != null) {
            sql += " and ssc.contaDeDestinacao_id = :contaDeDestinacao ";
        }
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (!subContas.isEmpty()) {
            q.setParameter("subConta", subContas);
        }
        if (contaDeDestinacao != null) {
            q.setParameter("contaDeDestinacao", contaDeDestinacao.getId());
        }
        q.executeUpdate();

        sql = " delete FROM movimentocontafinanceira ssc "
            + "WHERE trunc(ssc.datasaldo) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ";
        if (!subContas.isEmpty()) {
            sql += " and ssc.SUBCONTA_ID in :subConta ";
        }
        if (contaDeDestinacao != null) {
            sql += " and ssc.contaDeDestinacao_id = :contaDeDestinacao ";
        }
        q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (!subContas.isEmpty()) {
            q.setParameter("subConta", subContas);
        }
        if (contaDeDestinacao != null) {
            q.setParameter("contaDeDestinacao", contaDeDestinacao.getId());
        }
        q.executeUpdate();
    }
}


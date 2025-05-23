/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.ValidadorMatrizSaldoContabilBlc;
import br.com.webpublico.entidadesauxiliares.contabil.analise.AssistenteAnaliseContabil;
import br.com.webpublico.enums.SomaSubtraiSaldoContaContabil;
import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.enums.TipoLancamentoContabil;
import br.com.webpublico.negocios.contabil.singleton.SingletonFechamentoMensal;
import br.com.webpublico.util.DataUtil;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

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
public class SaldoContaContabilFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SingletonFechamentoMensal singletonFechamentoMensal;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;
    private ConfiguracaoContabil configuracaoContabil;

    @Lock(LockType.WRITE)
    public void geraSaldoContaContabil(Date dataSaldo, Conta conta, UnidadeOrganizacional uni, TipoLancamentoContabil tlc, BigDecimal valor, TipoBalancete tipoBalancete, SomaSubtraiSaldoContaContabil somaSubtraiSaldoContaContabil) throws ExcecaoNegocioGenerica {
        try {
            singletonFechamentoMensal.validarMesContabil(dataSaldo);
            Preconditions.checkNotNull(dataSaldo, "A Data esta vazia ao gerar o Saldo da Conta Contábil.");
            Preconditions.checkNotNull(conta, "A Conta esta vazia ao gerar o Saldo da Conta Contábil.");
            Preconditions.checkNotNull(uni, "A Unidade Organizacional esta vazia ao gerar o Saldo da Conta Contábil.");
            Preconditions.checkNotNull(tlc, "O Tipo de Lançamento Contábil esta vazio ao gerar o Saldo da Conta Contábil.");
            Preconditions.checkNotNull(valor, "O Valor esta vazio ao gerar o Saldo da Conta Contábil.");
            Preconditions.checkNotNull(tipoBalancete, "O Tipo de Balancete esta vazio ao gerar o Saldo da Conta Contábil.");

            movimentarSaldo(dataSaldo, conta, uni, tlc, valor, tipoBalancete, somaSubtraiSaldoContaContabil);
            geraSaldosFuturos(tlc, dataSaldo, valor, conta, uni, tipoBalancete, somaSubtraiSaldoContaContabil);
        } catch (OptimisticLockException optex) {
            throw new ExcecaoNegocioGenerica("Saldo contábil está sendo movimentado por outro usuário. Por favor, tente salvar o movimento em alguns instantes.");
        } catch (Exception e) {
            throw new ExcecaoNegocioGenerica("Problema ao gerar saldos das Contas Contábeis. Erro: " + e.getMessage());
        }
    }

    private void movimentarSaldo(Date dataMovimento, Conta conta, UnidadeOrganizacional uni, TipoLancamentoContabil tlc, BigDecimal valor, TipoBalancete tipoBalancete, SomaSubtraiSaldoContaContabil somaSubtraiSaldoContaContabil) {
        SaldoContaContabil ultimoSaldo = ultimoSaldoPorData(dataMovimento, conta, uni, tipoBalancete);

        if (ultimoSaldo == null || ultimoSaldo.getId() == null) {
            SaldoContaContabil scc = new SaldoContaContabil();
            scc.setContaContabil(conta);
            scc.setDataSaldo(dataMovimento);
            scc.setUnidadeOrganizacional(uni);
            scc.setTipoBalancete(tipoBalancete);
            alterarValores(tlc, valor, somaSubtraiSaldoContaContabil, scc);
            em.persist(scc);
        } else {
            if (DataUtil.dataSemHorario(ultimoSaldo.getDataSaldo()).compareTo(DataUtil.dataSemHorario(dataMovimento)) == 0) {
                alterarValores(tlc, valor, somaSubtraiSaldoContaContabil, ultimoSaldo);
                em.merge(ultimoSaldo);
            } else {
                SaldoContaContabil novoSaldo = new SaldoContaContabil();
                novoSaldo.setContaContabil(conta);
                novoSaldo.setUnidadeOrganizacional(uni);
                novoSaldo.setTipoBalancete(tipoBalancete);
                novoSaldo.setTotalCredito(ultimoSaldo.getTotalCredito());
                novoSaldo.setTotalDebito(ultimoSaldo.getTotalDebito());
                novoSaldo.setDataSaldo(dataMovimento);
                alterarValores(tlc, valor, somaSubtraiSaldoContaContabil, novoSaldo);
                em.persist(novoSaldo);
            }
        }
    }

    private void alterarValores(TipoLancamentoContabil tlc, BigDecimal valor, SomaSubtraiSaldoContaContabil somaSubtraiSaldoContaContabil, SaldoContaContabil ultimoSaldo) {
        if (TipoLancamentoContabil.CREDITO.equals(tlc)) {
            if (SomaSubtraiSaldoContaContabil.SOMA.equals(somaSubtraiSaldoContaContabil)) {
                ultimoSaldo.setTotalCredito(ultimoSaldo.getTotalCredito().add(valor));
            } else if (SomaSubtraiSaldoContaContabil.SUBTRAI.equals(somaSubtraiSaldoContaContabil)) {
                ultimoSaldo.setTotalCredito(ultimoSaldo.getTotalCredito().subtract(valor));
            }

        } else {
            if (SomaSubtraiSaldoContaContabil.SOMA.equals(somaSubtraiSaldoContaContabil)) {
                ultimoSaldo.setTotalDebito(ultimoSaldo.getTotalDebito().add(valor));
            } else if (SomaSubtraiSaldoContaContabil.SUBTRAI.equals(somaSubtraiSaldoContaContabil)) {
                ultimoSaldo.setTotalDebito(ultimoSaldo.getTotalDebito().subtract(valor));
            }
        }
    }

    public void geraSaldosFuturos(TipoLancamentoContabil tlc, Date data, BigDecimal valor, Conta conta, UnidadeOrganizacional uni, TipoBalancete tipoBalancete, SomaSubtraiSaldoContaContabil somaSubtraiSaldoContaContabil) {
        List<SaldoContaContabil> saldosPosteriores = saldosPosterioresAData(data, conta, uni, tipoBalancete);
        for (SaldoContaContabil s : saldosPosteriores) {
            alterarValores(tlc, valor, somaSubtraiSaldoContaContabil, s);
            em.merge(s);
        }
    }

    public List<SaldoContaContabil> saldosPosterioresAData(Date dataSaldo, Conta conta, UnidadeOrganizacional unidade, TipoBalancete tipoBalancete) {
        String sql = "Select s.* from SaldoContaContabil s "
            + "where trunc(s.dataSaldo) > to_date(:data,'dd/MM/yyyy') "
            + "and s.contaContabil_id  = :conta "
            + "and s.unidadeOrganizacional_id = :unidade "
            + "and s.tipoBalancete = :tipoBalancete "
            + "order by s.dataSaldo desc";
        Query q = em.createNativeQuery(sql, SaldoContaContabil.class);
        q.setParameter("data", DataUtil.getDataFormatada(dataSaldo));
        q.setParameter("conta", conta.getId());
        q.setParameter("unidade", unidade.getId());
        q.setParameter("tipoBalancete", tipoBalancete.name());
        try {

            List<SaldoContaContabil> saldos = q.getResultList();

            if (saldos.isEmpty()) {
                return new ArrayList<SaldoContaContabil>();
            } else {
                return saldos;
            }
        } catch (Exception e) {
            return new ArrayList<SaldoContaContabil>();
        }
    }

    public SaldoContaContabil ultimoSaldoPorData(Date dataSaldo, Conta conta, UnidadeOrganizacional unidade, TipoBalancete tipoBalancete) {
        String sql = "select s.* from SaldoContaContabil s "
            + " where trunc(s.dataSaldo) <= to_date(:data,'dd/MM/yyyy') "
            + " and s.contaContabil_id  = :conta "
            + " and s.unidadeOrganizacional_id = :unidade "
            + " and s.tipoBalancete = :tipoBalancete "
            + " order by s.dataSaldo desc";
        Query q = em.createNativeQuery(sql, SaldoContaContabil.class);
        q.setParameter("data", DataUtil.getDataFormatada(dataSaldo));
        q.setParameter("conta", conta.getId());
        q.setParameter("unidade", unidade.getId());
        q.setParameter("tipoBalancete", tipoBalancete.name());
        q.setMaxResults(1);
        List<SaldoContaContabil> resultado = q.getResultList();
        if (!resultado.isEmpty()) return resultado.get(0);
        SaldoContaContabil ultimoSaldo = new SaldoContaContabil();
        ultimoSaldo.setContaContabil(conta);
        ultimoSaldo.setUnidadeOrganizacional(unidade);
        ultimoSaldo.setTipoBalancete(tipoBalancete);
        if (ultimoSaldo.getDataSaldo() == null) {
            ultimoSaldo.setDataSaldo(dataSaldo);
        }
        return ultimoSaldo;
    }

    public BigDecimal buscarSaldoAtual(Date dataSaldo, Exercicio exercicio, Conta conta, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = " select COALESCE(sum(saldoatual.TOTALCREDITO), 0) - COALESCE(sum(saldoatual.TOTALDEBITO), 0) AS SALDOfinal from ( " +
            " SELECT COALESCE(SUM(scc.totalcredito), 0) as totalcredito, COALESCE(SUM(scc.totaldebito), 0) AS totaldebito " +
            " FROM SALDOCONTACONTABIL SCC   " +
            " INNER JOIN CONTA C ON SCC.CONTACONTABIL_ID = C.ID " +
            " where c.EXERCICIO_ID = :exercicio  " +
            "   and c.id = :conta  " +
            "   and scc.UNIDADEORGANIZACIONAL_ID = :unidade " +
            "   and trunc(scc.datasaldo) = (SELECT trunc(MAX(sld.DATASALDO)) AS maxdata " +
            "     FROM SALDOCONTACONTABIL sld " +
            "    WHERE trunc(sld.DATASALDO) <= TO_DATE(:DATAFINAL, 'DD/MM/YYYY') " +
            "      and sld.unidadeorganizacional_id = scc.unidadeorganizacional_id " +
            "      and sld.tipoBalancete = scc.tipoBalancete " +
            "      and sld.contacontabil_id = scc.contacontabil_id) " +
            " ) saldoatual ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("DATAFINAL", DataUtil.getDataFormatada(dataSaldo));
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("conta", conta.getId());
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setMaxResults(1);
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return BigDecimal.ZERO;
    }

    public BigDecimal buscarSaldoAtualContaAnalitica(Date dataInicial, Date dataFinal, Conta conta, UnidadeOrganizacional unidadeOrc) {
        String sql = " select coalesce(sum(s.totalcredito), 0) - coalesce(sum(s.totaldebito), 0) " +
            "         from SaldoContaContabil s " +
            "         inner join conta c on c.id = s.contacontabil_id " +
            "         inner join (select trunc(max(datasaldo)) as datasaldo, " +
            "                            tipobalancete, " +
            "                            unidadeorganizacional_id, " +
            "                            contacontabil_id " +
            "                     from saldocontacontabil " +
            "                     where trunc(datasaldo) >= to_date(:dataInicial, 'dd/MM/yyyy') " +
            "                       and trunc(datasaldo) <= to_date(:dataFinal, 'dd/MM/YYYY') " +
            "                     group by tipobalancete, unidadeorganizacional_id, contacontabil_id) ms " +
            "                    on trunc(s.datasaldo) = trunc(ms.datasaldo) " +
            "                        and s.contacontabil_id = ms.contacontabil_id " +
            "                        and s.unidadeorganizacional_id = ms.unidadeorganizacional_id " +
            "                        and s.tipobalancete = ms.tipobalancete " +
            " where trunc(s.dataSaldo) <= to_date(:dataFinal, 'dd/MM/yyyy') " +
            "  and c.codigo like :codigoConta " +
            "  and s.unidadeOrganizacional_id = :idUnidade ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("codigoConta", "%" + conta.getCodigoSemZerosAoFinal().trim() + "%");
        q.setParameter("idUnidade", unidadeOrc.getId());
        List<BigDecimal> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return BigDecimal.ZERO;
    }

    public ConfiguracaoContabil getConfiguracaoContabil() {
        if (configuracaoContabil == null) {
            configuracaoContabil = configuracaoContabilFacade.configuracaoContabilVigente();
        }
        return configuracaoContabil;
    }

    public List<LancamentoContabil> recuperLancamentosContabeis(AssistenteAnaliseContabil analiseContabil) {

        String sql = " select l.* from lancamentocontabil l " +
            " inner join lcp on l.lcp_id = lcp.id" +
            " inner join itemparametroevento item on l.itemparametroevento_id = item.id " +
            " inner join parametroevento p on item.parametroevento_id = p.id " +
            " inner join eventocontabil evento on p.eventocontabil_id = evento.id " +
            " where trunc(l.dataLancamento) between to_date(:dataInicial,'dd/MM/yyyy') and to_date(:dataFinal,'dd/MM/yyyy') ";
        if (analiseContabil.getHierarquiaOrganizacional() != null) {
            sql += " and l.unidadeorganizacional_id = :unidade ";
        }
        Query consulta = em.createNativeQuery(sql, LancamentoContabil.class);
        consulta.setParameter("dataInicial", DataUtil.getDataFormatada(analiseContabil.getDataInicial()));
        consulta.setParameter("dataFinal", DataUtil.getDataFormatada(analiseContabil.getDataFinal()));
        if (analiseContabil.getHierarquiaOrganizacional() != null) {
            consulta.setParameter("unidade", analiseContabil.getHierarquiaOrganizacional().getSubordinada());
        }
        try {
            return consulta.getResultList();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public BigDecimal buscarSaldoSiconfiAtual(Date dataSaldo, Exercicio exercicio, Conta conta, UnidadeOrganizacional unidadeOrganizacional) {
        String sql = " select COALESCE(sum(saldoatual.TOTALCREDITO), 0) - COALESCE(sum(saldoatual.TOTALDEBITO), 0) AS SALDOfinal from ( " +
            " SELECT COALESCE(SUM(scc.totalcredito), 0) as totalcredito, COALESCE(SUM(scc.totaldebito), 0) AS totaldebito " +
            " FROM SALDOCONTACONTABIL SCC   " +
            " inner join contaauxiliar ca on ca.id = scc.CONTACONTABIL_ID " +
            " INNER JOIN CONTA C ON ca.CONTACONTABIL_ID = C.ID " +
            " where c.EXERCICIO_ID = :exercicio  " +
            "   and c.id = :conta  " +
            "   and scc.UNIDADEORGANIZACIONAL_ID = :unidade " +
            "   and trunc(scc.datasaldo) = (SELECT trunc(MAX(sld.DATASALDO)) AS maxdata " +
            "     FROM SALDOCONTACONTABIL sld " +
            "    WHERE trunc(sld.DATASALDO) <= TO_DATE(:DATAFINAL, 'DD/MM/YYYY') " +
            "      and sld.unidadeorganizacional_id = scc.unidadeorganizacional_id " +
            "      and sld.tipoBalancete = scc.tipoBalancete " +
            "      and sld.contacontabil_id = scc.contacontabil_id) " +
            " ) saldoatual ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("DATAFINAL", DataUtil.getDataFormatada(dataSaldo));
        q.setParameter("exercicio", exercicio.getId());
        q.setParameter("conta", conta.getId());
        q.setParameter("unidade", unidadeOrganizacional.getId());
        q.setMaxResults(1);
        List<BigDecimal> resultado = q.getResultList();

        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return BigDecimal.ZERO;
    }

    public void limparConfiguracaoContabil() {
        configuracaoContabil = null;
    }

    public List<ValidadorMatrizSaldoContabilBlc> buscarSaldoAtualBalanceteContabil(Date dataInicial, Date dataFinal, Exercicio exercicio) {
        String sql = " select codigo, sum(saldoanteriorcredito) - sum(saldoanteriordebito) as saldoanterior," +
            "       sum(saldoanteriorcredito) -sum(saldofinalcredito) as movimentocredito," +
            "       sum(saldoanteriordebito) -sum(saldofinaldebito) as movimentodebito," +
            "       sum(saldofinalcredito) - sum(saldofinaldebito) as saldofinal" +
            " from (select codigo," +
            "             0                                         as saldoanteriorcredito," +
            "             0                                         as saldoanteriordebito," +
            "             coalesce(sum(saldoatual.totalcredito), 0) as saldofinalcredito," +
            "             coalesce(sum(saldoatual.totaldebito), 0)  as saldofinaldebito" +
            "      from (select substr(c.codigo, 0, 1)              as codigo," +
            "                   coalesce(sum(scc.totalcredito), 0)  as totalcredito," +
            "                   coalesce(sum(scc.totaldebito), 0)   as totaldebito" +
            "            from saldocontacontabil scc" +
            "                     inner join conta c on scc.contacontabil_id = c.id" +
            "                     inner join contacontabil cc on c.id = cc.id" +
            "            where c.exercicio_id = :exercicio" +
            "              and trunc(scc.datasaldo) = (select trunc(max(sld.datasaldo)) as maxdata" +
            "                                          from saldocontacontabil sld" +
            "                                          where trunc(sld.datasaldo) <= to_date(:datafinal, 'dd/mm/yyyy')" +
            "                                            and sld.unidadeorganizacional_id = scc.unidadeorganizacional_id" +
            "                                            and sld.tipobalancete = scc.tipobalancete" +
            "                                            and sld.contacontabil_id = scc.contacontabil_id)" +
            "            group by substr(c.codigo, 0, 1)) saldoatual" +
            "      group by codigo" +
            "      union all" +
            "      select codigo," +
            "             coalesce(sum(saldoanterior.totalcredito), 0) as saldoanteriorcredito," +
            "             coalesce(sum(saldoanterior.totaldebito), 0) as saldoanteriordebito," +
            "             0                                           as saldofinalcredito," +
            "             0                                           as saldofinaldebito" +
            "      from (select substr(c.codigo, 0, 1)             as codigo," +
            "                   coalesce(sum(scc.totalcredito), 0) as totalcredito," +
            "                   coalesce(sum(scc.totaldebito), 0)  as totaldebito" +
            "            from saldocontacontabil scc" +
            "                     inner join conta c on scc.contacontabil_id = c.id" +
            "                     inner join contacontabil cc on c.id = cc.id" +
            "            where c.exercicio_id = :exercicio" +
            "              and trunc(scc.datasaldo) = (select trunc(max(sld.datasaldo)) as maxdata" +
            "                                          from saldocontacontabil sld" +
            "                                          where trunc(sld.datasaldo) < to_date(:datainicial, 'dd/mm/yyyy')" +
            "                                            and sld.unidadeorganizacional_id = scc.unidadeorganizacional_id" +
            "                                            and sld.tipobalancete = scc.tipobalancete" +
            "                                            and sld.contacontabil_id = scc.contacontabil_id)" +
            "            group by substr(c.codigo, 0, 1)) saldoanterior" +
            "      group by codigo)" +
            "group by codigo " +
            "order by codigo";
        Query q = em.createNativeQuery(sql);
        q.setParameter("datafinal", DataUtil.getDataFormatada(dataFinal));
        q.setParameter("datainicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("exercicio", exercicio.getId());
        List resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            List<ValidadorMatrizSaldoContabilBlc> matriz = Lists.newArrayList();
            for (Object o : resultado) {
                Object[] obj = (Object[]) o;
                ValidadorMatrizSaldoContabilBlc blc = new ValidadorMatrizSaldoContabilBlc();
                blc.setCodigo((String) obj[0]);
                blc.setSaldoAnterior(new BigDecimal(((Number) obj[1]).doubleValue()));
                blc.setCredito(new BigDecimal(((Number) obj[2]).doubleValue()));
                blc.setDebito(new BigDecimal(((Number) obj[3]).doubleValue()));
                blc.setAtual(new BigDecimal(((Number) obj[4]).doubleValue()));
                matriz.add(blc);
            }
            return matriz;
        }
        return Lists.newArrayList();
    }
}

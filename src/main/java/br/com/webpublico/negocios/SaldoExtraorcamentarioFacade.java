/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.AssistenteReprocessamento;
import br.com.webpublico.entidadesauxiliares.contabil.ReprocessamentoSaldoExtraOrcamentario;
import br.com.webpublico.entidadesauxiliares.contabil.apiservicecontabil.SaldoExtraorcamentarioDTO;
import br.com.webpublico.enums.TipoOperacao;
import br.com.webpublico.negocios.contabil.ApiServiceContabil;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.joda.time.LocalDate;

import javax.ejb.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author venon
 */
@Singleton
@ConcurrencyManagement(ConcurrencyManagementType.CONTAINER)
@AccessTimeout(value = 50000)
public class SaldoExtraorcamentarioFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ConfiguracaoContabilFacade configuracaoContabilFacade;

    public void excluirSaldosPeriodo(Date dataInicial, Date dataFinal) {
        String sql = " delete FROM saldoextraorcamentario se "
            + " WHERE trunc(se.datasaldo) between to_date(:dataInicial, 'dd/MM/yyyy') and to_date(:dataFinal, 'dd/MM/yyyy') ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        q.executeUpdate();
    }

    @Lock(LockType.WRITE)
    public void gerarSaldoExtraorcamentario(Date data, TipoOperacao op, BigDecimal valor, Conta contaExtra, ContaDeDestinacao contaDeDestinacao, UnidadeOrganizacional uniOrg,  String idOrigem, String classeOrigem) throws ExcecaoNegocioGenerica {
        gerarSaldoExtraorcamentario(data, op, valor, contaExtra, contaDeDestinacao, uniOrg, true, idOrigem, classeOrigem);
    }

    @Lock(LockType.WRITE)
    public void gerarSaldoExtraorcamentario(Date data, TipoOperacao op, BigDecimal valor, Conta contaExtra, ContaDeDestinacao contaDeDestinacao, UnidadeOrganizacional uniOrg, Boolean validarSaldo, String idOrigem, String classeOrigem) throws ExcecaoNegocioGenerica {
        if (configuracaoContabilFacade.isGerarSaldoUtilizandoMicroService("SALDOEXTRAORCMICROSERVICE")) {
            SaldoExtraorcamentarioDTO dto = new SaldoExtraorcamentarioDTO();
            dto.setIdContaExtraorcamentaria(contaExtra.getId());
            dto.setIdUnidadeOrganizacional(uniOrg.getId());
            dto.setIdContaDeDestinacao(contaDeDestinacao.getId());
            dto.setIdFonteDeRecursos(contaDeDestinacao.getFonteDeRecursos().getId());
            dto.setValor(valor);
            dto.setTipoOperacao(op);
            dto.setDataSaldo(DataUtil.dateToLocalDate(data));
            dto.setClasseOrigem(classeOrigem);
            dto.setIdOrigem(idOrigem);
            dto.setRealizarValidacoes(validarSaldo);
            ApiServiceContabil.getService().gerarSaldoExtraorcamentario(dto);
        } else {
            SaldoExtraorcamentario ultimoSaldo = recuperaUltimoSaldoPorData(data, contaExtra, contaDeDestinacao, uniOrg);
            List<SaldoExtraorcamentario> saldosPosteriores = buscarSaldosPosterioresAData(data, contaExtra, contaDeDestinacao, uniOrg);
            if (ultimoSaldo == null || ultimoSaldo.getId() == null) {
                ultimoSaldo = atribuirValorSaldoContaExtra(data, contaDeDestinacao, uniOrg, contaExtra);
                ultimoSaldo = alterarValorSaldo(ultimoSaldo, op, valor, uniOrg, contaExtra, contaDeDestinacao, data, validarSaldo);
                em.persist(ultimoSaldo);
            } else {
                if (DataUtil.getDataFormatada(ultimoSaldo.getDataSaldo()).equals(DataUtil.getDataFormatada(data))) {
                    ultimoSaldo = alterarValorSaldo(ultimoSaldo, op, valor, uniOrg, contaExtra, contaDeDestinacao, data, validarSaldo);
                    em.merge(ultimoSaldo);
                } else {
                    SaldoExtraorcamentario novoSaldo;
                    novoSaldo = atribuirValorSaldoExtra(data, contaDeDestinacao, uniOrg, contaExtra);
                    novoSaldo = copiaValoresDoSaldoRecuperado(ultimoSaldo, novoSaldo);
                    novoSaldo = alterarValorSaldo(novoSaldo, op, valor, uniOrg, contaExtra, contaDeDestinacao, data, validarSaldo);
                    em.persist(novoSaldo);
                }
            }
            if (!saldosPosteriores.isEmpty()) {
                for (SaldoExtraorcamentario saldoParaAtualizar : saldosPosteriores) {
                    saldoParaAtualizar = alterarValorSaldo(saldoParaAtualizar, op, valor, uniOrg, contaExtra, contaDeDestinacao, saldoParaAtualizar.getDataSaldo(), validarSaldo);
                    em.merge(saldoParaAtualizar);
                }
            }
        }
    }

    private SaldoExtraorcamentario copiaValoresDoSaldoRecuperado(SaldoExtraorcamentario saldo, SaldoExtraorcamentario novoSaldo) {
        novoSaldo.setValor(saldo.getValor());
        return novoSaldo;
    }

    private SaldoExtraorcamentario atribuirValorSaldoExtra(Date data, ContaDeDestinacao contaDeDestinacao, UnidadeOrganizacional unidadeOrganizacional, Conta contaExtra) {
        SaldoExtraorcamentario saldo = new SaldoExtraorcamentario();
        saldo.setDataSaldo(data);
        saldo.setUnidadeOrganizacional(unidadeOrganizacional);
        saldo.setContaExtraorcamentaria(contaExtra);
        saldo.setContaDeDestinacao(contaDeDestinacao);
        saldo.setFonteDeRecursos(contaDeDestinacao.getFonteDeRecursos());
        saldo.setValor(BigDecimal.ZERO);
        return saldo;
    }

    private SaldoExtraorcamentario alterarValorSaldo(SaldoExtraorcamentario saldo, TipoOperacao operacao, BigDecimal valor, UnidadeOrganizacional uniOrg, Conta contaExtra, ContaDeDestinacao contaDeDestinacao, Date data, Boolean validarSaldo) {

        if (TipoOperacao.CREDITO.equals(operacao)) {
            saldo.setValor(saldo.getValor().add(valor));
        } else {
            if (validarSaldo) {
                validarSaldoContaExtra(uniOrg, contaExtra, contaDeDestinacao, data, valor);
            }
            saldo.setValor(saldo.getValor().subtract(valor));
        }
        return saldo;
    }

    private void validarSaldoContaExtra(UnidadeOrganizacional unidadeOrganizacional, Conta contaExtra, ContaDeDestinacao contaDeDestinacao, Date dataSaldo, BigDecimal valor) {
        SaldoExtraorcamentario saldoExtraorcamentario = recuperaUltimoSaldoPorData(dataSaldo, contaExtra, contaDeDestinacao, unidadeOrganizacional);
        BigDecimal saldoDoDia = saldoExtraorcamentario.getValor().subtract(valor);
        if (saldoDoDia.compareTo(BigDecimal.ZERO) < 0) {
            throw new ExcecaoNegocioGenerica(" O Saldo da Conta Extraorçamentária: " + contaExtra + ", ficará negativo " + "<b>" + Util.formataValor(saldoDoDia) + "</b>" + " na data " + DataUtil.getDataFormatada(dataSaldo) + ".");
        }
    }

    private SaldoExtraorcamentario atribuirValorSaldoContaExtra(Date data, ContaDeDestinacao contaDeDestinacao, UnidadeOrganizacional unidadeOrganizacional, Conta contaExtra) {
        SaldoExtraorcamentario saldo = new SaldoExtraorcamentario();
        saldo.setDataSaldo(data);
        saldo.setUnidadeOrganizacional(unidadeOrganizacional);
        saldo.setContaExtraorcamentaria(contaExtra);
        saldo.setContaDeDestinacao(contaDeDestinacao);
        saldo.setFonteDeRecursos(contaDeDestinacao.getFonteDeRecursos());
        saldo.setValor(BigDecimal.ZERO);
        return saldo;
    }

    public SaldoExtraorcamentario recuperaUltimoSaldoPorData(Date dataSaldo, Conta conta, ContaDeDestinacao contaDeDestinacao, UnidadeOrganizacional uo) {
        String hql = " select s.* from saldoextraorcamentario s " +
            " where trunc(s.datasaldo) <= to_date(:data, 'dd/MM/yyyy') " +
            " and s.contaextraorcamentaria_id  = :conta " +
            " and s.contaDeDestinacao_id = :contaDeDestinacao " +
            " and s.unidadeorganizacional_id = :uni " +
            " order by datasaldo desc ";

        Query q = em.createNativeQuery(hql, SaldoExtraorcamentario.class);
        q.setParameter("data", DataUtil.getDataFormatada(dataSaldo));
        q.setParameter("conta", conta.getId());
        q.setParameter("contaDeDestinacao", contaDeDestinacao.getId());
        q.setParameter("uni", uo.getId());
        q.setMaxResults(1);
        List<SaldoExtraorcamentario> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return new SaldoExtraorcamentario();
        } else {
            return resultado.get(0);
        }
    }

    private List<SaldoExtraorcamentario> buscarSaldosPosterioresAData(Date data, Conta conta, ContaDeDestinacao contaDeDestinacao, UnidadeOrganizacional uo) {

        String hql = " select s.* from saldoextraorcamentario s " +
            " where trunc(s.datasaldo) > to_date(:data, 'dd/MM/yyyy') " +
            " and s.contaextraorcamentaria_id = :conta " +
            " and s.contaDeDestinacao_id = :contaDeDestinacao " +
            " and s.unidadeorganizacional_id = :uni ";

        Query q = em.createNativeQuery(hql, SaldoExtraorcamentario.class);
        q.setParameter("data", DataUtil.getDataFormatada(data));
        q.setParameter("conta", conta.getId());
        q.setParameter("contaDeDestinacao", contaDeDestinacao.getId());
        q.setParameter("uni", uo.getId());
        List<SaldoExtraorcamentario> resultado = q.getResultList();
        if (resultado.isEmpty()) {
            return Lists.newArrayList();
        } else {
            return resultado;
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ReceitaExtra> getReceitaExtra(AssistenteReprocessamento assistente) {
        try {
            assistente.getAssistenteBarraProgresso().setMensagem("BUSCANDO RECEITAS EXTRAS");
            if (assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryReceita().isEmpty()) {
                return Lists.newArrayList();
            }
            String sql = assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryReceita();
            Query consulta = em.createNativeQuery(sql, ReceitaExtra.class);
            consulta.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
            consulta.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));

            return consulta.getResultList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<AjusteDeposito> getAjusteDepositoNormal(AssistenteReprocessamento assistente) {
        try {
            assistente.getAssistenteBarraProgresso().setMensagem("BUSCANDO AJUSTES EM DEPÓSITOS");
            if (assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryAjuste().isEmpty()) {
                return Lists.newArrayList();
            }
            String sql = assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryAjuste();
            Query consulta = em.createNativeQuery(sql, AjusteDeposito.class);
            consulta.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
            consulta.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));

            return consulta.getResultList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<AjusteDepositoEstorno> getAjusteDepositoEstorno(AssistenteReprocessamento assistente) {
        try {
            assistente.getAssistenteBarraProgresso().setMensagem("BUSCANDO ESTORNOS DOS AJUSTES EM DEPÓSITOS");
            if (assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryAjusteEstorno().isEmpty()) {
                return Lists.newArrayList();
            }
            String sql = assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryAjusteEstorno();
            Query consulta = em.createNativeQuery(sql, AjusteDepositoEstorno.class);
            consulta.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
            consulta.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));

            return consulta.getResultList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<ReceitaExtraEstorno> getReceitaExtraEstorno(AssistenteReprocessamento assistente) {
        try {
            assistente.getAssistenteBarraProgresso().setMensagem("BUSCANDO ESTORNOS DE RECEITAS EXTRAS");
            if (assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryReceitaEstorno().isEmpty()) {
                return Lists.newArrayList();
            }
            String sql = assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryReceitaEstorno();
            Query consulta = em.createNativeQuery(sql, ReceitaExtraEstorno.class);
            consulta.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
            consulta.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
            return consulta.getResultList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PagamentoExtra> getPagamentoExtra(AssistenteReprocessamento assistente) {
        try {
            assistente.getAssistenteBarraProgresso().setMensagem("BUSCANDO DESPESAS EXTRAS");
            if (assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryPagamento().isEmpty()) {
                return Lists.newArrayList();
            }
            String sql = assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryPagamento();
            Query consulta = em.createNativeQuery(sql, PagamentoExtra.class);
            consulta.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
            consulta.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
            return consulta.getResultList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }

    @TransactionAttribute(TransactionAttributeType.NOT_SUPPORTED)
    public List<PagamentoExtraEstorno> getPagamentoExtraEstorno(AssistenteReprocessamento assistente) {
        try {
            assistente.getAssistenteBarraProgresso().setMensagem("BUSCANDO ESTORNOS DE DESPESAS EXTRAS");
            if (assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryPagamentoEstorno().isEmpty()) {
                return Lists.newArrayList();
            }
            String sql = assistente.getReprocessamentoSaldoExtraOrcamentario().getQueryPagamentoEstorno();
            Query consulta = em.createNativeQuery(sql, PagamentoExtraEstorno.class);
            consulta.setParameter("dataInicial", DataUtil.getDataFormatada(assistente.getDataInicial()));
            consulta.setParameter("dataFinal", DataUtil.getDataFormatada(assistente.getDataFinal()));
            return consulta.getResultList();
        } catch (Exception e) {
            return Lists.newArrayList();
        }
    }
}


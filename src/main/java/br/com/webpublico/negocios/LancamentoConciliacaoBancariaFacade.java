/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.FiltroPendenciaConciliacao;
import br.com.webpublico.entidadesauxiliares.contabil.IdentificadorMovimentoConciliacaoBancaria;
import br.com.webpublico.entidadesauxiliares.contabil.MovimentoConciliacaoBancaria;
import br.com.webpublico.enums.SituacaoCadastralContabil;
import br.com.webpublico.enums.TipoOperacaoConcilicaoBancaria;
import br.com.webpublico.exception.ValidacaoException;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Renato
 */
@Stateless
public class LancamentoConciliacaoBancariaFacade extends AbstractFacade<LancamentoConciliacaoBancaria> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SubContaFacade subContaFacade;
    @EJB
    private FaseFacade faseFacade;
    @EJB
    private OperacaoConciliacaoFacade operacaoConciliacaoFacade;
    @EJB
    private TipoConciliacaoFacade tipoConciliacaoFacade;
    @EJB
    private SaldoConstContaBancariaFacade saldoConstContaBancariaFacade;
    @EJB
    private ContaBancariaEntidadeFacade contaBancariaEntidadeFacade;
    @EJB
    private IdentificadorFacade identificadorFacade;
    @EJB
    private PagamentoFacade pagamentoFacade;
    @EJB
    private PagamentoEstornoFacade pagamentoEstornoFacade;
    @EJB
    private PagamentoExtraFacade pagamentoExtraFacade;
    @EJB
    private PagamentoExtraEstornoFacade pagamentoExtraEstornoFacade;
    @EJB
    private ReceitaExtraFacade receitaExtraFacade;
    @EJB
    private ReceitaExtraEstornoFacade receitaExtraEstornoFacade;
    @EJB
    private LancamentoReceitaOrcFacade lancamentoReceitaOrcFacade;
    @EJB
    private ReceitaORCEstornoFacade receitaORCEstornoFacade;
    @EJB
    private LiberacaoCotaFinanceiraFacade liberacaoCotaFinanceiraFacade;
    @EJB
    private EstornoLibCotaFinanceiraFacade estornoLibCotaFinanceiraFacade;
    @EJB
    private TransferenciaContaFinanceiraFacade transferenciaContaFinanceiraFacade;
    @EJB
    private EstornoTransferenciaFacade estornoTransferenciaFacade;
    @EJB
    private AjusteAtivoDisponivelFacade ajusteAtivoDisponivelFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;

    public LancamentoConciliacaoBancariaFacade() {
        super(LancamentoConciliacaoBancaria.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SubContaFacade getSubContaFacade() {
        return subContaFacade;
    }

    public OperacaoConciliacaoFacade getOperacaoConciliacaoFacade() {
        return operacaoConciliacaoFacade;
    }

    public TipoConciliacaoFacade getTipoConciliacaoFacade() {
        return tipoConciliacaoFacade;
    }

    public Long retornaUltimoCodigoLong() {
        Long num;
        String sql = " SELECT max(coalesce(obj.numero,0)) FROM LANCCONCILIACAOBANCARIA obj ";
        Query query = getEntityManager().createNativeQuery(sql);
        query.setMaxResults(1);
        if (!query.getResultList().isEmpty()) {
            BigDecimal b = (BigDecimal) query.getSingleResult();

            if (b != null) {
                b = b.add(BigDecimal.ONE);
            } else {
                b = BigDecimal.ONE;
            }
            num = b.setScale(0, BigDecimal.ROUND_UP).longValueExact();
        } else {
            return 1l;
        }
        return num;
    }

    public List<LancamentoConciliacaoBancaria> buscarLancamentosPorContaBancariaConciliacao(FiltroPendenciaConciliacao filtro) {
        String sql = " " +
            "   select lanc.* " +
            "       from LANCCONCILIACAOBANCARIA lanc " +
            "       inner join subconta sub on lanc.subconta_id = sub.id " +
            "      where trunc(lanc.data) BETWEEN to_date(:dataInicial,'dd/mm/yyyy') and to_date(:dataFinal,'dd/mm/yyyy')" +
            "        and sub.contabancariaentidade_id = :contaBanc ";
        if (filtro.getVisualizarPendenciasBaixadas()) {
            sql += " and trunc(LANC.DATACONCILIACAO) >= to_date(:dataFinal,'dd/mm/yyyy')";
        }
        if (filtro.getContaFinanceira() != null) {
            sql += " and sub.id = :contaFinanceira ";
        }
        if (filtro.getOperacaoConciliacao() != null) {
            sql += " and lanc.TIPOOPERACAOCONCILIACAO = :operacao ";
        }
        if (filtro.getTipoConciliacao() != null) {
            sql += " and lanc.TIPOCONCILIACAO_ID = :tipo ";
        }
        if (filtro.getNumero() != null) {
            sql += " and lanc.NUMERO like :numero ";
        }
        if (filtro.getDataConciliacao() != null) {
            sql += " and trunc(lanc.DATACONCILIACAO) = to_date(:dataConciliacao, 'dd/MM/yyyy') ";
        }
        if (filtro.getValor() != null && filtro.getValor().compareTo(BigDecimal.ZERO) > 0) {
            sql += " and lanc.VALOR = :valor ";
        }
        sql += "order by lanc.numero desc ";

        Query consulta = em.createNativeQuery(sql, LancamentoConciliacaoBancaria.class);
        adicionarParametros(filtro, consulta);
        try {
            return consulta.getResultList();
        } catch (NoResultException e) {
            return new ArrayList<>();
        }
    }

    public List<MovimentoConciliacaoBancaria> buscarMovimentosPorContaBancariaConciliacaoSemIdentificador(FiltroPendenciaConciliacao filtro) {
        String sql = " select datamovimento, " +
            "       dataconciliacao, " +
            "       historico, " +
            "       credito, " +
            "       debito, " +
            "       TIPOOPERACAOCONCILIACAO, " +
            "       movimentoId, " +
            "       tipoMovimento, " +
            "       subConta, " +
            "       numero, " +
            "       situacao " +
            "  from ( " +
            "select vwmov.data_movimento as datamovimento, " +
            "       vwmov.data_conciliacao as dataconciliacao, " +
            "       vwmov.historico, " +
            "       vwmov.credito as credito,  " +
            "       vwmov.debito as debito, " +
            "       eve.TIPOOPERACAOCONCILIACAO, " +
            "       movimento_id as movimentoId, " +
            "       vwmov.tipo_movimento as tipoMovimento, " +
            "       sub.codigo || ' - ' || sub.descricao as subConta, " +
            "       vwmov.NUMERO as numero, " +
            "       vwmov.situacao as situacao " +
            "  from VWMOVIMENTOCONTABIL vwmov " +
            " inner join eventocontabil eve on vwmov.eventocontabil_id = eve.id " +
            " inner join subconta sub on vwmov.subconta_id = sub.id " +
            " where sub.contabancariaentidade_id = :contaBanc " +
            "   and vwmov.TIPO_MOVIMENTO not in ('RECEITA_EXTRA', 'ESTORNO_RECEITA_EXTRA') " +
            "   and vwmov.identificador_id is null ";
        if (filtro.getDataInicial() == null && filtro.getDataFinal() == null && filtro.getDataConciliacaoPorIdentificador() != null) {
            sql += " and trunc(vwmov.data_movimento) <= to_date(:dataConcPorIdentificador, 'dd/MM/yyyy') ";
        }
        sql += adicionarFiltrosPorData("vwmov.data_movimento", "vwmov.data_conciliacao", filtro);
        if (filtro.getContaFinanceira() != null) {
            sql += " and vwmov.subconta_id = :contaFinanceira ";
        }
        if (filtro.getOperacaoConciliacao() != null) {
            sql += " and eve.TIPOOPERACAOCONCILIACAO = :operacao ";
        }
        if (filtro.getNumero() != null) {
            sql += " and vwmov.NUMERO like :numero ";
        }
        if (filtro.getNumeros() != null && !filtro.getNumeros().isEmpty()) {
            sql += " and vwmov.numero in :numeros ";
        }
        if (filtro.getValor() != null && filtro.getValor().compareTo(BigDecimal.ZERO) > 0) {
            sql += " and (case when vwmov.credito > 0 then vwmov.credito else vwmov.debito end) = :valor ";
        }
        if (filtro.getValores() != null && !filtro.getValores().isEmpty()) {
            sql += " and (case when vwmov.credito > 0 then vwmov.credito else vwmov.debito end) in :valores ";
        }
        sql += " union all " +
            "select vwmov.data_movimento as datamovimento, " +
            "       vwmov.data_conciliacao as dataconciliacao, " +
            "       vwmov.historico, " +
            "       vwmov.credito as credito,  " +
            "       vwmov.debito as debito, " +
            "       eve.TIPOOPERACAOCONCILIACAO, " +
            "       movimento_id as movimentoId, " +
            "       vwmov.tipo_movimento as tipoMovimento, " +
            "       sub.codigo || ' - ' || sub.descricao as subConta, " +
            "       vwmov.NUMERO as numero, " +
            "       vwmov.situacao as situacao " +
            "  from VWMOVIMENTOCONTABIL vwmov " +
            " inner join eventocontabil eve on vwmov.eventocontabil_id = eve.id " +
            " INNER join receitaextra recextra on vwmov.movimento_id = recextra.id " +
            " INNER JOIN CONTAEXTRAORCAMENTARIA CEXTRA ON RECEXTRA.CONTAEXTRAORCAMENTARIA_ID = CEXTRA.ID " +
            " inner join subconta sub on vwmov.subconta_id = sub.id " +
            " where sub.contabancariaentidade_id = :contaBanc " +
            "   and vwmov.TIPO_MOVIMENTO IN ('RECEITA_EXTRA') " +
            "   AND CEXTRA.TIPOCONTAEXTRAORCAMENTARIA <> 'DEPOSITOS_CONSIGNACOES' " +
            "   and vwmov.identificador_id is null ";
        if (filtro.getDataInicial() == null && filtro.getDataFinal() == null && filtro.getDataConciliacaoPorIdentificador() != null) {
            sql += " and trunc(vwmov.data_movimento) <= to_date(:dataConcPorIdentificador, 'dd/MM/yyyy') ";
        }
        sql += adicionarFiltrosPorData("vwmov.data_movimento", "vwmov.data_conciliacao", filtro);
        if (filtro.getContaFinanceira() != null) {
            sql += " and vwmov.subconta_id = :contaFinanceira ";
        }
        if (filtro.getOperacaoConciliacao() != null) {
            sql += " and eve.TIPOOPERACAOCONCILIACAO = :operacao ";
        }
        if (filtro.getNumero() != null) {
            sql += " and vwmov.NUMERO like :numero ";
        }
        if (filtro.getNumeros() != null && !filtro.getNumeros().isEmpty()) {
            sql += " and vwmov.numero in :numeros ";
        }
        if (filtro.getValor() != null && filtro.getValor().compareTo(BigDecimal.ZERO) > 0) {
            sql += " and (case when vwmov.credito > 0 then vwmov.credito else vwmov.debito end) = :valor ";
        }
        if (filtro.getValores() != null && !filtro.getValores().isEmpty()) {
            sql += " and (case when vwmov.credito > 0 then vwmov.credito else vwmov.debito end) in :valores ";
        }
        sql += " union all " +
            "select vwmov.data_movimento as datamovimento, " +
            "       vwmov.data_conciliacao as dataconciliacao, " +
            "       vwmov.historico, " +
            "       vwmov.credito as credito,  " +
            "       vwmov.debito as debito, " +
            "       eve.TIPOOPERACAOCONCILIACAO, " +
            "       movimento_id as movimentoId, " +
            "       vwmov.tipo_movimento as tipoMovimento, " +
            "       sub.codigo || ' - ' || sub.descricao as subConta, " +
            "       vwmov.NUMERO as numero, " +
            "       vwmov.situacao as situacao " +
            "  from VWMOVIMENTOCONTABIL vwmov " +
            " inner join eventocontabil eve on vwmov.eventocontabil_id = eve.id " +
            " inner join receitaextraestorno est on vwmov.movimento_id = est.id " +
            " INNER join receitaextra recextra on est.receitaextra_id = recextra.id " +
            " INNER JOIN CONTAEXTRAORCAMENTARIA CEXTRA ON RECEXTRA.CONTAEXTRAORCAMENTARIA_ID = CEXTRA.ID " +
            " inner join subconta sub on vwmov.subconta_id = sub.id " +
            " where sub.contabancariaentidade_id = :contaBanc " +
            "   and vwmov.TIPO_MOVIMENTO IN ('ESTORNO_RECEITA_EXTRA') " +
            "   AND CEXTRA.TIPOCONTAEXTRAORCAMENTARIA <> 'DEPOSITOS_CONSIGNACOES' " +
            "   and vwmov.identificador_id is null ";
        if (filtro.getDataInicial() == null && filtro.getDataFinal() == null && filtro.getDataConciliacaoPorIdentificador() != null) {
            sql += " and trunc(vwmov.data_movimento) <= to_date(:dataConcPorIdentificador, 'dd/MM/yyyy') ";
        }
        sql += adicionarFiltrosPorData("vwmov.data_movimento", "vwmov.data_conciliacao", filtro);
        if (filtro.getVisualizarPendenciasBaixadas()) {
            sql += " and trunc(vwmov.data_conciliacao) >= to_date(:dataFinal,'dd/mm/yyyy')";
        }
        if (filtro.getContaFinanceira() != null) {
            sql += " and vwmov.subconta_id = :contaFinanceira ";
        }
        if (filtro.getOperacaoConciliacao() != null) {
            sql += " and eve.TIPOOPERACAOCONCILIACAO = :operacao ";
        }
        if (filtro.getNumero() != null) {
            sql += " and vwmov.NUMERO like :numero ";
        }
        if (filtro.getNumeros() != null && !filtro.getNumeros().isEmpty()) {
            sql += " and vwmov.numero in :numeros ";
        }
        if (filtro.getValor() != null && filtro.getValor().compareTo(BigDecimal.ZERO) > 0) {
            sql += " and (case when vwmov.credito > 0 then vwmov.credito else vwmov.debito end) = :valor ";
        }
        if (filtro.getValores() != null && !filtro.getValores().isEmpty()) {
            sql += " and (case when vwmov.credito > 0 then vwmov.credito else vwmov.debito end) in :valores ";
        }
        sql += " union all " +
            "select lanc.data as datamovimento, " +
            "       lanc.dataconciliacao as dataconciliacao, " +
            "       lanc.historico, " +
            "       case LANC.TIPOOPERACAOCONCILIACAO " +
            "          when 'CREDITO_NO_BANCO_NAO_CONSIDERADO_NO_RAZAO' then lanc.valor " +
            "          when 'CREDITO_NO_RAZAO_NAO_CONSIDERADO_NO_BANCO' then lanc.valor " +
            "          else 0 " +
            "       end as credito,  " +
            "       case LANC.TIPOOPERACAOCONCILIACAO " +
            "          when 'DEBITO_NO_BANCO_NAO_CONSIDERADO_NO_RAZAO' then lanc.valor " +
            "          when 'DEBITO_NO_RAZAO_NAO_CONSIDERADO_NO_BANCO' then lanc.valor " +
            "          else 0 " +
            "       end as debito,  " +
            "       lanc.TIPOOPERACAOCONCILIACAO, " +
            "       lanc.id as movimentoId, " +
            "       'LANCCONCILIACAOBANCARIA' as tipoMovimento, " +
            "       sub.codigo || ' - ' || sub.descricao as subConta, " +
            "       cast(lanc.NUMERO as varchar(20)) as numero, " +
            "       'NAO_APLICAVEL' as situacao " +
            "  from LANCCONCILIACAOBANCARIA lanc " +
            " inner join subconta sub on lanc.subconta_id = sub.id " +
            " where sub.contabancariaentidade_id = :contaBanc " +
            "   and lanc.identificador_id is null ";
        if (filtro.getDataInicial() == null && filtro.getDataFinal() == null && filtro.getDataConciliacaoPorIdentificador() != null) {
            sql += " and trunc(lanc.data) <= to_date(:dataConcPorIdentificador, 'dd/MM/yyyy') ";
        }
        sql += adicionarFiltrosPorData("lanc.data", "LANC.DATACONCILIACAO", filtro);
        if (filtro.getVisualizarPendenciasBaixadas()) {
            sql += " and LANC.DATACONCILIACAO >= to_date(:dataFinal,'dd/mm/yyyy')";
        }
        if (filtro.getContaFinanceira() != null) {
            sql += " and sub.id = :contaFinanceira ";
        }
        if (filtro.getOperacaoConciliacao() != null) {
            sql += " and lanc.TIPOOPERACAOCONCILIACAO = :operacao ";
        }
        if (filtro.getTipoConciliacao() != null) {
            sql += " and lanc.TIPOCONCILIACAO_ID = :tipo ";
        }
        if (filtro.getNumero() != null) {
            sql += " and lanc.NUMERO like :numero ";
        }
        if (filtro.getNumeros() != null && !filtro.getNumeros().isEmpty()) {
            sql += " and lanc.numero in :numeros ";
        }
        if (filtro.getValor() != null && filtro.getValor().compareTo(BigDecimal.ZERO) > 0) {
            sql += " and lanc.VALOR = :valor ";
        }
        if (filtro.getValores() != null && !filtro.getValores().isEmpty()) {
            sql += " and lanc.VALOR in :valores ";
        }
        sql += " ) order by datamovimento desc, to_number(numero) desc ";

        Query consulta = em.createNativeQuery(sql);
        adicionarParametros(filtro, consulta);
        List<MovimentoConciliacaoBancaria> retorno = Lists.newArrayList();
        List<Object[]> resultado = consulta.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                MovimentoConciliacaoBancaria movimentoConciliacaoBancaria = popularMovimentoConciliacaoBancaria(obj);
                Util.adicionarObjetoEmLista(retorno, movimentoConciliacaoBancaria);
            }
        }
        return retorno;
    }

    private String adicionarFiltrosPorData(String campoDataMovimento, String campoDataConciliacao, FiltroPendenciaConciliacao filtro) {
        String sql = "";
        if (filtro.getDataInicial() != null && filtro.getDataFinal() != null) {
            sql += " and trunc(" + campoDataMovimento + ") BETWEEN to_date(:dataInicial,'dd/mm/yyyy') and to_date(:dataFinal,'dd/mm/yyyy')";
        } else if (filtro.getDataInicial() != null) {
            sql += " and trunc(" + campoDataMovimento + ") >= to_date(:dataInicial, 'dd/MM/yyyy') ";
        } else if (filtro.getDataFinal() != null) {
            sql += " and trunc(" + campoDataMovimento + ") <= to_date(:dataFinal, 'dd/MM/yyyy') ";
        }
        if (filtro.getDataConciliacaoInicial() != null && filtro.getDataConciliacaoFinal() != null) {
            sql += " and trunc(" + campoDataConciliacao + ") between to_date(:dataConciliacaoInicial, 'dd/MM/yyyy') and to_date(:dataConciliacaoFinal, 'dd/MM/yyyy') ";
        } else if (filtro.getDataConciliacaoInicial() != null) {
            sql += " and trunc(" + campoDataConciliacao + ") >= to_date(:dataConciliacaoInicial, 'dd/MM/yyyy') ";
        } else if (filtro.getDataConciliacaoFinal() != null) {
            sql += " and trunc(" + campoDataConciliacao + ") <= to_date(:dataConciliacaoFinal, 'dd/MM/yyyy') ";
        }
        if (filtro.getConciliado() != null) {
            if (filtro.getConciliado().isNao()) {
                sql += " and (" + campoDataConciliacao + " is null " +
                    (filtro.getDataConciliacaoPorIdentificador() != null ? " or trunc(" + campoDataConciliacao + ") > :dataConcPorIdentificador)" : ")");
            } else if (filtro.getConciliado().isSim() && filtro.getDataConciliacaoPorIdentificador() != null) {
                sql += " and trunc(" + campoDataMovimento + ") <= :dataConcPorIdentificador";
            }
        }
        return sql;
    }

    private MovimentoConciliacaoBancaria popularMovimentoConciliacaoBancaria(Object[] obj) {
        MovimentoConciliacaoBancaria movimentoConciliacaoBancaria = new MovimentoConciliacaoBancaria();
        movimentoConciliacaoBancaria.setDataMovimento((Date) obj[0]);
        movimentoConciliacaoBancaria.setDataConciliacao((Date) obj[1]);
        movimentoConciliacaoBancaria.setHistorico((String) obj[2]);
        movimentoConciliacaoBancaria.setCredito((BigDecimal) obj[3]);
        movimentoConciliacaoBancaria.setDebito((BigDecimal) obj[4]);
        movimentoConciliacaoBancaria.setTipoOperacaoConciliacao(obj[5] != null ? TipoOperacaoConcilicaoBancaria.valueOf((String) obj[5]) : null);
        movimentoConciliacaoBancaria.setMovimentoId(((BigDecimal) obj[6]).longValue());
        movimentoConciliacaoBancaria.setTipoMovimento(obj[7] != null ? MovimentoConciliacaoBancaria.TipoMovimento.valueOf((String) obj[7]) : null);
        movimentoConciliacaoBancaria.setSubConta((String) obj[8]);
        movimentoConciliacaoBancaria.setNumero((String) obj[9]);
        movimentoConciliacaoBancaria.setSituacao(MovimentoConciliacaoBancaria.Situacao.valueOf((String) obj[10]));
        return movimentoConciliacaoBancaria;
    }

    public List<IdentificadorMovimentoConciliacaoBancaria> buscarMovimentosPorContaBancariaConciliacaoComIdentificador(FiltroPendenciaConciliacao filtro) {
        String sql = " select ident.* " +
            "  from identificador ident " +
            " order by ident.numero ";
        Query q = em.createNativeQuery(sql, Identificador.class);
        List<IdentificadorMovimentoConciliacaoBancaria> retorno = Lists.newArrayList();
        List<Identificador> resultado = q.getResultList();
        if (!resultado.isEmpty()) {
            for (Identificador identificador : resultado) {
                IdentificadorMovimentoConciliacaoBancaria identificadorMovimentoConciliacaoBancaria = new IdentificadorMovimentoConciliacaoBancaria();
                identificadorMovimentoConciliacaoBancaria.setIdentificador(identificador);
                identificadorMovimentoConciliacaoBancaria.setMovimentos(buscarMovimentosPorIdentificaor(filtro, identificador.getId()));
                if (!identificadorMovimentoConciliacaoBancaria.getMovimentos().isEmpty()) {
                    Util.adicionarObjetoEmLista(retorno, identificadorMovimentoConciliacaoBancaria);
                }
            }
        }
        return retorno;
    }

    public List<MovimentoConciliacaoBancaria> buscarMovimentosPorIdentificaor(FiltroPendenciaConciliacao filtro, Long idIdentificador) {
        String sql = " select datamovimento, " +
            "       dataconciliacao, " +
            "       historico, " +
            "       credito, " +
            "       debito, " +
            "       TIPOOPERACAOCONCILIACAO, " +
            "       movimentoId, " +
            "       tipoMovimento, " +
            "       subConta, " +
            "       numero, " +
            "       situacao " +
            "  from ( " +
            "select vwmov.data_movimento as datamovimento, " +
            "       vwmov.data_conciliacao as dataconciliacao, " +
            "       vwmov.historico, " +
            "       vwmov.credito as credito, " +
            "       vwmov.debito as debito, " +
            "       eve.TIPOOPERACAOCONCILIACAO, " +
            "       movimento_id as movimentoId, " +
            "       vwmov.tipo_movimento as tipoMovimento, " +
            "       sub.codigo || ' - ' || sub.descricao as subConta, " +
            "       vwmov.NUMERO as numero, " +
            "       vwmov.situacao as situacao " +
            "  from VWMOVIMENTOCONTABIL vwmov " +
            " inner join eventocontabil eve on vwmov.eventocontabil_id = eve.id " +
            " inner join subconta sub on vwmov.subconta_id = sub.id " +
            " inner join identificador ident on vwmov.identificador_id = ident.id " +
            " where sub.contabancariaentidade_id = :contaBanc " +
            "   and vwmov.TIPO_MOVIMENTO not in ('RECEITA_EXTRA', 'ESTORNO_RECEITA_EXTRA') " +
            "   and vwmov.identificador_id = :idIdentificador ";
        if (filtro.getDataInicial() == null && filtro.getDataFinal() == null) {
            sql += " and trunc(vwmov.data_movimento) <= to_date(:dataConcPorIdentificador, 'dd/MM/yyyy') ";
        }
        sql += adicionarFiltrosPorData("vwmov.data_movimento", "vwmov.data_conciliacao", filtro);
        if (filtro.getContaFinanceira() != null) {
            sql += " and vwmov.subconta_id = :contaFinanceira ";
        }
        if (filtro.getOperacaoConciliacao() != null) {
            sql += " and eve.TIPOOPERACAOCONCILIACAO = :operacao ";
        }
        if (filtro.getNumero() != null) {
            sql += " and vwmov.NUMERO like :numero ";
        }
        if (filtro.getValor() != null && filtro.getValor().compareTo(BigDecimal.ZERO) > 0) {
            sql += " and (case when vwmov.credito > 0 then vwmov.credito else vwmov.debito end) = :valor ";
        }
        sql += " union all " +
            "select vwmov.data_movimento as datamovimento, " +
            "       vwmov.data_conciliacao as dataconciliacao, " +
            "       vwmov.historico, " +
            "       vwmov.credito as credito, " +
            "       vwmov.debito as debito, " +
            "       eve.TIPOOPERACAOCONCILIACAO, " +
            "       movimento_id as movimentoId, " +
            "       vwmov.tipo_movimento as tipoMovimento, " +
            "       sub.codigo || ' - ' || sub.descricao as subConta, " +
            "       vwmov.NUMERO as numero, " +
            "       vwmov.situacao as situacao " +
            "  from VWMOVIMENTOCONTABIL vwmov " +
            " inner join eventocontabil eve on vwmov.eventocontabil_id = eve.id " +
            " INNER join receitaextra recextra on vwmov.movimento_id = recextra.id " +
            " INNER JOIN CONTAEXTRAORCAMENTARIA CEXTRA ON RECEXTRA.CONTAEXTRAORCAMENTARIA_ID = CEXTRA.ID " +
            " inner join subconta sub on vwmov.subconta_id = sub.id " +
            " inner join identificador ident on vwmov.identificador_id = ident.id " +
            " where sub.contabancariaentidade_id = :contaBanc " +
            "   and vwmov.TIPO_MOVIMENTO IN ('RECEITA_EXTRA') " +
            "   AND CEXTRA.TIPOCONTAEXTRAORCAMENTARIA <> 'DEPOSITOS_CONSIGNACOES' " +
            "   and ident.id = :idIdentificador ";
        if (filtro.getDataInicial() == null && filtro.getDataFinal() == null) {
            sql += " and trunc(vwmov.data_movimento) <= to_date(:dataConcPorIdentificador, 'dd/MM/yyyy') ";
        }
        sql += adicionarFiltrosPorData("vwmov.data_movimento", "vwmov.data_conciliacao", filtro);
        if (filtro.getContaFinanceira() != null) {
            sql += " and vwmov.subconta_id = :contaFinanceira ";
        }
        if (filtro.getOperacaoConciliacao() != null) {
            sql += " and eve.TIPOOPERACAOCONCILIACAO = :operacao ";
        }
        if (filtro.getNumero() != null) {
            sql += " and vwmov.NUMERO like :numero ";
        }
        if (filtro.getValor() != null && filtro.getValor().compareTo(BigDecimal.ZERO) > 0) {
            sql += " and (case when vwmov.credito > 0 then vwmov.credito else vwmov.debito end) = :valor ";
        }
        sql += " union all " +
            "select vwmov.data_movimento as datamovimento, " +
            "       vwmov.data_conciliacao as dataconciliacao, " +
            "       vwmov.historico, " +
            "       vwmov.credito as credito, " +
            "       vwmov.debito as debito, " +
            "       eve.TIPOOPERACAOCONCILIACAO, " +
            "       movimento_id as movimentoId, " +
            "       vwmov.tipo_movimento as tipoMovimento, " +
            "       sub.codigo || ' - ' || sub.descricao as subConta, " +
            "       vwmov.NUMERO as numero, " +
            "       vwmov.situacao as situacao " +
            "  from VWMOVIMENTOCONTABIL vwmov " +
            " inner join eventocontabil eve on vwmov.eventocontabil_id = eve.id " +
            " inner join receitaextraestorno est on vwmov.movimento_id = est.id " +
            " INNER join receitaextra recextra on est.receitaextra_id = recextra.id " +
            " INNER JOIN CONTAEXTRAORCAMENTARIA CEXTRA ON RECEXTRA.CONTAEXTRAORCAMENTARIA_ID = CEXTRA.ID " +
            " inner join subconta sub on vwmov.subconta_id = sub.id " +
            " inner join identificador ident on vwmov.identificador_id = ident.id " +
            " where sub.contabancariaentidade_id = :contaBanc " +
            "   and vwmov.TIPO_MOVIMENTO IN ('ESTORNO_RECEITA_EXTRA') " +
            "   AND CEXTRA.TIPOCONTAEXTRAORCAMENTARIA <> 'DEPOSITOS_CONSIGNACOES' " +
            "   and ident.id = :idIdentificador ";
        if (filtro.getDataInicial() == null && filtro.getDataFinal() == null) {
            sql += " and trunc(vwmov.data_movimento) <= to_date(:dataConcPorIdentificador, 'dd/MM/yyyy') ";
        }
        sql += adicionarFiltrosPorData("vwmov.data_movimento", "vwmov.data_conciliacao", filtro);
        if (filtro.getContaFinanceira() != null) {
            sql += " and vwmov.subconta_id = :contaFinanceira ";
        }
        if (filtro.getOperacaoConciliacao() != null) {
            sql += " and eve.TIPOOPERACAOCONCILIACAO = :operacao ";
        }
        if (filtro.getNumero() != null) {
            sql += " and vwmov.NUMERO like :numero ";
        }
        if (filtro.getValor() != null && filtro.getValor().compareTo(BigDecimal.ZERO) > 0) {
            sql += " and (case when vwmov.credito > 0 then vwmov.credito else vwmov.debito end) = :valor ";
        }
        sql += " union all " +
            "select lanc.data as datamovimento, " +
            "       lanc.dataconciliacao as dataconciliacao, " +
            "       lanc.historico, " +
            "       case LANC.TIPOOPERACAOCONCILIACAO " +
            "          when 'CREDITO_NO_BANCO_NAO_CONSIDERADO_NO_RAZAO' then lanc.valor " +
            "          when 'CREDITO_NO_RAZAO_NAO_CONSIDERADO_NO_BANCO' then lanc.valor " +
            "          else 0 " +
            "       end as credito,  " +
            "       case LANC.TIPOOPERACAOCONCILIACAO " +
            "          when 'DEBITO_NO_BANCO_NAO_CONSIDERADO_NO_RAZAO' then lanc.valor " +
            "          when 'DEBITO_NO_RAZAO_NAO_CONSIDERADO_NO_BANCO' then lanc.valor " +
            "          else 0 " +
            "       end as debito,  " +
            "       lanc.TIPOOPERACAOCONCILIACAO, " +
            "       lanc.id as movimentoId, " +
            "       'LANCCONCILIACAOBANCARIA' as tipoMovimento, " +
            "       sub.codigo || ' - ' || sub.descricao as subConta, " +
            "       cast(lanc.NUMERO as varchar(20)) as numero, " +
            "       'NAO_APLICAVEL' as situacao " +
            "  from LANCCONCILIACAOBANCARIA lanc " +
            " inner join subconta sub on lanc.subconta_id = sub.id " +
            " inner join identificador ident on lanc.identificador_id = ident.id " +
            " where sub.contabancariaentidade_id = :contaBanc " +
            "   and ident.id = :idIdentificador ";
        if (filtro.getDataInicial() == null && filtro.getDataFinal() == null) {
            sql += " and trunc(lanc.data) <= to_date(:dataConcPorIdentificador, 'dd/MM/yyyy') ";
        }
        sql += adicionarFiltrosPorData("lanc.data", "LANC.DATACONCILIACAO", filtro);
        if (filtro.getContaFinanceira() != null) {
            sql += " and sub.id = :contaFinanceira ";
        }
        if (filtro.getOperacaoConciliacao() != null) {
            sql += " and lanc.TIPOOPERACAOCONCILIACAO = :operacao ";
        }
        if (filtro.getTipoConciliacao() != null) {
            sql += " and lanc.TIPOCONCILIACAO_ID = :tipo ";
        }
        if (filtro.getNumero() != null) {
            sql += " and lanc.NUMERO like :numero ";
        }
        if (filtro.getValor() != null && filtro.getValor().compareTo(BigDecimal.ZERO) > 0) {
            sql += " and lanc.VALOR = :valor ";
        }
        sql += " ) order by to_number(numero) desc ";
        Query consulta = em.createNativeQuery(sql);
        adicionarParametros(filtro, consulta);
        consulta.setParameter("idIdentificador", idIdentificador);
        List<MovimentoConciliacaoBancaria> retorno = Lists.newArrayList();
        List<Object[]> resultado = consulta.getResultList();
        if (!resultado.isEmpty()) {
            for (Object[] obj : resultado) {
                MovimentoConciliacaoBancaria movimentoConciliacaoBancaria = popularMovimentoConciliacaoBancaria(obj);
                Util.adicionarObjetoEmLista(retorno, movimentoConciliacaoBancaria);
            }
        }
        return retorno;
    }

    private void adicionarParametros(FiltroPendenciaConciliacao filtro, Query consulta) {
        consulta.setParameter("contaBanc", filtro.getContaBancariaEntidade().getId());
        if (filtro.getDataInicial() != null) {
            consulta.setParameter("dataInicial", DataUtil.getDataFormatada(filtro.getDataInicial()));
        }
        if (filtro.getDataFinal() != null) {
            consulta.setParameter("dataFinal", DataUtil.getDataFormatada(filtro.getDataFinal()));
        }
        if (filtro.getContaFinanceira() != null) {
            consulta.setParameter("contaFinanceira", filtro.getContaFinanceira().getId());
        }
        if (filtro.getOperacaoConciliacao() != null) {
            consulta.setParameter("operacao", filtro.getOperacaoConciliacao().name());
        }
        if (filtro.getTipoConciliacao() != null) {
            consulta.setParameter("tipo", filtro.getTipoConciliacao().getId());
        }
        if (filtro.getNumero() != null) {
            consulta.setParameter("numero", filtro.getNumero() + "%");
        }
        if (filtro.getNumeros() != null && !filtro.getNumeros().isEmpty()) {
            consulta.setParameter("numeros", filtro.getNumeros());
        }
        if (filtro.getDataConciliacao() != null) {
            consulta.setParameter("dataConciliacao", DataUtil.getDataFormatada(filtro.getDataConciliacao()));
        }
        if (filtro.getDataConciliacaoInicial() != null) {
            consulta.setParameter("dataConciliacaoInicial", DataUtil.getDataFormatada(filtro.getDataConciliacaoInicial()));
        }
        if (filtro.getDataConciliacaoFinal() != null) {
            consulta.setParameter("dataConciliacaoFinal", DataUtil.getDataFormatada(filtro.getDataConciliacaoFinal()));
        }
        if (((filtro.getDataInicial() == null && filtro.getDataFinal() == null) || !filtro.getConciliado().isTodos()) && filtro.getDataConciliacaoPorIdentificador() != null) {
            consulta.setParameter("dataConcPorIdentificador", DataUtil.getDataFormatada(filtro.getDataConciliacaoPorIdentificador()));
        }
        if (filtro.getValor() != null && filtro.getValor().compareTo(BigDecimal.ZERO) > 0) {
            consulta.setParameter("valor", filtro.getValor());
        }
        if (filtro.getValores() != null && !filtro.getValores().isEmpty()) {
            consulta.setParameter("valores", filtro.getValores());
        }
    }

    @Override
    public void salvarNovo(LancamentoConciliacaoBancaria selecionado) {
        if (selecionado.getId() == null) {
            selecionado.setNumero(retornaUltimoCodigoLong());
            selecionado.setSituacao(SituacaoCadastralContabil.ATIVO);
            em.persist(selecionado);
        } else {
            selecionado.setSituacao(SituacaoCadastralContabil.ATIVO);
            em.merge(selecionado);
        }
    }

    public FaseFacade getFaseFacade() {
        return faseFacade;
    }

    public SaldoConstContaBancariaFacade getSaldoConstContaBancariaFacade() {
        return saldoConstContaBancariaFacade;
    }

    public ContaBancariaEntidadeFacade getContaBancariaEntidadeFacade() {
        return contaBancariaEntidadeFacade;
    }

    public IdentificadorFacade getIdentificadorFacade() {
        return identificadorFacade;
    }

    public PagamentoFacade getPagamentoFacade() {
        return pagamentoFacade;
    }

    public PagamentoEstornoFacade getPagamentoEstornoFacade() {
        return pagamentoEstornoFacade;
    }

    public PagamentoExtraFacade getPagamentoExtraFacade() {
        return pagamentoExtraFacade;
    }

    public PagamentoExtraEstornoFacade getPagamentoExtraEstornoFacade() {
        return pagamentoExtraEstornoFacade;
    }

    public ReceitaExtraFacade getReceitaExtraFacade() {
        return receitaExtraFacade;
    }

    public ReceitaExtraEstornoFacade getReceitaExtraEstornoFacade() {
        return receitaExtraEstornoFacade;
    }

    public LancamentoReceitaOrcFacade getLancamentoReceitaOrcFacade() {
        return lancamentoReceitaOrcFacade;
    }

    public ReceitaORCEstornoFacade getReceitaORCEstornoFacade() {
        return receitaORCEstornoFacade;
    }

    public LiberacaoCotaFinanceiraFacade getLiberacaoCotaFinanceiraFacade() {
        return liberacaoCotaFinanceiraFacade;
    }

    public EstornoLibCotaFinanceiraFacade getEstornoLibCotaFinanceiraFacade() {
        return estornoLibCotaFinanceiraFacade;
    }

    public TransferenciaContaFinanceiraFacade getTransferenciaContaFinanceiraFacade() {
        return transferenciaContaFinanceiraFacade;
    }

    public EstornoTransferenciaFacade getEstornoTransferenciaFacade() {
        return estornoTransferenciaFacade;
    }

    public AjusteAtivoDisponivelFacade getAjusteAtivoDisponivelFacade() {
        return ajusteAtivoDisponivelFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public void conciliarMovimento(MovimentoConciliacaoBancaria movimentoConciliacaoBancaria, Date dataConciliacao) {
        switch (movimentoConciliacaoBancaria.getTipoMovimento()) {
            case LANCCONCILIACAOBANCARIA:
                LancamentoConciliacaoBancaria lancamentoConciliacaoBancaria = recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(lancamentoConciliacaoBancaria.getData(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (lancamentoConciliacaoBancaria.getDataConciliacao() == null) {
                    lancamentoConciliacaoBancaria.setDataConciliacao(dataConciliacao);
                }
                salvar(lancamentoConciliacaoBancaria);
                break;
            case PAGAMENTO:
            case PAGAMENTO_RESTO:
                Pagamento pagamento = pagamentoFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(pagamento.getDataPagamento(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (pagamento.getDataConciliacao() == null) {
                    pagamento.setDataConciliacao(dataConciliacao);
                    getPagamentoFacade().salvar(pagamento);
                }
                break;
            case ESTORNO_PAGAMENTO:
            case ESTORNO_PAGAMENTO_RESTO:
                PagamentoEstorno pagamentoEstorno = pagamentoEstornoFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(pagamentoEstorno.getDataEstorno(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (pagamentoEstorno.getDataConciliacao() == null) {
                    pagamentoEstorno.setDataConciliacao(dataConciliacao);
                    getPagamentoEstornoFacade().salvar(pagamentoEstorno);
                }
                break;
            case DESPESA_EXTRA:
                PagamentoExtra pagamentoExtra = pagamentoExtraFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(pagamentoExtra.getDataPagto(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (pagamentoExtra.getDataConciliacao() == null) {
                    pagamentoExtra.setDataConciliacao(dataConciliacao);
                    getPagamentoExtraFacade().salvar(pagamentoExtra);
                }
                break;
            case ESTORNO_DESPESA_EXTRA:
                PagamentoExtraEstorno pagamentoExtraEstorno = pagamentoExtraEstornoFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(pagamentoExtraEstorno.getDataEstorno(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (pagamentoExtraEstorno.getDataConciliacao() == null) {
                    pagamentoExtraEstorno.setDataConciliacao(dataConciliacao);
                    getPagamentoExtraEstornoFacade().salvar(pagamentoExtraEstorno);
                }
                break;
            case RECEITA_EXTRA:
                ReceitaExtra receitaExtra = receitaExtraFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(receitaExtra.getDataReceita(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (receitaExtra.getDataConciliacao() == null) {
                    receitaExtra.setDataConciliacao(dataConciliacao);
                    getReceitaExtraFacade().salvar(receitaExtra);
                }
                break;
            case ESTORNO_RECEITA_EXTRA:
                ReceitaExtraEstorno receitaExtraEstorno = receitaExtraEstornoFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(receitaExtraEstorno.getDataEstorno(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (receitaExtraEstorno.getDataConciliacao() == null) {
                    receitaExtraEstorno.setDataConciliacao(dataConciliacao);
                    getReceitaExtraEstornoFacade().salvar(receitaExtraEstorno);
                }
                break;
            case RECEITA_REALIZADA:
                LancamentoReceitaOrc lancamentoReceitaOrc = lancamentoReceitaOrcFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(lancamentoReceitaOrc.getDataLancamento(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (lancamentoReceitaOrc.getDataConciliacao() == null) {
                    lancamentoReceitaOrc.setDataConciliacao(dataConciliacao);
                    getLancamentoReceitaOrcFacade().salvar(lancamentoReceitaOrc);
                }
                break;
            case ESTORNO_RECEITA_REALIZADA:
                ReceitaORCEstorno receitaORCEstorno = receitaORCEstornoFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(receitaORCEstorno.getDataEstorno(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (receitaORCEstorno.getDataConciliacao() == null) {
                    receitaORCEstorno.setDataConciliacao(dataConciliacao);
                    getReceitaORCEstornoFacade().salvar(receitaORCEstorno);
                }
                break;
            case LIBERACAO_FINANCEIRA:
            case LIBERACAO_FINANCEIRA_REPASSE:
                LiberacaoCotaFinanceira liberacaoCotaFinanceira = liberacaoCotaFinanceiraFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(liberacaoCotaFinanceira.getDataLiberacao(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (movimentoConciliacaoBancaria.getTipoOperacaoConciliacao().isOperacaoCredito()) {
                    if (liberacaoCotaFinanceira.getDataConciliacao() == null) {
                        liberacaoCotaFinanceira.setDataConciliacao(dataConciliacao);
                        getLiberacaoCotaFinanceiraFacade().salvar(liberacaoCotaFinanceira);
                    }
                } else {
                    if (liberacaoCotaFinanceira.getRecebida() == null) {
                        liberacaoCotaFinanceira.setRecebida(dataConciliacao);
                        getLiberacaoCotaFinanceiraFacade().salvar(liberacaoCotaFinanceira);
                    }
                }
                break;
            case ESTORNO_LIBERACAO_FINANCEIRA:
            case ESTORNO_LIBERACAO_FINANCEIRA_REPASSE:
                EstornoLibCotaFinanceira estornoLibCotaFinanceira = estornoLibCotaFinanceiraFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(estornoLibCotaFinanceira.getDataEstorno(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (movimentoConciliacaoBancaria.getTipoOperacaoConciliacao().isOperacaoCredito()) {
                    if (estornoLibCotaFinanceira.getDataConciliacao() == null) {
                        estornoLibCotaFinanceira.setDataConciliacao(dataConciliacao);
                        getEstornoLibCotaFinanceiraFacade().salvar(estornoLibCotaFinanceira);
                    }
                } else {
                    if (estornoLibCotaFinanceira.getRecebida() == null) {
                        estornoLibCotaFinanceira.setRecebida(dataConciliacao);
                        getEstornoLibCotaFinanceiraFacade().salvar(estornoLibCotaFinanceira);
                    }
                }
                break;
            case TRANSFERENCIA_FINANCEIRA_DEPOSITO:
                TransferenciaContaFinanceira transferenciaDeposito = transferenciaContaFinanceiraFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(transferenciaDeposito.getDataTransferencia(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (transferenciaDeposito.getRecebida() == null) {
                    transferenciaDeposito.setRecebida(dataConciliacao);
                    getTransferenciaContaFinanceiraFacade().salvar(transferenciaDeposito);
                }
                break;
            case TRANSFERENCIA_FINANCEIRA_RETIRADA:
                TransferenciaContaFinanceira transferenciaRetirada = transferenciaContaFinanceiraFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(transferenciaRetirada.getDataTransferencia(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (transferenciaRetirada.getDataConciliacao() == null) {
                    transferenciaRetirada.setDataConciliacao(dataConciliacao);
                    getTransferenciaContaFinanceiraFacade().salvar(transferenciaRetirada);
                }
                break;
            case ESTORNO_TRANSFERENCIA_FINANCEIRA_DEPOSITO:
                EstornoTransferencia estornoDeposito = estornoTransferenciaFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(estornoDeposito.getDataEstorno(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (estornoDeposito.getRecebida() == null) {
                    estornoDeposito.setRecebida(dataConciliacao);
                    getEstornoTransferenciaFacade().salvar(estornoDeposito);
                }
                break;
            case ESTORNO_TRANSFERENCIA_FINANCEIRA_RETIRADA:
                EstornoTransferencia estornoRetirada = estornoTransferenciaFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(estornoRetirada.getDataEstorno(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (estornoRetirada.getDataConciliacao() == null) {
                    estornoRetirada.setDataConciliacao(dataConciliacao);
                    getEstornoTransferenciaFacade().salvar(estornoRetirada);
                }
                break;
            case AJUSTE_ATIVO_DISPONIVEL_AUMENTATIVO:
            case AJUSTE_ATIVO_DISPONIVEL_DIMINUTIVO:
            case ESTORNO_AJUSTE_ATIVO_DISPONIVEL_AUMENTATIVO:
            case ESTORNO_AJUSTE_ATIVO_DISPONIVEL_DIMINUTIVO:
                AjusteAtivoDisponivel ajusteAtivoDisponivel = ajusteAtivoDisponivelFacade.recuperar(movimentoConciliacaoBancaria.getMovimentoId());
                validarDataDeConciliacao(ajusteAtivoDisponivel.getDataAjuste(), dataConciliacao, movimentoConciliacaoBancaria.getTipoMovimento());
                if (ajusteAtivoDisponivel.getDataConciliacao() == null) {
                    ajusteAtivoDisponivel.setDataConciliacao(dataConciliacao);
                    getAjusteAtivoDisponivelFacade().salvar(ajusteAtivoDisponivel);
                }
                break;
        }
    }

    private void validarDataDeConciliacao(Date dataMovimento, Date dataConciliacao, MovimentoConciliacaoBancaria.TipoMovimento tipoMovimento) {
        ValidacaoException ve = new ValidacaoException();
        if (dataMovimento.after(dataConciliacao)) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A Data de Conciliação deve ser posterior ou igual a Data do Movimento (" + tipoMovimento.getDescricao() + ").");
        }
        if (DataUtil.getAno(dataMovimento).compareTo(DataUtil.getAno(dataConciliacao)) != 0) {
            ve.adicionarMensagemDeOperacaoNaoPermitida("A data de conciliação está com o exercício diferente da data do registro.");
        }
        ve.lancarException();
    }
}

package br.com.webpublico.negocios.contabil.conciliacaocontabil;

import br.com.webpublico.entidades.Conta;
import br.com.webpublico.entidades.Exercicio;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabil;
import br.com.webpublico.entidades.contabil.conciliacaocontabil.ConfigConciliacaoContabilConta;
import br.com.webpublico.entidadesauxiliares.ParametrosRelatorios;
import br.com.webpublico.entidadesauxiliares.contabil.conciliacaocontabil.ConciliacaoContabilVO;
import br.com.webpublico.enums.conciliacaocontabil.TipoMovimentoSaldo;
import br.com.webpublico.negocios.ExercicioFacade;
import br.com.webpublico.negocios.HierarquiaOrganizacionalFacade;
import br.com.webpublico.negocios.SistemaFacade;
import br.com.webpublico.seguranca.service.QueryReprocessamentoService;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import br.com.webpublico.util.UtilRelatorioContabil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Stateless
public class ConciliacaoContabilFacade implements Serializable {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ExercicioFacade exercicioFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SistemaFacade sistemaFacade;

    public List<ConciliacaoContabilVO> montarDados(Date dataInicial, Date dataFinal, List<ParametrosRelatorios> parametros) {
        String sql = " select * from ConfigConciliacaoContabil cfg " +
            " where to_date(:dataInicial, 'dd/MM/yyyy') between trunc(cfg.datainicial) and coalesce(cfg.datafinal, to_date(:dataFinal, 'dd/MM/yyyy')) " +
            " order by cfg.quadro, cfg.ordem ";
        Query q = em.createNativeQuery(sql, ConfigConciliacaoContabil.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        List<ConciliacaoContabilVO> retorno = Lists.newArrayList();
        List<ConfigConciliacaoContabil> resultado = q.getResultList();
        Exercicio exercicio = exercicioFacade.getExercicioPorAno(DataUtil.getAno(dataFinal));
        if (!resultado.isEmpty()) {
            for (ConfigConciliacaoContabil config : resultado) {
                ConciliacaoContabilVO vo = new ConciliacaoContabilVO();
                vo.setDescricao(config.getDescricao());
                vo.setQuadro(config.getQuadro());
                vo.setOrdem(config.getOrdem());
                vo.setTotalizador(config.getTotalizador());
                for (ConfigConciliacaoContabilConta configConta : config.getContasContabeis()) {
                    vo.setValorContabil(vo.getValorContabil().add(buscarSaldoContabil(dataFinal, configConta.getConta(), exercicio, parametros)));
                }
                vo.setValorIntercorrente(buscarSaldo(dataInicial, dataFinal, config, getSqlPorTotalizadorAndTipo(config), parametros));
                retorno.add(vo);
            }
        }
        return retorno;
    }

    private BigDecimal buscarSaldoContabil(Date dataFinal, Conta conta, Exercicio exercicio, List<ParametrosRelatorios> parametros) {
        String sql = " select COALESCE(sum(saldoatual.TOTALCREDITO), 0) - COALESCE(sum(saldoatual.TOTALDEBITO), 0) AS SALDOfinal from (" +
            "SELECT COALESCE(SUM(scc.totalcredito), 0) as totalcredito, COALESCE(SUM(scc.totaldebito), 0) AS totaldebito " +
            "FROM SALDOCONTACONTABIL SCC   " +
            "INNER JOIN CONTA C ON SCC.CONTACONTABIL_ID = C.ID " +
            "INNER JOIN VWHIERARQUIAORCAMENTARIA vw on scc.UNIDADEORGANIZACIONAL_ID = vw.SUBORDINADA_ID " +
            "where trunc(scc.datasaldo) = (SELECT trunc(MAX(sld.DATASALDO)) AS maxdata " +
            "    FROM SALDOCONTACONTABIL sld " +
            "   WHERE trunc(sld.DATASALDO) <= TO_DATE(:dataFinal, 'DD/MM/YYYY') " +
            "     and sld.unidadeorganizacional_id = scc.unidadeorganizacional_id " +
            "     and sld.tipoBalancete = scc.tipoBalancete " +
            "     and sld.contacontabil_id = scc.contacontabil_id) " +
            " and c.codigo like :codigoConta " +
            " and c.exercicio_id = :exercicio " +
            " and TO_DATE(:dataFinal, 'DD/MM/YYYY') between vw.INICIOVIGENCIA and coalesce(vw.FIMVIGENCIA, TO_DATE(:dataFinal, 'DD/MM/YYYY')) " +
            Util.concatenarParametros(parametros, 1, " and ") +
            ") saldoatual ";
        Query query = em.createNativeQuery(sql);
        query.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        query.setParameter("codigoConta", conta.getCodigoSemZerosAoFinal() + "%");
        query.setParameter("exercicio", exercicio.getId());
        UtilRelatorioContabil.adicionarParametros(parametros, query);
        query.setMaxResults(1);
        List<BigDecimal> resultado = query.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal buscarSaldo(Date dataInicial, Date dataFinal, ConfigConciliacaoContabil configConciliacaoContabil, String sql, List<ParametrosRelatorios> parametros) {
        if (sql.isEmpty()) {
            return BigDecimal.ZERO;
        }
        Query query = em.createNativeQuery(sql + UtilRelatorioContabil.concatenarParametros(parametros, 1, " and "));
        if (sql.contains(":dataInicial")) {
            query.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        }
        if (sql.contains(":dataFinal")) {
            query.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        }
        query.setParameter("cfgId", configConciliacaoContabil.getId());
        UtilRelatorioContabil.adicionarParametros(parametros, query);
        query.setMaxResults(1);
        List<BigDecimal> resultado = query.getResultList();
        if (!resultado.isEmpty()) {
            return resultado.get(0);
        }
        return BigDecimal.ZERO;
    }

    private String getSqlPorTotalizadorAndTipo(ConfigConciliacaoContabil configConciliacaoContabil) {
        switch (configConciliacaoContabil.getTotalizador()) {
            case DISPONIBILIDADE_DE_CAIXA:
                return TipoMovimentoSaldo.SALDO.equals(configConciliacaoContabil.getTipoMovimentoSaldo()) ? QueryReprocessamentoService.getService().getQuerySaldoDisponibidadeCaixaBruta() : "";
            case CREDITOS_A_RECEBER:
                return TipoMovimentoSaldo.SALDO.equals(configConciliacaoContabil.getTipoMovimentoSaldo()) ? QueryReprocessamentoService.getService().getQuerySaldoCreditoReceber() : QueryReprocessamentoService.getService().getQueryMovimentoReceitaRealizada();
            case ESTOQUE:
                return TipoMovimentoSaldo.SALDO.equals(configConciliacaoContabil.getTipoMovimentoSaldo()) ? QueryReprocessamentoService.getService().getQuerySaldoNaturezaTipoGrupoMaterial() : "";
            case DIVIDA_ATIVA:
                return TipoMovimentoSaldo.SALDO.equals(configConciliacaoContabil.getTipoMovimentoSaldo()) ? QueryReprocessamentoService.getService().getQuerySaldoDividaAtiva() : QueryReprocessamentoService.getService().getQueryMovimentoReceitaRealizada();
            case GRUPO_BEM_MOVEL:
                return TipoMovimentoSaldo.SALDO.equals(configConciliacaoContabil.getTipoMovimentoSaldo()) ? QueryReprocessamentoService.getService().getQuerySaldoGrupoBemMovel() : "";
            case BEM_IMOVEL:
                return TipoMovimentoSaldo.SALDO.equals(configConciliacaoContabil.getTipoMovimentoSaldo()) ? QueryReprocessamentoService.getService().getQuerySaldoGrupoBemImovel() : "";
            case DIVIDA_PUBLICA_E_PRECATORIO:
                return TipoMovimentoSaldo.SALDO.equals(configConciliacaoContabil.getTipoMovimentoSaldo()) ? QueryReprocessamentoService.getService().getQuerySaldoCategoriaDividaPublica() : QueryReprocessamentoService.getService().getQueryMovimentoCategoriaDividaPublica();
            case PASSIVO_ATUARIAL:
                return TipoMovimentoSaldo.SALDO.equals(configConciliacaoContabil.getTipoMovimentoSaldo()) ? QueryReprocessamentoService.getService().getQuerySaldoPassivoAtuarial() : "";
            case RECEITA_DE_OPERACAO_DE_CREDITO:
                return TipoMovimentoSaldo.SALDO.equals(configConciliacaoContabil.getTipoMovimentoSaldo()) ? "" : QueryReprocessamentoService.getService().getQueryMovimentoReceitaRealizada();
            case LIQUIDACAO_DE_DESPESA_E_DE_RESTOS_A_PAGAR:
                return TipoMovimentoSaldo.SALDO.equals(configConciliacaoContabil.getTipoMovimentoSaldo()) ? "" : QueryReprocessamentoService.getService().getQueryMovimentoLiquidacao();
            case RECEITA_DE_ALIENACAO_DE_BEM:
                return TipoMovimentoSaldo.SALDO.equals(configConciliacaoContabil.getTipoMovimentoSaldo()) ? "" : QueryReprocessamentoService.getService().getQueryMovimentoReceitaRealizada();
            case OBRAS_E_INSTALACOES_E_AQUISICAO_DE_IMOVEIS:
                return TipoMovimentoSaldo.SALDO.equals(configConciliacaoContabil.getTipoMovimentoSaldo()) ? "" : QueryReprocessamentoService.getService().getQueryMovimentoLiquidacaoPorConta();
            case MATERIAL_DE_CONSUMO:
                return TipoMovimentoSaldo.SALDO.equals(configConciliacaoContabil.getTipoMovimentoSaldo()) ? "" : QueryReprocessamentoService.getService().getQueryMovimentoGrupoMaterial();
            default:
                return "";
        }
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public ExercicioFacade getExercicioFacade() {
        return exercicioFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }
}

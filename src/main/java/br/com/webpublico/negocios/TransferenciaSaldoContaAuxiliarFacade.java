package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidadesauxiliares.contabil.ContaAuxiliarDetalhada;
import br.com.webpublico.enums.SomaSubtraiSaldoContaContabil;
import br.com.webpublico.enums.TipoBalancete;
import br.com.webpublico.enums.TipoLancamentoContabil;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.UtilGeradorContaAuxiliar;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

@Stateless
public class TransferenciaSaldoContaAuxiliarFacade extends AbstractFacade<TransferenciaSaldoContaAuxiliar> {

    @PersistenceContext(name = "webpublicoPU")
    private EntityManager em;
    @EJB
    private ContaFacade contaFacade;
    @EJB
    private SaldoContaContabilFacade saldoContaContabilFacade;
    @EJB
    private TipoContaAuxiliarFacade tipoContaAuxiliarFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ExercicioFacade exercicioFacade;

    public TransferenciaSaldoContaAuxiliarFacade() {
        super(TransferenciaSaldoContaAuxiliar.class);
    }

    @Override
    public void salvarNovo(TransferenciaSaldoContaAuxiliar entity) {
        if (Strings.isNullOrEmpty(entity.getNumero())) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(TransferenciaSaldoContaAuxiliar.class, "numero").toString());
        }
        super.salvarNovo(entity);
        gerarSaldoContabil(entity);
    }

    public void gerarSaldoContabil(TransferenciaSaldoContaAuxiliar entity) {
        saldoContaContabilFacade.geraSaldoContaContabil(entity.getDataLancamento(), entity.getContaAuxiliarCredito(), entity.getUnidadeOrganizacional(), TipoLancamentoContabil.CREDITO, entity.getValor(), TipoBalancete.MENSAL, SomaSubtraiSaldoContaContabil.SOMA);
        saldoContaContabilFacade.geraSaldoContaContabil(entity.getDataLancamento(), entity.getContaAuxiliarDebito(), entity.getUnidadeOrganizacional(), TipoLancamentoContabil.DEBITO, entity.getValor(), TipoBalancete.MENSAL, SomaSubtraiSaldoContaContabil.SOMA);
        saldoContaContabilFacade.geraSaldoContaContabil(entity.getDataLancamento(), entity.getContaAuxiliarDetalhadaCred(), entity.getUnidadeOrganizacional(), TipoLancamentoContabil.CREDITO, entity.getValor(), TipoBalancete.MENSAL, SomaSubtraiSaldoContaContabil.SOMA);
        saldoContaContabilFacade.geraSaldoContaContabil(entity.getDataLancamento(), entity.getContaAuxiliarDetalhadaDeb(), entity.getUnidadeOrganizacional(), TipoLancamentoContabil.DEBITO, entity.getValor(), TipoBalancete.MENSAL, SomaSubtraiSaldoContaContabil.SOMA);
    }

    public ContaAuxiliar buscarContaAuxiliarPorContaAuxiliarDetalhada(ContaAuxiliarDetalhada contaAuxiliarDetalhada) {
        return tipoContaAuxiliarFacade.gerarMapContaAuxiliar(contaAuxiliarDetalhada.getTipoContaAuxiliar(),
            contaAuxiliarDetalhada.getContaContabil(),
            montarContaAuxiliar(contaAuxiliarDetalhada),
            sistemaFacade.getExercicioCorrente());
    }

    private TreeMap montarContaAuxiliar(ContaAuxiliarDetalhada contaAuxiliarDetalhada) {
        TreeMap mapaContaAuxiliar = new TreeMap();
        switch (contaAuxiliarDetalhada.getTipoContaAuxiliar().getCodigo()) {
            case "91":
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar1(contaAuxiliarDetalhada.getUnidadeOrganizacional()));
                break;
            case "92":
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar2(contaAuxiliarDetalhada.getUnidadeOrganizacional(),
                    contaAuxiliarDetalhada.getSubSistema()));
                break;
            case "93":
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar3(contaAuxiliarDetalhada.getUnidadeOrganizacional(),
                    contaAuxiliarDetalhada.getSubSistema(),
                    contaAuxiliarDetalhada.getDividaConsolidada()));
                break;
            case "94":
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar4(contaAuxiliarDetalhada.getUnidadeOrganizacional(),
                    contaAuxiliarDetalhada.getSubSistema(),
                    contaAuxiliarDetalhada.getContaDeDestinacao()));
                break;
            case "95":
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar5(contaAuxiliarDetalhada.getUnidadeOrganizacional(),
                    contaAuxiliarDetalhada.getContaDeDestinacao()));
                break;
            case "96":
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar6(contaAuxiliarDetalhada.getUnidadeOrganizacional(),
                    contaAuxiliarDetalhada.getContaDeDestinacao(),
                    (!Strings.isNullOrEmpty(contaAuxiliarDetalhada.getConta().getCodigoSICONFI()) ?
                        contaAuxiliarDetalhada.getConta().getCodigoSICONFI() :
                        contaAuxiliarDetalhada.getConta().getCodigo()).replace(".", "")));
                break;
            case "97":
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar7(contaAuxiliarDetalhada.getUnidadeOrganizacional(),
                    contaAuxiliarDetalhada.getClassificacaoFuncional(),
                    contaAuxiliarDetalhada.getContaDeDestinacao(),
                    (contaAuxiliarDetalhada.getConta().getCodigoSICONFI() != null ?
                        contaAuxiliarDetalhada.getConta().getCodigoSICONFI() :
                        contaAuxiliarDetalhada.getConta().getCodigo().replace(".", "")),
                    contaAuxiliarDetalhada.getEs()));
                break;
            case "98":
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar8(contaAuxiliarDetalhada.getUnidadeOrganizacional(),
                    contaAuxiliarDetalhada.getSubSistema(),
                    contaAuxiliarDetalhada.getDividaConsolidada(),
                    contaAuxiliarDetalhada.getContaDeDestinacao()));
                break;
            case "99":
                mapaContaAuxiliar.putAll(UtilGeradorContaAuxiliar.gerarContaAuxiliar9(contaAuxiliarDetalhada.getUnidadeOrganizacional(),
                    contaAuxiliarDetalhada.getClassificacaoFuncional(),
                    contaAuxiliarDetalhada.getContaDeDestinacao(),
                    contaAuxiliarDetalhada.getConta(),
                    contaAuxiliarDetalhada.getEs(),
                    exercicioFacade.getExercicioPorAno(contaAuxiliarDetalhada.getExercicioAtual().getAno() - 1).getAno(),
                    contaAuxiliarDetalhada.getExercicio()));
                break;
        }
        return mapaContaAuxiliar;
    }

    public Long count(String sql) {
        try {
            Query query = em.createNativeQuery(sql);
            query.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
            return ((BigDecimal) query.getSingleResult()).longValue();
        } catch (Exception nre) {
            return 0L;
        }
    }


    public Object[] filtrarComContadorDeRegistros(String sql, String sqlCount, int inicio, int max) {
        Query consulta = em.createNativeQuery(sql);
        Query consultaCount = em.createNativeQuery(sqlCount);
        Long count = 0L;
        consulta.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));
        consultaCount.setParameter("dataOperacao", DataUtil.getDataFormatada(sistemaFacade.getDataOperacao()));

        List<TransferenciaSaldoContaAuxiliar> transferencias = Lists.newArrayList();
        try {
            count = ((BigDecimal) consultaCount.getSingleResult()).longValue();
            if (max != 0) {
                consulta.setMaxResults(max);
                consulta.setFirstResult(inicio);
            }
            List<Object[]> resultado = consulta.getResultList();
            for (Object[] object : resultado) {
                TransferenciaSaldoContaAuxiliar transferencia = new TransferenciaSaldoContaAuxiliar();
                transferencia.setId(object[0] != null ? ((BigDecimal) object[0]).longValue() : null);
                transferencia.setNumero(object[1] != null ? (String) object[1] : null);
                transferencia.setDataLancamento(object[2] != null ? (Date) object[2] : null);
                transferencia.setContaContabil(em.find(ContaContabil.class, ((BigDecimal) object[3]).longValue()));
                transferencia.setContaAuxiliarDebito(em.find(ContaAuxiliar.class, ((BigDecimal) object[4]).longValue()));
                transferencia.setContaAuxiliarCredito(em.find(ContaAuxiliar.class, ((BigDecimal) object[5]).longValue()));
                transferencia.setUnidadeOrcamentaria((String) object[6]);
                transferencia.setValor((BigDecimal) object[7]);
                transferencias.add(transferencia);
            }
        } catch (NoResultException nre) {
            logger.error("Erro ao montar a pesquisa genérica do transferencia de saldo de conta auxiliar: {} ", nre);
        }
        Object[] retorno = new Object[2];
        retorno[0] = transferencias;
        retorno[1] = count;
        return retorno;
    }

    public List<TransferenciaSaldoContaAuxiliar> buscarTransferenciasPorPeriodo(Date dataInicial, Date dataFinal, List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        String sql = " select * from TRANSFSALDOCONTAAUXILIAR transf " +
            " where trunc(transf.dataLancamento) between to_date(:dataInicial, 'dd/mm/yyyy') and to_date(:dataFinal, 'dd/mm/yyyy') ";
        if (!hierarquiasOrganizacionais.isEmpty()) {
            sql += "and transf.unidadeorganizacional_id in (:unidades) ";
        }
        sql += " order by transf.dataLancamento asc ";
        Query q = em.createNativeQuery(sql, TransferenciaSaldoContaAuxiliar.class);
        q.setParameter("dataInicial", DataUtil.getDataFormatada(dataInicial));
        q.setParameter("dataFinal", DataUtil.getDataFormatada(dataFinal));
        if (!hierarquiasOrganizacionais.isEmpty()) {
            q.setParameter("unidades", getIdUnidades(hierarquiasOrganizacionais));
        }
        return q.getResultList();
    }

    private List<Long> getIdUnidades(List<HierarquiaOrganizacional> hierarquiasOrganizacionais) {
        List<Long> retorno = Lists.newArrayList();
        for (HierarquiaOrganizacional hierarquiaOrganizacional : hierarquiasOrganizacionais) {
            retorno.add(hierarquiaOrganizacional.getSubordinada().getId());
        }
        return retorno;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ContaFacade getContaFacade() {
        return contaFacade;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public BigDecimal buscarSaldoContabilAtual(Conta conta, UnidadeOrganizacional unidadeOrganizacional) {
        return saldoContaContabilFacade.buscarSaldoAtual(sistemaFacade.getDataOperacao(), sistemaFacade.getExercicioCorrente(), conta, unidadeOrganizacional);
    }
}

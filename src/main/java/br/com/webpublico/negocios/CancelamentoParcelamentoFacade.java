package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoParcela;
import br.com.webpublico.negocios.tributario.dao.JdbcDividaAtivaDAO;
import br.com.webpublico.negocios.tributario.dao.JdbcParcelaValorDividaDAO;
import br.com.webpublico.singletons.SingletonGeradorCodigoPorExercicio;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;
import org.springframework.context.ApplicationContext;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.context.ContextLoader;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Stateless
public class CancelamentoParcelamentoFacade extends AbstractFacade<CancelamentoParcelamento> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SingletonGeradorCodigoPorExercicio singletonGeradorCodigoPorExercicio;
    @EJB
    private ConsultaDebitoFacade consultaDebitoFacade;
    private JdbcParcelaValorDividaDAO parcelaValorDividaDAO;
    private JdbcDividaAtivaDAO dividaAtivaDAO;

    public CancelamentoParcelamentoFacade() {
        super(CancelamentoParcelamento.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public CancelamentoParcelamento recuperar(Object id) {
        CancelamentoParcelamento cancelamentoParcelamento = super.recuperar(id);
        if (cancelamentoParcelamento.getUsuario() != null) {
            cancelamentoParcelamento.setNomeUsuarioResponsavel(cancelamentoParcelamento.getUsuario().toString());
        } else {
            cancelamentoParcelamento.setNomeUsuarioResponsavel(CancelamentoParcelamento.NOME_USUARIO_PROCESSO_AUTOMATICO);
        }
        Hibernate.initialize(cancelamentoParcelamento.getParcelas());
        Hibernate.initialize(cancelamentoParcelamento.getArquivos());
        return cancelamentoParcelamento;
    }

    private JdbcParcelaValorDividaDAO getParcelaValorDividaDAO() {
        if (parcelaValorDividaDAO == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            parcelaValorDividaDAO = (JdbcParcelaValorDividaDAO) ap.getBean("jdbcParcelaValorDividaDAO");
        }
        return parcelaValorDividaDAO;
    }

    public JdbcDividaAtivaDAO getDividaAtivaDAO() {
        if (dividaAtivaDAO == null) {
            ApplicationContext ap = ContextLoader.getCurrentWebApplicationContext();
            dividaAtivaDAO = (JdbcDividaAtivaDAO) ap.getBean("dividaAtivaDAO");
        }
        return dividaAtivaDAO;
    }

    public Long buscarProximoCodigoPorExercicio(Exercicio exercicio) {
        try {
            return singletonGeradorCodigoPorExercicio.getProximoCodigoDoExercicio(CancelamentoParcelamento.class, "codigo", "exercicio", exercicio);
        } catch (Exception e) {
            return 1L;
        }
    }

    private List<String> getTiposParcelasCancelamento() {
        List<String> lista = Lists.newArrayList();
        lista.add(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ABATIDAS.name());
        lista.add(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_EM_ABERTO.name());
        return lista;
    }

    public Long buscarIdParcelaOriginalDaParcelaDoCancelamento(Long idParcelaDoCancelamento) {
        StringBuilder tiposParcelas = new StringBuilder();
        String sql = "select parcCanc.parcela_id from CancelamentoParcelamento canc " +
            "inner join ParcelasCancelParcelamento parcCanc on parcCanc.CancelamentoParcelamento_id = canc.id " +
            "inner join ValorDivida vd on vd.calculo_id = canc.id " +
            "inner join ParcelaValorDivida pvd on pvd.valorDivida_id = vd.id " +
            "where pvd.id = :id " +
            "  and parcCanc.tipoParcelaCancelamento in (:lista) " +
            "group by parcCanc.parcela_id " +
            "having count(parcCanc.parcela_id) > 1";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(getParcelaValorDividaDAO().getJdbcTemplate());
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("id", idParcelaDoCancelamento);
        parameters.addValue("lista", getTiposParcelasCancelamento());
        BigDecimal bigDecimal = namedParameterJdbcTemplate.query(sql, parameters, new ResultSetExtractor<BigDecimal>() {
            @Override
            public BigDecimal extractData(ResultSet resultSet) throws SQLException, DataAccessException {
                if (resultSet.next()) {
                    return resultSet.getBigDecimal(1);
                }
                return null;
            }
        });
        return bigDecimal == null ? null : bigDecimal.longValue();
    }

    public Long buscarIdParcelaDoCancelamentoDaParcelaOriginal(Long idParcelaOriginal) {
        String sql = "select distinct pvd.id from CancelamentoParcelamento canc " +
            "inner join ParcelasCancelParcelamento parcCanc on parcCanc.CancelamentoParcelamento_id = canc.id " +
            "inner join ValorDivida vd on vd.calculo_id = canc.id " +
            "inner join ParcelaValorDivida pvd on pvd.valorDivida_id = vd.id " +
            "where parcCanc.parcela_id = :idParcelaOriginal " +
            "  and parcCanc.tipoParcelaCancelamento = :tipoParcela" +
            " order by pvd.id desc ";

        Query q = em.createNativeQuery(sql);
        q.setParameter("idParcelaOriginal", idParcelaOriginal);
        q.setParameter("tipoParcela", ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_EM_ABERTO.name());
        q.setMaxResults(1);
        List<BigDecimal> lista = q.getResultList();
        if (!lista.isEmpty()) {
            return lista.get(0).longValue();
        }
        return null;
    }

    public CancelamentoParcelamento buscarCancelamentoDaParcelaPaga(Long idParcela) {
        String hql = "select pcp.cancelamentoParcelamento from ParcelasCancelamentoParcelamento pcp " +
            " where pcp.parcela.id = :idParcela and pcp.situacaoParcela = :situacaoParcela";
        Query q = em.createQuery(hql);
        q.setParameter("idParcela", idParcela);
        q.setParameter("situacaoParcela", SituacaoParcela.PAGO_PARCELAMENTO);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (CancelamentoParcelamento) resultList.get(0);
        }
        return null;
    }

    public ParcelaValorDivida buscarParcelaDoCancelamento(CancelamentoParcelamento cancelamento) {
        String sql = "select pvd.* from parcelavalordivida pvd " +
            "inner join valordivida vd on vd.id = pvd.valordivida_id " +
            "where vd.calculo_id = :cancelamento";
        Query q = em.createNativeQuery(sql, ParcelaValorDivida.class);
        q.setParameter("cancelamento", cancelamento.getId());
        q.setMaxResults(1);
        List resultList = q.getResultList();
        if (!resultList.isEmpty()) {
            return (ParcelaValorDivida) resultList.get(0);
        }
        return null;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void corrigirTributosDoCancelamento(CancelamentoParcelamento cancelamentoParcelamento) {
        ParcelasCancelamentoParcelamento novaParcela = cancelamentoParcelamento.getNovaParcelaAbertoComValorResidual();

        if (novaParcela != null) {
            ParcelaValorDivida parcelaValorDivida = removerItensRepetidosDaParcela(cancelamentoParcelamento);
            corrigirTributoDaParcela(parcelaValorDivida, Tributo.TipoTributo.IMPOSTO, novaParcela.getValorImposto());
            corrigirTributoDaParcela(parcelaValorDivida, Tributo.TipoTributo.TAXA, novaParcela.getValorTaxa());
            corrigirTributoDaParcela(parcelaValorDivida, Tributo.TipoTributo.JUROS, novaParcela.getValorJuros());
            corrigirTributoDaParcela(parcelaValorDivida, Tributo.TipoTributo.MULTA, novaParcela.getValorMulta());
            corrigirTributoDaParcela(parcelaValorDivida, Tributo.TipoTributo.CORRECAO, novaParcela.getValorCorrecao());
            corrigirTributoDaParcela(parcelaValorDivida, Tributo.TipoTributo.HONORARIOS, novaParcela.getValorHonorarios());
            em.merge(parcelaValorDivida);
        }
    }

    private void corrigirTributoDaParcela(ParcelaValorDivida parcelaValorDivida, Tributo.TipoTributo tipoTributo, BigDecimal valor) {
        BigDecimal valorPorTipoDeTributo = getValorPorTipoDeTributo(parcelaValorDivida.getItensParcelaValorDivida(), tipoTributo);
        for (ItemParcelaValorDivida itemParcelaValorDivida : parcelaValorDivida.getItensParcelaValorDivida()) {
            if (itemParcelaValorDivida.getTributo().getTipoTributo().equals(tipoTributo)) {
                BigDecimal valorProporcional = valor.multiply(itemParcelaValorDivida.getValor()).divide(valorPorTipoDeTributo, 6, RoundingMode.HALF_UP);
                itemParcelaValorDivida.setValor(valorProporcional.setScale(2, RoundingMode.HALF_UP));
            }
        }
    }

    private BigDecimal getValorPorTipoDeTributo(List<ItemParcelaValorDivida> itensParcelaValorDivida, Tributo.TipoTributo tipoTributo) {
        BigDecimal valor = BigDecimal.ZERO;
        for (ItemParcelaValorDivida itemParcelaValorDivida : itensParcelaValorDivida) {
            if (itemParcelaValorDivida.getTributo().getTipoTributo().equals(tipoTributo)) {
                valor = valor.add(itemParcelaValorDivida.getValor());
            }
        }
        return valor;
    }

    private ParcelaValorDivida removerItensRepetidosDaParcela(CancelamentoParcelamento cancelamentoParcelamento) {
        ParcelaValorDivida parcelaValorDivida = buscarParcelaDoCancelamento(cancelamentoParcelamento);
        List<ItemParcelaValorDivida> itensRepetido = Lists.newArrayList();
        List<ItemParcelaValorDivida> itensDaParcela = Lists.newArrayList();
        for (ItemParcelaValorDivida itemParcelaValorDivida : parcelaValorDivida.getItensParcelaValorDivida()) {
            boolean jaTem = false;
            for (ItemParcelaValorDivida item : itensDaParcela) {
                if (item.getTributo().equals(itemParcelaValorDivida.getTributo())) {
                    jaTem = true;
                }
            }
            if (jaTem) {
                itensRepetido.add(itemParcelaValorDivida);
            } else {
                itensDaParcela.add(itemParcelaValorDivida);
            }
        }
        for (ItemParcelaValorDivida itemRepetido : itensRepetido) {
            parcelaValorDivida.getItensParcelaValorDivida().remove(itemRepetido);
            em.remove(itemRepetido);
        }
        return parcelaValorDivida;
    }

    public List<BigDecimal> buscarIdsCancelamentos() {
        String sql = "select distinct canc.id from CancelamentoParcelamento canc " +
            "inner join PARCELASCANCELPARCELAMENTO parc on parc.CANCELAMENTOPARCELAMENTO_ID = canc.id " +
            "where parc.TIPOPARCELACANCELAMENTO = 'PARCELAS_ABATIDAS' " +
            "order by id desc";
        Query q = em.createNativeQuery(sql);
        return q.getResultList();
    }

    public List<BigDecimal> buscarIdsCancelamentosQueNaoGerouResidual() {
        String sql = "select cp.id from CANCELAMENTOPARCELAMENTO cp " +
            "inner join calculo cal on cal.id = cp.id " +
            "inner join valordivida vd on vd.calculo_id = cal.id " +
            "where not exists (select pvd.id from parcelavalordivida pvd where pvd.valordivida_id = vd.id) " +
            "order by cp.id";
        Query q = em.createNativeQuery(sql);
        return q.getResultList();
    }

    public void criarParcelaValorDivida(CancelamentoParcelamento cancelamentoParcelamento, ValorDivida valorDivida) {
        ParcelaValorDivida parcelaValorDivida = new ParcelaValorDivida();
        parcelaValorDivida.setValorDivida(valorDivida);
        parcelaValorDivida.setValor(valorDivida.getValor());
        parcelaValorDivida.setPercentualValorTotal(BigDecimal.valueOf(100));
        parcelaValorDivida.setDividaAtiva(cancelamentoParcelamento.getParcelaValorDivida().getDividaAtiva());
        parcelaValorDivida.setDividaAtivaAjuizada(cancelamentoParcelamento.getParcelaValorDivida().getDividaAtivaAjuizada());
        parcelaValorDivida.setVencimento(cancelamentoParcelamento.getParcelaValorDivida().getVencimento());
        parcelaValorDivida.setOpcaoPagamento(cancelamentoParcelamento.getParcelaValorDivida().getOpcaoPagamento());
        parcelaValorDivida.setSequenciaParcela(cancelamentoParcelamento.getParcelaValorDivida().getSequenciaParcela());
        parcelaValorDivida.setQuantidadeParcela(cancelamentoParcelamento.getParcelaValorDivida().getQuantidadeParcela());
        parcelaValorDivida.setDataRegistro(cancelamentoParcelamento.getDataCancelamento());
        parcelaValorDivida.setDataPrescricao(cancelamentoParcelamento.getParcelaValorDivida().getDataPrescricao());

        criarSituacaoParcelaValorDivida(cancelamentoParcelamento, parcelaValorDivida);
        criarItemParcelaValorDivida(valorDivida, parcelaValorDivida);

        valorDivida.getParcelaValorDividas().add(parcelaValorDivida);
    }

    private void criarItemParcelaValorDivida(ValorDivida valorDivida, ParcelaValorDivida parcelaValorDivida) {
        for (ItemValorDivida itemValorDivida : valorDivida.getItemValorDividas()) {
            ItemParcelaValorDivida item = new ItemParcelaValorDivida();
            item.setItemValorDivida(itemValorDivida);
            item.setParcelaValorDivida(parcelaValorDivida);
            item.setValor(itemValorDivida.getValor());
            parcelaValorDivida.getItensParcelaValorDivida().add(item);
        }
    }

    private void criarSituacaoParcelaValorDivida(CancelamentoParcelamento cancelamentoParcelamento, ParcelaValorDivida parcelaValorDivida) {
        SituacaoParcelaValorDivida situacao = new SituacaoParcelaValorDivida();
        situacao.setParcela(parcelaValorDivida);
        situacao.setDataLancamento(parcelaValorDivida.getDataRegistro());
        situacao.setSaldo(parcelaValorDivida.getValor());
        situacao.setReferencia(cancelamentoParcelamento.getReferencia());
        situacao.setInconsistente(false);
        situacao.setSituacaoParcela(SituacaoParcela.EM_ABERTO);
        parcelaValorDivida.getSituacoes().add(situacao);
    }

    public void criarItemValorDivida(CancelamentoParcelamento cancelamentoParcelamento, ValorDivida valorDivida) {
        for (ItemCancelamentoParcelamento item : cancelamentoParcelamento.getItens()) {
            ItemValorDivida itemValorDivida = new ItemValorDivida();
            itemValorDivida.setValorDivida(valorDivida);
            itemValorDivida.setTributo(item.getTributo());
            itemValorDivida.setValor(item.getValor());
            itemValorDivida.setIsento(false);
            valorDivida.getItemValorDividas().add(itemValorDivida);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void corrigirCancelamentoQuandoNaoGerouDebitoResidual(CancelamentoParcelamento cancelamentoParcelamento, ValorDivida valorDivida) {
        try {
            if (valorDivida == null) {
                valorDivida = new ValorDivida();
                valorDivida.setCalculo(cancelamentoParcelamento);
                valorDivida.setValor(cancelamentoParcelamento.getValorReal());
                valorDivida.setDivida(cancelamentoParcelamento.getParcelaValorDivida().getValorDivida().getDivida());
                valorDivida.setEmissao(cancelamentoParcelamento.getDataCancelamento());
                valorDivida.setExercicio(cancelamentoParcelamento.getParcelaValorDivida().getValorDivida().getExercicio());

                criarItemValorDivida(cancelamentoParcelamento, valorDivida);
                criarParcelaValorDivida(cancelamentoParcelamento, valorDivida);

                getDividaAtivaDAO().persisteValorDivida(valorDivida, false);
            } else {
                if (valorDivida.getItemValorDividas().isEmpty()) {
                    criarItemValorDivida(cancelamentoParcelamento, valorDivida);
                    getDividaAtivaDAO().persisteItemValorDivida(valorDivida.getItemValorDividas());
                }
                if (valorDivida.getParcelaValorDividas().isEmpty()) {
                    criarParcelaValorDivida(cancelamentoParcelamento, valorDivida);
                    getDividaAtivaDAO().persisteParcelaValorDivida(valorDivida.getParcelaValorDividas(), false);
                }
            }
            corrigirReferenciaDasParcelas(cancelamentoParcelamento);
            corrigirTributosDoCancelamento(cancelamentoParcelamento);
        } catch (Exception ex) {
            logger.error("Erro ao corrigir o cancelamento do parcelamento: {}", ex);
        }
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void corrigirReferenciaDasParcelas(CancelamentoParcelamento cancelamentoParcelamento) {
        for (ParcelasCancelamentoParcelamento abatida : cancelamentoParcelamento.getParcelasPorTipo(ParcelasCancelamentoParcelamento.TipoParcelaCancelamento.PARCELAS_ABATIDAS)) {
            ParcelaValorDivida parcela = consultaDebitoFacade.recuperaParcela(abatida.getParcela().getId());
            getDividaAtivaDAO().mergeReferenciaSituacaoParcelaValorDivida(parcela.getSituacaoAtual().getId(), abatida.getReferencia());
        }
        ParcelaValorDivida parcelaCancelamento = buscarParcelaDoCancelamento(cancelamentoParcelamento);
        if (parcelaCancelamento != null) {
            getDividaAtivaDAO().mergeReferenciaSituacaoParcelaValorDivida(parcelaCancelamento.getSituacaoAtual().getId(), cancelamentoParcelamento.getReferencia());
        }
    }
}

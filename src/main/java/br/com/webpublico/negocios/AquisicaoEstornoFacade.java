package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoAquisicao;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.SituacaoRequisicaoCompra;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigoPatrimonio;
import br.com.webpublico.util.AssistenteMovimentacaoBens;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * Created by mga on 24/04/2018.
 */
@Stateless
public class AquisicaoEstornoFacade extends AbstractFacade<AquisicaoEstorno> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @EJB
    private BemFacade bemFacade;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private EfetivacaoAquisicaoFacade efetivacaoAquisicaoFacade;
    @EJB
    private RequisicaoDeCompraFacade requisicaoDeCompraFacade;
    @EJB
    private SingletonGeradorCodigoPatrimonio singletonGeradorCodigoPatrimonio;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;

    public AquisicaoEstornoFacade() {
        super(AquisicaoEstorno.class);
    }

    @Override
    public AquisicaoEstorno recuperar(Object id) {
        AquisicaoEstorno entity = super.recuperar(id);
        if (entity.getDetentorArquivoComposicao() != null) {
            entity.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return entity;
    }

    @Asynchronous
    @TransactionTimeout(unit = TimeUnit.HOURS, value = 8)
    public Future<AquisicaoEstorno> salvarEstorno(AquisicaoEstorno entity, List<ItemAquisicao> itensAquisicao, AssistenteMovimentacaoBens assistente) {
        try {
            criarItemAquisicaoEventoBemEstorno(entity, itensAquisicao, assistente);
            estornandoItensAquisicao(itensAquisicao, assistente);

            assistente.zerarContadoresProcesso();
            assistente.setDescricaoProcesso("Persistindo Registros...");
            estornarAquisicao(entity);

            requisicaoDeCompraFacade.movimentarSituacaoRequisicaoCompra(entity.getAquisicao().getSolicitacaoAquisicao().getRequisicaoDeCompra(), SituacaoRequisicaoCompra.RECUSADA);
            if (entity.getNumero() == null) {
                entity.setNumero(singletonGeradorCodigo.getProximoCodigo(AquisicaoEstorno.class, "numero"));
            }
            entity = em.merge(entity);
            assistente.setSelecionado(entity);
            assistente.setDescricaoProcesso(" ");
            singletonGeradorCodigoPatrimonio.desbloquear(Aquisicao.class);
        } catch (Exception ex) {
            singletonGeradorCodigoPatrimonio.desbloquear(Aquisicao.class);
            logger.error("Erro ao salvar estorno de aquisição de bens móveis. " + ex.getMessage());
        }
        return new AsyncResult<>(entity);
    }

    private void estornarAquisicao(AquisicaoEstorno entity) {
        entity.getAquisicao().setSituacao(SituacaoAquisicao.ESTORNADO);
        entity.setAquisicao(em.merge(entity.getAquisicao()));
    }

    private void estornandoItensAquisicao(List<ItemAquisicao> itensAquisicao, AssistenteMovimentacaoBens assistente) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Estornando Itens da Aquisição de Bens...");
        assistente.setTotal(itensAquisicao.size());

        List<Number> idsBem = Lists.newArrayList();
        for (ItemAquisicao itemAquisicao : itensAquisicao) {
            itemAquisicao.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
            idsBem.add(itemAquisicao.getBem().getId());
            em.merge(itemAquisicao);
            assistente.conta();
        }
        configMovimentacaoBemFacade.bloquearBens(assistente.getConfigMovimentacaoBem(), idsBem);
    }

    private void criarItemAquisicaoEventoBemEstorno(AquisicaoEstorno entity, List<ItemAquisicao> itensAquisicao, AssistenteMovimentacaoBens assistente) {
        assistente.zerarContadoresProcesso();
        assistente.setDescricaoProcesso("Criando Evento Bem para Estorno do Itens da Aquisição...");
        assistente.setTotal(itensAquisicao.size());

        for (ItemAquisicao itemAquisicao : itensAquisicao) {
            ItemAquisicaoEstorno itemEstorno = criarItemEstornoAquisicao(entity, assistente, itemAquisicao);
            entity.getItensEstornoAquisicao().add(itemEstorno);
            assistente.conta();
        }
    }

    private ItemAquisicaoEstorno criarItemEstornoAquisicao(AquisicaoEstorno entity, AssistenteMovimentacaoBens assistente, ItemAquisicao itemAquisicao) {
        ItemAquisicaoEstorno itemEstorno = new ItemAquisicaoEstorno();
        itemEstorno.setSituacaoEventoBem(SituacaoEventoBem.BLOQUEADO);
        itemEstorno.setBem(itemAquisicao.getBem());
        itemEstorno.setAquisicaoEstorno(entity);
        Date dataLancamento = DataUtil.getDataComHoraAtual(assistente.getDataLancamento());
        itemEstorno.setDataLancamento(dataLancamento);
        itemEstorno.setValorDoLancamento(itemAquisicao.getValorDoLancamento());
        itemEstorno.setDetentorArquivoComposicao(entity.getDetentorArquivoComposicao());

        EstadoBem ultimoEstadoDoBem = bemFacade.recuperarUltimoEstadoDoBem(itemAquisicao.getBem());
        EstadoBem estadoBemResultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstadoDoBem);
        estadoBemResultante.setValorOriginal(BigDecimal.ZERO);
        estadoBemResultante = em.merge(estadoBemResultante);

        itemEstorno.setEstadoInicial(ultimoEstadoDoBem);
        itemEstorno.setEstadoResultante(estadoBemResultante);
        itemEstorno.setItemAquisicao(itemAquisicao);
        return itemEstorno;
    }

    public int quantidadeItensAquisicao(Aquisicao entity) {
        Criteria criteria = getCriteriaAquisicao(entity);
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.uniqueResult()).intValue();
    }

    private Criteria getCriteriaAquisicao(Aquisicao entity) {
        Session session = em.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ItemAquisicao.class);
        criteria.add(Restrictions.eq("aquisicao", entity));
        return criteria;
    }

    @SuppressWarnings("unchecked")
    public List<ItemAquisicao> recuperarItemAquisicaoCriteria(int primeiroRegistro, int qtdeRegistro, Aquisicao entity) {
        Criteria criteria = getCriteriaAquisicao(entity);
        criteria.setFirstResult(primeiroRegistro);
        criteria.setMaxResults(qtdeRegistro);
        return criteria.list();
    }

    public int quantidadeItensItemAquisicaoEstorno(AquisicaoEstorno entity) {
        Criteria criteria = getCriteriaItensAquisicaoEstorno(entity);
        criteria.setProjection(Projections.rowCount());
        return ((Number) criteria.uniqueResult()).intValue();
    }

    private Criteria getCriteriaItensAquisicaoEstorno(AquisicaoEstorno entity) {
        Session session = em.unwrap(Session.class);
        Criteria criteria = session.createCriteria(ItemAquisicaoEstorno.class);
        criteria.add(Restrictions.eq("aquisicaoEstorno", entity));
        return criteria;
    }

    @SuppressWarnings("unchecked")
    public List<ItemAquisicaoEstorno> recuperarItemAquisicaoEstornoCriteria(int primeiroRegistro, int qtdeRegistro, AquisicaoEstorno entity) {
        Criteria criteria = getCriteriaItensAquisicaoEstorno(entity);
        criteria.setFirstResult(primeiroRegistro);
        criteria.setMaxResults(qtdeRegistro);
        return criteria.list();
    }


    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public EfetivacaoAquisicaoFacade getEfetivacaoAquisicaoFacade() {
        return efetivacaoAquisicaoFacade;
    }

    public SingletonGeradorCodigoPatrimonio getSingletonGeradorCodigoPatrimonio() {
        return singletonGeradorCodigoPatrimonio;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }
}

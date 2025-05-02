package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 11/06/14
 * Time: 16:59
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class AvaliacaoProrrogacaoCessaoFacade extends AbstractFacade<AvaliacaoSolicitacaoProrrogacaoCessao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private LoteCessaoFacade loteCessaoFacade;
    @EJB
    private SolicitacaoProrrogacaoCessaoFacade solicitacaoProrrogacaoCessaoFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;

    public AvaliacaoProrrogacaoCessaoFacade() {
        super(AvaliacaoSolicitacaoProrrogacaoCessao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public AvaliacaoSolicitacaoProrrogacaoCessao recuperar(Object id) {
        AvaliacaoSolicitacaoProrrogacaoCessao avaliacao = em.find(AvaliacaoSolicitacaoProrrogacaoCessao.class, id);
        if (avaliacao.getDetentorArquivoComposicao() != null) {
            avaliacao.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        avaliacao.getItensAvaliacao().size();
        return avaliacao;
    }

    public AvaliacaoSolicitacaoProrrogacaoCessao salvarAvaliacao(AvaliacaoSolicitacaoProrrogacaoCessao avaliacao, List<Cessao> cessoes) {
        if (avaliacao.getNumero() == null) {
            avaliacao.setNumero(singletonGeradorCodigo.getProximoCodigo(AvaliacaoSolicitacaoProrrogacaoCessao.class, "numero"));
        }
        criarItemAvaliacaoProrrogacao(avaliacao, cessoes);
        if (AvaliacaoSolicitacaoProrrogacaoCessao.SituacaoAvaliacaoProrrogacaoCessao.APROVADA.equals(avaliacao.getSituacaoAvaliacaoProrrogacao())) {
            avaliacao = em.merge(avaliacao);
            PrazoCessao ultimoPrazo = ultimoPrazoCessao(avaliacao.getSolicitaProrrogacaoCessao().getLoteCessao());
            PrazoCessao novoPrazo = criarNovoPrazo(avaliacao, ultimoPrazo);
            ProrrogacaoCessao prorrogacaoCessao = criarProrrogacaoCessao(avaliacao, novoPrazo);
            ultimoPrazo.setProrrogacaoCessao(prorrogacaoCessao);
            em.merge(ultimoPrazo);
        } else {
            avaliacao = em.merge(avaliacao);
        }
        rejeitarEventoSolicitacaoProrrogacao(avaliacao);
        return avaliacao;
    }

    private void criarItemAvaliacaoProrrogacao(AvaliacaoSolicitacaoProrrogacaoCessao selecionado, List<Cessao> cessoes) {
        boolean aprovada = AvaliacaoSolicitacaoProrrogacaoCessao.SituacaoAvaliacaoProrrogacaoCessao.APROVADA.equals(selecionado.getSituacaoAvaliacaoProrrogacao());
        selecionado.setItensAvaliacao(new ArrayList<ItemAvaliacaoProrrogacao>());
        List<Number> bens = Lists.newArrayList();

        for (Cessao cessao : cessoes) {
            EstadoBem ultimoEstadoDoBem = bemFacade.recuperarUltimoEstadoDoBem(cessao.getBem());
            EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstadoDoBem));
            Date dataLancamento = DataUtil.getDataComHoraAtual(selecionado.getDataAvaliacao());
            ItemAvaliacaoProrrogacao item = new ItemAvaliacaoProrrogacao(selecionado, ultimoEstadoDoBem, estadoResultante, dataLancamento, cessao);
            item.setSituacaoEventoBem(aprovada ? SituacaoEventoBem.FINALIZADO : SituacaoEventoBem.RECUSADO);
            selecionado.getItensAvaliacao().add(item);
            bens.add(cessao.getBem().getId());
        }
        movimentarBloqueioBens(selecionado, aprovada, bens);
    }

    private void movimentarBloqueioBens(AvaliacaoSolicitacaoProrrogacaoCessao selecionado, Boolean aprovada, List<Number> bens) {
        ConfigMovimentacaoBem configMovimentacaoBem = configMovimentacaoBemFacade.buscarConfiguracaoMovimentacaoBem(selecionado.getDataAvaliacao(), OperacaoMovimentacaoBem.AVALIACAO_PRORROGACAO_CESSAO_BEM);
        if (aprovada) {
            configMovimentacaoBemFacade.bloquearBens(configMovimentacaoBem, bens);
        } else {
            configMovimentacaoBemFacade.desbloquearBensRejeicaoDuranteProcesso(configMovimentacaoBem, bens);
        }
    }

    private void rejeitarEventoSolicitacaoProrrogacao(AvaliacaoSolicitacaoProrrogacaoCessao selecionado) {
        boolean aprovada = AvaliacaoSolicitacaoProrrogacaoCessao.SituacaoAvaliacaoProrrogacaoCessao.APROVADA.equals(selecionado.getSituacaoAvaliacaoProrrogacao());
        SolicitacaoProrrogacaoCessao solicitacao = solicitacaoProrrogacaoCessaoFacade.recuperar(selecionado.getSolicitaProrrogacaoCessao().getId());
        for (ItemSolicitacaoProrrogacao itemSolicitacao : solicitacao.getProrrogacoesCessao()) {
            itemSolicitacao.setSituacaoEventoBem(aprovada ? SituacaoEventoBem.FINALIZADO : SituacaoEventoBem.RECUSADO);
            em.merge(itemSolicitacao);
        }
    }

    private PrazoCessao criarNovoPrazo(AvaliacaoSolicitacaoProrrogacaoCessao avaliacao, PrazoCessao ultimoPrazo) {
        PrazoCessao novoPrazo = new PrazoCessao();
        novoPrazo.setLoteCessao(em.find(LoteCessao.class, avaliacao.getSolicitaProrrogacaoCessao().getLoteCessao().getId()));
        novoPrazo.setInicioDoPrazo(ultimoPrazo.getInicioDoPrazo());
        novoPrazo.setFimDoPrazo(avaliacao.getSolicitaProrrogacaoCessao().getNovaDataFinal());
        novoPrazo = em.merge(novoPrazo);
        return novoPrazo;
    }

    private ProrrogacaoCessao criarProrrogacaoCessao(AvaliacaoSolicitacaoProrrogacaoCessao avaliacao, PrazoCessao novoPrazo) {
        ProrrogacaoCessao prorrogacaoCessao = new ProrrogacaoCessao();
        prorrogacaoCessao.setAvaliacaoProrrogacaoCessao(avaliacao);
        prorrogacaoCessao.setNovoPrazo(novoPrazo);
        prorrogacaoCessao = em.merge(prorrogacaoCessao);
        return prorrogacaoCessao;
    }

    public PrazoCessao ultimoPrazoCessao(LoteCessao lote) {
        String sql = "select pz.* " +
            "       from prazocessao pz " +
            "      where pz.id = (select max(id) from prazocessao p where p.lotecessao_id = :lote)";

        Query q = em.createNativeQuery(sql, PrazoCessao.class);
        q.setParameter("lote", lote.getId());

        return (PrazoCessao) q.getSingleResult();
    }

    public AvaliacaoSolicitacaoProrrogacaoCessao recuperarAvaliacaoDaSolicitacao(SolicitacaoProrrogacaoCessao solicitacaoProrrogacaoCessao) {
        Query q = em.createQuery("from AvaliacaoSolicitacaoProrrogacaoCessao where solicitaProrrogacaoCessao = :solicitacao").setParameter("solicitacao", solicitacaoProrrogacaoCessao);

        List resultList = q.getResultList();
        if (resultList.isEmpty()) {
            return null;
        }

        return (AvaliacaoSolicitacaoProrrogacaoCessao) resultList.get(0);
    }

    public AvaliacaoSolicitacaoProrrogacaoCessao recuperarAvaliacaoProrrogacaoPorLoteCessao(LoteCessao loteCessao) {
        String sql = "select " +
            "  av.* " +
            "from solicitaprorrogacaocessao sol " +
            "inner join avalisoliprorrogacaocessao av on av.solicitaprorrogacaocessao_id = sol.id " +
            "where sol.lotecessao_id = :idLote ";
        Query q = em.createNativeQuery(sql, AvaliacaoSolicitacaoProrrogacaoCessao.class);
        q.setParameter("idLote", loteCessao.getId());
        if (!q.getResultList().isEmpty()) {
            return (AvaliacaoSolicitacaoProrrogacaoCessao) q.getResultList().get(0);
        }
        return null;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public LoteCessaoFacade getLoteCessaoFacade() {
        return loteCessaoFacade;
    }

    public SolicitacaoProrrogacaoCessaoFacade getSolicitacaoProrrogacaoCessaoFacade() {
        return solicitacaoProrrogacaoCessaoFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }
}

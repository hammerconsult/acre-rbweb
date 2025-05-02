package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.TipoCessao;
import br.com.webpublico.enums.administrativo.OperacaoMovimentacaoBem;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import com.google.common.collect.Lists;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 11/06/14
 * Time: 16:44
 * To change this template use File | Settings | File Templates.
 */
@Stateless
public class SolicitacaoProrrogacaoCessaoFacade extends AbstractFacade<SolicitacaoProrrogacaoCessao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private LoteCessaoFacade loteCessaoFacade;
    @EJB
    private AvaliacaoProrrogacaoCessaoFacade avaliacaoProrrogacaoCessaoFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;

    public SolicitacaoProrrogacaoCessaoFacade() {
        super(SolicitacaoProrrogacaoCessao.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public SolicitacaoProrrogacaoCessao recuperar(Object id) {
        SolicitacaoProrrogacaoCessao solicitacaoProrrogacaoCessao = em.find(SolicitacaoProrrogacaoCessao.class, id);
        solicitacaoProrrogacaoCessao.getProrrogacoesCessao().size();
        if (solicitacaoProrrogacaoCessao.getDetentorArquivoComposicao() != null) {
            solicitacaoProrrogacaoCessao.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        if (solicitacaoProrrogacaoCessao.getLoteCessao() != null) {
            solicitacaoProrrogacaoCessao.getLoteCessao().getListaDePrazos().size();
            solicitacaoProrrogacaoCessao.getLoteCessao().getListaDeSolicitacaoProrrogacaoCessao().size();
        }
        return solicitacaoProrrogacaoCessao;
    }


    @Override
    public void remover(SolicitacaoProrrogacaoCessao entity) {
        if (entity.getId() != null) {
            ConfigMovimentacaoBem configMovimentacaoBem = configMovimentacaoBemFacade.buscarConfiguracaoMovimentacaoBem(entity.getDataDaSolicitacao(), OperacaoMovimentacaoBem.SOLICITACAO_PRORROGACAO_CESSAO_BEM);
            List<Number> bens = buscarIdsBemSolicitacaoProrrogacaoCessao(entity);
            configMovimentacaoBemFacade.deletarMovimentoBloqueioBem(configMovimentacaoBem, bens);
        }
        super.remover(entity);
    }

    public SolicitacaoProrrogacaoCessao salvarRetornando(SolicitacaoProrrogacaoCessao entity) {
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(SolicitacaoProrrogacaoCessao.class, "numero"));
        }
        List<Number> idsBens = Lists.newArrayList();
        for (Cessao cessao : entity.getLoteCessao().getListaDeCessoes()) {
            EstadoBem ultimoEstadoBem = bemFacade.recuperarUltimoEstadoDoBem(cessao.getBem());
            ItemSolicitacaoProrrogacao itemProrrogacaoCessao = new ItemSolicitacaoProrrogacao(cessao.getBem(), entity, ultimoEstadoBem, entity.getDataDaSolicitacao(), cessao);
            entity.getProrrogacoesCessao().add(itemProrrogacaoCessao);
            idsBens.add(cessao.getBem().getId());
        }
        ConfigMovimentacaoBem configMovimentacaoBem = configMovimentacaoBemFacade.buscarConfiguracaoMovimentacaoBem(entity.getDataDaSolicitacao(), OperacaoMovimentacaoBem.SOLICITACAO_PRORROGACAO_CESSAO_BEM);
        configMovimentacaoBemFacade.bloquearBens(configMovimentacaoBem, idsBens);
        return em.merge(entity);
    }

    public List<Number> buscarIdsBemSolicitacaoProrrogacaoCessao(SolicitacaoProrrogacaoCessao entity) {
        String sql = "" +
            " select " +
            "   ev.bem_id " +
            " from itemsolicitacaoprorrogacao item " +
            "   inner join eventobem ev on ev.id = item.id " +
            "   inner join solicitaprorrogacaocessao sol on sol.id = item.solicitacaoprorrogacao_id " +
            " where sol.id = :idSolicitacao ";
        Query q = em.createNativeQuery(sql);
        q.setParameter("idSolicitacao", entity.getId());
        return ((List<Number>) q.getResultList());
    }

    public List<SolicitacaoProrrogacaoCessao> completarSolicitacaoSemAvaliacao(String filtro, TipoCessao tipoCessao) {
        String sql = " " +
            " select DISTINCT sol.* " +
            "  from SOLICITAPRORROGACAOCESSAO sol" +
            "   inner join lotecessao lote on lote.id = sol.lotecessao_id" +
            "   INNER JOIN UNIDADEORGANIZACIONAL UO ON UO.ID = LOTE.UNIDADEORIGEM_ID " +
            "   INNER JOIN USUARIOUNIDADEORGANIZACIO UU ON UU.UNIDADEORGANIZACIONAL_ID = UO.ID " +
            "   left join  AVALISOLIPRORROGACAOCESSAO av on sol.id = av.solicitaProrrogacaoCessao_id " +
            " where (av.id is null)" +
            "  and ((to_char(sol.numero)                      like :filtro)" +
            "              or (to_char(sol.dataDaSolicitacao) like :filtro)" +
            "              or (to_char(lote.codigo)           like :filtro)" +
            "              or (lower(lote.descricao)          like :filtro)" +
            "             )" +
            "  AND UU.GESTORPATRIMONIO = :GESTOR " +
            "  AND UU.USUARIOSISTEMA_ID = :USUARIO_ID " +
            "  AND sol.tipocessao = :tipoCessao " +
            "  AND EXISTS (SELECT 1 " +
            "                FROM CESSAO CS" +
            "              WHERE CS.LOTECESSAO_ID = LOTE.ID" +
            "                AND NOT EXISTS( SELECT 1 " +
            "                                  FROM CESSAODEVOLUCAO CD " +
            "                                INNER JOIN EVENTOBEM EV ON EV.ID = CD.ID" +
            "                                WHERE CD.CESSAO_ID = CS.ID" +
            "                                  AND EV.SITUACAOEVENTOBEM = :SIT" +
            "                              )" +
            "             )";
        Query q = em.createNativeQuery(sql, SolicitacaoProrrogacaoCessao.class);
        q.setParameter("filtro", "%" + filtro.toLowerCase().trim() + "%");
        q.setParameter("GESTOR", Boolean.TRUE);
        q.setParameter("tipoCessao", tipoCessao.name());
        q.setParameter("SIT", SituacaoEventoBem.FINALIZADO.name());
        q.setParameter("USUARIO_ID", sistemaFacade.getUsuarioCorrente().getId());
        return q.getResultList();
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public void setSistemaFacade(SistemaFacade sistemaFacade) {
        this.sistemaFacade = sistemaFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public void setSingletonGeradorCodigo(SingletonGeradorCodigo singletonGeradorCodigo) {
        this.singletonGeradorCodigo = singletonGeradorCodigo;
    }

    public LoteCessaoFacade getLoteCessaoFacade() {
        return loteCessaoFacade;
    }

    public void setLoteCessaoFacade(LoteCessaoFacade loteCessaoFacade) {
        this.loteCessaoFacade = loteCessaoFacade;
    }

    public AvaliacaoProrrogacaoCessaoFacade getAvaliacaoProrrogacaoCessaoFacade() {
        return avaliacaoProrrogacaoCessaoFacade;
    }

    public void setAvaliacaoProrrogacaoCessaoFacade(AvaliacaoProrrogacaoCessaoFacade avaliacaoProrrogacaoCessaoFacade) {
        this.avaliacaoProrrogacaoCessaoFacade = avaliacaoProrrogacaoCessaoFacade;
    }

    public AvaliacaoSolicitacaoProrrogacaoCessao recuperarAvaliacaoSolicitacaoProrrogacaoCessao(SolicitacaoProrrogacaoCessao solicitacao) {
        String sql = "SELECT * " +
            "FROM AVALISOLIPRORROGACAOCESSAO avaliacao where avaliacao.SOLICITAPRORROGACAOCESSAO_ID = :solicitacao";
        Query q = em.createNativeQuery(sql, AvaliacaoSolicitacaoProrrogacaoCessao.class);
        q.setParameter("solicitacao", solicitacao.getId());
        if (!q.getResultList().isEmpty()) {
            return (AvaliacaoSolicitacaoProrrogacaoCessao) q.getResultList().get(0);
        }
        return new AvaliacaoSolicitacaoProrrogacaoCessao();
    }

    public SolicitacaoProrrogacaoCessao recuperarSolicitacaoProrrogacaoPorLoteCessao(LoteCessao loteCessao) {
        String sql = "select " +
            "  sol.* " +
            " from solicitaprorrogacaocessao sol " +
            " where sol.lotecessao_id = :idLote ";
        Query q = em.createNativeQuery(sql, SolicitacaoProrrogacaoCessao.class);
        q.setParameter("idLote", loteCessao.getId());
        if (!q.getResultList().isEmpty()) {
            return (SolicitacaoProrrogacaoCessao) q.getResultList().get(0);
        }
        return null;
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public ParametroPatrimonioFacade getParametroPatrimonioFacade() {
        return parametroPatrimonioFacade;
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }
}

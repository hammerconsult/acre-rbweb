package br.com.webpublico.negocios;

import br.com.webpublico.entidades.*;
import br.com.webpublico.enums.SituacaoEventoBem;
import br.com.webpublico.enums.SituacaoLoteCessao;
import br.com.webpublico.enums.TipoCessao;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import com.google.common.collect.Lists;
import org.jboss.ejb3.annotation.TransactionTimeout;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 * User: Desenvolvimento
 * Date: 15/05/14
 * Time: 16:35
 * To change this template use File | Settings | File Templates.
 */

@Stateless
public class LoteEfetivacaoCessaoFacade extends AbstractFacade<LoteEfetivacaoCessao> {

    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    @EJB
    private BemFacade bemFacade;
    @EJB
    private LoteCessaoFacade loteCessaoFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private UsuarioSistemaFacade usuarioSistemaFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private InventarioBensMoveisFacade inventarioBensMoveisFacade;
    @EJB
    private PessoaFisicaFacade pessoaFisicaFacade;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    @Override
    public LoteEfetivacaoCessao recuperar(Object id) {
        LoteEfetivacaoCessao lote = super.recuperar(id);
        lote.getListaEfetivacaoCessao().size();
        if (lote.getDetentorArquivoComposicao() != null) {
            lote.getDetentorArquivoComposicao().getArquivosComposicao().size();
        }
        return lote;
    }

    public LoteEfetivacaoCessaoFacade() {
        super(LoteEfetivacaoCessao.class);
    }

    public LoteEfetivacaoCessao salvarGerandoEfetivacoes(LoteEfetivacaoCessao entity, List<LoteCessao> lotesSelecionados, ConfigMovimentacaoBem configuracao) {
        for (LoteCessao lote : lotesSelecionados) {
            lote.setListaDeCessoes(loteCessaoFacade.getListaDeCessaoPorLote(lote));
            for (Cessao cessao : lote.getListaDeCessoes()) {
                entity.getListaEfetivacaoCessao().add(criarNovaEfetivacao(entity, cessao, lote.getSituacaoLoteCessao()));
            }
            movimentarBloqueioBens(lote, configuracao);
            em.merge(lote);
        }
        if (entity.getNumero() == null) {
            entity.setNumero(singletonGeradorCodigo.getProximoCodigo(LoteEfetivacaoCessao.class, "numero"));
        }
        return em.merge(entity);
    }

    private EfetivacaoCessao criarNovaEfetivacao(LoteEfetivacaoCessao entity, Cessao cessao, SituacaoLoteCessao situacao) {
        EfetivacaoCessao efetivacaoCessao = new EfetivacaoCessao();
        EstadoBem ultimoEstado = bemFacade.recuperarUltimoEstadoDoBem(cessao.getBem());
        EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(ultimoEstado));
        Date dataLancamento = DataUtil.getDataComHoraAtual(entity.getDataEfetivacao());

        efetivacaoCessao.setEstadoInicial(ultimoEstado);
        efetivacaoCessao.setEstadoResultante(estadoResultante);
        efetivacaoCessao.setDataLancamento(dataLancamento);
        efetivacaoCessao.setBem(cessao.getBem());
        efetivacaoCessao.setLoteEfetivacaoCessao(entity);
        efetivacaoCessao.setCessao(cessao);
        if (entity.getDetentorArquivoComposicao() != null) {
            efetivacaoCessao.setDetentorArquivoComposicao(entity.getDetentorArquivoComposicao());
        }
        if (SituacaoLoteCessao.ACEITA.equals(situacao)) {
            efetivacaoCessao.setSituacaoEventoBem(SituacaoEventoBem.AGUARDANDO_DEVOLUCAO);
            cessao.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
        } else {
            efetivacaoCessao.setSituacaoEventoBem(SituacaoEventoBem.RECUSADO);
            cessao.setSituacaoEventoBem(SituacaoEventoBem.RECUSADO);
        }
        return efetivacaoCessao;
    }

    private void movimentarBloqueioBens(LoteCessao lote, ConfigMovimentacaoBem configuracao) {
        List<Number> bensRecuperados = loteCessaoFacade.buscarIdsBemPorCessao(lote);
        if (!bensRecuperados.isEmpty()) {
            if (lote.foiRecusado()) {
                configMovimentacaoBemFacade.desbloquearBens(configuracao, bensRecuperados);
            } else {
                configMovimentacaoBemFacade.bloquearBens(configuracao, bensRecuperados);
            }
        }
    }


    public List<LoteCessao> recuperarLotesEfetivados(LoteEfetivacaoCessao loteEfetivacaoCessao) {
        String sql = "select distinct lc.* " +
            "from loteefetivacaocessao lec " +
            "inner join efetivacaocessao ec on ec.loteefetivacaocessao_id = lec.id " +
            "inner join cessao ces on ces.id = ec.cessao_id " +
            "inner join lotecessao lc on ces.lotecessao_id = lc.id " +
            "where lec.id = :lote_efetivacao_id";

        Query q = em.createNativeQuery(sql, LoteCessao.class);
        q.setParameter("lote_efetivacao_id", loteEfetivacaoCessao.getId());
        return q.getResultList();
    }

    public LoteEfetivacaoCessao recuperarEfetivacaoCessaoPorLote(LoteCessao loteCessao) {
        String sql = "" +
            " select distinct " +
            "  lote.* " +
            " from loteefetivacaocessao lote " +
            "  inner join efetivacaocessao item on item.loteefetivacaocessao_id = lote.id" +
            "  inner join cessao cs on cs.id = item.cessao_id" +
            "  inner join lotecessao lc on lc.id = cs.lotecessao_id" +
            " where lc.id = :idLoteCessao ";
        Query q = em.createNativeQuery(sql, LoteEfetivacaoCessao.class);
        q.setParameter("idLoteCessao", loteCessao.getId());
        q.setMaxResults(1);
        return (LoteEfetivacaoCessao) q.getSingleResult();
    }

    public List<LoteEfetivacaoCessao> completaLoteCessaoDaEfetivacaoParaDevolucao(String filtro) {
        String sql = "SELECT distinct " +
            "            lote.* " +
            "     FROM LOTEEFETIVACAOCESSAO LOTE " +
            "     INNER JOIN UNIDADEORGANIZACIONAL UND ON UND.ID = LOTE.UNIDADEORGANIZACIONAL_ID " +
            "     WHERE NOT EXISTS (SELECT 1 " +
            "                             FROM LoteCessaoDevolucao devolucao " +
            "                           WHERE devolucao.loteEfetivacaoCessao_id = lote.id) " +
            "       and (LOTE.NUMERO like :filtro or lower(UND.DESCRICAO) like :filtro)";

        Query q = em.createNativeQuery(sql, LoteEfetivacaoCessao.class);
        q.setParameter("filtro", "%" + filtro.trim().toLowerCase() + "%");
        return q.getResultList();
    }

    public BemFacade getBemFacade() {
        return bemFacade;
    }

    public LoteCessaoFacade getLoteCessaoFacade() {
        return loteCessaoFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
    }

    public UsuarioSistemaFacade getUsuarioSistemaFacade() {
        return usuarioSistemaFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public InventarioBensMoveisFacade getInventarioBensMoveisFacade() {
        return inventarioBensMoveisFacade;
    }

    public Long count(String sql) {
        try {
            Query query = em.createNativeQuery(sql);
            return ((BigDecimal) query.getSingleResult()).longValue();
        } catch (NoResultException nre) {
            return 0L;
        }
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public SingletonConcorrenciaPatrimonio getSingletonBloqueioPatrimonio() {
        return singletonBloqueioPatrimonio;
    }
}

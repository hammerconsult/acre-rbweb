package br.com.webpublico.negocios.administrativo.patrimonio;

import br.com.webpublico.entidades.*;
import br.com.webpublico.entidades.administrativo.patrimonio.EfetivacaoReavaliacaoBem;
import br.com.webpublico.entidades.administrativo.patrimonio.LoteEfetivacaoReavaliacaoBem;
import br.com.webpublico.entidades.administrativo.patrimonio.LoteReavaliacaoBem;
import br.com.webpublico.entidades.administrativo.patrimonio.ReavaliacaoBem;
import br.com.webpublico.enums.*;
import br.com.webpublico.negocios.*;
import br.com.webpublico.negocios.tributario.singletons.SingletonConcorrenciaPatrimonio;
import br.com.webpublico.negocios.tributario.singletons.SingletonGeradorCodigo;
import br.com.webpublico.util.DataUtil;
import br.com.webpublico.util.Util;
import com.google.common.collect.Lists;
import org.hibernate.Hibernate;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Date;
import java.util.List;

/**
 * Created by William on 22/10/2015.
 */

@Stateless
public class LoteEfetivacaoReavaliacaoBemFacade extends AbstractFacade<LoteEfetivacaoReavaliacaoBem> {

    @EJB
    private LoteReavaliacaoBemFacade loteReavaliacaoBemFacade;
    @EJB
    private HierarquiaOrganizacionalFacade hierarquiaOrganizacionalFacade;
    @EJB
    private SingletonGeradorCodigo singletonGeradorCodigo;
    @EJB
    private SistemaFacade sistemaFacade;
    @EJB
    private ParametroPatrimonioFacade parametroPatrimonioFacade;
    @EJB
    private BemFacade bemFacade;
    @EJB
    private IntegradorPatrimonialContabilFacade integrador;
    @EJB
    private ConfigMovimentacaoBemFacade configMovimentacaoBemFacade;
    @EJB
    private SingletonConcorrenciaPatrimonio singletonBloqueioPatrimonio;
    @PersistenceContext(unitName = "webpublicoPU")
    private EntityManager em;

    public LoteEfetivacaoReavaliacaoBemFacade() {
        super(LoteEfetivacaoReavaliacaoBem.class);
    }

    @Override
    public LoteEfetivacaoReavaliacaoBem recuperar(Object id) {
        LoteEfetivacaoReavaliacaoBem lote = em.find(LoteEfetivacaoReavaliacaoBem.class, id);
        Hibernate.initialize(lote.getEfetivacoes());
        if (lote.getDetentorArquivoComposicao() != null) {
            Hibernate.initialize(lote.getDetentorArquivoComposicao().getArquivosComposicao());
        }
        HierarquiaOrganizacional hierarquiaDaUnidade = hierarquiaOrganizacionalFacade.getHierarquiaDaUnidade(TipoHierarquiaOrganizacional.ADMINISTRATIVA.name(), lote.getUnidadeOrganizacional(), sistemaFacade.getDataOperacao());
        if (hierarquiaDaUnidade != null) {
            lote.setHierarquiaOrganizacional(hierarquiaDaUnidade);
        }
        return lote;
    }

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }


    public LoteEfetivacaoReavaliacaoBem salvarGerandoReavaliacao(LoteEfetivacaoReavaliacaoBem entity, List<LoteReavaliacaoBem> loteReavaliacaoSelecionados, ConfigMovimentacaoBem configuracao) {
        entity.getEfetivacoes().clear();
        List<Number> bensParaDesbloquear = Lists.newArrayList();
        for (LoteReavaliacaoBem lote : loteReavaliacaoSelecionados) {
            for (ReavaliacaoBem reavaliacaoBem : lote.getReavaliacaoBens()) {
                lote.setSituacaoReavaliacaoBem(SituacaoDaSolicitacao.ACEITA);
                reavaliacaoBem.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
                lote = em.merge(lote);

                Date dataLancamento = DataUtil.getDataComHoraAtual(entity.getDataEfetivacao());
                EfetivacaoReavaliacaoBem efetivacaoReavaliacaoBem = new EfetivacaoReavaliacaoBem();
                efetivacaoReavaliacaoBem.setDataLancamento(dataLancamento);
                efetivacaoReavaliacaoBem.setLote(entity);
                efetivacaoReavaliacaoBem.setReavaliacaoBem(reavaliacaoBem);

                Bem bem = reavaliacaoBem.getBem();

                efetivacaoReavaliacaoBem.setBem(bem);
                efetivacaoReavaliacaoBem.setEstadoInicial(bemFacade.recuperarUltimoEstadoDoBem(bem));
                efetivacaoReavaliacaoBem.setSituacaoEventoBem(SituacaoEventoBem.FINALIZADO);
                efetivacaoReavaliacaoBem.setValorDoLancamento(reavaliacaoBem.getValor());

                EstadoBem resultante = bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(efetivacaoReavaliacaoBem.getEstadoInicial());
                if (entity.isReavaliacaoBemAumentativa()) {
                    efetivacaoReavaliacaoBem.setTipoOperacao(TipoOperacao.DEBITO);
                    resultante.setValorOriginal(resultante.getValorOriginal().add(reavaliacaoBem.getValor()));
                } else {
                    efetivacaoReavaliacaoBem.setTipoOperacao(TipoOperacao.CREDITO);
                    resultante.setValorOriginal(resultante.getValorOriginal().subtract(reavaliacaoBem.getValor()));
                }
                resultante = em.merge(resultante);
                efetivacaoReavaliacaoBem.setEstadoResultante(resultante);
                entity.getEfetivacoes().add(efetivacaoReavaliacaoBem);
                bensParaDesbloquear.add(efetivacaoReavaliacaoBem.getBem().getId());
            }
        }
        if (entity.getCodigo() == null) {
            entity.setCodigo(singletonGeradorCodigo.getProximoCodigo(LoteEfetivacaoReavaliacaoBem.class, "codigo"));
        }
        entity = em.merge(entity);
        contabilizar(entity);
        configMovimentacaoBemFacade.desbloquearBens(configuracao, bensParaDesbloquear);
        return entity;
    }

    public void efetivarLotesRejeitados(LoteEfetivacaoReavaliacaoBem loteEfetivacaoReavaliacaoBem, List<LoteReavaliacaoBem> loteReavaliacaoSelecionadosRejeitados, ConfigMovimentacaoBem configuracao) {
        List<Number> bensParaDesbloquear = Lists.newArrayList();
        for (LoteReavaliacaoBem lotes : loteReavaliacaoSelecionadosRejeitados) {
            for (ReavaliacaoBem reavaliacaoBem : lotes.getReavaliacaoBens()) {
                lotes.setSituacaoReavaliacaoBem(SituacaoDaSolicitacao.RECUSADA);
                reavaliacaoBem.setSituacaoEventoBem(SituacaoEventoBem.RECUSADO);
                em.merge(lotes);

                Date dataLancamento = DataUtil.getDataComHoraAtual(loteEfetivacaoReavaliacaoBem.getDataEfetivacao());
                EfetivacaoReavaliacaoBem efetivacaoReavaliacaoBem = new EfetivacaoReavaliacaoBem();
                efetivacaoReavaliacaoBem.setDataLancamento(dataLancamento);
                efetivacaoReavaliacaoBem.setLote(loteEfetivacaoReavaliacaoBem);
                efetivacaoReavaliacaoBem.setReavaliacaoBem(reavaliacaoBem);
                efetivacaoReavaliacaoBem.setBem(reavaliacaoBem.getBem());
                EstadoBem estadoInicial = bemFacade.recuperarUltimoEstadoDoBem(reavaliacaoBem.getBem());
                EstadoBem estadoResultante = em.merge(bemFacade.criarNovoEstadoResultanteAPartirDoEstadoInicial(estadoInicial));
                efetivacaoReavaliacaoBem.setEstadoInicial(estadoInicial);
                efetivacaoReavaliacaoBem.setEstadoResultante(estadoResultante);
                efetivacaoReavaliacaoBem.setSituacaoEventoBem(SituacaoEventoBem.RECUSADO);
                loteEfetivacaoReavaliacaoBem.getEfetivacoes().add(efetivacaoReavaliacaoBem);
                bensParaDesbloquear.add(efetivacaoReavaliacaoBem.getBem().getId());
            }
        }
        configMovimentacaoBemFacade.desbloquearBens(configuracao, bensParaDesbloquear);
        em.merge(loteEfetivacaoReavaliacaoBem);
    }

    private void contabilizar(LoteEfetivacaoReavaliacaoBem lote) {
        integrador.reavaliarBens(lote.getEfetivacoes(), "reavaliação de bens " + lote.getCodigo(), lote.isReavaliacaoBemAumentativa());
    }

    public List<EfetivacaoReavaliacaoBem> buscarEfetivacoesReavaliacaoPorLoteReavaliacao(LoteReavaliacaoBem lote) {
        StringBuilder sql = new StringBuilder();
        sql.append("  select item.*, ev.* from efetivacaoreavaliacaobem item ")
            .append("   inner join eventobem ev on ev.id = item.id ")
            .append("   inner join reavaliacaobem ra on ra.id = item.reavaliacaobem_id ")
            .append(" where ra.lotereavaliacaobem_id = :lote_id ");
        Query q = em.createNativeQuery(sql.toString(), EfetivacaoReavaliacaoBem.class);
        q.setParameter("lote_id", lote.getId());
        if (q.getResultList().isEmpty()) {
            return Lists.newArrayList();
        }
        return q.getResultList();
    }

    public List<LoteReavaliacaoBem> buscarLotesEfetivados(LoteEfetivacaoReavaliacaoBem lote) {
        StringBuilder sql = new StringBuilder();
        sql.append("  SELECT DISTINCT lote.* ")
            .append(" FROM lotereavaliacaobem lote ")
            .append("   INNER JOIN reavaliacaobem reavaliacao ON reavaliacao.lotereavaliacaobem_id = lote.id ")
            .append("   INNER JOIN efetivacaoreavaliacaobem efetivacao ON efetivacao.reavaliacaobem_id = reavaliacao.id ")
            .append(" WHERE efetivacao.lote_id = :lote_id");
        Query q = em.createNativeQuery(sql.toString(), LoteReavaliacaoBem.class);
        q.setParameter("lote_id", lote.getId());
        List<LoteReavaliacaoBem> lotes = Lists.newArrayList();
        if (!q.getResultList().isEmpty()) {
            lotes = q.getResultList();
            for (LoteReavaliacaoBem loteReavaliacaoBem : lotes) {
                loteReavaliacaoBemFacade.atribuirHierarquiaAoLoteRevaliacaoBem(loteReavaliacaoBem);
            }
        }
        return lotes;
    }

    public List<LoteReavaliacaoBem> buscarLotesNaoAvaliadosPorUnidadeEResponsavel(UnidadeOrganizacional unidadeOrganizacional, PessoaFisica responsavel, TipoBem tipoBem, TipoOperacaoReavaliacaoBens tipoOperacao) {
        StringBuilder sql = new StringBuilder();
        sql.append(" select lote.* ")
            .append("  from lotereavaliacaobem lote ")
            .append("where lote.unidadeorigem_id = :unidade_id ")
            .append("  and lote.responsavel_id = :responsavel_id ")
            .append("  and lote.situacaoreavaliacaobem = :situacao_lote")
            .append("  and lote.tipoBem = :tipobem ")
            .append("  and lote.tipooperacaobem = :tipoOperacao ")
            .append("order by lote.codigo ");
        Query q = em.createNativeQuery(sql.toString(), LoteReavaliacaoBem.class);
        q.setParameter("unidade_id", unidadeOrganizacional.getId());
        q.setParameter("responsavel_id", responsavel.getId());
        q.setParameter("tipobem", tipoBem.name());
        q.setParameter("situacao_lote", SituacaoDaSolicitacao.AGUARDANDO_EFETIVACAO.name());
        q.setParameter("tipoOperacao", tipoOperacao.name());
        Util.imprimeSQL(sql.toString(), q);
        return q.getResultList();
    }

    public ConfigMovimentacaoBemFacade getConfigMovimentacaoBemFacade() {
        return configMovimentacaoBemFacade;
    }

    public LoteReavaliacaoBemFacade getLoteReavaliacaoBemFacade() {
        return loteReavaliacaoBemFacade;
    }

    public HierarquiaOrganizacionalFacade getHierarquiaOrganizacionalFacade() {
        return hierarquiaOrganizacionalFacade;
    }

    public SingletonGeradorCodigo getSingletonGeradorCodigo() {
        return singletonGeradorCodigo;
    }

    public SistemaFacade getSistemaFacade() {
        return sistemaFacade;
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
